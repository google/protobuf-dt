/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util.editor;

import static com.google.eclipse.protobuf.ui.ProtobufUiModule.PLUGIN_ID;
import static com.google.eclipse.protobuf.ui.util.editor.Messages.*;
import static org.eclipse.compare.rangedifferencer.RangeDifferencer.findDifferences;
import static org.eclipse.core.filebuffers.FileBuffers.createTextFileBufferManager;
import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.core.runtime.Status.OK_STATUS;
import static org.eclipse.core.runtime.SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK;

import java.util.*;

import org.apache.log4j.Logger;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.core.filebuffers.*;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.text.*;

import com.google.eclipse.protobuf.ui.util.SimpleReference;
import com.google.inject.Singleton;

/**
 * Utility methods related to editors. Adapted from CDT's {@code org.eclipse.cdt.internal.ui.util.EditorUtility}.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Editors {

  private static Logger logger = Logger.getLogger(Editors.class);

  public IRegion[] calculateChangedLineRegions(final ITextFileBuffer buffer,
      final IDocument current, final IProgressMonitor monitor)
      throws CoreException {
    final SimpleReference<IRegion[]> result = new SimpleReference<IRegion[]>();
    final SimpleReference<IStatus> errorStatus = new SimpleReference<IStatus>(OK_STATUS);
    try {
      SafeRunner.run(new ISafeRunnable() {
        @Override public void handleException(Throwable exception) {
          logger.error(exception.getMessage(), exception);
          errorStatus.set(new Status(ERROR, PLUGIN_ID, 0, errorCalculatingChangedRegions, exception));
          result.set(null);
        }

        @Override public void run() throws Exception {
          monitor.beginTask(calculatingChangedRegions, 20);
          IFileStore fileStore = buffer.getFileStore();
          ITextFileBufferManager fileBufferManager = createTextFileBufferManager();
          fileBufferManager.connectFileStore(fileStore, getSubProgressMonitor(monitor, 15));
          try {
            IDocument old = ((ITextFileBuffer) fileBufferManager.getFileStoreFileBuffer(fileStore)).getDocument();
            result.set(getChangedLineRegions(old));
          } finally {
            fileBufferManager.disconnectFileStore(fileStore, getSubProgressMonitor(monitor, 5));
            monitor.done();
          }
        }

        /*
         * Returns regions of all lines which differ comparing {@code old}s content with {@code current}s content.
         * Successive lines are merged into one region.
         */
        private IRegion[] getChangedLineRegions(IDocument old) {
          RangeDifference[] differences = differencesWith(old);
          List<IRegion> regions = new ArrayList<IRegion>();
          int numberOfLines = current.getNumberOfLines();
          for (RangeDifference difference : differences) {
            if (difference.kind() == RangeDifference.CHANGE) {
              int startLine = Math.min(difference.rightStart(), numberOfLines - 1);
              int endLine = difference.rightEnd() - 1;
              IRegion startLineRegion;
              try {
                startLineRegion = current.getLineInformation(startLine);
                if (startLine >= endLine) {
                  // startLine > endLine indicates a deletion of one or more lines.
                  // Deletions are ignored except at the end of the document.
                  if (startLine == endLine
                      || startLineRegion.getOffset() + startLineRegion.getLength() == current.getLength()) {
                    regions.add(startLineRegion);
                  }
                  continue;
                }
                IRegion endLineRegion = current.getLineInformation(endLine);
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

        private RangeDifference[] differencesWith(IDocument old) {
          return findDifferences(new LineComparator(old), new LineComparator(current));
        }
      });
    } finally {
      IStatus status = errorStatus.get();
      if (!status.isOK()) throw new CoreException(status);
    }
    return result.get();
  }

  private static IProgressMonitor getSubProgressMonitor(IProgressMonitor monitor, int ticks) {
    if (monitor != null) return new SubProgressMonitor(monitor, ticks, PREPEND_MAIN_LABEL_TO_SUBTASK);
    return new NullProgressMonitor();
  }
}
