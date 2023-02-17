package com.github.libgraviton.gdk.generator.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.PropertiesRule;
import org.jsonschema2pojo.rules.RuleFactory;

public class GravitonPropertiesRule extends PropertiesRule {

    private final RuleFactory ruleFactory;
    protected GravitonPropertiesRule(RuleFactory ruleFactory) {
        super(ruleFactory);
        this.ruleFactory = ruleFactory;
    }

    @Override
    public JDefinedClass apply(String nodeName, JsonNode node, JsonNode parent, JDefinedClass jclass, Schema schema) {
        if (ruleFactory.getNameHelper() instanceof GravitonNameHelper) {
            ((GravitonNameHelper) ruleFactory.getNameHelper()).pushToNodeContext(nodeName);
        }

        JDefinedClass returnValue = super.apply(nodeName, node, parent, jclass, schema);

        if (ruleFactory.getNameHelper() instanceof GravitonNameHelper) {
            ((GravitonNameHelper) ruleFactory.getNameHelper()).popFromNodeContext();
        }

        return returnValue;
    }
}
