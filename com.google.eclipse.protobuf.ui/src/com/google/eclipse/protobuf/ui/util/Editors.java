/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static com.google.eclipse.protobuf.ui.ProtobufUiModule.PLUGIN_ID;
import static org.eclipse.compare.rangedifferencer.RangeDifferencer.findDifferences;
import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.core.runtime.Status.OK_STATUS;
import static org.eclipse.core.runtime.SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK;

import com.google.inject.Singleton;

import org.apache.log4j.Logger;
import org.eclipse.compare.rangedifferencer.*;
import org.eclipse.core.filebuffers.*;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.text.*;

import java.util.*;

/**
 * Utility methods related to editors. Adapted from CDT's {@code org.eclipse.cdt.internal.ui.util.EditorUtility}.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton 
public class Editors {

  private static Logger logger = Logger.getLogger(Editors.class);
  
  public static IRegion[] calculateChangedLineRegions(final ITextFileBuffer buffer, final IProgressMonitor monitor)
      throws CoreException {
    final IRegion[][] result = new IRegion[1][];
    final IStatus[] errorStatus = new IStatus[] { OK_STATUS };
    try {
      SafeRunner.run(new ISafeRunnable() {
        public void handleException(Throwable exception) {
          logger.error(exception.getMessage(), exception);
          String msg = "An error occurred while calculating the changed regions. See error log for details.";
          errorStatus[0] = new Status(ERROR, PLUGIN_ID, 0, msg, exception);
          result[0] = null;
        }

        public void run() throws Exception {
          monitor.beginTask("Calculating changed regions", 20);
          IFileStore fileStore = buffer.getFileStore();
          ITextFileBufferManager fileBufferManager = FileBuffers.createTextFileBufferManager();
          fileBufferManager.connectFileStore(fileStore, getSubProgressMonitor(monitor, 15));
          try {
            IDocument currentDocument = buffer.getDocument();
            IDocument oldDocument =
                ((ITextFileBuffer) fileBufferManager.getFileStoreFileBuffer(fileStore)).getDocument();
            result[0] = getChangedLineRegions(oldDocument, currentDocument);
          } finally {
            fileBufferManager.disconnectFileStore(fileStore, getSubProgressMonitor(monitor, 5));
            monitor.done();
          }
        }

        /*
         * Returns regions of all lines which differ comparing {@code oldDocument}s content with 
         * {@code currentDocument}s content. Successive lines are merged into one region.
         */
        private IRegion[] getChangedLineRegions(IDocument oldDocument, IDocument currentDocument) {
          RangeDifference[] differences = 
              findDifferences(new LineComparator(oldDocument), new LineComparator(currentDocument));
          List<IRegion> regions = new ArrayList<IRegion>();
          final int numberOfLines = currentDocument.getNumberOfLines();
          for (RangeDifference current : differences) {
            if (current.kind() == RangeDifference.CHANGE) {
              int startLine = Math.min(current.rightStart(), numberOfLines - 1);
              int endLine = current.rightEnd() - 1;
              IRegion startLineRegion;
              try {
                startLineRegion = currentDocument.getLineInformation(startLine);
                if (startLine >= endLine) {
                  // startLine > endLine indicates a deletion of one or more lines.
                  // Deletions are ignored except at the end of the document.
                  if (startLine == endLine
                      || startLineRegion.getOffset() + startLineRegion.getLength() == currentDocument.getLength()) {
                    regions.add(startLineRegion);
                  }
                  continue;
                } 
                IRegion endLineRegion = currentDocument.getLineInformation(endLine);
                int startOffset = startLineRegion.getOffset();
                int endOffset = endLineRegion.getOffset() + endLineRegion.getLength();
                regions.add(new Region(startOffset, endOffset - startOffset));
              } catch (BadLocationException e) {
                logger.error(e.getMessage(), e);
              }
            }
          }
          return regions.toArray(new IRegion[regions.size()]);
        }
      });
    } finally {
      if (!errorStatus[0].isOK()) throw new CoreException(errorStatus[0]);
    }
    return result[0];
  }
  
  private static IProgressMonitor getSubProgressMonitor(IProgressMonitor monitor, int ticks) {
    if (monitor != null) return new SubProgressMonitor(monitor, ticks, PREPEND_MAIN_LABEL_TO_SUBTASK);
    return new NullProgressMonitor();
  }
}
