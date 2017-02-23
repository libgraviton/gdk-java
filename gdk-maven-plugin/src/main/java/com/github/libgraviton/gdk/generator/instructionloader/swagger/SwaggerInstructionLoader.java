package com.github.libgraviton.gdk.generator.instructionloader.swagger;

import com.github.libgraviton.gdk.api.endpoint.Endpoint;
import com.github.libgraviton.gdk.generator.GeneratorInstruction;
import com.github.libgraviton.gdk.generator.GeneratorInstructionLoader;
import io.swagger.models.*;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwaggerInstructionLoader implements GeneratorInstructionLoader {

    public static final String ID = "swagger";

    private SwaggerParser parser = new SwaggerParser();

    private Swagger swagger;

    public SwaggerInstructionLoader(String swaggerLocation) {
        swagger = parser.read(swaggerLocation);
    }

    private HashMap<String, JSONObject> types = new HashMap<>();

    @Override
    public List<GeneratorInstruction> loadInstructions() {
        Map<String, GeneratorInstruction> instructions = new HashMap<>();

        for (Map.Entry<String, Model> modelEntry : swagger.getDefinitions().entrySet()) {
            Model model = modelEntry.getValue();
            String name = modelEntry.getKey();
            String className = null == model.getTitle() ? name : model.getTitle();
            JSONObject schema = new JSONObject(model);
            schema.put("javaType", toClassName(className));
            types.put(toClassName(name), schema);
        }

        for (Map.Entry<String, JSONObject> schemaEntry : types.entrySet()) {
            enrichSchema(schemaEntry.getValue());
        }

        for (Map.Entry<String, Model> modelEntry : swagger.getDefinitions().entrySet()) {
            Model model = modelEntry.getValue();
            String className = null == model.getTitle() ? modelEntry.getKey() : model.getTitle();
            JSONObject schema = new JSONObject(model);
            enrichSchema(schema);
            instructions.put(className, new GeneratorInstruction(
                    className,
                    "",
                    schema,
                    new Endpoint(null)
            ));
        }

        for ( Map.Entry<String, Path> entry: swagger.getPaths().entrySet()) {
            Path path = entry.getValue();
            JSONObject schema = determineSchema(path);
            enrichSchema(schema);
            if (null != schema) {
                instructions.put(schema.getString("javaType"), new GeneratorInstruction(
                        schema.getString("javaType"),
                        "",
                        schema,
                        new Endpoint(entry.getKey())
                ));
            }
        }

        return new ArrayList<>(instructions.values());
    }

    @Override
    public List<GeneratorInstruction> loadInstructions(boolean reload) {
        return null;
    }

    private JSONObject determineSchema(Path path) {
        JSONObject schema = null;
        if (null != path.getGet()) {
            schema = determineSchema(path.getGet());
        }
        if (null == schema && null != path.getPut()) {
            schema = determineSchema(path.getPut());
        }
        if (null == schema && null != path.getPost()) {
            schema = determineSchema(path.getPost());
        }
        return schema;
    }

    private JSONObject determineSchema(Operation operation) {
        for (Map.Entry<String, Response> entry : operation.getResponses().entrySet()) {
            String statusCode = entry.getKey();
            Response response = entry.getValue();
            if ('2' == statusCode.charAt(0)) {
                if (null != response.getSchema()) {
                    return determineSchema(response.getSchema());
                }
            }
        }
        return null;
    }

    private JSONObject determineSchema(Property property) {
        if (property instanceof RefProperty) {
            RefProperty refProperty = (RefProperty) property;
            String definitionId = refProperty.getSimpleRef();
            if (types.containsKey(toClassName(definitionId))) {
                return types.get(toClassName(definitionId));
            }
            Model model =  swagger.getDefinitions().get(definitionId);
            if (null != model && null == model.getTitle()) {
                model.setTitle(definitionId);
            }
            return new JSONObject(model);
        }
        return null;
    }

    private void enrichSchema(JSONObject json) {
        if (json.has("simpleRef")) {
            json.put("javaType", types.get(toClassName(json.getString("simpleRef"))).getString("javaType"));
        } else if (json.has("properties") && json.has("type") && "object".equals(json.get("type"))) {
            JSONObject properties = json.getJSONObject("properties");
            for (String property : properties.keySet()) {
                enrichSchema(properties.getJSONObject(property));
            }
        }
    }

    private String toClassName(String name) {
        return StringUtils.capitalize(name);
    }
}
