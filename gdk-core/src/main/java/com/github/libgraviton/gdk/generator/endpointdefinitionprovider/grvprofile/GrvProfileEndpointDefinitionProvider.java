package com.github.libgraviton.gdk.generator.endpointdefinitionprovider.grvprofile;

import com.github.libgraviton.gdk.Graviton;
import com.github.libgraviton.gdk.generator.EndpointDefinition;
import com.github.libgraviton.gdk.generator.EndpointDefinitionProvider;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class GrvProfileEndpointDefinitionProvider implements EndpointDefinitionProvider{

    private Graviton graviton;

    public GrvProfileEndpointDefinitionProvider(Graviton graviton) {
        this.graviton = graviton;
    }

    public Graviton getGraviton() {
        return graviton;
    }

    public List<EndpointDefinition> getEndpointDefinitions() {
        List<EndpointDefinition> definitions = new ArrayList<EndpointDefinition>();

        for (ServiceDefinition serviceDefinition : graviton.getServiceList().getServices()) {
            String profileJson = graviton.get(serviceDefinition.getProfile());
            JSONObject jsonObject = new JSONObject(profileJson);
            definitions.add(new EndpointDefinition(
                    determineClassName(jsonObject),
                    determinePackageName(jsonObject),
                    profileJson,
                    formatEndpointUrl(serviceDefinition.get$ref(), jsonObject)
            ));
        }
        return definitions;
    }

    private String determineClassName(JSONObject jsonObject) {
        if (jsonObject.has("items")) {
            jsonObject = jsonObject.getJSONObject("items");
        }
        if (!jsonObject.has("x-documentClass")) {
            return "";
        }
        String className = jsonObject.getString("x-documentClass");
        return className.substring(className.lastIndexOf('\\') + 1);
    }

    private String determinePackageName(JSONObject jsonObject) {
        if (jsonObject.has("items")) {
            jsonObject = jsonObject.getJSONObject("items");
        }
        if (!jsonObject.has("x-documentClass")) {
            return "";
        }
        String packageName = jsonObject.getString("x-documentClass");
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

    private String formatEndpointUrl(String url, JSONObject jsonObject) {
        if (jsonObject.has("items")) {
            url += "{id}";
        }
        return url;
    }
}
