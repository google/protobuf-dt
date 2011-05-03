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
import static java.lang.String.valueOf;

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
import com.google.eclipse.protobuf.scoping.Globals;
import com.google.eclipse.protobuf.ui.grammar.*;
import com.google.eclipse.protobuf.ui.grammar.CompoundElement;
import com.google.eclipse.protobuf.ui.labeling.Images;
import com.google.eclipse.protobuf.ui.util.*;
import com.google.eclipse.protobuf.util.ProtobufElementFinder;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 *
 * @see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on how to customize content assistant
 */
public class ProtobufProposalProvider extends AbstractProtobufProposalProvider {

  @Inject private ProtobufElementFinder finder;
  @Inject private Globals globals;
  @Inject private PluginImageHelper imageHelper;
  @Inject private Images imageRegistry;
  @Inject private Literals literals;
  @Inject private Properties properties;
  @Inject private Strings strings;

  @Override public void completeOption_Name(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    EObject container = model.eContainer();
    if (container instanceof Protobuf) {
      proposeCommonFileOptions(context, acceptor);
      return;
    }
  }

  private void proposeCommonFileOptions(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    for (Property fileOption : globals.fileOptions()) {
      String displayString = fileOption.getName();
      String proposalText = displayString + " " + EQUAL + " ";
      boolean isStringOption = properties.isString(fileOption);
      if (isStringOption)
        proposalText = proposalText + EMPTY_STRING + SEMICOLON;
      ICompletionProposal proposal = createCompletionProposal(proposalText, displayString, context);
      if (isStringOption && proposal instanceof ConfigurableCompletionProposal) {
        // set cursor between the proposal's quotes
        ConfigurableCompletionProposal configurable = (ConfigurableCompletionProposal) proposal;
        configurable.setCursorPosition(proposalText.length() - 2);
      }
      acceptor.accept(proposal);
    }
  }

  @Override public void completeOption_Value(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    Option option = (Option) model;
    Property fileOption = globals.lookupFileOption(option.getName());
    if (fileOption == null) return;
    if (globals.isOptimizeForOption(option)) {
      proposeAndAccept(globals.optimizedMode(), context, acceptor);
      return;
    }
    if (properties.isString(fileOption)) {
      proposeEmptyString(context, acceptor);
      return;
    }
    if (properties.isBool(fileOption)) {
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
    for (CommonKeyword keyword : keywords) proposeAndAccept(keyword, context, acceptor);
  }

  private void proposeAndAccept(CommonKeyword keyword, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(keyword.toString(), context, acceptor);
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
    if (model instanceof Option) return;
    super.complete_STRING(model, ruleCall, context, acceptor);
  }

  private void proposeEmptyString(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    ICompletionProposal proposal = createCompletionProposal(EMPTY_STRING, context);
    if (proposal instanceof ConfigurableCompletionProposal) {
      ConfigurableCompletionProposal configurable = (ConfigurableCompletionProposal) proposal;
      configurable.setCursorPosition(1);
    }
    acceptor.accept(proposal);
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
    if (isKeywordEqualToPreviousWordInEditor(keyword, context)) return true;
    if (TRUE.hasValue(keyword) || FALSE.hasValue(keyword)) {
      if (!isBoolProposalValid(context)) return true;
    }
    if (OPENING_BRACKET.hasValue(keyword)) {
      return proposeOpenBracket(context, acceptor);
    }
    if (PACKED.hasValue(keyword)) {
      proposePackedOption(context, acceptor);
      return true;
    }
    if (DEFAULT.hasValue(keyword)) {
      proposeDefaultValue(context, acceptor);
      return true;
    }
    return false;
  }

  private boolean isKeywordEqualToPreviousWordInEditor(String keyword, ContentAssistContext context) {
    StyledText styledText = context.getViewer().getTextWidget();
    int valueLength = keyword.length();
    int start = styledText.getCaretOffset() - valueLength;
    if (start < 0) return false;
    String previousWord = styledText.getTextRange(start, valueLength);
    return keyword.equals(previousWord);
  }

  private boolean isBoolProposalValid(ContentAssistContext context) {
    EObject model = context.getCurrentModel();
    if (model instanceof Property) return properties.isBool((Property) model);
    if (model instanceof Option) {
      Property fileOption = globals.lookupFileOption(((Option) model).getName());
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
      ICompletionProposal proposal = createCompletionProposal(display, context);
      if (proposal instanceof ConfigurableCompletionProposal) {
        ConfigurableCompletionProposal configurable = (ConfigurableCompletionProposal) proposal;
        configurable.setCursorPosition(cursorPosition);
      }
      acceptor.accept(proposal);
    }
    if (REPEATED.equals(modifier) && properties.isPrimitive(p))
      proposeAndAccept(PACKED_EQUAL_TRUE_IN_BRACKETS, context, acceptor);
    return true;
  }

  private void proposePackedOption(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    Property p = extractPropertyFrom(context);
    if (p == null) return;
    Modifier modifier = p.getModifier();
    if (!REPEATED.equals(modifier) || !properties.isPrimitive(p)) return;
    proposeAndAccept(PACKED_EQUAL_TRUE, context, acceptor);
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
    if (isStringProperty((Property) context.getCurrentModel())) {
      display = DEFAULT_EQUAL_STRING;
      cursorPosition++;
    }
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
    EObject model = context.getCurrentModel();
    // this is most likely a bug in Xtext:
    if (!(model instanceof Property)) model = context.getPreviousModel();
    if (!(model instanceof Property)) return null;
    return (Property) model;
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

  private void proposeAndAccept(Enum enumType, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    Image image = imageHelper.getImage(imageRegistry.imageFor(Literal.class));
    for (Literal literal : enumType.getLiterals())
      proposeAndAccept(literal.getName(), image, context, acceptor);
  }

  private void proposeAndAccept(String proposalText, Image image, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    ICompletionProposal proposal = createCompletionProposal(proposalText, proposalText, image, context);
    acceptor.accept(proposal);
  }

  @Override public void completeProperty_Index(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    int index = properties.calculateTagNumberOf((Property) model);
    proposeIndex(index, context, acceptor);
  }

  private void proposeIndex(int index, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    proposeAndAccept(valueOf(index), context, acceptor);
  }

  @Override public void completeProperty_Name(EObject model, Assignment assignment, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    String typeName = strings.firstCharToLowerCase(properties.typeNameOf((Property) model));
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
    ICompletionProposal proposal = createCompletionProposal(proposalText, context);
    acceptor.accept(proposal);
  }

  @Override protected ICompletionProposal createCompletionProposal(String proposal,
      ContentAssistContext contentAssistContext) {
    return createCompletionProposal(proposal, null, defaultImage(), getPriorityHelper().getDefaultPriority(),
        contentAssistContext.getPrefix(), contentAssistContext);
  }

  private Image defaultImage() {
    return imageHelper.getImage(imageRegistry.defaultImage());
  }
}
