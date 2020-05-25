package com.github.libgraviton.gdk.generator.instructionloader.grvprofile;

import com.github.libgraviton.gdk.GravitonApi;
import com.github.libgraviton.gdk.api.Response;
import com.github.libgraviton.gdk.api.endpoint.Endpoint;
import com.github.libgraviton.gdk.exception.CommunicationException;
import com.github.libgraviton.gdk.generator.GeneratorInstruction;
import com.github.libgraviton.gdk.generator.GeneratorInstructionLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Instruction loader providing generator instructions based on Graviton's main page / service overview.
 */
public class GrvProfileInstructionLoader implements GeneratorInstructionLoader {

    private final Logger LOG = LoggerFactory.getLogger(GrvProfileInstructionLoader.class);

    /**
     * The GravitonApi instance where the endpoint definitions will be loaded from.
     */
    private GravitonApi gravitonApi;

    /**
     * Holds all loaded generator instructions.
     */
    private List<GeneratorInstruction> loadedInstructions;

    /**
     * Constructor. Sets the GravitonApi instance which will be used.
     *
     * @param gravitonApi The GravitonApi instance which will be used.
     */
    public GrvProfileInstructionLoader(GravitonApi gravitonApi) {
        this.gravitonApi = gravitonApi;
    }

    /**
     * Loads the generator instructions from Graviton's main page.
     *
     * @return All generator instructions.
     */
    public List<GeneratorInstruction> loadInstructions() throws CommunicationException {
        return loadInstructions(false);
    }

    /**
     * Loads generator instructions according to Graviton's main page.
     *
     * @param reload If the instructions are already loaded and this addParam is set to false, a cached instruction set
     *               should be returned. Otherwise the instruction list should be (re-) loaded.
     *
     * @return All generator instructions.
     */
    public List<GeneratorInstruction> loadInstructions(boolean reload) throws CommunicationException {
        if (reload || null == this.loadedInstructions) {
            LOG.info("Loading endpoint definitions and schema from '" + gravitonApi.getBaseUrl() + "'.");
            loadedInstructions = new ArrayList<>();
            List<EndpointDefinition> endpointDefinitions;
            try {
                endpointDefinitions = loadService().getEndpointDefinitions();
            } catch (CommunicationException e) {
                LOG.error("Unable to load service. No instructions loaded.", e);
                throw e;
            }

            for (EndpointDefinition endpointDefinition : endpointDefinitions) {
                String profileJson = null;
                try {
                    Response response = gravitonApi.get(endpointDefinition.getProfile()).execute();
                    profileJson = response.getBody();
                } catch (CommunicationException e) {
                    LOG.warn("Unable to fetch profile from '" + endpointDefinition.getProfile() + "'. Skipping...");
                }
                JSONObject itemSchema = determineItemSchema(profileJson);
                try {
                    loadedInstructions.add(new GeneratorInstruction(
                            determineClassName(itemSchema),
                            determinePackageName(itemSchema),
                            enrichSchema(itemSchema),
                            generateEndpoint(endpointDefinition)
                    ));
                } catch (MalformedURLException e) {
                    LOG.warn("Skipping endpoint '" + endpointDefinition.getRef() + "' since it's a malformed Url.");
                }
            }
            LOG.info("Loaded " + loadedInstructions.size() + " endpoint definitions.");
        }
        return loadedInstructions;
    }

    /**
     * Every class that matches an endpoint, should by definition always implement the com.github.libgraviton.gdk.data.GravitonBase interface.
     * With that approach we know for sure, that all those classes implement the getId() method.
     * To achieve this, the following needs to be added to the root of the schema.
     *
     * <pre>
     *     "javaInterfaces" : ["com.github.libgraviton.gdk.data.GravitonBase"]
     * </pre>
     *
     * @param itemSchema schema to enrich
     * @return enriched schema
     */
    private JSONObject enrichSchema(JSONObject itemSchema) {
        JSONArray interfaces = new JSONArray();
        interfaces.put("com.github.libgraviton.gdk.data.GravitonBase");
        itemSchema.put("javaInterfaces", interfaces);
        return itemSchema;
    }

    /**
     * Loads the Graviton service.
     *
     * @return The Graviton service.
     */
    private Service loadService() throws CommunicationException {
        Response response = gravitonApi.get(gravitonApi.getBaseUrl()).execute();
        return response.getBodyItem(Service.class);
    }

    /**
     * Determines the classname for a given GravitonApi schema.
     *
     * @param itemSchema The GravitonApi item schema.
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
     * @param schema The Graviton endpoint schema, which can already be an item schema but also a collection schema.
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
     * Generates the endpoint by a given endpointDefinition and instruction schema.
     *
     * @param endpointDefinition The endpoint definition.
     *
     * @return The generated endpoint.
     */
    private Endpoint generateEndpoint(EndpointDefinition endpointDefinition) throws MalformedURLException {
        String url = endpointDefinition.getRef();
        String path = new URL(url).getPath();

        if (path.length() > 0 && '/' == path.charAt(path.length() - 1)) {
            return new Endpoint(path + "{id}", path);
        }
        return new Endpoint(path);
    }
}
