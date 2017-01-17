package org.jsonschema2pojo.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.Schema;

import java.util.List;
import java.util.Set;

/**
 * Array rule that doesn't change the nodeName.
 */
public class NonSingularArrayRule implements Rule<JPackage, JClass> {

    private final RuleFactory ruleFactory;

    protected NonSingularArrayRule(RuleFactory ruleFactory) {
        this.ruleFactory = ruleFactory;
    }

    public JClass apply(String nodeName, JsonNode node, JPackage jpackage, Schema schema) {
        boolean uniqueItems = node.has("uniqueItems") && node.get("uniqueItems").asBoolean();
        boolean rootSchemaIsArray = !schema.isGenerated();
        Object itemType = determineItemType(nodeName, node, jpackage, schema);

        JClass arrayType = determineArrayType(jpackage, uniqueItems, (JType) itemType);

        if(rootSchemaIsArray) {
            schema.setJavaType(arrayType);
        }

        return arrayType;
    }

    protected JClass determineArrayType(JPackage jpackage, boolean uniqueItems, JType itemType) {
        JClass arrayType;
        if(uniqueItems) {
            arrayType = jpackage.owner().ref(Set.class).narrow(itemType);
        } else {
            arrayType = jpackage.owner().ref(List.class).narrow(itemType);
        }
        return arrayType;
    }

    protected Object determineItemType(String nodeName, JsonNode node, JPackage jpackage, Schema schema) {
        Object itemType;
        if(node.has("items")) {
            itemType = this.ruleFactory.getSchemaRule().apply(nodeName, node.get("items"), jpackage, schema);
        } else {
            itemType = jpackage.owner().ref(Object.class);
        }
        return itemType;
    }
}
