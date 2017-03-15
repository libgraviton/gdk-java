package com.github.libgraviton.gdk.serialization.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * Created by taawemi6 on 14.03.17.
 */
public class RqlObjectMapper extends ObjectMapper {

    public RqlObjectMapper(Properties properties) {
        super();

        SimpleDateFormat dateFormat = new SimpleDateFormat(properties.getProperty("graviton.rql.date.format"));
        setDateFormat(dateFormat);
        setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}