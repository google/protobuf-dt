/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.editor.save;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * "Save actions" preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SaveActionsPreferences {

  private final RemoveTrailingSpace removeTrailingSpace;
  
  SaveActionsPreferences(RawPreferences preferences) {
    removeTrailingSpace = RemoveTrailingSpace.valueFrom(preferences);
  }
  
  public RemoveTrailingSpace removeTrailingSpace() {
    return removeTrailingSpace;
  }
  
  public static enum RemoveTrailingSpace {
    NONE, IN_EDITED_LINES, IN_ALL_LINES;
    
    static RemoveTrailingSpace valueFrom(RawPreferences preferences) {
      if (!preferences.removeTrailingWhitespace().value()) return NONE;
      return preferences.inEditedLines().value() ? IN_EDITED_LINES : IN_ALL_LINES;
    }
  }
}
