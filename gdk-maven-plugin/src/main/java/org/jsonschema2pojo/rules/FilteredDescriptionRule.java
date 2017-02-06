package org.jsonschema2pojo.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JDocCommentable;
import org.jsonschema2pojo.Schema;

/**
 * Same as DescriptionRule, but but removes all '@' characters to avoid the generation of corrupt javadoc.
 */
public class FilteredDescriptionRule extends DescriptionRule {

    /**
     * Create the javadoc from the title it takes from the schema.
     *
     * @param nodeName
     *            the name of the property to which this title applies
     * @param node
     *            the "title" schema node
     * @param generatableType
     *            comment-able code generation construct, usually a field or
     *            method, which should have this title applied
     * @return the JavaDoc comment created to contain the title
     */
    @Override
    public JDocComment apply(String nodeName, JsonNode node, JDocCommentable generatableType, Schema schema) {
        JDocComment javadoc = generatableType.javadoc();

        String text = node.asText();
        if (text != null) {
            javadoc.add(0, text.replaceAll("@", ""));
        }

        return javadoc;
    }

}
