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

    /**
     * <p>Applies this schema rule to take the required code generation steps.</p>
     *
     * <p>When constructs of type "array" appear in the schema, these are mapped to
     * Java collections in the generated POJO. If the array is marked as having
     * "uniqueItems" then the resulting Java type is {@link Set}, if not, then
     * the resulting Java type is {@link List}. The schema given by "items" will
     * decide the generic type of the collection.</p>
     *
     * <p>If the "items" property requires newly generated types, then the type
     * name will be the singular version of the nodeName (unless overridden by
     * the javaType property) e.g.
     * <pre>
     *  "fooBars" : {"type":"array", "uniqueItems":"true", "items":{type:"object"}}
     *  ==&gt;
     *  {@code Set<FooBar> getFooBars(); }
     * </pre>
     * </p>
     *
     * @param nodeName
     *            the name of the property which has type "array"
     * @param node
     *            the schema "type" node
     * @param parent
     *            the parent node
     * @param jpackage
     *            the package into which newly generated types should be added
     * @return the Java type associated with this array rule, either {@link Set}
     *         or {@link List}, narrowed by the "items" type
     */
    @Override
    public JClass apply(String nodeName, JsonNode node, JsonNode parent, JPackage jpackage, Schema schema) {

        boolean uniqueItems = node.has("uniqueItems") && node.get("uniqueItems").asBoolean();
        boolean rootSchemaIsArray = !schema.isGenerated();

        JType itemType;
        if (node.has("items")) {
            String pathToItems;
            if (schema.getId() == null || schema.getId().getFragment() == null) {
                pathToItems = "#/items";
            } else {
                pathToItems = "#" + schema.getId().getFragment() + "/items";
            }
            Schema itemsSchema = ruleFactory.getSchemaStore().create(schema, pathToItems, ruleFactory.getGenerationConfig().getRefFragmentPathDelimiters());
            if (itemsSchema.isGenerated()) {
                itemType = itemsSchema.getJavaType();
            } else {
                itemType = ruleFactory.getSchemaRule().apply(makeSingular(nodeName), node.get("items"), node, jpackage, itemsSchema);
                itemsSchema.setJavaTypeIfEmpty(itemType);
            }
        } else {
            itemType = jpackage.owner().ref(Object.class);
        }

        JClass arrayType;
        if (uniqueItems) {
            arrayType = jpackage.owner().ref(Set.class).narrow(itemType);
        } else {
            arrayType = jpackage.owner().ref(List.class).narrow(itemType);
        }

        if (rootSchemaIsArray) {
            schema.setJavaType(arrayType);
        }

        return arrayType;
    }

    private String makeSingular(String nodeName) {
        return nodeName;
        //return Inflector.getInstance().singularize(nodeName);
    }
}
