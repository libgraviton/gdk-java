package com.github.libgraviton.gdk.api.query.rql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.data.ComplexClass;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class RqlTest {

    @Test
    public void testGenerateWithAllStatements() {
        ComplexClass aClass1 = new ComplexClass();
        aClass1.setName("name1");
        ComplexClass aClass2 = new ComplexClass();
        aClass2.setName("name2");

        ComplexClass complexClass = new ComplexClass();
        complexClass.setName("aName");
        complexClass.setaClass(aClass1);
        complexClass.setClasses(Arrays.asList(aClass1, aClass2));

        Rql rql = new Rql.Builder()
                .setLimit(1)
                .addSelect("zip")
                .addSelect("city")
                .setResource(complexClass, new ObjectMapper())
                .build();

        String expectedRql = "?and(eq(name,string:aName),eq(aClass.name,string:name1),eq(classes..name,string:name1),eq(classes..name,string:name2))&limit(1)&select(zip,city)";

        assertEquals(expectedRql, rql.generate());
    }

    @Test
    public void testGenerateWithoutStatements() {
        Rql rql = new Rql.Builder().build();
        String expectedRql = "";
        assertEquals(expectedRql, rql.generate());
    }

    @Test
    public void testAddRqlStatements() {
        Rql rql1 = new Rql.Builder()
                .setLimit(2,3)
                .build();

        Rql rql2 = new Rql.Builder()
                .addSelect("attribute1")
                .build();

        rql1.addStatements(rql2.getStatements());

        String expectedRql = "?limit(2,3)&select(attribute1)";
        assertEquals(expectedRql, rql1.generate());
    }
}
