package com.github.libgraviton.gdk.generator.instructionloader.swagger;

import com.github.libgraviton.gdk.api.endpoint.Endpoint;
import com.github.libgraviton.gdk.generator.GeneratorInstruction;
import com.github.libgraviton.gdk.generator.GeneratorInstructionLoader;
import io.swagger.models.*;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;
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

    @Override
    public List<GeneratorInstruction> loadInstructions() {
        Map<String, GeneratorInstruction> instructions = new HashMap<>();

        for ( Map.Entry<String, Path> entry: swagger.getPaths().entrySet()) {
            Path path = entry.getValue();
            Model model = extractModel(path);
            if (null != model) {
                instructions.put(model.getTitle(), new GeneratorInstruction(
                        model.getTitle(),
                        "",
                        new JSONObject(model),
                        new Endpoint(entry.getKey())
                ));
            }
        }

        for (Map.Entry<String, Model> modelEntry : swagger.getDefinitions().entrySet()) {
            Model model = modelEntry.getValue();
            String className = null == model.getTitle() ? modelEntry.getKey() : model.getTitle();
            instructions.put(className, new GeneratorInstruction(
                    className,
                    "",
                    new JSONObject(model),
                    new Endpoint(null)
            ));
        }

        return new ArrayList<>(instructions.values());
    }

    @Override
    public List<GeneratorInstruction> loadInstructions(boolean reload) {
        return null;
    }

    private Model extractModel(Path path) {
        Model model = null;
        if (null != path.getGet()) {
            model = extractModel(path.getGet());
        }
        if (null == model && null != path.getPut()) {
            model = extractModel(path.getPut());
        }
        if (null == model && null != path.getPost()) {
            model = extractModel(path.getPost());
        }
        return model;
    }

    private Model extractModel(Operation operation) {
        for (Map.Entry<String, Response> entry : operation.getResponses().entrySet()) {
            String statusCode = entry.getKey();
            Response response = entry.getValue();
            if ('2' == statusCode.charAt(0)) {
                if (null != response.getSchema()) {
                    return extractModel(response.getSchema());
                }
            }
        }
        return null;
    }

    private Model extractModel(Property property) {
        if (property instanceof RefProperty) {
            RefProperty refProperty = (RefProperty) property;
            String definitionId = refProperty.getSimpleRef();
            Model model =  swagger.getDefinitions().get(definitionId);
            if (null != model && null == model.getTitle()) {
                model.setTitle(definitionId);
            }
            return model;
        }
        return null;
    }
}
