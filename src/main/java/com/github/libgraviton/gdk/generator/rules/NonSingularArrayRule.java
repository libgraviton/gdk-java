package com.github.libgraviton.gdk.generator.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;

import java.util.List;
import java.util.Set;

/**
 * Array rule that doesn't change the nodeName but else behaves the same as ArrayRule.
 * Since the makeSingular method within ArrayRule is private, this class here is copy and own.
 */
public class NonSingularArrayRule implements Rule<JPackage, JClass> {

    private final RuleFactory ruleFactory;

    protected NonSingularArrayRule(RuleFactory ruleFactory) {
        this.ruleFactory = ruleFactory;
    }


    @Override
    public JClass apply(String nodeName, JsonNode node, JsonNode parent, JPackage generatableType, Schema currentSchema) {
        boolean uniqueItems = node.has("uniqueItems") && node.get("uniqueItems").asBoolean();
        boolean rootSchemaIsArray = !currentSchema.isGenerated();
        Object itemType = determineItemType(nodeName, node, generatableType, currentSchema);

        JClass arrayType = determineArrayType(generatableType, uniqueItems, (JType) itemType);

        if(rootSchemaIsArray) {
            currentSchema.setJavaType(arrayType);
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

            itemType = this.ruleFactory.getSchemaRule().apply(nodeName, node.get("items"), node, jpackage, schema);
        } else {
            itemType = jpackage.owner().ref(Object.class);
        }
        return itemType;
    }
}
