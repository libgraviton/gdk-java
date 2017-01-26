package org.jsonschema2pojo.rules;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JPackage;

/**
 * Extended rule factory to match Graviton needs.
 */
public class GravitonRuleFactory extends RuleFactory {

    @Override
    public Rule<JPackage, JClass> getArrayRule() {
        return new NonSingularArrayRule(this);
    }
}
