package com.google.eclipse.protobuf.parser;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.Modifier;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parser.DefaultEcoreElementFactory;

public class ProtobufAstFactory extends DefaultEcoreElementFactory {
  private static final String UNMODIFIEDMESSAGEFIELD_RULE = "UnmodifiedMessageField";
  private static final String ONEOFELEMENT_RULE = "OneOfElement";

  @Override
  public void add(EObject object, String feature, Object value, String ruleName, INode node)
      throws ValueConverterException {
    // TODO(foremans): Auto-generated method stub
    super.add(object, feature, value, ruleName, node);
    if (ONEOFELEMENT_RULE.equals(ruleName) && value instanceof MessageField) {
      MessageField field = (MessageField) value;
      field.setModifier(Modifier.OPTIONAL);
    }
  }
  
  @Override
  public void set(EObject object, String feature, Object value, String ruleName, INode node)
      throws ValueConverterException {
    super.set(object, feature, value, ruleName, node);
    if (UNMODIFIEDMESSAGEFIELD_RULE.equals(ruleName)) {
      MessageField field = (MessageField) object;
      field.setModifier(Modifier.OPTIONAL);
    }
  }
}
