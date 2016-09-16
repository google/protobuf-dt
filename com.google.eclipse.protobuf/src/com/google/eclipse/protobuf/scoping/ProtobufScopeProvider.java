/*
 * Copyright (c) 2016 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.util.Encodings.UTF_8;
import static com.google.eclipse.protobuf.util.Tracer.DEBUG_SCOPING;
import static com.google.eclipse.protobuf.validation.ProtobufResourceValidator.getScopeProviderTimingCollector;
import static java.util.Collections.singletonMap;
import static org.eclipse.emf.ecore.resource.ContentHandler.UNSPECIFIED_CONTENT_TYPE;
import static org.eclipse.xtext.EcoreUtil2.resolveLazyCrossReferences;
import static org.eclipse.xtext.resource.XtextResource.OPTION_ENCODING;
import static org.eclipse.xtext.util.CancelIndicator.NullImpl;

import com.google.eclipse.protobuf.naming.ProtobufQualifiedNameConverter;
import com.google.eclipse.protobuf.naming.ProtobufQualifiedNameProvider;
import com.google.eclipse.protobuf.preferences.general.PreferenceNames;
import com.google.eclipse.protobuf.protobuf.ComplexType;
import com.google.eclipse.protobuf.protobuf.ComplexTypeLink;
import com.google.eclipse.protobuf.protobuf.ComplexValue;
import com.google.eclipse.protobuf.protobuf.ComplexValueField;
import com.google.eclipse.protobuf.protobuf.CustomFieldOption;
import com.google.eclipse.protobuf.protobuf.CustomOption;
import com.google.eclipse.protobuf.protobuf.DefaultValueFieldOption;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.ExtensionFieldName;
import com.google.eclipse.protobuf.protobuf.FieldName;
import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.LiteralLink;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.NativeFieldOption;
import com.google.eclipse.protobuf.protobuf.NativeOption;
import com.google.eclipse.protobuf.protobuf.OneOf;
import com.google.eclipse.protobuf.protobuf.OptionField;
import com.google.eclipse.protobuf.protobuf.OptionSource;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.protobuf.Rpc;
import com.google.eclipse.protobuf.protobuf.Stream;
import com.google.eclipse.protobuf.protobuf.TypeLink;
import com.google.eclipse.protobuf.protobuf.ValueField;
import com.google.eclipse.protobuf.util.EResources;
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.apache.log4j.Level;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.scoping.impl.ImportNormalizer;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.util.IResourceScopeCache;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A scope provider for the Protobuf language.
 *
 * @author atrookey@google.com (Alexander Rookey)
 */
public class ProtobufScopeProvider extends AbstractDeclarativeScopeProvider {
  /**
   * Returns the name of the descriptor.proto message declaring default options. The options must
   * match the option type of {@code context}. For example, if {@code context} is a file option,
   * {@code FileOptions} should be returned because it contains the declarations of the default file
   * options.
   */
  // TODO (atrookey) Create utility for getting the type of an Option.
  private static String getOptionType(EObject context) {
    if (context == null) {
      return "FileOptions";
    }
    if (context instanceof Message) {
      return "MessageOptions";
    }
    if (context instanceof Enum) {
      return "EnumOptions";
    }
    if (context instanceof FieldOption) {
      return "FieldOptions";
    }
    if (context instanceof Rpc) {
      return "MethodOptions";
    }
    if (context instanceof Stream) {
      return "StreamOptions";
    }
    if (context instanceof Literal) {
      return "EnumValueOptions";
    }
    return getOptionType(context.eContainer());
  }

  @Inject private IPreferenceStoreAccess storeAccess;
  @Inject private IUriResolver uriResolver;
  @Inject private IResourceScopeCache cache;
  @Inject private ProtobufQualifiedNameConverter nameConverter;
  @Inject private ProtobufQualifiedNameProvider nameProvider;

  private ImportNormalizer createImportNormalizerForEObject(EObject element, boolean ignoreCase) {
    QualifiedName name = nameProvider.getFullyQualifiedName(element);
    return getLocalScopeProvider().createImportedNamespaceResolver(name.toString(), ignoreCase);
  }

  private List<ImportNormalizer> createImportNormalizersForComplexType(
      ComplexType complexType, boolean ignoreCase) {
    List<ImportNormalizer> normalizers = new ArrayList<>();
    normalizers.add(createImportNormalizerForEObject(complexType, ignoreCase));
    normalizers.addAll(createImportNormalizersForOneOf(complexType.eContents(), ignoreCase));
    return normalizers;
  }

  private List<ImportNormalizer> createImportNormalizersForOneOf(
      EList<EObject> children, boolean ignoreCase) {
    List<ImportNormalizer> normalizers = new ArrayList<>();
    for (EObject child : children) {
      if (child instanceof OneOf) {
        normalizers.add(createImportNormalizerForEObject(child, ignoreCase));
        normalizers.addAll(createImportNormalizersForOneOf(child.eContents(), ignoreCase));
      }
    }
    return normalizers;
  }

  /**
   * An {@code IndexedElement} can be a MessageField or Group. When scoping types {@code FieldName},
   * {@code LiteralLink}, or {@code OptionField} that are all related to protocol buffer options, a
   * scope can be created by traversing the EMF Model to find a suitable {@code IndexedElement}, and
   * then creating an import normalized scope for the {@code ComplexType} of the {@code
   * MessageField} or {@code Group}.
   *
   * <p>For example: <pre>
   * enum MyEnum {
   *   FOO = 1;
   * }
   * extend google.protobuf.ServiceOptions {
   *   optional MyEnum my_service_option = 50005;
   * }
   * service MyService {
   *   option (my_service_option) = FOO;
   * }
   * </pre>
   *
   * To scope the {@code LiteralLink} {@code FOO} in {@code MyService}, the {@code MessageField}
   * {@code my_service_option} is found by traversing the model. The method
   * createNormalizedScopeForIndexedElement(IndexedElement, EReference) creates and returns an
   * import normalized scope for the type of the {@code MessageField}, {@code MyEnum}.
   */
  private IScope createNormalizedScopeForIndexedElement(
      IndexedElement indexedElement, EReference reference) {
    HashMap<EReference, IScope> scopeMap =
        cache.get(
            indexedElement,
            indexedElement.eResource(),
            new Provider<HashMap<EReference, IScope>>() {
              @Override
              public HashMap<EReference, IScope> get() {
                return new HashMap<>();
              }
            });
    if (!scopeMap.containsKey(reference)) {
      IScope scope = null;
      if (indexedElement instanceof MessageField) {
        TypeLink typeLink = ((MessageField) indexedElement).getType();
        if (typeLink instanceof ComplexTypeLink) {
          ComplexType complexType = ((ComplexTypeLink) typeLink).getTarget();
          scope = getGlobalScopeProvider().getScope(complexType.eResource(), reference);
          List<ImportNormalizer> normalizers =
              createImportNormalizersForComplexType(complexType, false);
          scope = createProtobufImportScope(scope, complexType, reference);
          ((ProtobufImportScope) scope).addAllNormalizers(normalizers);
        }
      }
      if (indexedElement instanceof Group) {
        Group group = (Group) indexedElement;
        scope = getGlobalScopeProvider().getScope(group.eResource(), reference);
        ImportNormalizer normalizer = createImportNormalizerForEObject(group, false);
        scope = createProtobufImportScope(scope, group, reference);
        ((ProtobufImportScope) scope).addNormalizer(normalizer);
      }
      scopeMap.put(reference, scope);
    }
    return scopeMap.get(reference);
  }

  private IScope createProtobufImportScope(IScope parent, EObject context, EReference reference) {
    IScope scope = parent;
    if (context.eContainer() == null) {
      scope = getLocalScopeProvider().getResourceScope(scope, context, reference);
    } else {
      scope = createProtobufImportScope(scope, context.eContainer(), reference);
    }
    return getLocalScopeProvider().getLocalElementsScope(scope, context, reference);
  }

  /** Returns descriptor associated with the current project. */
  private @Nullable Resource getDescriptorResource(EObject context) {
    IProject project = EResources.getProjectOf(context.eResource());
    IPreferenceStore store = storeAccess.getWritablePreferenceStore(project);
    String rawDescriptorLocation = store.getString(PreferenceNames.DESCRIPTOR_PROTO_PATH);
    String resolvedUri = null;
    if (PreferenceNames.DESCRIPTOR_PROTO_PATH.equals(rawDescriptorLocation)) {
      resolvedUri = PreferenceNames.DEFAULT_DESCRIPTOR_LOCATION.toString();
    } else {
      resolvedUri = uriResolver.resolveUri(rawDescriptorLocation, null, project);
    }
    if (resolvedUri != null) {
      URI descriptorLocation = URI.createURI(resolvedUri);
      ResourceSet resourceSet = context.eResource().getResourceSet();
      Resource resource = resourceSet.getResource(descriptorLocation, true);
      if (resource != null) {
        return resource;
      }
      try {
        InputStream contents = openFile(descriptorLocation);
        resource = resourceSet.createResource(descriptorLocation, UNSPECIFIED_CONTENT_TYPE);
        resource.load(contents, singletonMap(OPTION_ENCODING, UTF_8));
        resolveLazyCrossReferences(resource, NullImpl);
        return resource;
      } catch (IOException e) {
        logger.log(Level.ERROR, e);
      }
    }
    return null;
  }

  /** Returns the global scope provider. */
  private ProtobufImportUriGlobalScopeProvider getGlobalScopeProvider() {
    return getLocalScopeProvider().getGlobalScopeProvider();
  }

  /** Returns the local scope provider. */
  private ProtobufImportedNamespaceAwareLocalScopeProvider getLocalScopeProvider() {
    return (ProtobufImportedNamespaceAwareLocalScopeProvider) super.getDelegate();
  }

  // TODO (atrookey) Create utility for getting package.
  private String getPackageOfResource(Resource resource) {
    return cache.get(
        "Package",
        resource,
        new Provider<String>() {
          @Override
          public String get() {
            Protobuf protobuf;
            if (resource != null && (protobuf = (Protobuf) resource.getContents().get(0)) != null) {
              for (EObject content : protobuf.getElements()) {
                if (content instanceof Package) {
                  return ((Package) content).getImportedNamespace();
                }
              }
            }
            return "";
          }
        });
  }

  @Override
  public IScope getScope(EObject context, EReference reference) {
    if (DEBUG_SCOPING) {
      getScopeProviderTimingCollector().startTimer();
    }
    IScope scope = super.getScope(context, reference);
    if (DEBUG_SCOPING) {
      getScopeProviderTimingCollector().stopTimer();
    }
    return scope;
  }

  /**
   * Returns the InputStream associated with the resource at location {@code descriptorLocation}.
   */
  private InputStream openFile(URI fileLocation) throws IOException {
    URL url = new URL(fileLocation.toString());
    return url.openConnection().getInputStream();
  }

  /**
   * Scopes the {@code FieldName}.
   *
   * <p>For example: <pre>
   * message FooOptions {
   *   optional int32 opt1 = 1;
   * }
   * extend google.protobuf.FieldOptions {
   *   optional FooOptions foo_options = 1234;
   * }
   * message Bar {
   *   optional int32 b = 1 [(foo_options) = { opt1: 123 }];
   * }
   * </pre>
   *
   * The {@code NormalFieldName} {@code opt1} contains a cross-reference to {@code FooOptions.opt1}.
   */
  public IScope scope_FieldName_target(FieldName fieldName, EReference reference) {
    if (fieldName instanceof ExtensionFieldName) {
      return getLocalScopeProvider().getResourceScope(fieldName.eResource(), reference);
    }
    IndexedElement indexedElement = null;
    OptionSource optionSource = null;
    EObject valueField = fieldName.eContainer();
    if (valueField instanceof ValueField) {
      EObject complexValue = valueField.eContainer();
      if (complexValue instanceof ComplexValue) {
        EObject unknownOption = complexValue.eContainer();
        if (unknownOption instanceof ComplexValueField) {
          indexedElement = ((ComplexValueField) unknownOption).getName().getTarget();
        }
        if (unknownOption instanceof NativeFieldOption) {
          NativeFieldOption nativeFieldOption = (NativeFieldOption) unknownOption;
          optionSource = nativeFieldOption.getSource();
        }
        if (unknownOption instanceof CustomFieldOption) {
          CustomFieldOption customFieldOption = (CustomFieldOption) unknownOption;
          optionSource = customFieldOption.getSource();
        }
        if (unknownOption instanceof NativeOption) {
          NativeOption option = (NativeOption) unknownOption;
          optionSource = option.getSource();
        }
        if (unknownOption instanceof CustomOption) {
          CustomOption option = (CustomOption) unknownOption;
          optionSource = option.getSource();
        }
        if (optionSource != null) {
          indexedElement = optionSource.getTarget();
        }
        if (indexedElement instanceof MessageField) {
          return createNormalizedScopeForIndexedElement(indexedElement, reference);
        }
      }
    }
    return null;
  }

  /**
   * Creates a scope containing elements of type {@code Literal} that can be referenced with their
   * local name only.
   *
   * <p>For example: <pre>
   * enum MyEnum {
   *   FOO = 1;
   * }
   * extend google.protobuf.ServiceOptions {
   *   optional MyEnum my_service_option = 50005;
   * }
   * service MyService {
   *   option (my_service_option) = FOO;
   * }
   * </pre>
   *
   * The {@code LiteralLink} {@code FOO} contains a cross-reference to {@code MyEnum.FOO}.
   */
  public @Nullable IScope scope_LiteralLink_target(LiteralLink literalLink, EReference reference) {
    EObject container = literalLink.eContainer();
    IndexedElement indexedElement = null;
    if (container instanceof DefaultValueFieldOption) {
      container = container.eContainer();
      if (container instanceof IndexedElement) {
        indexedElement = (IndexedElement) container;
      }
    }
    if (container instanceof NativeFieldOption) {
      indexedElement = ((NativeFieldOption) container).getSource().getTarget();
    }
    if (container instanceof NativeOption) {
      indexedElement = ((NativeOption) container).getSource().getTarget();
    }
    if (container instanceof CustomFieldOption) {
      EList<OptionField> fields = ((CustomFieldOption) container).getFields();
      if (!fields.isEmpty()) {
        indexedElement = fields.get(fields.size() - 1).getTarget();
      } else {
        indexedElement = ((CustomFieldOption) container).getSource().getTarget();
      }
    }
    if (container instanceof CustomOption) {
      EList<OptionField> fields = ((CustomOption) container).getFields();
      if (!fields.isEmpty()) {
        indexedElement = fields.get(fields.size() - 1).getTarget();
      } else {
        indexedElement = ((CustomOption) container).getSource().getTarget();
      }
    }
    return createNormalizedScopeForIndexedElement(indexedElement, reference);
  }

  /**
   * Recursively scopes the {@code OptionField} starting with the {@code OptionSource}.
   *
   * <p>For example: <pre>
   * message Code {
   *   optional double number = 1;
   * }
   * message Type {
   *   optional Code code = 1;
   * }
   * extend proto2.FieldOptions {
   *   optional Type type = 1000;
   * }
   * message Person {
   *   optional bool active = 1 [(type).code.number = 68];
   * }
   * </pre>
   *
   * The {@code OptionField} {@code number} contains a cross-reference to {@code Code.number}.
   */
  public IScope scope_OptionField_target(OptionField optionField, EReference reference) {
    IScope scope = getLocalScopeProvider().getResourceScope(optionField.eResource(), reference);
    EObject customOption = optionField.eContainer();
    if (customOption != null) {
      OptionSource optionSource = null;
      EList<OptionField> fields = null;
      if (customOption instanceof CustomFieldOption) {
        optionSource = ((CustomFieldOption) customOption).getSource();
        fields = ((CustomFieldOption) customOption).getFields();
      }
      if (customOption instanceof CustomOption) {
        optionSource = ((CustomOption) customOption).getSource();
        fields = ((CustomOption) customOption).getFields();
      }
      if (optionSource != null && fields != null) {
        int index = fields.indexOf(optionField);
        if (index < 0 || fields.size() <= index) {
          throw new IllegalArgumentException(
              "index is " + index + " but field.size() is " + fields.size());
        }
        IndexedElement indexedElement = null;
        if (index == 0) {
          indexedElement = optionSource.getTarget();
        } else {
          indexedElement = fields.get(index - 1).getTarget();
        }
        return createNormalizedScopeForIndexedElement(indexedElement, reference);
      }
    }
    return scope;
  }

  /**
   * Creates a scope containing the default options defined in descriptor.proto.
   *
   * <p>For example: <pre>
   * option java_package = "com.example.foo";
   * </pre>
   *
   * The {@code OptionSource} {@code java_package} contains a cross-reference to {@code
   * google.protobuf.FileOptions.java_package} defined in descriptor.proto.
   */
  public IScope scope_OptionSource_target(OptionSource optionSource, EReference reference) {
    String optionType = getOptionType(optionSource);
    Resource resource = optionSource.eResource();
    IScope descriptorScope =
        cache.get(
            optionType,
            resource,
            new Provider<IScope>() {
              @Override
              public IScope get() {
                IScope scope = getGlobalScopeProvider().getScope(resource, reference);
                Resource descriptorResource = getDescriptorResource(optionSource);
                String descriptorMessage =
                    getPackageOfResource(descriptorResource)
                        + nameConverter.getDelimiter()
                        + optionType;
                ImportNormalizer normalizer =
                    getLocalScopeProvider()
                        .createImportedNamespaceResolver(descriptorMessage, false);
                scope =
                    createProtobufImportScope(
                        scope, getDescriptorResource(optionSource).getContents().get(0), reference);
                ((ProtobufImportScope) scope).addNormalizer(normalizer);
                return scope;
              }
            });
    return createProtobufImportScope(descriptorScope, optionSource, reference);
  }
}
