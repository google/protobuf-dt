/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.contentassist;

import static com.google.eclipse.protobuf.grammar.CommonKeyword.*;
import static com.google.eclipse.protobuf.protobuf.Modifier.*;
import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.*;
import static com.google.eclipse.protobuf.util.CommonWords.space;
import static java.lang.String.valueOf;
import static java.util.Collections.*;
import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;
import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.findActualSemanticObjectFor;
import static org.eclipse.xtext.util.Strings.toFirstLower;

import java.util.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.*;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.ui.PluginImageHelper;
import org.eclipse.xtext.ui.editor.contentassist.*;

import com.google.eclipse.protobuf.grammar.CommonKeyword;
import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.model.util.Properties;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.scoping.*;
import com.google.eclipse.protobuf.ui.grammar.CompoundElement;
import com.google.eclipse.protobuf.ui.labeling.Images;
import com.google.eclipse.protobuf.ui.util.*;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 *
 * @see <a href="http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist">Xtext Content Assist</a>
 */
public class ProtobufProposalProvider extends AbstractProtobufProposalProvider {

  @Inject private IEObjectDescriptionChooser descriptionChooser;
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private FieldOptions fieldOptions;
  @Inject private ModelFinder finder;
  @Inject private Images images;
  @Inject private IndexedElements indexedElements;
  @Inject private PluginImageHelper imageHelper;
  @Inject private Literals literals;
  @Inject private Options options;
  @Inject private Properties properties;

  @Override public void completeProtobuf_Syntax(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {}

  @Override public void completeSyntax_Name(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(PROTO2_IN_QUOTES, context, acceptor);
  }

  @Override public void complete_Syntax(EObject model, RuleCall ruleCall, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    String proposal = SYNTAX + space() + EQUAL_PROTO2_IN_QUOTES;
    proposeAndAccept(proposal, imageHelper.getImage(images.imageFor(Syntax.class)), context, acceptor);
  }

  @Override public void completeTypeRef_Type(EObject model, Assignment assignment, ContentAssistContext context, 
      ICompletionProposalAcceptor acceptor) {
    Collection<IEObjectDescription> scope = scoping().findMessageScope(model);
    for (IEObjectDescription d : descriptionChooser.shortestQualifiedNamesIn(scope)) {
      Image image = imageHelper.getImage(images.imageFor(d.getEObjectOrProxy()));
      proposeAndAccept(d, image, context, acceptor);
    }
  }

  @Override public void completeMessageRef_Type(EObject model, Assignment assignment, ContentAssistContext context, 
      ICompletionProposalAcceptor acceptor) {
    Collection<IEObjectDescription> scope = scoping().findTypeScope(model);
    for (IEObjectDescription d : descriptionChooser.shortestQualifiedNamesIn(scope)) {
      Image image = imageHelper.getImage(images.imageFor(d.getEObjectOrProxy()));
      proposeAndAccept(d, image, context, acceptor);
    }
  }
  
  @Override public void completeNativeOption_Source(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    Collection<Property> optionProperties = descriptor.availableOptionsFor(model);
    if (!optionProperties.isEmpty()) proposeOptions(optionProperties, context, acceptor);
  }

  private void proposeOptions(Collection<Property> optionProperties, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    for (Property p : optionProperties) proposeOption(p, context, acceptor);
  }

  @Override public void completeNativeOption_Value(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    NativeOption option = (NativeOption) model;
    Property property = (Property) options.sourceOf(option);
    if (property == null) return;
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    Enum enumType = descriptor.enumTypeOf(property);
    if (enumType != null) {
      proposeAndAccept(enumType, context, acceptor);
      return;
    }
    proposePrimitiveValues(property, context, acceptor);
  }

  @Override public void complete_ID(EObject model, RuleCall ruleCall, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {}

  @Override public void complete_STRING(EObject model, RuleCall ruleCall, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {}

  @Override public void completeKeyword(Keyword keyword, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (keyword == null) return;
    boolean proposalWasHandledAlready = completeKeyword(keyword.getValue(), context, acceptor);
    if (proposalWasHandledAlready) return;
    super.completeKeyword(keyword, context, acceptor);
  }

  private boolean completeKeyword(String keyword, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (isLastWordFromCaretPositionEqualTo(keyword, context)) return true;
    if (EQUAL.hasValue(keyword)) {
      EObject grammarElement = context.getLastCompleteNode().getGrammarElement();
      if (isKeyword(grammarElement, SYNTAX)) {
        proposeEqualProto2(context, acceptor);
      }
      return true;
    }
    if (OPENING_BRACKET.hasValue(keyword)) return proposeOpeningBracket(context, acceptor);
    if (OPENING_CURLY_BRACKET.hasValue(keyword)) {
      return context.getCurrentModel() instanceof Option;
    }
    if (TRUE.hasValue(keyword) || FALSE.hasValue(keyword)) {
      if (isBoolProposalValid(context)) {
        proposeBooleanValues(context, acceptor);
      }
      return true;
    }
    if (NAN.hasValue(keyword)) {
      if (isNanProposalValid(context)) {
        proposeAndAccept(keyword.toString(), context, acceptor);
      }
      return true;
    }
    // remove keyword proposals when current node is "]". At this position we
    // only accept "default" or field options.
    return context.getCurrentNode().getText().equals(CLOSING_BRACKET.toString());
  }

  private boolean isLastWordFromCaretPositionEqualTo(String word, ContentAssistContext context) {
    StyledText styledText = context.getViewer().getTextWidget();
    int valueLength = word.length();
    int start = styledText.getCaretOffset() - valueLength;
    if (start < 0) return false;
    String previousWord = styledText.getTextRange(start, valueLength);
    return word.equals(previousWord);
  }

  private boolean isKeyword(EObject object, CommonKeyword keyword) {
    return object instanceof Keyword && keyword.hasValue(((Keyword) object).getValue());
  }
  
  private void proposeEqualProto2(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(EQUAL_PROTO2_IN_QUOTES, context, acceptor);
  }

  private void proposeAndAccept(CompoundElement proposalText, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(proposalText.toString(), context, acceptor);
  }

  private boolean isBoolProposalValid(ContentAssistContext context) {
    Property p = propertyFrom(context);
    return p != null && properties.isBool(p);
  }

  private boolean isNanProposalValid(ContentAssistContext context) {
    Property p = propertyFrom(context);
    return p != null && properties.mayBeNan(p);
  }

  private Property propertyFrom(ContentAssistContext context) {
    EObject model = context.getCurrentModel();
    if (model instanceof Property) return (Property) model;
    if (model instanceof Option) {
      Option option = (Option) model;
      IndexedElement source = options.sourceOf(option);
      if (source instanceof Property) return (Property) source;
    }
    if (model instanceof FieldOption) {
      FieldOption option = (FieldOption) model;
      IndexedElement source = fieldOptions.sourceOf(option);
      if (source instanceof Property) return (Property) source;
    }
    return null;
  }

  private boolean proposeOpeningBracket(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    EObject model = context.getCurrentModel();
    if (!(model instanceof Property)) return false;
    Property p = (Property) model;
    Modifier modifier = p.getModifier();
    if (OPTIONAL.equals(modifier)) {
      CompoundElement display = DEFAULT_EQUAL_IN_BRACKETS;
      int cursorPosition = display.indexOf(CLOSING_BRACKET);
      if (properties.isString(p)) {
        display = DEFAULT_EQUAL_STRING_IN_BRACKETS;
        cursorPosition++;
      }
      createAndAccept(display, cursorPosition, context, acceptor);
    }
    return true;
  }

  private <T> T extractElementFromContext(ContentAssistContext context, Class<T> type) {
    EObject model = context.getCurrentModel();
    // this is most likely a bug in Xtext:
    if (!type.isInstance(model)) model = context.getPreviousModel();
    if (!type.isInstance(model)) return null;
    return type.cast(model);
  }

  private ICompletionProposal createCompletionProposal(CompoundElement proposal, ContentAssistContext context) {
    return createCompletionProposal(proposal.toString(), context);
  }

  @Override public void completeLiteral_Index(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    long index = literals.calculateIndexOf((Literal) model);
    proposeIndex(index, context, acceptor);
  }

  @Override public void completeProperty_Index(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    long index = indexedElements.calculateTagNumberOf((Property) model);
    proposeIndex(index, context, acceptor);
  }

  private void proposeIndex(long index, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(valueOf(index), context, acceptor);
  }

  @Override public void completeProperty_Name(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    String typeName = toFirstLower(properties.typeNameOf((Property) model));
    int index = 1;
    String name = typeName + index;
    for (EObject o : model.eContainer().eContents()) {
      if (o == model || !(o instanceof Property)) continue;
      Property p = (Property) o;
      if (!name.equals(p.getName())) continue;
      name = typeName + (++index);
    }
    proposeAndAccept(name, context, acceptor);
  }

  private void proposeAndAccept(String proposalText, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    acceptor.accept(createCompletionProposal(proposalText, context));
  }

  @Override protected ICompletionProposal createCompletionProposal(String proposalText, ContentAssistContext context) {
    return createCompletionProposal(proposalText, null, defaultImage(), getPriorityHelper().getDefaultPriority(),
        context.getPrefix(), context);
  }

  private Image defaultImage() {
    return imageHelper.getImage(images.defaultImage());
  }

  @Override public void completeNativeFieldOption_Source(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    Property p = extractElementFromContext(context, Property.class);
    if (p != null) {
      proposeNativeOptions(p, context, acceptor);
    }
  }

  private void proposeNativeOptions(Property p, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    List<String> optionNames = existingFieldOptionNames(p);
    proposeDefaultKeyword(p, optionNames, context, acceptor);
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    for (Property option : descriptor.availableOptionsFor(p)) {
      String optionName = option.getName();
      if (optionNames.contains(optionName) || ("packed".equals(optionName) && !canBePacked(p))) continue;
      proposeOption(option, context, acceptor);
    }
  }

  private List<String> existingFieldOptionNames(IndexedElement e) {
    List<FieldOption> allFieldOptions = indexedElements.fieldOptionsOf(e);
    if (allFieldOptions.isEmpty()) return emptyList();
    List<String> optionNames = new ArrayList<String>();
    for (FieldOption option : allFieldOptions)
      optionNames.add(fieldOptions.nameOf(option));
    return optionNames;
  }

  private void proposeDefaultKeyword(IndexedElement e, List<String> existingOptionNames, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (!(e instanceof Property)) return;
    Property property = (Property) e;
    if (!properties.isOptional(property) || existingOptionNames.contains(DEFAULT.toString())) return;
    CompoundElement display = DEFAULT_EQUAL;
    int cursorPosition = display.charCount();
    if (properties.isString(property)) {
      display = DEFAULT_EQUAL_STRING;
      cursorPosition++;
    }
    createAndAccept(display, cursorPosition, context, acceptor);
  }

  private boolean canBePacked(IndexedElement e) {
    if (!(e instanceof Property)) return false;
    Property property = (Property) e;
    return properties.isPrimitive(property) && REPEATED.equals(property.getModifier());
  }

  private void proposeOption(Property option, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    String displayString = option.getName();
    String proposalText = displayString + space() + EQUAL + space();
    Object value = defaultValueOf(option);
    if (value != null) proposalText = proposalText + value;
    ICompletionProposal proposal = createCompletionProposal(proposalText, displayString, imageForOption(), context);
    if (value == EMPTY_STRING && proposal instanceof ConfigurableCompletionProposal) {
      // set cursor between the proposal's quotes
      ConfigurableCompletionProposal configurable = (ConfigurableCompletionProposal) proposal;
      configurable.setCursorPosition(proposalText.length() - 1);
    }
    acceptor.accept(proposal);
  }
  
  private Object defaultValueOf(Property p) {
    if (properties.isBool(p)) return TRUE;
    if (properties.isString(p)) return EMPTY_STRING;
    return null;
  }
  
  @Override public void completeDefaultValueFieldOption_Value(EObject model, Assignment assignment, 
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (!(model instanceof Property)) return;
    Property p = (Property) model;
    if (!properties.isOptional(p)) return;
    proposeDefaultValue(p, context, acceptor);
  }
  
  @Override public ICompletionProposal createCompletionProposal(String proposal, String displayString, Image image,
      ContentAssistContext contentAssistContext) {
    StyledString styled = null;
    if (displayString != null) styled = new StyledString(displayString);
    return createCompletionProposal(proposal, styled, image, getPriorityHelper().getDefaultPriority(), 
        contentAssistContext.getPrefix(), contentAssistContext);
  }

  @Override public void completeNativeFieldOption_Value(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (!(model instanceof NativeFieldOption)) return;
    NativeFieldOption option = (NativeFieldOption) model;
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    Property property = (Property) fieldOptions.sourceOf(option);
    Enum enumType = descriptor.enumTypeOf(property);
    if (enumType != null) {
      proposeAndAccept(enumType, context, acceptor);
      return;
    }
    proposePrimitiveValues(property, context, acceptor);
  }

  private boolean proposePrimitiveValues(Property property, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (properties.isBool(property)) {
      proposeBooleanValues(context, acceptor);
      return true;
    }
    if (properties.isString(property)) {
      proposeEmptyString(context, acceptor);
      return true;
    }
    return false;
  }

  private void proposeBooleanValues(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    CommonKeyword[] keywords = { FALSE, TRUE };
    proposeAndAccept(keywords, context, acceptor);
  }

  private void proposeAndAccept(CommonKeyword[] keywords, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    for (CommonKeyword keyword : keywords)
      proposeAndAccept(keyword.toString(), context, acceptor);
  }

  private void proposeEmptyString(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    createAndAccept(EMPTY_STRING, 1, context, acceptor);
  }

  private void createAndAccept(CompoundElement display, int cursorPosition, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    ICompletionProposal proposal = createCompletionProposal(display, context);
    if (proposal instanceof ConfigurableCompletionProposal) {
      ConfigurableCompletionProposal configurable = (ConfigurableCompletionProposal) proposal;
      configurable.setCursorPosition(cursorPosition);
    }
    acceptor.accept(proposal);
  }

  @Override public void completeCustomOption_Source(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (!(model instanceof CustomOption)) return;
    CustomOption option = (CustomOption) model;
    Collection<IEObjectDescription> scope = scoping().findScope(option);
    proposeAndAcceptOptions(scope, context, acceptor);
  }

  @Override public void completeCustomFieldOption_Source(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (!(model instanceof CustomFieldOption)) return;
    CustomFieldOption option = (CustomFieldOption) model;
    Collection<IEObjectDescription> scope = scoping().findScope(option);
    proposeAndAcceptOptions(scope, context, acceptor);
  }

  private void proposeAndAcceptOptions(Collection<IEObjectDescription> scope, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    Image image = imageForOption();
    for (IEObjectDescription d : descriptionChooser.shortestQualifiedNamesIn(scope)) {
      proposeAndAccept(d, image, context, acceptor);
    }
  }

  private Image imageForOption() {
    return imageHelper.getImage(images.imageFor(Option.class));
  }

  @Override public void completeCustomOption_OptionFields(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {}

  @Override public void completeCustomFieldOption_OptionFields(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {}

  @Override public void completeOptionSource_OptionField(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {}

  @Override public void completeOptionMessageFieldSource_OptionMessageField(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {}

  @Override public void completeOptionExtendMessageFieldSource_OptionExtendMessageField(EObject model,
      Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {}

  @Override public void complete_OptionMessageFieldSource(EObject model, RuleCall ruleCall,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    EObject e = findActualSemanticObjectFor(context.getCurrentNode());
    if (e instanceof Protobuf) e = model;
    Collection<IEObjectDescription> scope = emptySet();
    if (e instanceof CustomOption) {
      CustomOption option = (CustomOption) e;
      scope = scoping().findMessageFieldScope(option);
    }
    if (e instanceof CustomFieldOption) {
      CustomFieldOption option = (CustomFieldOption) e;
      scope = scoping().findMessageFieldScope(option);
    }
    if (e instanceof OptionMessageFieldSource) {
      OptionMessageFieldSource source = (OptionMessageFieldSource) e;
      scope = scoping().findScope(source);
    }
    for (IEObjectDescription d : descriptionChooser.shortestQualifiedNamesIn(scope)) {
      Image image = imageHelper.getImage(images.imageFor(d.getEObjectOrProxy()));
      proposeAndAccept(d, image, context, acceptor);
    }
  }

  @Override public void complete_OptionExtendMessageFieldSource(EObject model, RuleCall ruleCall,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    EObject e = findActualSemanticObjectFor(context.getCurrentNode());
    if (e instanceof Protobuf) e = model;
    Collection<IEObjectDescription> scope = emptySet();
    if (e instanceof CustomOption) {
      CustomOption option = (CustomOption) e;
      scope = scoping().findExtendMessageFieldScope(option);
    }
    if (e instanceof CustomFieldOption) {
      CustomFieldOption option = (CustomFieldOption) e;
      scope = scoping().findExtendMessageFieldScope(option);
    }
    if (e instanceof OptionExtendMessageFieldSource) {
      OptionExtendMessageFieldSource source = (OptionExtendMessageFieldSource) e;
      scope = scoping().findScope(source);
    }
    for (IEObjectDescription d : descriptionChooser.shortestQualifiedNamesIn(scope)) {
      Image image = imageHelper.getImage(images.imageFor(d.getEObjectOrProxy()));
      proposeAndAccept(d, image, context, acceptor);
    }
  }

  private void proposeAndAccept(IEObjectDescription d, Image image, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    QualifiedName name = d.getName();
    String display = name.getLastSegment()  + " - " + name.toString();
    ICompletionProposal proposal = createCompletionProposal(name.toString(), display, image, context);
    acceptor.accept(proposal);
  }

  @Override public void completeCustomOption_Value(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (!(model instanceof CustomOption)) return;
    CustomOption option = (CustomOption) model;
    IndexedElement e = options.lastFieldSourceFrom(option);
    if (e == null) e = options.sourceOf(option);
    if (e instanceof Property) {
      proposeDefaultValue((Property) e, context, acceptor);
    }
  }

  @Override public void completeCustomFieldOption_Value(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    // TODO content assist returns "{"
    if (!(model instanceof CustomFieldOption)) return;
    CustomFieldOption option = (CustomFieldOption) model;
    IndexedElement e = fieldOptions.lastFieldSourceFrom(option);
    if (e == null) e = fieldOptions.sourceOf(option);
    if (e instanceof Property) {
      proposeDefaultValue((Property) e, context, acceptor);
    }
  }

  private void proposeDefaultValue(Property property, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (property == null) return;
    if (proposePrimitiveValues(property, context, acceptor)) return;
    Enum enumType = finder.enumTypeOf(property);
    if (enumType != null) {
      proposeAndAccept(enumType, context, acceptor);
    }
  }

  private void proposeAndAccept(Enum enumType, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    Image image = imageHelper.getImage(images.imageFor(Literal.class));
    for (Literal literal : getAllContentsOfType(enumType, Literal.class))
      proposeAndAccept(literal.getName(), image, context, acceptor);
  }

  private void proposeAndAccept(String proposalText, Image image, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    ICompletionProposal proposal = createCompletionProposal(proposalText, proposalText, image, context);
    acceptor.accept(proposal);
  }

  private Scoping scoping() {
    return (ProtobufScopeProvider) super.getScopeProvider();
  }
}
