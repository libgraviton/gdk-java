package com.github.libgraviton.gdk.generator.instructionloader.swagger;

import com.github.libgraviton.gdk.api.endpoint.Endpoint;
import com.github.libgraviton.gdk.generator.GeneratorInstruction;
import com.github.libgraviton.gdk.generator.GeneratorInstructionLoader;
import io.swagger.models.*;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;
import org.json.JSONObject;

import java.util.ArrayList;
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
        Map<String, Model> definitions = swagger.getDefinitions();
        List<GeneratorInstruction> instructions = new ArrayList<>();
        for (Map.Entry<String, Path> entry: swagger.getPaths().entrySet()) {
            Path path = entry.getValue();
            Model model = extractModel(path.getParameters());

            if (null == model) {
                continue;
            }

            JSONObject schema;
            if (model instanceof RefModel) {
                schema = new JSONObject();
                schema.put("$ref", ((RefModel) model).get$ref());
            } else {
                schema = new JSONObject(model);
            }
            schema.put("definitions", definitions);
            instructions.add(new GeneratorInstruction(
                    null == model.getTitle() ? ,
                    "",
                    schema,
                    new Endpoint(entry.getKey())
            ));
        }

        return instructions;
    }

    @Override
    public List<GeneratorInstruction> loadInstructions(boolean reload) {
        return null;
    }

    private Model extractModel(Map<String, Response> responses) {
        for (Map.Entry<String, Response> entry : responses.entrySet()) {
            String statusCode = entry.getKey();
            Response response = entry.getValue();
            if ('2' == statusCode.charAt(0) && null != response && null != response.getSchema()) {
                return extractModel(response.getSchema());
            }
        }
        return null;
    }

    private Model extractModel(List<Parameter> parameters) {
        for (Parameter parameter : parameters) {
            if (parameter instanceof BodyParameter) {
                return ((BodyParameter) parameter).getSchema();
            } else if (parameter instanceof FormParameter) {
                // @todo: support form parameters & file uploads
            }
        }
        return null;
    }

    private Model extractModel(Property property) {
        property = property instanceof ArrayProperty ? ((ArrayProperty) property).getItems() : property;

        if (property instanceof RefProperty) {
            return new RefModel(((RefProperty) property).getSimpleRef());
        } else if (property instanceof MapProperty) {
            // todo: support non-ref properties.
        }
        return null;
    }

}
