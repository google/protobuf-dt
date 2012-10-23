/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.outline;

import java.util.Map;

import org.eclipse.xtext.ui.editor.outline.actions.LinkWithEditorOutlineContribution;
import org.eclipse.xtext.ui.editor.outline.actions.OutlineWithEditorLinker;
import org.eclipse.xtext.ui.editor.outline.impl.OutlinePage;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class LinkWithEditor extends LinkWithEditorOutlineContribution {
  @Inject private Provider<OutlineWithEditorLinker> outlineWithEditorLinkerProvider;

  private final Map<OutlinePage, OutlineWithEditorLinker> page2linker = Maps.newHashMap();

  /** {@inheritDoc} */
  @Override public void register(OutlinePage outlinePage) {
    addPropertyChangeListener();
    OutlineWithEditorLinker outlineWithEditorLinker = outlineWithEditorLinkerProvider.get();
    outlineWithEditorLinker.activate(outlinePage);
    getPreferenceStoreAccess().getPreferenceStore().addPropertyChangeListener(outlineWithEditorLinker);
    outlineWithEditorLinker.setLinkingEnabled(true);
    page2linker.put(outlinePage, outlineWithEditorLinker);
  }

  @Override
  public void deregister(OutlinePage outlinePage) {
    removePropertyChangeListener();
    OutlineWithEditorLinker outlineWithEditorLinker = page2linker.remove(outlinePage);
    outlineWithEditorLinker.deactivate();
    getPreferenceStoreAccess().getPreferenceStore().removePropertyChangeListener(outlineWithEditorLinker);
  }
}
