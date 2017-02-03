package org.jsonschema2pojo.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.NoopAnnotator;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.SchemaStore;
import org.junit.Test;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NonSingularArrayRuleTest {

    private GenerationConfig config = mock(GenerationConfig.class);

    private RuleFactory ruleFactory = spy(new RuleFactory(config, new NoopAnnotator(), new SchemaStore()));

    private NonSingularArrayRule rule = new NonSingularArrayRule(ruleFactory);

    @Test
    public void testAppliedNodeName() {
        String nodeName = "EventStatus";

        JCodeModel codeModel = new JCodeModel();
        JPackage jpackage = codeModel._package(getClass().getPackage().getName());

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode itemsNode = mapper.createObjectNode();
        itemsNode.put("type", "boolean");

        ObjectNode propertyNode = mapper.createObjectNode();
        propertyNode.put("uniqueItems", false);
        propertyNode.put("items", itemsNode);

        Schema schema = mock(Schema.class);

        doReturn(spy(new SchemaRule(ruleFactory))).when(ruleFactory).getSchemaRule();

        rule.apply(nodeName, propertyNode, jpackage, schema);

        verify(ruleFactory.getSchemaRule(), times(1))
                .apply(eq(nodeName), any(JsonNode.class), eq(jpackage), eq(schema));
    }

    @Test
    public void arrayWithUniqueItemsProducesSet() {
        JCodeModel codeModel = new JCodeModel();
        JPackage jpackage = codeModel._package(getClass().getPackage().getName());

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode itemsNode = mapper.createObjectNode();
        itemsNode.put("type", "integer");

        ObjectNode propertyNode = mapper.createObjectNode();
        propertyNode.put("uniqueItems", true);
        propertyNode.put("items", itemsNode);

        JClass propertyType = rule.apply("fooBars", propertyNode, jpackage, mock(Schema.class));

        assertThat(propertyType, notNullValue());
        assertThat(propertyType.erasure(), is(codeModel.ref(Set.class)));
        assertThat(propertyType.getTypeParameters().get(0).fullName(), is(Integer.class.getName()));
    }

    @Test
    public void arrayWithNonUniqueItemsProducesList() {
        JCodeModel codeModel = new JCodeModel();
        JPackage jpackage = codeModel._package(getClass().getPackage().getName());

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode itemsNode = mapper.createObjectNode();
        itemsNode.put("type", "number");

        ObjectNode propertyNode = mapper.createObjectNode();
        propertyNode.put("uniqueItems", false);
        propertyNode.put("items", itemsNode);

        Schema schema = mock(Schema.class);
        when(schema.getId()).thenReturn(URI.create("http://example/nonUniqueArray"));
        when(config.isUseDoubleNumbers()).thenReturn(true);

        JClass propertyType = rule.apply("fooBars", propertyNode, jpackage, schema);

        assertThat(propertyType, notNullValue());
        assertThat(propertyType.erasure(), is(codeModel.ref(List.class)));
        assertThat(propertyType.getTypeParameters().get(0).fullName(), is(Double.class.getName()));
    }

    @Test
    public void arrayOfPrimitivesProducesCollectionOfWrapperTypes() {
        JCodeModel codeModel = new JCodeModel();
        JPackage jpackage = codeModel._package(getClass().getPackage().getName());

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode itemsNode = mapper.createObjectNode();
        itemsNode.put("type", "number");

        ObjectNode propertyNode = mapper.createObjectNode();
        propertyNode.put("uniqueItems", false);
        propertyNode.put("items", itemsNode);

        Schema schema = mock(Schema.class);
        when(schema.getId()).thenReturn(URI.create("http://example/nonUniqueArray"));
        when(config.isUsePrimitives()).thenReturn(true);
        when(config.isUseDoubleNumbers()).thenReturn(true);

        JClass propertyType = rule.apply("fooBars", propertyNode, jpackage, schema);

        assertThat(propertyType, notNullValue());
        assertThat(propertyType.erasure(), is(codeModel.ref(List.class)));
        assertThat(propertyType.getTypeParameters().get(0).fullName(), is(Double.class.getName()));
    }

    @Test
    public void arrayDefaultsToNonUnique() {
        JCodeModel codeModel = new JCodeModel();
        JPackage jpackage = codeModel._package(getClass().getPackage().getName());

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode itemsNode = mapper.createObjectNode();
        itemsNode.put("type", "boolean");

        ObjectNode propertyNode = mapper.createObjectNode();
        propertyNode.put("uniqueItems", false);
        propertyNode.put("items", itemsNode);

        Schema schema = mock(Schema.class);
        when(schema.getId()).thenReturn(URI.create("http://example/defaultArray"));

        JClass propertyType = rule.apply("fooBars", propertyNode, jpackage, schema);

        assertThat(propertyType.erasure(), is(codeModel.ref(List.class)));
    }
}
