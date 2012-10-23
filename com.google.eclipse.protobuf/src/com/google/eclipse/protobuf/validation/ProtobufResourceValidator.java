/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static org.eclipse.xtext.diagnostics.Severity.ERROR;
import static org.eclipse.xtext.diagnostics.Severity.WARNING;
import static org.eclipse.xtext.validation.AbstractInjectableValidator.CURRENT_LANGUAGE_NAME;
import static org.eclipse.xtext.validation.CancelableDiagnostician.CANCEL_INDICATOR;
import static org.eclipse.xtext.validation.CheckMode.KEY;
import static org.eclipse.xtext.validation.CheckType.FAST;
import static org.eclipse.xtext.validation.impl.ConcreteSyntaxEValidator.DISABLE_CONCRETE_SYNTAX_EVALIDATOR;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.eclipse.protobuf.validation.Messages.importingNonProto2;
import static com.google.eclipse.protobuf.validation.Messages.scopingError;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.IAcceptor;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.validation.ResourceValidatorImpl;

import com.google.eclipse.protobuf.linking.ProtobufDiagnostic;

/**
 * Adds support for converting scoping errors into warnings if non-proto2 files are imported.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufResourceValidator extends ResourceValidatorImpl {
  private static final Logger log = Logger.getLogger(ProtobufResourceValidator.class);

  @Override public List<Issue> validate(Resource resource, CheckMode mode, CancelIndicator indicator) {
    CancelIndicator monitor = indicator == null ? CancelIndicator.NullImpl : indicator;
    resolveProxies(resource, monitor);
    if (monitor.isCanceled()) {
      return null;
    }
    List<Issue> result = newArrayListWithExpectedSize(resource.getErrors().size() + resource.getWarnings().size());
    try {
      IAcceptor<Issue> acceptor = createAcceptor(result);
      Status status = delegateValidationToDiagnostician(resource, mode, monitor, acceptor);
      if (status.isCanceled()) {
        return null;
      }
      if (mode.shouldCheck(FAST)) {
        status = createErrors(resource, status.hasProto1Imports(), acceptor, monitor);
        if (status.isCanceled()) {
          return null;
        }
        status = createWarnings(resource, acceptor, monitor);
        if (status.isCanceled()) {
          return null;
        }
      }
    } catch (RuntimeException e) {
      log.error(e.getMessage(), e);
    }
    return result;
  }

  private Status delegateValidationToDiagnostician(Resource resource, CheckMode mode,
      CancelIndicator monitor, IAcceptor<Issue> acceptor) {
    Status hasNonProto2Import = Status.OK;
    for (EObject element : resource.getContents()) {
      if (monitor.isCanceled()) {
        return Status.CANCELED;
      }
      Diagnostic diagnostic = getDiagnostician().validate(element, validationOptions(resource, mode, monitor));
      if (convertIssuesToMarkers(acceptor, diagnostic) == Status.PROTO1_IMPORTS_FOUND) {
        hasNonProto2Import = Status.PROTO1_IMPORTS_FOUND;
      }
    }
    return hasNonProto2Import;
  }

  private Map<Object, Object> validationOptions(Resource resource, CheckMode mode, CancelIndicator monitor) {
    Map<Object, Object> options = newHashMap();
    options.put(KEY, mode);
    options.put(CANCEL_INDICATOR, monitor);
    options.put(DISABLE_CONCRETE_SYNTAX_EVALIDATOR, true);
    options.put(EValidator.class, getDiagnostician());
    if (resource instanceof XtextResource) {
      options.put(CURRENT_LANGUAGE_NAME, ((XtextResource) resource).getLanguageName());
    }
    return options;
  }

  private Status convertIssuesToMarkers(IAcceptor<Issue> acceptor, Diagnostic diagnostic) {
    Status hasNonProto2Import = Status.OK;
    if (diagnostic.getChildren().isEmpty()) {
      issueFromEValidatorDiagnostic(diagnostic, acceptor);
      return hasNonProto2Import;
    }
    for (Diagnostic child : diagnostic.getChildren()) {
      if (importingNonProto2.equals(child.getMessage())) {
        hasNonProto2Import = Status.PROTO1_IMPORTS_FOUND;
      }
      issueFromEValidatorDiagnostic(child, acceptor);
    }
    return hasNonProto2Import;
  }

  private Status createErrors(Resource resource, boolean proto1ImportsFound, IAcceptor<Issue> acceptor,
      CancelIndicator monitor) {
    for (Resource.Diagnostic error : resource.getErrors()) {
      if (monitor.isCanceled()) {
        return Status.CANCELED;
      }
      Severity severity = ERROR;
      if (proto1ImportsFound && isUnresolvedReferenceError(error)) {
        severity = WARNING;
        ProtobufDiagnostic d = (ProtobufDiagnostic) error;
        String message = d.getMessage();
        if (message.endsWith(scopingError)) {
          continue;
        }
        if (!message.endsWith(".")) {
          d.appendToMessage(".");
        }
        d.appendToMessage(" ");
        d.appendToMessage(scopingError);
      }
      issueFromXtextResourceDiagnostic(error, severity, acceptor);
    }
    return Status.OK;
  }

  private boolean isUnresolvedReferenceError(Resource.Diagnostic error) {
    if (!(error instanceof ProtobufDiagnostic)) {
      return false;
    }
    ProtobufDiagnostic d = (ProtobufDiagnostic) error;
    if ("org.eclipse.xtext.diagnostics.Diagnostic.Linking".equals(d.getCode())) {
      return error.getMessage().startsWith("Couldn't resolve");
    }
    return false;
  }

  private Status createWarnings(Resource resource, IAcceptor<Issue> acceptor, CancelIndicator monitor) {
    for (Resource.Diagnostic warning : resource.getWarnings()) {
      if (monitor.isCanceled()) {
        return Status.CANCELED;
      }
      issueFromXtextResourceDiagnostic(warning, WARNING, acceptor);
    }
    return Status.OK;
  }

  private static enum Status {
    OK, CANCELED, PROTO1_IMPORTS_FOUND;

    boolean hasProto1Imports() {
      return (this == PROTO1_IMPORTS_FOUND);
    }

    boolean isCanceled() {
      return (this == CANCELED);
    }
  }
}