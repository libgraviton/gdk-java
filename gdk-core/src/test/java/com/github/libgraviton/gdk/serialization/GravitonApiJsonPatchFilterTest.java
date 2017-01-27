package com.github.libgraviton.gdk.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class GravitonApiJsonPatchFilterTest {

    private GravitonJsonPatchFilter filter;

    private ObjectMapper mapper;

    @Before
    public void setup() {
        filter = new GravitonJsonPatchFilter();
        mapper = new ObjectMapper();
    }

    @Test
    public void testFilter() throws Exception {
        String patchJson = FileUtils.readFileToString(new File("src/test/resources/serialization/patch1.json"));
        String expectedPatchJson = FileUtils.readFileToString(new File("src/test/resources/serialization//filteredPatch1.json"));

        JsonNode patchNode = mapper.readTree(patchJson);
        JsonNode filteredPatchNode = this.filter.filter(patchNode);
        assertEquals(expectedPatchJson, mapper.writeValueAsString(filteredPatchNode));
    }
}