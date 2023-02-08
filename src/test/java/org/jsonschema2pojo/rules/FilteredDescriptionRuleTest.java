package org.jsonschema2pojo.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilteredDescriptionRuleTest {

    private static final String TARGET_CLASS_NAME = FilteredDescriptionRuleTest.class.getName() + ".DummyClass";

    @Test
    public void testApply() throws Exception {
        JDefinedClass jclass = new JCodeModel()._class(TARGET_CLASS_NAME);

        JsonNode node = mock(JsonNode.class);
        when(node.asText()).thenReturn("@todo a description");
        FilteredDescriptionRule rule = new FilteredDescriptionRule();
        JDocComment jDocComment = rule.apply(null, node, jclass, null);

        assertThat(jDocComment, sameInstance(jclass.javadoc()));
        assertThat(jDocComment.size(), is(1));
        assertThat((String) jDocComment.get(0), is("todo a description"));
    }
}
