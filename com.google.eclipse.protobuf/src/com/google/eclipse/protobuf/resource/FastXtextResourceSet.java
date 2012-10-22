package com.google.eclipse.protobuf.resource;

import static org.eclipse.xtext.resource.ClasspathUriUtil.isClasspathUri;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.xtext.resource.*;

import java.io.*;
import java.util.Map;

public class FastXtextResourceSet extends SynchronizedXtextResourceSet {
  @Override public URIConverter getURIConverter() {
    if (uriConverter == null) {
      uriConverter = new ExtensibleURIConverterImpl() {
        @Override public URI normalize(URI uri) {
          if (isClasspathUri(uri)) {
            URI result = resolveClasspathURI(uri);
            if (isClasspathUri(result)) {
              throw new ClasspathUriResolutionException(result);
            }
            result = super.normalize(result);
            return result;
          }
          return super.normalize(uri);
        }

        @Override public InputStream createInputStream(URI uri) throws IOException {
          InputStream in = doCreateInputStream(uri);
          return in != null ? in : super.createInputStream(uri);
        }

        @Override public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
          InputStream in = doCreateInputStream(uri);
          return in != null ? in : super.createInputStream(uri, options);
        }

        private InputStream doCreateInputStream(URI uri) throws IOException {
          if (isClasspathUri(uri)) {
            return getClass().getResourceAsStream(uri.path());
          }
          if ("proto".equals(uri.fileExtension())) {
            if (uri.isPlatformResource()) {
              return getClass().getResourceAsStream(uri.path());
            }
            if (uri.isFile()) {
              return new FileInputStream(uri.path());
            }
          }
          return null;
        }
      };
    }
    return uriConverter;
  }

  private URI resolveClasspathURI(URI uri) {
    return getClasspathUriResolver().resolve(getClasspathURIContext(), uri);
  }
}
