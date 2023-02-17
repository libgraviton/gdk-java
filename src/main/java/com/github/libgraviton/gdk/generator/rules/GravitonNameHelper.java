package com.github.libgraviton.gdk.generator.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JPackage;
import org.apache.commons.lang3.StringUtils;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.util.NameHelper;

import java.util.Stack;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class GravitonNameHelper extends NameHelper {

    private final Stack<String> nodeContext = new Stack<>();
    private final GenerationConfig generationConfig;

    public GravitonNameHelper(GenerationConfig generationConfig) {
        super(generationConfig);
        this.generationConfig = generationConfig;
    }

    /**
     * Generates a class name prefix based on the current nodeContext
     *
     * @param prefix Any already defined class name prefix
     *
     * @return The class name prefix
     */
    public String getContextualClassPrefix(String prefix) {
        if (nodeContext.size() > 0) {
            prefix += StringUtils.join(nodeContext, "") + "";
        }
        return prefix;
    }

    /**
     * Generates a sub-package name based on the current nodeContext
     *
     * @return The sub-package name
     */
    public String getContextualSubPackage() {
        return nodeContext.size() > 0 ? nodeContext.get(nodeContext.size() - 1).toLowerCase() : "";
    }

    @Override
    public String getClassName(String nodeName, JsonNode node, JPackage _package) {
        String prefix = generationConfig.getClassNamePrefix();

        prefix = getContextualClassPrefix(prefix);

        String suffix = generationConfig.getClassNameSuffix();
        String fieldName = getClassName(nodeName, node);
        String capitalizedFieldName = capitalize(fieldName);
        String fullFieldName = createFullFieldName(capitalizedFieldName, prefix, suffix);

        String className = replaceIllegalCharacters(fullFieldName);
        return normalizeName(className);
    }

    private String createFullFieldName(String nodeName, String prefix, String suffix) {
        String returnString = nodeName;
        if (prefix != null) {
            returnString = prefix + returnString;
        }

        if (suffix != null) {
            returnString = returnString + suffix;
        }

        return returnString;
    }

    /**
     * Pushes a node to the nodeContext.
     *
     * @param nodeName The name of the node
     */
    public void pushToNodeContext(String nodeName) {
        nodeContext.push(capitalizeTrailingWords(nodeName));
    }

    /**
     * Gets and removes the lastly pushed node from the nodeContext.
     *
     * @return The name of the removed node
     */
    public String popFromNodeContext() {
        return nodeContext.pop();
    }
}
