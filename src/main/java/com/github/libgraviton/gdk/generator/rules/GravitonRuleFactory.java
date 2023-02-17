package com.github.libgraviton.gdk.generator.rules;

import com.sun.codemodel.*;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.util.NameHelper;
import org.jsonschema2pojo.util.ParcelableHelper;

/**
 * Extended rule factory to match Graviton needs.
 */
public class GravitonRuleFactory extends RuleFactory {

    private GravitonNameHelper nameHelper;

    /**
    @Override
    public Rule<JPackage, JClass> getArrayRule() {
        return new NonSingularArrayRule(this);
    }
     **/

    @Override
    public Rule<JPackage, JType> getObjectRule() {
        return new GravitonObjectRule(this, new ParcelableHelper(), getReflectionHelper());
    }

    @Override
    public Rule<JDefinedClass, JDefinedClass> getPropertiesRule() {
        return new GravitonPropertiesRule(this);
    }

    @Override
    public Rule<JDocCommentable, JDocComment> getTitleRule() {
        return new FilteredTitleRule();
    }

    @Override
    public Rule<JDocCommentable, JDocComment> getDescriptionRule() {
        return new FilteredDescriptionRule();
    }

    @Override
    public NameHelper getNameHelper() {
        if (nameHelper == null) {
            nameHelper = new GravitonNameHelper(getGenerationConfig());
        }
        return nameHelper;
    }

    @Override
    public SchemaStore getSchemaStore() {
        // the stuff cached collides with the name (different part of schemas collide with other schemas from other objects). so let's not use it.
        super.getSchemaStore().clearCache();
        return super.getSchemaStore();
    }

}
