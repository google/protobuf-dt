/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.stubs.resources;

import static java.util.Collections.*;

import java.io.*;
import java.net.URI;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class FileStub implements IFile {

  private final Map<String, List<MarkerStub>> markersByType = new HashMap<String, List<MarkerStub>>();
  private IPath location;

  /** {@inheritDoc} */
  @Override public void accept(IResourceProxyVisitor visitor, int memberFlags) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void accept(IResourceVisitor visitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void accept(IResourceVisitor visitor, int depth, int memberFlags) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void appendContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor)
      {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void appendContents(InputStream source, int updateFlags, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void clearHistory(IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean contains(ISchedulingRule rule) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void copy(IPath destination, boolean force, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void create(InputStream source, boolean force, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void create(InputStream source, int updateFlags, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void createLink(URI location, int updateFlags, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public IMarker createMarker(String type) {
    MarkerStub marker = new MarkerStub(type);
    addMarker(marker);
    return marker;
  }

  /** {@inheritDoc} */
  @Override public IResourceProxy createProxy() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void delete(boolean force, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void delete(int updateFlags, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void deleteMarkers(String type, boolean includeSubtypes, int depth) {
    List<MarkerStub> markers = markersByType.get(type);
    if (markers != null) {
      markers.clear();
    }
  }

  /** {@inheritDoc} */
  @Override public boolean exists() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public IMarker findMarker(long id) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) {
    List<MarkerStub> markers = markersByType.get(type);
    if (markers == null) {
      return new IMarker[0];
    }
    return markers.toArray(new IMarker[markers.size()]);
  }

  /** {@inheritDoc} */
  @Override public int findMaxProblemSeverity(String type, boolean includeSubtypes, int depth) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override @SuppressWarnings("rawtypes") public Object getAdapter(Class adapter) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public String getCharset() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public String getCharset(boolean checkImplicit) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public String getCharsetFor(Reader reader) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public IContentDescription getContentDescription() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public InputStream getContents() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public InputStream getContents(boolean force) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override @Deprecated public int getEncoding() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public String getFileExtension() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public IPath getFullPath() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public IFileState[] getHistory(IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public long getLocalTimeStamp() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public IPath getLocation() {
    return location;
  }

  public void setLocation(IPath location) {
    this.location = location;
  }

  /** {@inheritDoc} */
  @Override public URI getLocationURI() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public IMarker getMarker(long id) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public long getModificationStamp() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public String getName() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public IContainer getParent() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public IPathVariableManager getPathVariableManager() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public Map<QualifiedName, String> getPersistentProperties() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public String getPersistentProperty(QualifiedName key) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public IProject getProject() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public IPath getProjectRelativePath() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public IPath getRawLocation() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public URI getRawLocationURI() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public ResourceAttributes getResourceAttributes() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public Map<QualifiedName, Object> getSessionProperties() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public Object getSessionProperty(QualifiedName key) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public int getType() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public IWorkspace getWorkspace() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isAccessible() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isConflicting(ISchedulingRule rule) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isDerived() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isDerived(int options) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isHidden() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isHidden(int options) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isLinked() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isLinked(int options) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override @Deprecated public boolean isLocal(int depth) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isPhantom() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isReadOnly() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isSynchronized(int depth) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isTeamPrivateMember() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isTeamPrivateMember(int options) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isVirtual() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor)
      {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void move(IPath destination, boolean force, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void move(IPath destination, int updateFlags, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void move(IProjectDescription description, boolean force, boolean keepHistory, IProgressMonitor monitor)
      {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void refreshLocal(int depth, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void revertModificationStamp(long value) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override @Deprecated public void setCharset(String newCharset) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setCharset(String newCharset, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setContents(IFileState source, boolean force, boolean keepHistory, IProgressMonitor monitor)
      {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setContents(IFileState source, int updateFlags, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor)
      {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setContents(InputStream source, int updateFlags, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override @Deprecated public void setDerived(boolean isDerived) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setDerived(boolean isDerived, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setHidden(boolean isHidden) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override @Deprecated public void setLocal(boolean flag, int depth, IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public long setLocalTimeStamp(long value) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setPersistentProperty(QualifiedName key, String value) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override @Deprecated public void setReadOnly(boolean readOnly) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setResourceAttributes(ResourceAttributes attributes) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setSessionProperty(QualifiedName key, Object value) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setTeamPrivateMember(boolean isTeamPrivate) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void touch(IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  public int markerCount(String type) {
    List<MarkerStub> markers = markersByType.get(type);
    return (markers == null) ? 0 : markers.size();
  }

  public List<MarkerStub> markersOfType(String type) {
    List<MarkerStub> markers = markersByType.get(type);
    if (markers == null) {
      return emptyList();
    }
    return unmodifiableList(markers);
  }

  public void addMarker(MarkerStub marker) {
    String type = marker.getType();
    List<MarkerStub> markers = markersByType.get(type);
    if (markers == null) {
      markers = new ArrayList<MarkerStub>();
      markersByType.put(type, markers);
    }
    markers.add(marker);
  }
}
