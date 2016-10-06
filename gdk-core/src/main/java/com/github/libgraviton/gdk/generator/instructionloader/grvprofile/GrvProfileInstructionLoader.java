package com.github.libgraviton.gdk.generator.instructionloader.grvprofile;

import com.github.libgraviton.gdk.Service;
import com.github.libgraviton.gdk.Graviton;
import com.github.libgraviton.gdk.generator.GeneratorInstruction;
import com.github.libgraviton.gdk.generator.GeneratorInstructionLoader;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Instruction loader providing generator instructions based on Graviton's main page / service overview.
 */
public class GrvProfileInstructionLoader implements GeneratorInstructionLoader {

    /**
     * The Graviton instance where the service definitions will be loaded from.
     */
    private Graviton graviton;

    /**
     * Holds all loaded generator instructions.
     */
    private List<GeneratorInstruction> loadedInstructions;

    /**
     * Constructor. Sets the Graviton instance which will be used.
     *
     * @param graviton The Graviton instance which will be used.
     */
    public GrvProfileInstructionLoader(Graviton graviton) {
        this.graviton = graviton;
    }

    /**
     * Loads the generator instructions from Graviton's main page.
     *
     * @return All generator instructions.
     */
    public List<GeneratorInstruction> loadInstructions() {
        return loadInstructions(false);
    }

    /**
     * Loads generator instructions according to Graviton's main page.
     *
     * @param reload If the instructions are already loaded and this param is set to false, a cached instruction set
     *               should be returned. Otherwise the instruction list should be (re-) loaded.
     *
     * @return All generator instructions.
     */
    public List<GeneratorInstruction> loadInstructions(boolean reload) {
        if (reload || null == this.loadedInstructions) {
            loadedInstructions = new ArrayList<>();
            for (ServiceDefinition serviceDefinition : loadServiceList().getServices()) {
                String profileJson = graviton.get(serviceDefinition.getProfile());
                JSONObject itemSchema = determineItemSchema(profileJson);
                loadedInstructions.add(new GeneratorInstruction(
                        determineClassName(itemSchema),
                        determinePackageName(itemSchema),
                        itemSchema,
                        generateService(serviceDefinition)
                ));
            }
        }
        return loadedInstructions;
    }

    /**
     * Loads the service list from Graviton.
     *
     * @return The service list.
     */
    private ServiceList loadServiceList() {
        return graviton.get(graviton.getBaseUrl(), ServiceList.class);
    }

    /**
     * Determines the classname for a given Graviton schema.
     *
     * @param itemSchema The Graviton item schema.
     *
     * @return The determined class name. May be empty if no class name could be determined.
     */
    private String determineClassName(JSONObject itemSchema) {
        if (!itemSchema.has("x-documentClass")) {
            return "";
        }
        String className = itemSchema.getString("x-documentClass");
        return className.substring(className.lastIndexOf('\\') + 1);
    }

    /**
     * Determines the item schema of a given Graviton schema.
     *
     * @param schema The Graviton schema, which can already be an item schema but also a collection schema.
     *
     * @return The item schema
     */
    private JSONObject determineItemSchema(String schema) {
        JSONObject schemaObject = new JSONObject(schema);
        // If the schema contains an "items" field, we got an array and therefore a collection schema.
        if (schemaObject.has("items")) {
            schemaObject = schemaObject.getJSONObject("items");
        }
        return schemaObject;
    }

    /**
     * Determines the package name for generated classes for a given Graviton schema.
     *
     * @param itemSchema The Graviton item schema.
     *
     * @return The determined package name. May be empty if no package name could be determined.
     */
    private String determinePackageName(JSONObject itemSchema) {
        if (!itemSchema.has("x-documentClass")) {
            return "";
        }
        String packageName = itemSchema.getString("x-documentClass");
        try {
            packageName = packageName.substring(0, packageName.lastIndexOf('\\'));

        } catch (Exception e) {
            return "";
        }
        packageName = packageName.replaceAll("Bundle", "");
        packageName = packageName.replaceAll("\\\\", ".");
        packageName = packageName.toLowerCase();
        return packageName;
    }

    /**
     * Generates the service by a given serviceDefinition and instruction schema.
     *
     * @param serviceDefinition The service definition.
     *
     * @return The generated service.
     */
    private Service generateService(ServiceDefinition serviceDefinition) {
        String url = serviceDefinition.get$ref();
        if (url.length() > 0 && '/' == url.charAt(url.length() - 1)) {
            return new Service(url + "{id}", url);
        }
        return new Service(url);
    }
}
