/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.google.eclipse.protobuf.junit.stubs;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Map;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public abstract class FileStub implements IFile {

  /** {@inheritDoc} */
  public void accept(IResourceProxyVisitor visitor, int memberFlags) throws CoreException {}

  /** {@inheritDoc} */
  public void accept(IResourceVisitor visitor) throws CoreException {}

  /** {@inheritDoc} */
  public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {}

  /** {@inheritDoc} */
  public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException {}

  /** {@inheritDoc} */
  public void clearHistory(IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public boolean contains(ISchedulingRule rule) {
    return false;
  }

  /** {@inheritDoc} */
  public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public IMarker createMarker(String type) throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public IResourceProxy createProxy() {
    return null;
  }

  /** {@inheritDoc} */
  public void delete(boolean force, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {}

  /** {@inheritDoc} */
  public boolean exists() {
    return false;
  }

  /** {@inheritDoc} */
  public IMarker findMarker(long id) throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public int findMaxProblemSeverity(String type, boolean includeSubtypes, int depth) throws CoreException {
    return 0;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes") public Object getAdapter(Class adapter) {
    return null;
  }

  /** {@inheritDoc} */
  public String getFileExtension() {
    return null;
  }

  /** {@inheritDoc} */
  public long getLocalTimeStamp() {
    return 0;
  }

  /** {@inheritDoc} */
  public IPath getLocation() {
    return null;
  }

  /** {@inheritDoc} */
  public URI getLocationURI() {
    return null;
  }

  /** {@inheritDoc} */
  public IMarker getMarker(long id) {
    return null;
  }

  /** {@inheritDoc} */
  public long getModificationStamp() {
    return 0;
  }

  /** {@inheritDoc} */
  public IPathVariableManager getPathVariableManager() {
    return null;
  }

  /** {@inheritDoc} */
  public IContainer getParent() {
    return null;
  }

  /** {@inheritDoc} */
  public Map<QualifiedName, String> getPersistentProperties() throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public String getPersistentProperty(QualifiedName key) throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public IProject getProject() {
    return null;
  }

  /** {@inheritDoc} */
  public IPath getProjectRelativePath() {
    return null;
  }

  /** {@inheritDoc} */
  public IPath getRawLocation() {
    return null;
  }

  /** {@inheritDoc} */
  public URI getRawLocationURI() {
    return null;
  }

  /** {@inheritDoc} */
  public ResourceAttributes getResourceAttributes() {
    return null;
  }

  /** {@inheritDoc} */
  public Map<QualifiedName, Object> getSessionProperties() throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public Object getSessionProperty(QualifiedName key) throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public int getType() {
    return 0;
  }

  /** {@inheritDoc} */
  public IWorkspace getWorkspace() {
    return null;
  }

  /** {@inheritDoc} */
  public boolean isAccessible() {
    return false;
  }

  /** {@inheritDoc} */
  public boolean isConflicting(ISchedulingRule rule) {
    return false;
  }

  /** {@inheritDoc} */
  public boolean isDerived() {
    return false;
  }

  /** {@inheritDoc} */
  public boolean isDerived(int options) {
    return false;
  }

  /** {@inheritDoc} */
  public boolean isHidden() {
    return false;
  }

  /** {@inheritDoc} */
  public boolean isHidden(int options) {
    return false;
  }

  /** {@inheritDoc} */
  public boolean isLinked() {
    return false;
  }

  /** {@inheritDoc} */
  public boolean isVirtual() {
    return false;
  }

  /** {@inheritDoc} */
  public boolean isLinked(int options) {
    return false;
  }

  /** {@inheritDoc} */
  public boolean isLocal(int depth) {
    return false;
  }

  /** {@inheritDoc} */
  public boolean isPhantom() {
    return false;
  }

  /** {@inheritDoc} */
  public boolean isSynchronized(int depth) {
    return false;
  }

  /** {@inheritDoc} */
  public boolean isTeamPrivateMember() {
    return false;
  }

  /** {@inheritDoc} */
  public boolean isTeamPrivateMember(int options) {
    return false;
  }

  /** {@inheritDoc} */
  public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void move(IProjectDescription description, boolean force, boolean keepHistory, IProgressMonitor monitor)
      throws CoreException {}

  /** {@inheritDoc} */
  public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void revertModificationStamp(long value) throws CoreException {}

  /** {@inheritDoc} */
  public void setDerived(boolean isDerived) throws CoreException {}

  /** {@inheritDoc} */
  public void setDerived(boolean isDerived, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void setHidden(boolean isHidden) throws CoreException {}

  /** {@inheritDoc} */
  public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public long setLocalTimeStamp(long value) throws CoreException {
    return 0;
  }

  /** {@inheritDoc} */
  public void setPersistentProperty(QualifiedName key, String value) throws CoreException {}

  /** {@inheritDoc} */
  public void setReadOnly(boolean readOnly) {}

  /** {@inheritDoc} */
  public void setResourceAttributes(ResourceAttributes attributes) throws CoreException {}

  /** {@inheritDoc} */
  public void setSessionProperty(QualifiedName key, Object value) throws CoreException {}

  /** {@inheritDoc} */
  public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException {}

  /** {@inheritDoc} */
  public void touch(IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void appendContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor)
      throws CoreException {}

  /** {@inheritDoc} */
  public void appendContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void create(InputStream source, boolean force, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void create(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void createLink(URI location, int updateFlags, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public String getCharset() throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public String getCharset(boolean checkImplicit) throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public String getCharsetFor(Reader reader) throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public InputStream getContents() throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public InputStream getContents(boolean force) throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public int getEncoding() throws CoreException {
    return 0;
  }

  /** {@inheritDoc} */
  public IPath getFullPath() {
    return null;
  }

  /** {@inheritDoc} */
  public IFileState[] getHistory(IProgressMonitor monitor) throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public String getName() {
    return null;
  }

  /** {@inheritDoc} */
  public boolean isReadOnly() {
    return false;
  }

  /** {@inheritDoc} */
  public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor)
      throws CoreException {}

  /** {@inheritDoc} */
  public void setCharset(String newCharset) throws CoreException {}

  /** {@inheritDoc} */
  public void setCharset(String newCharset, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void setContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor)
      throws CoreException {}

  /** {@inheritDoc} */
  public void setContents(IFileState source, boolean force, boolean keepHistory, IProgressMonitor monitor)
      throws CoreException {}

  /** {@inheritDoc} */
  public void setContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {}

  /** {@inheritDoc} */
  public void setContents(IFileState source, int updateFlags, IProgressMonitor monitor) throws CoreException {}
}
