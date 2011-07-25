/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.formatting;

import org.eclipse.xtext.formatting.impl.*;

import com.google.eclipse.protobuf.services.ProtobufGrammarAccess;

/**
 * This class contains custom formatting description.
 *
 * @see <a href="http://www.eclipse.org/Xtext/documentation/2_0_0/105-formatting.php">Xtext Formatting</a>
 */
public class ProtobufFormatter extends AbstractDeclarativeFormatter {

	@Override
	protected void configureFormatting(FormattingConfig c) {
	  ProtobufGrammarAccess g = (ProtobufGrammarAccess) getGrammarAccess();
	  c.setLinewrap(0, 1, 2).before(g.getSL_COMMENTRule());
	}
}
