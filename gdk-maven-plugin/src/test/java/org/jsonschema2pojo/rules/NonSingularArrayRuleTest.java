package org.jsonschema2pojo.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.Schema;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NonSingularArrayRuleTest {

    NonSingularArrayRule rule;

    SchemaRule schemaRule;

    @Before
    public void setup() {
        JType jType = mock(JType.class);
        schemaRule = mock(SchemaRule.class);
        when(schemaRule.apply(any(String.class), any(JsonNode.class), any(JClassContainer.class), any(Schema.class)))
                .thenReturn(jType);
        RuleFactory factory = mock(RuleFactory.class);
        when(factory.getSchemaRule()).thenReturn(schemaRule);

        rule = new NonSingularArrayRule(factory);
    }

    @Test
    public void testAppliedNodeName() {
        String nodeName = "EventStatus";
        JsonNode node = mock(JsonNode.class);
        when(node.has("items")).thenReturn(true);
        Schema schema = mock(Schema.class);

        //rule.apply(nodeName, node, null, schema);
        rule.determineItemType(nodeName, node, null, schema);

        verify(schemaRule, times(1))
                .apply(eq(nodeName), ArgumentMatchers.<JsonNode>isNull(), ArgumentMatchers.<JClassContainer>isNull(), eq(schema));
    }
}
