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
import com.google.eclipse.protobuf.ui.grammar.CompoundElements;
import com.google.eclipse.protobuf.ui.grammar.Keywords;
import com.google.eclipse.protobuf.ui.labeling.Images;
import com.google.eclipse.protobuf.ui.util.*;
import com.google.eclipse.protobuf.util.EObjectFinder;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 *
 * @see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on how to customize content assistant
 */
public class ProtobufProposalProvider extends AbstractProtobufProposalProvider {

  @Inject private CompoundElements compoundElements;
  @Inject private EObjectFinder finder;
  @Inject private Globals globalScope;
  @Inject private PluginImageHelper imageHelper;
  @Inject private Images imageRegistry;
  @Inject private Keywords keywords;
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
    for (Property fileOption : globalScope.fileOptions()) {
      String displayString = fileOption.getName();
      String proposalText = displayString + " " + keywords.equalSign().getValue() + " ";
      boolean isStringOption = properties.isString(fileOption);
      if (isStringOption)
        proposalText = proposalText + compoundElements.emptyString() + keywords.semicolon().getValue();
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
    Property fileOption = globalScope.lookupFileOption(option.getName());
    if (fileOption == null) return;
    if (globalScope.isOptimizeForOption(option)) {
      proposeAndAccept(globalScope.optimizedMode(), context, acceptor);
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
    proposeAndAccept(keywords.boolFalse().getValue(), context, acceptor);
    proposeAndAccept(keywords.boolTrue().getValue(), context, acceptor);
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
    ICompletionProposal proposal = createCompletionProposal(compoundElements.emptyString(), context);
    if (proposal instanceof ConfigurableCompletionProposal) {
      ConfigurableCompletionProposal configurable = (ConfigurableCompletionProposal) proposal;
      configurable.setCursorPosition(1);
    }
    acceptor.accept(proposal);
  }

  private boolean isProposalForDefaultValue(ContentAssistContext context) {
    return isProposalForAssignment(keywords.defaultValue().getValue(), context);
  }

  private boolean isProposalForAssignment(String feature, ContentAssistContext context) {
    ImmutableList<AbstractElement> grammarElements = context.getFirstSetGrammarElements();
    for (AbstractElement e : grammarElements) {
      if (!(e instanceof Assignment)) continue;
      Assignment a = (Assignment) e;
      String equalSign = keywords.equalSign().getValue();
      if (feature.equals(a.getFeature()) && equalSign.equals(a.getOperator())) return true;
    }
    return false;
  }

  @Override public void completeKeyword(Keyword keyword, ContentAssistContext context,
      ICompletionProposalAcceptor acceptor) {
    if (keyword == null) return;
    if (isKeywordEqualToPreviousWordInEditor(keyword, context)) return;
    if (keyword.equals(keywords.boolTrue()) || keyword.equals(keywords.boolFalse())) {
      if (!isBoolProposalValid(context)) return;
    }
    if (keyword.equals(keywords.openingBracket())) {
      boolean proposalWasHandledAlready = proposeOpenBracket(context, acceptor);
      if (proposalWasHandledAlready) return;
    }
    if (keyword.equals(keywords.packed())) {
      proposePackedOption(context, acceptor);
      return;
    }
    if (keyword.equals(keywords.defaultValue())) {
      proposeDefaultValue(context, acceptor);
      return;
    }
    super.completeKeyword(keyword, context, acceptor);
  }

  private boolean isKeywordEqualToPreviousWordInEditor(Keyword keyword, ContentAssistContext context) {
    StyledText styledText = context.getViewer().getTextWidget();
    String value = keyword.getValue();
    int valueLength = value.length();
    int start = styledText.getCaretOffset() - valueLength;
    if (start < 0) return false;
    String previousWord = styledText.getTextRange(start, valueLength);
    return value.equals(previousWord);
  }

  private boolean isBoolProposalValid(ContentAssistContext context) {
    EObject model = context.getCurrentModel();
    if (model instanceof Property) return properties.isBool((Property) model);
    if (model instanceof Option) {
      Property fileOption = globalScope.lookupFileOption(((Option) model).getName());
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
      String display = compoundElements.defaultValueInBrackets();
      int cursorPosition = display.indexOf(keywords.closingBracket().getValue());
      if (isStringProperty(p)) {
        display = compoundElements.defaultStringValueInBrackets();
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
      proposeAndAccept(compoundElements.packedInBrackets(), context, acceptor);
    return true;
  }

  private void proposePackedOption(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    Property p = extractPropertyFrom(context);
    if (p == null) return;
    Modifier modifier = p.getModifier();
    if (!REPEATED.equals(modifier) || !properties.isPrimitive(p)) return;
    proposeAndAccept(compoundElements.packed(), context, acceptor);
  }

  private void proposeDefaultValue(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    Modifier modifier = extractModifierFromModel(context);
    if (!OPTIONAL.equals(modifier)) return;
    String display = compoundElements.defaultValue();
    int cursorPosition = display.length();
    if (isStringProperty((Property) context.getCurrentModel())) {
      display = compoundElements.defaultStringValue();
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
