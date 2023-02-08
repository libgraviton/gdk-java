package org.jsonschema2pojo.rules;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GravitonRuleFactoryTest {

    private GravitonRuleFactory ruleFactory;

    @Before
    public void setup() {
        ruleFactory = new GravitonRuleFactory();
    }

    @Test
    public void testGetArrayRule() {
        assertTrue(ruleFactory.getArrayRule() instanceof NonSingularArrayRule);
    }

    @Test
    public void testGetTitleRule() {
        assertTrue(ruleFactory.getTitleRule() instanceof FilteredTitleRule);
    }

    @Test
    public void testGetDescriptionRule() {
        assertTrue(ruleFactory.getDescriptionRule() instanceof FilteredDescriptionRule);
    }
}
