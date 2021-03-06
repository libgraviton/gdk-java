package com.github.libgraviton.gdk.serialization.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.libgraviton.gdk.data.SimpleClass;
import com.github.libgraviton.gdk.util.PropertiesLoader;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RqlObjectMapperTest {

    @Test
    public void testDateFormat() throws Exception {
        Properties properties = PropertiesLoader.load();
        RqlObjectMapper mapper = new RqlObjectMapper(properties);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(2001, 10, 20, 9, 8, 7);
        Date date = calendar.getTime();
        JsonNode jsonNode = mapper.valueToTree(date);
        assertEquals("2001-11-20T09:08:07Z", jsonNode.textValue());
    }

    @Test
    public void testIgnoreNullValues() throws Exception {
        Properties properties = PropertiesLoader.load();
        RqlObjectMapper mapper = new RqlObjectMapper(properties);

        SimpleClass simpleClass = new SimpleClass();
        simpleClass.setId("123");

        JsonNode node = mapper.valueToTree(simpleClass);
        assertEquals("123", node.get("id").textValue());
        assertFalse(node.has("name"));

        simpleClass.setName("aName");

        node = mapper.valueToTree(simpleClass);
        assertEquals("123", node.get("id").textValue());
        assertEquals("aName", node.get("name").textValue());
    }
}
