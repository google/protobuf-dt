/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util.editor;

import static org.eclipse.compare.rangedifferencer.RangeDifferencer.findDifferences;
import static org.eclipse.core.filebuffers.FileBuffers.createTextFileBufferManager;
import static org.eclipse.core.runtime.Status.OK_STATUS;
import static org.eclipse.core.runtime.SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.ui.util.IStatusFactory.error;
import static com.google.eclipse.protobuf.ui.util.editor.Messages.calculatingChangedRegions;
import static com.google.eclipse.protobuf.ui.util.editor.Messages.errorCalculatingChangedRegions;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import com.google.inject.Singleton;

/**
 * Utility methods related to editors. Adapted from CDT's
 * {@code org.eclipse.cdt.internal.ui.util.EditorUtility}.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class ChangedLineRegionCalculator {
  private static Logger logger = Logger.getLogger(ChangedLineRegionCalculator.class);

  public IRegion[] calculateChangedLineRegions(final ITextFileBuffer buffer, final IDocument current,
      final IProgressMonitor monitor) throws CoreException {
    final AtomicReference<IRegion[]> result = new AtomicReference<IRegion[]>();
    final AtomicReference<IStatus> errorStatus = new AtomicReference<IStatus>(OK_STATUS);
    try {
      SafeRunner.run(new ISafeRunnable() {
        @Override public void handleException(Throwable exception) {
          logger.error(exception.getMessage(), exception);
          errorStatus.set(error(errorCalculatingChangedRegions, exception));
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
          List<IRegion> regions = newArrayList();
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
      if (!status.isOK()) {
        throw new CoreException(status);
      }
    }
    return result.get();
  }

  private static IProgressMonitor getSubProgressMonitor(IProgressMonitor monitor, int ticks) {
    if (monitor != null) {
      return new SubProgressMonitor(monitor, ticks, PREPEND_MAIN_LABEL_TO_SUBTASK);
    }
    return new NullProgressMonitor();
  }
}
