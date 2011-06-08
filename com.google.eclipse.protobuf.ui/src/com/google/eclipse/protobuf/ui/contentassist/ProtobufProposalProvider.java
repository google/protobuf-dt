/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.contentassist;

import static com.google.eclipse.protobuf.protobuf.Modifier.*;
import static com.google.eclipse.protobuf.protobuf.ScalarType.STRING;
import static com.google.eclipse.protobuf.ui.grammar.CommonKeyword.*;
import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.*;
import static com.google.eclipse.protobuf.ui.util.Strings.firstCharToLowerCase;
import static java.lang.String.valueOf;
import static java.util.Collections.emptyList;

import java.util.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.*;
import org.eclipse.xtext.ui.PluginImageHelper;
import org.eclipse.xtext.ui.editor.contentassist.*;

import com.google.common.collect.ImmutableList;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.scoping.*;
import com.google.eclipse.protobuf.ui.grammar.*;
import com.google.eclipse.protobuf.ui.grammar.CompoundElement;
import com.google.eclipse.protobuf.ui.labeling.Images;
import com.google.eclipse.protobuf.ui.util.*;
import com.google.eclipse.protobuf.ui.util.Properties;
import com.google.eclipse.protobuf.util.ProtobufElementFinder;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 *
 * @see <a href="http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist">Xtext Content Assist</a>
 */
public class ProtobufProposalProvider extends AbstractProtobufProposalProvider {

  private static final String SPACE = " ";

  @Inject private ProtobufElementFinder finder;
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private PluginImageHelper imageHelper;

  @Inject private Fields fields;
  @Inject private Images images;
  @Inject private Literals literals;
  @Inject private Properties properties;

  /** {@inheritDoc} */
  @Override public void completeProtobuf_Syntax(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
  }

  @Override public void completeSyntax_Name(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(PROTO2_IN_QUOTES, context, acceptor);
  }

  @Override public void complete_Syntax(EObject model, RuleCall ruleCall, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    String proposal = SYNTAX + SPACE + EQUAL_PROTO2_IN_QUOTES;
    proposeAndAccept(proposal, imageHelper.getImage(images.imageFor(Syntax.class)), context, acceptor);
  }

  @Override public void completeOption_Name(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (proposeOptions(model, context, acceptor)) return;
    if (model instanceof Option) {
      EObject container = model.eContainer();
      proposeOptions(container, context, acceptor);
    }
  }

  private boolean proposeOptions(EObject model, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (model instanceof Protobuf) {
      proposeCommonFileOptions(context, acceptor);
      return true;
    }
    if (model instanceof Message) {
      proposeCommonMessageOptions(context, acceptor);
      return true;
    }
    return false;
  }

  private void proposeCommonFileOptions(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    for (Property option : descriptorProvider.get().fileOptions())
      proposeOption(option, context, acceptor);
  }

  private void proposeCommonMessageOptions(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    for (Property option : descriptorProvider.get().messageOptions())
      proposeOption(option, context, acceptor);
  }

  @Override public void completeOption_Value(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    Option option = (Option) model;
    ProtoDescriptor descriptor = descriptorProvider.get();
    Enum enumType = descriptor.enumTypeOf(option);
    if (enumType != null) {
      proposeAndAccept(enumType, context, acceptor);
      return;
    }
    Property fileOption = descriptor.lookupOption(option.getName());
    if (fileOption == null) return;
    if (properties.isString(fileOption)) {
      proposeEmptyString(context, acceptor);
      return;
    }
    if (properties.isBool(fileOption)) {
      proposeBooleanValues(context, acceptor);
      return;
    }
  }

  @Override public void complete_ID(EObject model, RuleCall ruleCall, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {}

  @Override public void complete_STRING(EObject model, RuleCall ruleCall, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (model instanceof Property && isProposalForDefaultValue(context)) {
      Property p = (Property) model;
      if (!isStringProperty(p)) return;
      proposeEmptyString(context, acceptor);
      return;
    }
    if (model instanceof Option || model instanceof FieldOption || model instanceof Syntax) return;
    for (AbstractElement element : context.getFirstSetGrammarElements()) {
      if (!(element instanceof Assignment)) continue;
      Assignment assignment = (Assignment) element;
      if (EQUAL.hasValue(assignment.getOperator()) && assignment.getFeature().equals("name")) return;
    }
    super.complete_STRING(model, ruleCall, context, acceptor);
  }

  private void proposeEmptyString(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    createAndAccept(EMPTY_STRING, 1, context, acceptor);
  }

  private boolean isProposalForDefaultValue(ContentAssistContext context) {
    return isProposalForAssignment(DEFAULT, context);
  }

  private boolean isProposalForAssignment(CommonKeyword feature, ContentAssistContext context) {
    ImmutableList<AbstractElement> grammarElements = context.getFirstSetGrammarElements();
    for (AbstractElement e : grammarElements) {
      if (!(e instanceof Assignment)) continue;
      Assignment a = (Assignment) e;
      if (feature.hasValue(a.getFeature()) && EQUAL.hasValue(a.getOperator())) return true;
    }
    return false;
  }

  @Override public void completeKeyword(Keyword keyword, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (keyword == null) return;
    boolean proposalWasHandledAlready = completeKeyword(keyword.getValue(), context, acceptor);
    if (proposalWasHandledAlready) return;
    super.completeKeyword(keyword, context, acceptor);
  }

  private boolean completeKeyword(String keyword, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (isLastWordFromCaretPositionEqualTo(keyword, context)) return true;
    if (DEFAULT.hasValue(keyword)) {
      proposeDefaultValue(context, acceptor);
      return true;
    }
    if (EQUAL.hasValue(keyword)) {
      EObject grammarElement = context.getLastCompleteNode().getGrammarElement();
      if (isKeyword(grammarElement, SYNTAX)) {
        proposeEqualProto2(context, acceptor);
        return true;
      }
    }
    if (OPENING_BRACKET.hasValue(keyword)) {
      return proposeOpenBracket(context, acceptor);
    }
    if (TRUE.hasValue(keyword) || FALSE.hasValue(keyword)) {
      if (!isBoolProposalValid(context)) return true;
    }
    // remove keyword proposals when current node is "]". At this position we only accept "default" or field options.
    return context.getCurrentNode().getText().equals(CLOSING_BRACKET.toString());
  }

  private void proposeEqualProto2(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(EQUAL_PROTO2_IN_QUOTES, context, acceptor);
  }

  private boolean isKeyword(EObject object, CommonKeyword keyword) {
    return object instanceof Keyword && keyword.hasValue(((Keyword)object).getValue());
  }

  private boolean isBoolProposalValid(ContentAssistContext context) {
    EObject model = context.getCurrentModel();
    if (model instanceof Property) return properties.isBool((Property) model);
    if (model instanceof Option) {
      Property fileOption = descriptorProvider.get().lookupOption(((Option) model).getName());
      return fileOption != null && properties.isBool(fileOption);
    }
    if (model instanceof FieldOption) {
      Property fileOption = descriptorProvider.get().lookupOption(((FieldOption) model).getName());
      return fileOption != null && properties.isBool(fileOption);
    }
    return false;
  }

  private boolean proposeOpenBracket(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    EObject model = context.getCurrentModel();
    if (!(model instanceof Property)) return false;
    Property p = (Property) model;
    Modifier modifier = p.getModifier();
    if (OPTIONAL.equals(modifier)) {
      CompoundElement display = DEFAULT_EQUAL_IN_BRACKETS;
      int cursorPosition = display.indexOf(CLOSING_BRACKET);
      if (isStringProperty(p)) {
        display = DEFAULT_EQUAL_STRING_IN_BRACKETS;
        cursorPosition++;
      }
      createAndAccept(display, cursorPosition, context, acceptor);
    }
    return true;
  }

  private void proposeAndAccept(CompoundElement proposalText, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(proposalText.toString(), context, acceptor);
  }

  private void proposeDefaultValue(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    Modifier modifier = extractModifierFromModel(context);
    if (!OPTIONAL.equals(modifier)) return;
    CompoundElement display = DEFAULT_EQUAL;
    int cursorPosition = display.charCount();
    Property property = extractPropertyFrom(context);
    if (isStringProperty(property)) {
      display = DEFAULT_EQUAL_STRING;
      cursorPosition++;
    }
    createAndAccept(display, cursorPosition, context, acceptor);
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

  private Modifier extractModifierFromModel(ContentAssistContext context) {
    Property p = extractPropertyFrom(context);
    return (p == null) ? null : p.getModifier();
  }

  private Property extractPropertyFrom(ContentAssistContext context) {
    return extractElementFromContext(context, Property.class);
  }

  private <T> T extractElementFromContext(ContentAssistContext context, Class<T> type) {
    EObject model = context.getCurrentModel();
    // this is most likely a bug in Xtext:
    if (!type.isInstance(model)) model = context.getPreviousModel();
    if (!type.isInstance(model)) return null;
    return type.cast(model);
  }

  private boolean isStringProperty(Property p) {
    return STRING.equals(finder.scalarTypeOf(p));
  }

  private ICompletionProposal createCompletionProposal(CompoundElement proposal, ContentAssistContext context) {
    return createCompletionProposal(proposal.toString(), context);
  }

  @Override public void completeLiteral_Index(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    int index = literals.calculateIndexOf((Literal) model);
    proposeIndex(index, context, acceptor);
  }

  @Override public void completeProperty_Default(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    Enum enumType = finder.enumTypeOf((Property) model);
    if (enumType == null) return;
    proposeAndAccept(enumType, context, acceptor);
  }

  @Override public void completeProperty_Index(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    int index = fields.calculateTagNumberOf((Property) model);
    proposeIndex(index, context, acceptor);
  }

  private void proposeIndex(int index, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(valueOf(index), context, acceptor);
  }

  @Override public void completeProperty_Name(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    String typeName = firstCharToLowerCase(properties.typeNameOf((Property) model));
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

  private ICompletionProposal createCompletionProposal(String proposal, String displayString,
      ContentAssistContext context) {
    return createCompletionProposal(proposal, displayString, defaultImage(), context);
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

  @Override public void complete_FieldOption(EObject model, RuleCall ruleCall, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    System.out.println("complete_FieldOption");
  }

  @Override public void completeFieldOption_Name(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    Field field = extractFieldFrom(context);
    proposeCommonFieldOptions(field, context, acceptor);
  }

  private Field extractFieldFrom(ContentAssistContext context) {
    return extractElementFromContext(context, Field.class);
  }

  private void proposeCommonFieldOptions(Field field, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    List<String> options = existingFieldOptionNames(field);
    for (Property option : descriptorProvider.get().fieldOptions()) {
      String optionName = option.getName();
      if (options.contains(optionName) || ("packed".equals(optionName) && !canBePacked(field))) continue;
      proposeOption(option, context, acceptor);
    }
  }

  private List<String> existingFieldOptionNames(Field field) {
    List<FieldOption> options = field.getFieldOptions();
    if (options.isEmpty()) return emptyList();
    List<String> optionNames = new ArrayList<String>();
    for (FieldOption option : options) optionNames.add(option.getName());
    return optionNames;
  }

  private boolean canBePacked(Field field) {
    if (!(field instanceof Property)) return false;
    Property property = (Property) field;
    return properties.isPrimitive(property) && REPEATED.equals(property.getModifier());
  }

  private void proposeOption(Property option, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    String displayString = option.getName();
    String proposalText = displayString + SPACE + EQUAL + SPACE;
    boolean isStringOption = properties.isString(option);
    if (isStringOption) {
      proposalText = proposalText + EMPTY_STRING;
    } else if (properties.isBool(option)) {
      proposalText = proposalText + TRUE;
    }
    ICompletionProposal proposal = createCompletionProposal(proposalText, displayString, context);
    if (isStringOption && proposal instanceof ConfigurableCompletionProposal) {
      // set cursor between the proposal's quotes
      ConfigurableCompletionProposal configurable = (ConfigurableCompletionProposal) proposal;
      configurable.setCursorPosition(proposalText.length() - 2);
    }
    acceptor.accept(proposal);
  }

  @Override public void completeFieldOption_Value(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    FieldOption option = (FieldOption) model;
    ProtoDescriptor descriptor = descriptorProvider.get();
    Enum enumType = descriptor.enumTypeOf(option);
    if (enumType != null) {
      proposeAndAccept(enumType, context, acceptor);
      return;
    }
    Property fieldOption = descriptor.lookupFieldOption(option.getName());
    if (fieldOption == null) return;
    if (properties.isBool(fieldOption)) {
      proposeBooleanValues(context, acceptor);
      return;
    }
  }

  private void proposeBooleanValues(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    CommonKeyword[] keywords = { FALSE, TRUE };
    proposeAndAccept(keywords, context, acceptor);
  }

  private void proposeAndAccept(CommonKeyword[] keywords, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    for (CommonKeyword keyword : keywords) proposeAndAccept(keyword.toString(), context, acceptor);
  }

  private void proposeAndAccept(Enum enumType, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    Image image = imageHelper.getImage(images.imageFor(Literal.class));
    for (Literal literal : enumType.getLiterals())
      proposeAndAccept(literal.getName(), image, context, acceptor);
  }

  private void proposeAndAccept(String proposalText, Image image, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    ICompletionProposal proposal = createCompletionProposal(proposalText, proposalText, image, context);
    acceptor.accept(proposal);
  }

  private boolean isLastWordFromCaretPositionEqualTo(String word, ContentAssistContext context) {
    StyledText styledText = context.getViewer().getTextWidget();
    int valueLength = word.length();
    int start = styledText.getCaretOffset() - valueLength;
    if (start < 0) return false;
    String previousWord = styledText.getTextRange(start, valueLength);
    return word.equals(previousWord);
  }
}
