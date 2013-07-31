/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.contentassist;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.CLOSING_BRACKET;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.DEFAULT;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.EQUAL;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.FALSE;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.NAN;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.OPENING_BRACKET;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.OPENING_CURLY_BRACKET;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.SYNTAX;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.TRUE;
import static com.google.eclipse.protobuf.protobuf.Modifier.OPTIONAL;
import static com.google.eclipse.protobuf.protobuf.Modifier.REPEATED;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.LITERAL;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.OPTION;
import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.DEFAULT_EQUAL;
import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.DEFAULT_EQUAL_IN_BRACKETS;
import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.DEFAULT_EQUAL_STRING;
import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.DEFAULT_EQUAL_STRING_IN_BRACKETS;
import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.EMPTY_STRING;
import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.EQUAL_PROTO2_IN_QUOTES;
import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.PROTO2_IN_QUOTES;
import static com.google.eclipse.protobuf.util.CommonWords.space;
import static java.lang.String.valueOf;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;
import static org.eclipse.xtext.util.Strings.isEmpty;
import static org.eclipse.xtext.util.Strings.toFirstLower;

import com.google.eclipse.protobuf.grammar.CommonKeyword;
import com.google.eclipse.protobuf.model.util.IndexedElements;
import com.google.eclipse.protobuf.model.util.Literals;
import com.google.eclipse.protobuf.model.util.MessageFields;
import com.google.eclipse.protobuf.model.util.Options;
import com.google.eclipse.protobuf.protobuf.AbstractCustomOption;
import com.google.eclipse.protobuf.protobuf.AbstractOption;
import com.google.eclipse.protobuf.protobuf.ComplexValue;
import com.google.eclipse.protobuf.protobuf.CustomFieldOption;
import com.google.eclipse.protobuf.protobuf.CustomOption;
import com.google.eclipse.protobuf.protobuf.DefaultValueFieldOption;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.FieldName;
import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.Modifier;
import com.google.eclipse.protobuf.protobuf.NativeFieldOption;
import com.google.eclipse.protobuf.protobuf.NativeOption;
import com.google.eclipse.protobuf.protobuf.Option;
import com.google.eclipse.protobuf.protobuf.Rpc;
import com.google.eclipse.protobuf.protobuf.SimpleValueField;
import com.google.eclipse.protobuf.protobuf.Stream;
import com.google.eclipse.protobuf.protobuf.TypeExtension;
import com.google.eclipse.protobuf.scoping.ProtoDescriptor;
import com.google.eclipse.protobuf.scoping.ProtoDescriptorProvider;
import com.google.eclipse.protobuf.scoping.ProtobufScopeProvider;
import com.google.eclipse.protobuf.scoping.ScopeProvider;
import com.google.eclipse.protobuf.ui.grammar.CompoundElement;
import com.google.eclipse.protobuf.ui.labeling.Images;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.ui.PluginImageHelper;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

import java.util.Collection;
import java.util.List;


/**
 * @author alruiz@google.com (Alex Ruiz)
 *
 * @see <a href="http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist">Xtext Content Assist</a>
 */
public class ProtobufProposalProvider extends AbstractProtobufProposalProvider {
  @Inject private IEObjectDescriptionChooser descriptionChooser;
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private Images images;
  @Inject private IndexedElements indexedElements;
  @Inject private PluginImageHelper imageHelper;
  @Inject private Literals literals;
  @Inject private MessageFields messageFields;
  @Inject private Options options;

  @Override public void completeProtobuf_Syntax(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {}

  @Override public void completeSyntax_Name(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(PROTO2_IN_QUOTES, context, acceptor);
  }

  @Override public void complete_Syntax(EObject model, RuleCall ruleCall, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    String proposal = SYNTAX + space() + EQUAL_PROTO2_IN_QUOTES;
    proposeAndAccept(proposal, imageHelper.getImage(images.imageFor(SYNTAX)), context, acceptor);
  }

  @Override public void completeComplexTypeLink_Target(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (model instanceof MessageField) {
      Collection<IEObjectDescription> scope = scopeProvider().potentialComplexTypesFor((MessageField) model);
      for (IEObjectDescription d : descriptionChooser.shortestQualifiedNamesIn(scope)) {
        Image image = imageHelper.getImage(images.imageFor(d.getEObjectOrProxy()));
        proposeAndAccept(d, image, context, acceptor);
      }
    }
  }

  @Override public void completeExtensibleTypeLink_Target(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    Collection<IEObjectDescription> scope = emptySet();
    if (model instanceof TypeExtension) {
      TypeExtension typeExtension = (TypeExtension) model;
      scope = scopeProvider().potentialExtensibleTypesFor(typeExtension);
    }
    for (IEObjectDescription d : descriptionChooser.shortestQualifiedNamesIn(scope)) {
      Image image = imageHelper.getImage(images.imageFor(d.getEObjectOrProxy()));
      proposeAndAccept(d, image, context, acceptor);
    }
  }

  @Override public void completeMessageLink_Target(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    Collection<IEObjectDescription> scope = emptySet();
    if (model instanceof Rpc) {
      Rpc rpc = (Rpc) model;
      scope = scopeProvider().potentialMessagesFor(rpc);
    }
    if (model instanceof Stream) {
      Stream stream = (Stream) model;
      scope = scopeProvider().potentialMessagesFor(stream);
    }
    for (IEObjectDescription d : descriptionChooser.shortestQualifiedNamesIn(scope)) {
      Image image = imageHelper.getImage(images.imageFor(d.getEObjectOrProxy()));
      proposeAndAccept(d, image, context, acceptor);
    }
  }

  @Override public void completeNativeOption_Source(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    Collection<MessageField> optionProperties = descriptor.availableOptionsFor(model);
    if (!optionProperties.isEmpty()) {
      proposeOptions(optionProperties, context, acceptor);
    }
  }

  private void proposeOptions(Collection<MessageField> optionSources, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    for (MessageField source : optionSources) {
      proposeOption(source, context, acceptor);
    }
  }

  @Override public void completeNativeOption_Value(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    NativeOption option = (NativeOption) model;
    MessageField optionSource = (MessageField) options.rootSourceOf(option);
    if (optionSource == null) {
      return;
    }
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    Enum enumType = descriptor.enumTypeOf(optionSource);
    if (enumType != null) {
      proposeAndAccept(enumType, context, acceptor);
      return;
    }
    proposePrimitiveValues(optionSource, context, acceptor);
  }

  @Override public void complete_ID(EObject model, RuleCall ruleCall, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {}

  @Override public void complete_STRING(EObject model, RuleCall ruleCall, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {}

  @Override public void completeKeyword(Keyword keyword, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (keyword == null) {
      return;
    }
    boolean proposalWasHandledAlready = completeKeyword(keyword.getValue(), context, acceptor);
    if (proposalWasHandledAlready) {
      return;
    }
    super.completeKeyword(keyword, context, acceptor);
  }

  private boolean completeKeyword(String keyword, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (isLastWordFromCaretPositionEqualTo(keyword, context)) {
      return true;
    }
    if (EQUAL.hasValue(keyword)) {
      EObject grammarElement = context.getLastCompleteNode().getGrammarElement();
      if (isKeyword(grammarElement, SYNTAX)) {
        proposeEqualProto2(context, acceptor);
      }
      return true;
    }
    if (OPENING_BRACKET.hasValue(keyword)) {
      return proposeOpeningBracket(context, acceptor);
    }
    if (OPENING_CURLY_BRACKET.hasValue(keyword)) {
      EObject model = context.getCurrentModel();
      return model instanceof Option || model instanceof ComplexValue;
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
    if (start < 0) {
      return false;
    }
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
    MessageField field = fieldFrom(context);
    return field != null && messageFields.isBool(field);
  }

  private boolean isNanProposalValid(ContentAssistContext context) {
    MessageField field = fieldFrom(context);
    return field != null && messageFields.isFloatingPointNumber(field);
  }

  private MessageField fieldFrom(ContentAssistContext context) {
    EObject model = context.getCurrentModel();
    if (model instanceof MessageField) {
      return (MessageField) model;
    }
    if (model instanceof AbstractOption) {
      AbstractOption option = (AbstractOption) model;
      IndexedElement source = options.rootSourceOf(option);
      if (source instanceof MessageField) {
        return (MessageField) source;
      }
    }
    return null;
  }

  private boolean proposeOpeningBracket(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    EObject model = context.getCurrentModel();
    if (model instanceof ComplexValue) {
      return true;
    }
    if (model instanceof MessageField) {
      MessageField field = (MessageField) model;
      Modifier modifier = field.getModifier();
      if (OPTIONAL.equals(modifier)) {
        CompoundElement display = DEFAULT_EQUAL_IN_BRACKETS;
        int cursorPosition = display.indexOf(CLOSING_BRACKET);
        if (messageFields.isString(field)) {
          display = DEFAULT_EQUAL_STRING_IN_BRACKETS;
          cursorPosition++;
        }
        createAndAccept(display, cursorPosition, context, acceptor);
      }
      return true;
    }
    return false;
  }

  private <T> T extractElementFromContext(ContentAssistContext context, Class<T> type) {
    EObject model = context.getCurrentModel();
    // this is most likely a bug in Xtext:
    if (!type.isInstance(model)) {
      model = context.getPreviousModel();
    }
    if (!type.isInstance(model)) {
      return null;
    }
    return type.cast(model);
  }

  private ICompletionProposal createCompletionProposal(CompoundElement proposal, ContentAssistContext context) {
    return createCompletionProposal(proposal.toString(), context);
  }

  @Override public void completeLiteral_Index(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    long index = literals.calculateNewIndexOf((Literal) model);
    proposeIndex(index, context, acceptor);
  }

  @Override public void completeLiteralLink_Target(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    MessageField field = null;
    if (model instanceof DefaultValueFieldOption) {
      field = (MessageField) model.eContainer();
    }
    if (field == null || !messageFields.isOptional(field)) {
      return;
    }
    Enum enumType = messageFields.enumTypeOf(field);
    if (enumType != null) {
      proposeAndAccept(enumType, context, acceptor);
    }
  }

  @Override public void completeMessageField_Index(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    long index = indexedElements.calculateNewIndexFor((MessageField) model);
    proposeIndex(index, context, acceptor);
  }

  private void proposeIndex(long index, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(valueOf(index), context, acceptor);
  }

  @Override public void completeMessageField_Name(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    String typeName = toFirstLower(messageFields.typeNameOf((MessageField) model));
    int index = 1;
    String name = typeName + index;
    for (EObject o : model.eContainer().eContents()) {
      if (o == model || !(o instanceof MessageField)) {
        continue;
      }
      MessageField field = (MessageField) o;
      if (!name.equals(field.getName())) {
        continue;
      }
      name = typeName + (++index);
    }
    proposeAndAccept(name, context, acceptor);
  }

  private void proposeAndAccept(String proposalText, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
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
    MessageField field = extractElementFromContext(context, MessageField.class);
    if (field != null) {
      proposeNativeOptions(field, context, acceptor);
    }
  }

  private void proposeNativeOptions(MessageField field, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    List<String> optionNames = existingFieldOptionNames(field);
    proposeDefaultKeyword(field, optionNames, context, acceptor);
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    for (MessageField optionSource : descriptor.availableOptionsFor(field)) {
      String optionName = optionSource.getName();
      if (optionNames.contains(optionName) || ("packed".equals(optionName) && !canBePacked(field))) {
        continue;
      }
      proposeOption(optionSource, context, acceptor);
    }
  }

  private List<String> existingFieldOptionNames(IndexedElement e) {
    List<FieldOption> allFieldOptions = indexedElements.fieldOptionsOf(e);
    if (allFieldOptions.isEmpty()) {
      return emptyList();
    }
    List<String> optionNames = newArrayList();
    for (FieldOption option : allFieldOptions) {
      optionNames.add(options.nameOf(option));
    }
    return optionNames;
  }

  private void proposeDefaultKeyword(IndexedElement e, List<String> existingOptionNames, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (e instanceof MessageField) {
      MessageField field = (MessageField) e;
      if (!messageFields.isOptional(field) || existingOptionNames.contains(DEFAULT.toString())) {
        return;
      }
      CompoundElement display = DEFAULT_EQUAL;
      int cursorPosition = display.charCount();
      if (messageFields.isString(field)) {
        display = DEFAULT_EQUAL_STRING;
        cursorPosition++;
      }
      createAndAccept(display, cursorPosition, context, acceptor);
    }
  }

  private boolean canBePacked(IndexedElement e) {
    if (e instanceof MessageField) {
      MessageField field = (MessageField) e;
      return messageFields.isPrimitive(field) && REPEATED.equals(field.getModifier());
    }
    return false;
  }

  private void proposeOption(MessageField optionSource, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    String displayString = optionSource.getName();
    String proposalText = displayString + space() + EQUAL + space();
    Object value = defaultValueOf(optionSource);
    if (value != null) {
      proposalText = proposalText + value;
    }
    ICompletionProposal proposal = createCompletionProposal(proposalText, displayString, imageForOption(), context);
    if (value == EMPTY_STRING && proposal instanceof ConfigurableCompletionProposal) {
      // set cursor between the proposal's quotes
      ConfigurableCompletionProposal configurable = (ConfigurableCompletionProposal) proposal;
      configurable.setCursorPosition(proposalText.length() - 1);
    }
    acceptor.accept(proposal);
  }

  private Object defaultValueOf(MessageField field) {
    if (messageFields.isBool(field)) {
      return TRUE;
    }
    if (messageFields.isString(field)) {
      return EMPTY_STRING;
    }
    return null;
  }

  @Override public void completeDefaultValueFieldOption_Value(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    MessageField field = null;
    if (model instanceof DefaultValueFieldOption) {
      field = (MessageField) model.eContainer();
    }
    if (model instanceof MessageField) {
      field = (MessageField) model;
    }
    if (field == null || !messageFields.isOptional(field)) {
      return;
    }
    proposeFieldValue(field, context, acceptor);
  }

  @Override public void completeNativeFieldOption_Value(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (model instanceof NativeFieldOption) {
      NativeFieldOption option = (NativeFieldOption) model;
      ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
      MessageField field = (MessageField) options.rootSourceOf(option);
      Enum enumType = descriptor.enumTypeOf(field);
      if (enumType != null) {
        proposeAndAccept(enumType, context, acceptor);
        return;
      }
      proposePrimitiveValues(field, context, acceptor);
    }
  }

  private boolean proposePrimitiveValues(MessageField field, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (messageFields.isBool(field)) {
      proposeBooleanValues(context, acceptor);
      return true;
    }
    if (messageFields.isString(field)) {
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
    for (CommonKeyword keyword : keywords) {
      proposeAndAccept(keyword.toString(), context, acceptor);
    }
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
    completeAbstractCustomOptionSource(model, context, acceptor);
  }

  @Override public void completeCustomFieldOption_Source(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    completeAbstractCustomOptionSource(model, context, acceptor);
  }

  private void completeAbstractCustomOptionSource(EObject model, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (model instanceof AbstractCustomOption) {
      AbstractCustomOption option = (AbstractCustomOption) model;
      Collection<IEObjectDescription> scope = scopeProvider().potentialSourcesFor(option);
      proposeAndAcceptOptions(scope, context, acceptor);
    }
  }

  private void proposeAndAcceptOptions(Collection<IEObjectDescription> scope, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    Image image = imageForOption();
    for (IEObjectDescription d : descriptionChooser.shortestQualifiedNamesIn(scope)) {
      proposeAndAccept(d, image, context, acceptor);
    }
  }

  private Image imageForOption() {
    return imageHelper.getImage(images.imageFor(OPTION));
  }

  private void proposeAndAccept(IEObjectDescription d, Image image, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(d, null, null, image, context, acceptor);
  }

  @Override public void completeCustomOption_Fields(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (model instanceof CustomOption) {
      CustomOption option = (CustomOption) model;
      proposeAndAccept(scopeProvider().potentialMessageFieldsFor(option), context, acceptor);
      proposeAndAccept(scopeProvider().potentialExtensionFieldsFor(option), "(%s)", "(%s)", context, acceptor);
    }
  }

  @Override public void completeCustomFieldOption_Fields(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (model instanceof CustomFieldOption) {
      CustomFieldOption option = (CustomFieldOption) model;
      proposeAndAccept(scopeProvider().potentialMessageFieldsFor(option), context, acceptor);
      proposeExtensionFields(scopeProvider().potentialExtensionFieldsFor(option), context, acceptor);
    }
  }

  private void proposeExtensionFields(Collection<IEObjectDescription> scope, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    String format = "(%s)";
    proposeAndAccept(scope, format, format, context, acceptor);
  }

  private void proposeAndAccept(Collection<IEObjectDescription> scope, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(scope, null, null, context, acceptor);
  }

  @Override public void completeOptionSource_Target(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
  }

  @Override public void completeMessageOptionField_Target(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
  }

  @Override public void completeExtensionOptionField_Target(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
  }

  @Override public void complete_MessageOptionField(EObject model, RuleCall ruleCall,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {}

  @Override public void complete_ExtensionOptionField(EObject model, RuleCall ruleCall,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {}

  @Override public void completeCustomOption_Value(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (model instanceof CustomOption) {
      CustomOption option = (CustomOption) model;
      IndexedElement e = options.sourceOfLastFieldIn(option);
      if (e == null) {
        e = options.rootSourceOf(option);
      }
      if (e instanceof MessageField) {
        proposeFieldValue((MessageField) e, context, acceptor);
      }
    }
  }

  @Override public void completeCustomFieldOption_Value(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    // TODO content assist returns "{"
    if (model instanceof CustomFieldOption) {
      // TODO check if this is the same as sourceOf
      CustomFieldOption option = (CustomFieldOption) model;
      IndexedElement e = options.sourceOfLastFieldIn(option);
      if (e == null) {
        e = options.rootSourceOf(option);
      }
      if (e instanceof MessageField) {
        proposeFieldValue((MessageField) e, context, acceptor);
      }
    }
  }

  private void proposeFieldValue(MessageField field, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (field == null || proposePrimitiveValues(field, context, acceptor)) {
      return;
    }
    Enum enumType = messageFields.enumTypeOf(field);
    if (enumType != null) {
      proposeAndAccept(enumType, context, acceptor);
    }
  }

  private void proposeAndAccept(Enum enumType, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    Image image = imageHelper.getImage(images.imageFor(LITERAL));
    for (Literal literal : getAllContentsOfType(enumType, Literal.class)) {
      proposeAndAccept(literal.getName(), image, context, acceptor);
    }
  }

  @Override public void completeNormalFieldName_Target(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
  }

  @Override public void completeExtensionFieldName_Target(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
  }

  @Override public void completeSimpleValueField_Value(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (model instanceof SimpleValueField) {
      SimpleValueField field = (SimpleValueField) model;
      FieldName name = field.getName();
      if (name != null) {
        IndexedElement target = name.getTarget();
        if (target instanceof MessageField) {
          proposeFieldValue((MessageField) target, context, acceptor);
        }
      }
    }
  }

  @Override public void completeSimpleValueField_Name(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (model instanceof ComplexValue) {
      ComplexValue value = (ComplexValue) model;
      proposeAndAccept(scopeProvider().potentialNormalFieldNames(value), "%s:", null, context, acceptor);
      proposeAndAccept(scopeProvider().potentialExtensionFieldNames(value), "[%s]:", "[%s]", context, acceptor);
    }
  }

  private void proposeAndAccept(Collection<IEObjectDescription> scope, String proposalFormat, String displayFormat,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    for (IEObjectDescription d : descriptionChooser.shortestQualifiedNamesIn(scope)) {
      Image image = imageHelper.getImage(images.imageFor(d.getEObjectOrProxy()));
      proposeAndAccept(d, proposalFormat, displayFormat, image, context, acceptor);
    }
  }

  private void proposeAndAccept(IEObjectDescription d, String proposalFormat, String displayFormat, Image image,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    QualifiedName name = d.getName();
    String proposalText = name.toString();
    if (!isEmpty(proposalFormat)) {
      proposalText = String.format(proposalFormat, proposalText);
    }
    String lastSegment = name.getLastSegment();
    if (!isEmpty(displayFormat)) {
      lastSegment = String.format(displayFormat, lastSegment);
    }
    String display = String.format("%s - %s", lastSegment, name.toString());
    ICompletionProposal proposal = createCompletionProposal(proposalText, display, image, context);
    acceptor.accept(proposal);
  }

  private void proposeAndAccept(String proposalText, Image image, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    ICompletionProposal proposal = createCompletionProposal(proposalText, proposalText, image, context);
    acceptor.accept(proposal);
  }

  private ScopeProvider scopeProvider() {
    return (ProtobufScopeProvider) super.getScopeProvider();
  }

  @Override public ICompletionProposal createCompletionProposal(String proposal, String displayString, Image image,
      ContentAssistContext contentAssistContext) {
    StyledString styled = null;
    if (displayString != null) {
      styled = new StyledString(displayString);
    }
    int priority = getPriorityHelper().getDefaultPriority();
    String prefix = contentAssistContext.getPrefix();
    return createCompletionProposal(proposal, styled, image, priority, prefix, contentAssistContext);
  }
}
