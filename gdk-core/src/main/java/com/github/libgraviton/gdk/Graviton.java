package com.github.libgraviton.gdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.generator.EndpointMapper;
import com.github.libgraviton.gdk.generator.endpointdefinitionprovider.grvprofile.ServiceList;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;

public class Graviton {

    private String baseUrl;

    private ObjectMapper objectMapper = new ObjectMapper();

    private EndpointMapper endpointMapper;

    public Graviton(String baseUrl, EndpointMapper endpointMapper) {
        this.baseUrl = baseUrl;
        this.endpointMapper = endpointMapper;
    }

    public EndpointMapper getEndpointMapper() {
        return endpointMapper;
    }

    public ServiceList getServiceList() {
        return get(baseUrl, ServiceList.class);
    }

    public String get(String url) {
        try {
            return Unirest.get(url).asString().getBody();
        } catch (UnirestException e) {
            // @todo
            return null;
        }
    }

    public void post(Object data) {
        //String endpoint = endpointMapper.getEndpoint(data.getClass().getName());
    }

    public void post(String url, Object data) {
        //objectMapper.writeValueAsString(data);
    }

    public <BeanClass> BeanClass get(String url, Class<? extends BeanClass> beanClass) {
        try {
            return objectMapper.readValue(get(url), beanClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
