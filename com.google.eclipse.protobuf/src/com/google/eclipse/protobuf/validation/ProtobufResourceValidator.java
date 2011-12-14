/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.eclipse.protobuf.validation.Messages.*;
import static org.eclipse.xtext.diagnostics.Severity.*;
import static org.eclipse.xtext.validation.AbstractInjectableValidator.CURRENT_LANGUAGE_NAME;
import static org.eclipse.xtext.validation.CancelableDiagnostician.CANCEL_INDICATOR;
import static org.eclipse.xtext.validation.CheckMode.KEY;
import static org.eclipse.xtext.validation.CheckType.FAST;
import static org.eclipse.xtext.validation.impl.ConcreteSyntaxEValidator.DISABLE_CONCRETE_SYNTAX_EVALIDATOR;

import java.util.*;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.*;
import org.eclipse.xtext.validation.*;

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
    if (monitor.isCanceled()) return null;
    List<Issue> result = newArrayListWithExpectedSize(resource.getErrors().size() + resource.getWarnings().size());
    try {
      IAcceptor<Issue> acceptor = createAcceptor(result);
      boolean hasNonProto2Import = false;
      for (EObject element : resource.getContents()) {
        try {
          if (monitor.isCanceled()) return null;
          Diagnostic diagnostic = getDiagnostician().validate(element, validationOptions(resource, mode, monitor));
          if (!diagnostic.getChildren().isEmpty()) {
            for (Diagnostic child : diagnostic.getChildren()) {
              if (importingNonProto2.equals(child.getMessage())) hasNonProto2Import = true;
              issueFromEValidatorDiagnostic(child, acceptor);
            }
          } else {
            issueFromEValidatorDiagnostic(diagnostic, acceptor);
          }
        } catch (RuntimeException e) {
          log.error(e.getMessage(), e);
        }
      }
      if (mode.shouldCheck(FAST)) {
        for (Resource.Diagnostic error : resource.getErrors()) {
          if (monitor.isCanceled()) return null;
          Severity severity = ERROR;
          if (hasNonProto2Import && isUnresolveReferenceError(error)) {
            severity = WARNING;
            ProtobufDiagnostic d = (ProtobufDiagnostic) error;
            if (!d.getMessage().endsWith(scopingError)) {
              if (!d.getMessage().endsWith(".")) d.appendToMessage(".");
              d.appendToMessage(" ");
              d.appendToMessage(scopingError);
            }
          }
          issueFromXtextResourceDiagnostic(error, severity, acceptor);
        }
        for (Resource.Diagnostic warning : resource.getWarnings()) {
          if (monitor.isCanceled()) return null;
          issueFromXtextResourceDiagnostic(warning, WARNING, acceptor);
        }
      }
    } catch (RuntimeException e) {
      log.error(e.getMessage(), e);
    }
    return result;
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

  private boolean isUnresolveReferenceError(Resource.Diagnostic error) {
    if (!(error instanceof ProtobufDiagnostic)) return false;
    ProtobufDiagnostic d = (ProtobufDiagnostic) error;
    if (!"org.eclipse.xtext.diagnostics.Diagnostic.Linking".equals(d.getCode())) return false;
    return error.getMessage().startsWith("Couldn't resolve");
  }
}