/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui;

import static com.google.inject.name.Names.named;
import static org.eclipse.ui.PlatformUI.isWorkbenchRunning;

import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.ui.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.ui.LanguageSpecific;
import org.eclipse.xtext.ui.editor.*;
import org.eclipse.xtext.ui.editor.model.XtextDocumentProvider;
import org.eclipse.xtext.ui.editor.outline.actions.IOutlineContribution;
import org.eclipse.xtext.ui.editor.preferences.*;
import org.eclipse.xtext.ui.editor.quickfix.XtextQuickAssistProcessor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.*;

import com.google.eclipse.protobuf.scoping.IFileUriResolver;
import com.google.eclipse.protobuf.ui.builder.AutoAddNatureEditorCallback;
import com.google.eclipse.protobuf.ui.documentation.ProtobufDocumentationProvider;
import com.google.eclipse.protobuf.ui.editor.ProtobufUriEditorOpener;
import com.google.eclipse.protobuf.ui.editor.hyperlinking.ProtobufHyperlinkDetector;
import com.google.eclipse.protobuf.ui.editor.model.ProtobufDocumentProvider;
import com.google.eclipse.protobuf.ui.editor.spelling.ProtobufReconciler;
import com.google.eclipse.protobuf.ui.editor.syntaxcoloring.*;
import com.google.eclipse.protobuf.ui.internal.ProtobufActivator;
import com.google.eclipse.protobuf.ui.outline.*;
import com.google.eclipse.protobuf.ui.preferences.PreferenceStoreAccess;
import com.google.eclipse.protobuf.ui.preferences.pages.compiler.CompilerPreferenceStoreInitializer;
import com.google.eclipse.protobuf.ui.preferences.pages.editor.numerictag.NumericTagPreferenceStoreInitializer;
import com.google.eclipse.protobuf.ui.preferences.pages.editor.save.SaveActionsPreferenceStoreInitializer;
import com.google.eclipse.protobuf.ui.preferences.pages.general.GeneralPreferenceStoreInitializer;
import com.google.eclipse.protobuf.ui.preferences.pages.paths.PathsPreferenceStoreInitializer;
import com.google.eclipse.protobuf.ui.quickfix.ProtobufQuickAssistProcessor;
import com.google.eclipse.protobuf.ui.scoping.FileUriResolver;
import com.google.eclipse.protobuf.ui.validation.ValidateOnActivation;
import com.google.inject.Binder;

/**
 * Use this class to register components to be used within the IDE.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufUiModule extends AbstractProtobufUiModule {

  public static final String PLUGIN_ID = "com.google.eclipse.protobuf.ui";

  public ProtobufUiModule(AbstractUIPlugin plugin) {
    super(plugin);
    setValidationTrigger(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), plugin);
  }

  private void setValidationTrigger(IWorkbenchWindow w, AbstractUIPlugin plugin) {
    if (w == null || !(plugin instanceof ProtobufActivator)) return;
    w.getPartService().addPartListener(new ValidateOnActivation());
  }

  public Class<? extends IFileUriResolver> bindFileUriResolver() {
    return FileUriResolver.class;
  }

  public Class<? extends IHighlightingConfiguration> bindHighlightingConfiguration() {
    return HighlightingConfiguration.class;
  }

  @Override public Class<? extends IContentOutlinePage> bindIContentOutlinePage() {
    return ProtobufOutlinePage.class;
  }
  
  @Override public Class<? extends IHyperlinkDetector> bindIHyperlinkDetector() {
    return ProtobufHyperlinkDetector.class;
  }
  
  @Override public Class<? extends IReconciler> bindIReconciler() {
    return ProtobufReconciler.class;
  }
  
  @Override public Class<? extends IXtextEditorCallback> bindIXtextEditorCallback() {
    return AutoAddNatureEditorCallback.class;
  }
  
  public Class<? extends IEObjectDocumentationProvider> bindIEObjectDocumentationProvider() {
    return ProtobufDocumentationProvider.class;
  }

  public Class<? extends IPreferenceStoreAccess> bindIPreferenceStoreAccess() {
    return PreferenceStoreAccess.class;
  }

  public Class<? extends ISemanticHighlightingCalculator> bindISemanticHighlightingCalculator() {
    return ProtobufSemanticHighlightingCalculator.class;
  }

  public Class<? extends XtextDocumentProvider> bindXtextDocumentProvider() {
    return ProtobufDocumentProvider.class;
  }

  public Class<? extends XtextQuickAssistProcessor> bindXtextQuickAssistProcessor(){
    return ProtobufQuickAssistProcessor.class;
  }

  @Override public void configureLanguageSpecificURIEditorOpener(Binder binder) {
    if (!isWorkbenchRunning())return;
    binder.bind(IURIEditorOpener.class)
          .annotatedWith(LanguageSpecific.class)
          .to(ProtobufUriEditorOpener.class);
  }

  public void configurePreferencesInitializers(Binder binder) {
    configurePreferenceInitializer(binder, "compilerPreferences", CompilerPreferenceStoreInitializer.class);
    configurePreferenceInitializer(binder, "generalPreferences", GeneralPreferenceStoreInitializer.class);
    configurePreferenceInitializer(binder, "numericTagPreferences", NumericTagPreferenceStoreInitializer.class);
    configurePreferenceInitializer(binder, "pathsPreferences", PathsPreferenceStoreInitializer.class);
    configurePreferenceInitializer(binder, "saveActionsPreferences", SaveActionsPreferenceStoreInitializer.class);
  }

  private void configurePreferenceInitializer(Binder binder, String name,
      Class<? extends IPreferenceStoreInitializer> initializerType) {
    binder.bind(IPreferenceStoreInitializer.class).annotatedWith(named(name)).to(initializerType);
  }

  @Override public void configureToggleLinkWithEditorOutlineContribution(Binder binder) {
    binder.bind(IOutlineContribution.class)
          .annotatedWith(IOutlineContribution.LinkWithEditor.class)
          .to(LinkWithEditor.class);
  }
}
