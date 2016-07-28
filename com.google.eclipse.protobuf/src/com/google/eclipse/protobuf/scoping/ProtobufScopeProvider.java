/*
 * Copyright (c) 2016 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.OPTION_SOURCE__TARGET;
import static com.google.eclipse.protobuf.scoping.OptionType.typeOf;
import static com.google.eclipse.protobuf.util.Encodings.UTF_8;
import static com.google.eclipse.protobuf.util.Tracer.DEBUG_SCOPING;
import static com.google.eclipse.protobuf.validation.ProtobufResourceValidator.getScopeProviderTimingCollector;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonMap;
import static org.eclipse.emf.ecore.resource.ContentHandler.UNSPECIFIED_CONTENT_TYPE;
import static org.eclipse.xtext.EcoreUtil2.resolveLazyCrossReferences;
import static org.eclipse.xtext.resource.XtextResource.OPTION_ENCODING;
import static org.eclipse.xtext.util.CancelIndicator.NullImpl;

import com.google.eclipse.protobuf.model.util.ModelObjects;
import com.google.eclipse.protobuf.naming.NameResolver;
import com.google.eclipse.protobuf.naming.ProtobufQualifiedNameConverter;
import com.google.eclipse.protobuf.preferences.general.PreferenceNames;
import com.google.eclipse.protobuf.protobuf.AbstractCustomOption;
import com.google.eclipse.protobuf.protobuf.AbstractOption;
import com.google.eclipse.protobuf.protobuf.ComplexType;
import com.google.eclipse.protobuf.protobuf.ComplexTypeLink;
import com.google.eclipse.protobuf.protobuf.ComplexValue;
import com.google.eclipse.protobuf.protobuf.ComplexValueField;
import com.google.eclipse.protobuf.protobuf.CustomFieldOption;
import com.google.eclipse.protobuf.protobuf.CustomOption;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.ExtensibleType;
import com.google.eclipse.protobuf.protobuf.FieldName;
import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.LiteralLink;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.NativeFieldOption;
import com.google.eclipse.protobuf.protobuf.NativeOption;
import com.google.eclipse.protobuf.protobuf.OneOf;
import com.google.eclipse.protobuf.protobuf.OptionField;
import com.google.eclipse.protobuf.protobuf.OptionSource;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.protobuf.Rpc;
import com.google.eclipse.protobuf.protobuf.Stream;
import com.google.eclipse.protobuf.protobuf.TypeExtension;
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
import org.eclipse.xtext.linking.impl.LinkingHelper;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.ISelectable;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.scoping.impl.ImportNormalizer;
import org.eclipse.xtext.scoping.impl.SelectableBasedScope;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.util.IResourceScopeCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A scope provider for the Protobuf language.
 *
 * @author atrookey@google.com (Alexander Rookey)
 */
public class ProtobufScopeProvider extends AbstractDeclarativeScopeProvider
    implements ScopeProvider {
  @Inject private ComplexTypeFinderStrategy complexTypeFinderDelegate;
  @Inject private CustomOptionFieldFinder customOptionFieldFinder;
  @Inject private CustomOptionFieldNameFinder customOptionFieldNameFinder;
  @Inject private CustomOptionFinderStrategy customOptionFinderDelegate;
  @Inject private ExtensionFieldFinderStrategy extensionFieldFinderDelegate;
  @Inject private ExtensionFieldNameFinderStrategy extensionFieldNameFinderDelegate;
  @Inject private MessageFieldFinderStrategy messageFieldFinderDelegate;
  @Inject private ModelElementFinder modelElementFinder;
  @Inject private ModelObjects modelObjects;
  @Inject private NormalFieldNameFinderStrategy normalFieldNameFinderDelegate;
  @Inject private LinkingHelper linkingHelper;
  @Inject private IPreferenceStoreAccess storeAccess;
  @Inject private IUriResolver uriResolver;
  @Inject private IResourceScopeCache cache;
  @Inject private NameResolver nameResolver;
  @Inject private ProtobufQualifiedNameConverter nameConverter;

  private static final String CACHEKEY = "IMPORT_NORMALIZER_CACHE_KEY";

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

  private Collection<IEObjectDescription> allMessages(Protobuf root) {
    return modelElementFinder.find(root, complexTypeFinderDelegate, Message.class);
  }

  /**
   * Returns the InputStream associated with the resource at location {@code descriptorLocation}.
   */
  private InputStream openFile(URI fileLocation) throws IOException {
    URL url = new URL(fileLocation.toString());
    return url.openConnection().getInputStream();
  }

  // TODO (atrookey) Create utility for getting package.
  private String getPackageOfResource(Resource resource) {
    Protobuf object;
    if (resource != null && (object = (Protobuf) resource.getContents().get(0)) != null) {
      for (EObject content : object.getElements()) {
        if (content instanceof com.google.eclipse.protobuf.protobuf.Package) {
          return ((com.google.eclipse.protobuf.protobuf.Package) content).getImportedNamespace();
        }
      }
    }
    return "";
  }

  /**
   * Returns descriptor associated with the current project.
   */
  private  @Nullable Resource getDescriptorResource(EObject context) {
    URI descriptorLocation;
    IProject project = EResources.getProjectOf(context.eResource());
    IPreferenceStore store = storeAccess.getWritablePreferenceStore(project);
    String rawDescriptorLocation = store.getString(PreferenceNames.DESCRIPTOR_PROTO_PATH);
    descriptorLocation =
        URI.createURI(uriResolver.resolveUri(rawDescriptorLocation, null, project));
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
    return null;
  }

  /**
   * Returns name of an object as a QualifiedName.
   */
  private QualifiedName getEObjectName(EObject object) {
    ICompositeNode node = NodeModelUtils.getNode(object);
    String name = linkingHelper.getCrossRefNodeAsString(node, true);
    return QualifiedName.create(name);
  }

  /**
   * Returns the local scope provider.
   */
  private ProtobufImportedNamespaceAwareLocalScopeProvider getLocalScopeProvider() {
    return (ProtobufImportedNamespaceAwareLocalScopeProvider) super.getDelegate();
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
   * Recursively scope {@code FieldName} starting with an {@code OptionSource}.
   */
  private IScope getScopeOfFieldName(
      IScope delegatedScope, OptionSource optionSource, EReference reference) {
    IScope retval = IScope.NULLSCOPE;
    IScope parentScope = super.getScope(optionSource, OPTION_SOURCE__TARGET);
    QualifiedName optionSourceName = getEObjectName(optionSource);
    IEObjectDescription indexedElementDescription = parentScope.getSingleElement(optionSourceName);
    if (indexedElementDescription != null) {
      EObject indexedElement = indexedElementDescription.getEObjectOrProxy();
      retval = getLocalScopeOfMessageFieldOrGroup(delegatedScope, indexedElement, reference);
    }
    return retval;
  }

  /**
   * Locally scope any children of {@code context} that are of type {@code OneOf}.
   */
  private IScope getScopeOfOneOf(IScope scope, EObject context, EReference reference) {
    IScope result = scope;
    for (EObject element : context.eContents()) {
      if (element instanceof OneOf) {
        result = getLocalScopeProvider().getLocalElementsScope(result, element, reference);
      }
    }
    return result;
  }

  /**
   * Recursively scope {@code OptionField} starting with an {@code OptionSource}.
   */
  private IScope getScopeOfOptionField(
      OptionSource optionSource, EReference reference, int fieldIndex, EList<OptionField> fields) {
    if (fieldIndex < 0 || fields.size() <= fieldIndex) {
      throw new IllegalArgumentException();
    }
    IScope parentScope, retval = IScope.NULLSCOPE;
    QualifiedName name = QualifiedName.EMPTY;
    if (fieldIndex == 0) {
      parentScope = super.getScope(optionSource, OPTION_SOURCE__TARGET);
      name = getEObjectName(optionSource);
    } else {
      OptionField parentOptionField = fields.get(fieldIndex - 1);
      parentScope = getScopeOfOptionField(optionSource, reference, fieldIndex - 1, fields);
      name = getEObjectName(parentOptionField);
    }
    IEObjectDescription indexedElementDescription = parentScope.getSingleElement(name);
    if (indexedElementDescription != null) {
      EObject indexedElement = indexedElementDescription.getEObjectOrProxy();
      retval = getLocalScopeOfMessageFieldOrGroup(retval, indexedElement, reference);
    }
    return retval;
  }

  /**
   * Recursively scope nested OptionFields and FieldNames.
   *
   * <p>If {@code IndexedElement} refers to an {@code MessageField}, scope the {@code Message} of
   * that field. If it refers to a {@code Group}, return the scope of that {@code Group}.
   */
  private IScope getLocalScopeOfMessageFieldOrGroup(
      IScope parentScope, EObject indexedElement, EReference reference) {
    IScope retval = IScope.NULLSCOPE;
    if (indexedElement instanceof MessageField) {
      TypeLink typeLink = ((MessageField) indexedElement).getType();
      if (typeLink instanceof ComplexTypeLink) {
        ComplexType complexType = ((ComplexTypeLink) typeLink).getTarget();
        IScope result =
            getLocalScopeProvider().getLocalElementsScope(parentScope, complexType, reference);
        retval = getScopeOfOneOf(result, complexType, reference);
      }
    }
    if (indexedElement instanceof Group) {
      retval =
          getLocalScopeProvider().getLocalElementsScope(parentScope, indexedElement, reference);
    }
    return retval;
  }

  @Override
  public Collection<IEObjectDescription> potentialComplexTypesFor(MessageField field) {
    return modelElementFinder.find(field, complexTypeFinderDelegate, ComplexType.class);
  }

  @Override
  public Collection<IEObjectDescription> potentialExtensibleTypesFor(TypeExtension extension) {
    Protobuf root = modelObjects.rootOf(extension);
    return modelElementFinder.find(root, complexTypeFinderDelegate, ExtensibleType.class);
  }

  @Override
  public Collection<IEObjectDescription> potentialExtensionFieldNames(ComplexValue value) {
    return customOptionFieldNameFinder.findFieldNamesSources(
        value, extensionFieldNameFinderDelegate);
  }

  @Override
  public Collection<IEObjectDescription> potentialExtensionFieldsFor(AbstractCustomOption option) {
    return customOptionFieldFinder.findOptionFields(option, extensionFieldFinderDelegate);
  }

  @Override
  public Collection<IEObjectDescription> potentialMessageFieldsFor(AbstractCustomOption option) {
    return customOptionFieldFinder.findOptionFields(option, messageFieldFinderDelegate);
  }

  @Override
  public Collection<IEObjectDescription> potentialMessagesFor(Rpc rpc) {
    Protobuf root = modelObjects.rootOf(rpc);
    return allMessages(root);
  }

  @Override
  public Collection<IEObjectDescription> potentialMessagesFor(Stream stream) {
    Protobuf root = modelObjects.rootOf(stream);
    return allMessages(root);
  }

  @Override
  public Collection<IEObjectDescription> potentialNormalFieldNames(ComplexValue value) {
    return customOptionFieldNameFinder.findFieldNamesSources(value, normalFieldNameFinderDelegate);
  }

  @Override
  public Collection<IEObjectDescription> potentialSourcesFor(AbstractCustomOption option) {
    OptionType optionType = typeOf((AbstractOption) option);
    Collection<IEObjectDescription> descriptions = emptySet();
    if (optionType != null) {
      descriptions = modelElementFinder.find(option, customOptionFinderDelegate, optionType);
    }
    return descriptions;
  }

  /**
   * Recursively scopes the {@code FieldName} starting with the {@code OptionSource}.
   *
   * For example:
   *
   * <pre>
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
    OptionSource optionSource = null;
    EObject valueField = fieldName.eContainer();
    if (valueField != null && valueField instanceof ValueField) {
      EObject complexValue = valueField.eContainer();
      if (complexValue != null && complexValue instanceof ComplexValue) {
        EObject unknownOption = complexValue.eContainer();
        IScope delegatedScope = super.delegateGetScope(fieldName, reference);
        if (unknownOption != null && unknownOption instanceof ComplexValueField) {
          ComplexValueField complexValueField = (ComplexValueField) unknownOption;
          IScope parentScope = scope_FieldName_target(complexValueField.getName(), reference);
          IEObjectDescription indexedElementDescription =
              parentScope.getSingleElement(this.getEObjectName(complexValueField.getName()));
          // TODO (atrookey) This is only necessary because of ExtensionFieldName.
          // Could be made more specific.
          return getLocalScopeOfMessageFieldOrGroup(
              delegatedScope, indexedElementDescription.getEObjectOrProxy(), reference);
        }
        if (unknownOption != null && unknownOption instanceof NativeFieldOption) {
          NativeFieldOption nativeFieldOption = (NativeFieldOption) unknownOption;
          optionSource = nativeFieldOption.getSource();
        }
        if (unknownOption != null && unknownOption instanceof CustomFieldOption) {
          CustomFieldOption customFieldOption = (CustomFieldOption) unknownOption;
          optionSource = customFieldOption.getSource();
        }
        if (unknownOption != null && unknownOption instanceof NativeOption) {
          NativeOption option = (NativeOption) unknownOption;
          optionSource = option.getSource();
        }
        if (unknownOption != null && unknownOption instanceof CustomOption) {
          CustomOption option = (CustomOption) unknownOption;
          optionSource = option.getSource();
        }
        return getScopeOfFieldName(delegatedScope, optionSource, reference);
      }
    }
    return null;
  }

  /**
   * Creates a scope containing elements of type {@code Literal} that can be
   * referenced with their local name only.
   *
   * For example:
   *
   * <pre>
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
  public IScope scope_LiteralLink_target(LiteralLink literalLink, EReference reference) {
    IScope scope = IScope.NULLSCOPE;
    scope =
        createLiteralLinkResolvedScopeForResource(
            reference, scope, getDescriptorResource(literalLink));
    scope = createLiteralLinkResolvedScopeForResource(reference, scope, literalLink.eResource());
    return scope;
  }

  // TODO (atrookey) Create resolvers for ProtobufImportScopes at all scope levels,
  // not just top level.
  private IScope createLiteralLinkResolvedScopeForResource(
      EReference reference, IScope parent, Resource resource) {
    IScope scope = parent;
    scope = getProtobufImportScope(scope, resource, reference);
    List<ImportNormalizer> protoNormalizers =
        cache.get(
            CACHEKEY,
            resource,
            new Provider<List<ImportNormalizer>>() {
              @Override
              public List<ImportNormalizer> get() {
                return createEnumElementResolvers(
                    resource.getContents().get(0), getPackageOfResource(resource), false);
              }
            });
    if (scope instanceof ProtobufImportScope) {
      for (ImportNormalizer normalizer : protoNormalizers) {
        ((ProtobufImportScope) scope).addNormalizer(normalizer);
      }
    } else if (scope instanceof SelectableBasedScope) {
      ISelectable allDescriptions = getLocalScopeProvider().getAllDescriptions(resource);
      scope =
          getLocalScopeProvider()
              .createImportScope(
                  scope, protoNormalizers, allDescriptions, reference.getEReferenceType(), false);
    }
    return scope;
  }

  /**
   * Creates an {@code ImportNormalizer} for every {@code Enum} that is a descendant of {@code context}.
   */
  private List<ImportNormalizer> createEnumElementResolvers(
      EObject context, String qualifiedName, boolean ignoreCase) {
    List<ImportNormalizer> importedNamespaceResolvers = new ArrayList<>();
    for (EObject child : context.eContents()) {
      if (child instanceof Enum) {
        String name = appendNameOfEObject(qualifiedName, child);
        if (!name.isEmpty()) {
          ImportNormalizer resolver =
              getLocalScopeProvider().createImportedNamespaceResolver(name, ignoreCase);
          if (resolver != null) {
            importedNamespaceResolvers.add(resolver);
          }
        }
      }
      if (child instanceof Message) {
        String name = appendNameOfEObject(qualifiedName, child);
        importedNamespaceResolvers.addAll(createEnumElementResolvers(child, name, ignoreCase));
      }
    }
    return importedNamespaceResolvers;
  }

  private String appendNameOfEObject(String qualifiedName, EObject child) {
    String childName = nameResolver.nameOf(child);
    if (qualifiedName.isEmpty()) {
      return childName;
    }
    return qualifiedName + nameConverter.getDelimiter() + childName;
  }

  /**
   * Recursively scopes the {@code OptionField} starting with the {@code OptionSource}.
   *
   * For example:
   *
   * <pre>
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
  public @Nullable IScope scope_OptionField_target(OptionField optionField, EReference reference) {
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
        return getScopeOfOptionField(optionSource, reference, index, fields);
      }
    }
    return null;
  }

  /**
   * Creates a scope containing the default options defined in descriptor.proto.
   *
   * For example:
   *
   * <pre>
   * option java_package = "com.example.foo";
   * </pre>
   *
   * The {@code OptionSource} {@code java_package} contains a cross-reference to {@code
   * google.protobuf.FileOptions.java_package} defined in descriptor.proto.
   */
  public IScope scope_OptionSource_target(OptionSource optionSource, EReference reference) {
    Resource descriptorResource = getDescriptorResource(optionSource);
    String descriptorMessage =
        getPackageOfResource(descriptorResource)
            + nameConverter.getDelimiter()
            + getOptionType(optionSource);
    ImportNormalizer normalizer =
        getLocalScopeProvider().createImportedNamespaceResolver(descriptorMessage, false);
    IScope scope = delegateGetScope(optionSource, reference);
    scope = getProtobufImportScope(scope, getDescriptorResource(optionSource), reference);
    ((ProtobufImportScope) scope).addNormalizer(normalizer);
    return scope;
  }

  /** Returns the top level scope of the {@code Resource}. */
  private ProtobufImportScope getProtobufImportScope(
      IScope parent, Resource resource, EReference reference) {
    EObject protobuf = resource.getContents().get(0);
    IScope descriptorResourceScope =
        getLocalScopeProvider().getResourceScope(parent, protobuf, reference);
    return (ProtobufImportScope)
        getLocalScopeProvider().getLocalElementsScope(descriptorResourceScope, protobuf, reference);
  }
}
