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
import static java.util.Collections.emptyList;
import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;
import static org.eclipse.xtext.util.Strings.toFirstLower;

import java.util.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.*;
import org.eclipse.xtext.ui.PluginImageHelper;
import org.eclipse.xtext.ui.editor.contentassist.*;

import com.google.eclipse.protobuf.grammar.CommonKeyword;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.scoping.*;
import com.google.eclipse.protobuf.ui.grammar.CompoundElement;
import com.google.eclipse.protobuf.ui.labeling.Images;
import com.google.eclipse.protobuf.ui.util.*;
import com.google.eclipse.protobuf.util.*;
import com.google.eclipse.protobuf.util.Properties;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 *
 * @see <a href="http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist">Xtext Content Assist</a>
 */
public class ProtobufProposalProvider extends AbstractProtobufProposalProvider {

  @Inject private CustomOptionProperties customOptionProperties;
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private FieldOptions fieldOptions;
  @Inject private ProtobufElementFinder finder;
  @Inject private Fields fields;
  @Inject private Images images;
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

  @Override public void completeBuiltInOption_Property(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    Collection<Property> optionProperties = descriptor.availableOptionPropertiesFor(model);
    if (!optionProperties.isEmpty()) proposeOptions(optionProperties, context, acceptor);
  }

  private void proposeOptions(Collection<Property> optionProperties, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    for (Property p : optionProperties) proposeOption(p, context, acceptor);
  }

  @Override public void completeBuiltInOption_Value(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    BuiltInOption option = (BuiltInOption) model;
    Property property = options.propertyFrom(option);
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

  private void proposeEqualProto2(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(EQUAL_PROTO2_IN_QUOTES, context, acceptor);
  }

  private boolean isKeyword(EObject object, CommonKeyword keyword) {
    return object instanceof Keyword && keyword.hasValue(((Keyword) object).getValue());
  }

  private boolean isBoolProposalValid(ContentAssistContext context) {
    EObject model = context.getCurrentModel();
    if (model instanceof Property) return properties.isBool((Property) model);
    if (model instanceof Option) {
      Property option = options.propertyFrom((Option) model);
      return option != null && properties.isBool(option);
    }
    if (model instanceof FieldOption) {
      Property fileOption = descriptorProvider.primaryDescriptor().lookupOption(((FieldOption) model).getName());
      return fileOption != null && properties.isBool(fileOption);
    }
    return false;
  }

  private boolean isNanProposalValid(ContentAssistContext context) {
    EObject model = context.getCurrentModel();
    if (model instanceof Property) return properties.mayBeNan((Property) model);
    if (model instanceof Option) {
      Property option = options.propertyFrom((Option) model);
      return option != null && properties.mayBeNan(option);
    }
    if (model instanceof FieldOption) {
      Property fileOption = descriptorProvider.primaryDescriptor().lookupOption(((FieldOption) model).getName());
      return fileOption != null && properties.mayBeNan(fileOption);
    }
    return false;
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

  private void proposeAndAccept(CompoundElement proposalText, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(proposalText.toString(), context, acceptor);
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
    long index = fields.calculateTagNumberOf((Property) model);
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

  @Override public void completeBuiltInFieldOption_Name(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    Field field = extractFieldFrom(context);
    proposeCommonFieldOptions(field, context, acceptor);
  }

  private Field extractFieldFrom(ContentAssistContext context) {
    return extractElementFromContext(context, Field.class);
  }

  private void proposeCommonFieldOptions(Field field, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    List<String> optionNames = existingFieldOptionNames(field);
    proposeDefaultKeyword(field, optionNames, context, acceptor);
    for (Property option : descriptorProvider.primaryDescriptor().fieldOptions()) {
      String optionName = option.getName();
      if (optionNames.contains(optionName) || ("packed".equals(optionName) && !canBePacked(field))) continue;
      proposeOption(option, context, acceptor);
    }
  }

  private List<String> existingFieldOptionNames(Field field) {
    List<FieldOption> allFieldOptions = field.getFieldOptions();
    if (allFieldOptions.isEmpty()) return emptyList();
    List<String> optionNames = new ArrayList<String>();
    for (FieldOption option : allFieldOptions)
      optionNames.add(option.getName());
    return optionNames;
  }

  private void proposeDefaultKeyword(Field field, List<String> existingFieldOptionNames, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (!(field instanceof Property)) return;
    Property property = (Property) field;
    if (!properties.isOptional(property) || existingFieldOptionNames.contains(DEFAULT.toString())) return;
    CompoundElement display = DEFAULT_EQUAL;
    int cursorPosition = display.charCount();
    if (properties.isString(property)) {
      display = DEFAULT_EQUAL_STRING;
      cursorPosition++;
    }
    createAndAccept(display, cursorPosition, context, acceptor);
  }

  private boolean canBePacked(Field field) {
    if (!(field instanceof Property)) return false;
    Property property = (Property) field;
    return properties.isPrimitive(property) && REPEATED.equals(property.getModifier());
  }

  private void proposeOption(Property option, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    String displayString = option.getName();
    String proposalText = displayString + space() + EQUAL + space();
    boolean isStringOption = properties.isString(option);
    if (isStringOption) {
      proposalText = proposalText + EMPTY_STRING;
    } else if (properties.isBool(option)) {
      proposalText = proposalText + TRUE;
    }
    ICompletionProposal proposal = createCompletionProposal(proposalText, displayString, imageForOption(), context);
    if (isStringOption && proposal instanceof ConfigurableCompletionProposal) {
      // set cursor between the proposal's quotes
      ConfigurableCompletionProposal configurable = (ConfigurableCompletionProposal) proposal;
      configurable.setCursorPosition(proposalText.length() - 1);
    }
    acceptor.accept(proposal);
  }

  @Override public void completeBuiltInFieldOption_Value(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (!(model instanceof BuiltInFieldOption)) return;
    BuiltInFieldOption option = (BuiltInFieldOption) model;
    if (fieldOptions.isDefaultValueOption(option)) {
      proposeDefaultValue(option, context, acceptor);
      return;
    }
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    Enum enumType = descriptor.enumTypeOf(option);
    if (enumType != null) {
      proposeAndAccept(enumType, context, acceptor);
      return;
    }
    Property fieldOption = descriptor.lookupFieldOption(option.getName());
    if (fieldOption == null) return;
    proposePrimitiveValues(fieldOption, context, acceptor);
  }

  private void proposeDefaultValue(FieldOption option, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    Property property = (Property) option.eContainer();
    if (!properties.isOptional(property)) return;
    if (proposePrimitiveValues(property, context, acceptor)) return;
    Enum enumType = finder.enumTypeOf(property);
    if (enumType != null) {
      proposeAndAccept(enumType, context, acceptor);
    }
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

  private void proposeEmptyString(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    createAndAccept(EMPTY_STRING, 1, context, acceptor);
  }

  private void proposeAndAccept(CommonKeyword[] keywords, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    for (CommonKeyword keyword : keywords)
      proposeAndAccept(keyword.toString(), context, acceptor);
  }

  private void proposeAndAccept(Enum enumType, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    Image image = imageHelper.getImage(images.imageFor(Literal.class));
    for (Literal literal : getAllContentsOfType(enumType, Literal.class))
      proposeAndAccept(literal.getName(), image, context, acceptor);
  }

  private boolean isLastWordFromCaretPositionEqualTo(String word, ContentAssistContext context) {
    StyledText styledText = context.getViewer().getTextWidget();
    int valueLength = word.length();
    int start = styledText.getCaretOffset() - valueLength;
    if (start < 0) return false;
    String previousWord = styledText.getTextRange(start, valueLength);
    return word.equals(previousWord);
  }

  @Override public void completePropertyRef_Property(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
  }
  
  @Override public void completeSimplePropertyRef_Property(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
  }
  
  @Override public void completeCustomOption_Property(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (!(model instanceof CustomOption)) return;
    Collection<Property> allProperties = customOptionProperties.propertiesFor((CustomOption) model);
    proposeAndAccept(allProperties, imageForOption(), context, acceptor);
  }

  private Image imageForOption() {
    return imageHelper.getImage(images.imageFor(Option.class));
  }
  
  @Override public void completeCustomOption_PropertyField(EObject model, Assignment assignment,
      ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    if (!(model instanceof CustomOption)) return;
    Property property = options.propertyFrom((CustomOption) model);
    if (property == null) return;
    Message message = finder.messageTypeOf(property);
    if (message != null) {
      Image image = imageHelper.getImage("property.gif");
      proposeAndAccept(finder.propertiesOf(message), image, context, acceptor);
    }
  }

  private void proposeAndAccept(Collection<Property> allProperties, Image image, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    for (Property p : allProperties)
      proposeAndAccept(p.getName(), image, context, acceptor);
  }

  private void proposeAndAccept(String proposalText, Image image, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    ICompletionProposal proposal = createCompletionProposal(proposalText, proposalText, image, context);
    acceptor.accept(proposal);
  }
}
