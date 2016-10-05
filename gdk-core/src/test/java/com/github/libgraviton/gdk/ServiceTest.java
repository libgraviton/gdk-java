package com.github.libgraviton.gdk;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ServiceTest {

    private Service service;

    @Before
    public void setupService() {
        service = new Service("service://item", "service://collection/");
    }

    @Test
    public void testEqualsSameNotNull() {
        Service equalService = new Service("service://item", "service://collection/");
        assertTrue(service.equals(equalService));
        assertTrue(equalService.equals(service));
    }

    @Test
    public void testEqualsSameNull() {
        Service service = new Service(null, null);
        Service equalService = new Service(null, null);
        assertTrue(service.equals(equalService));
        assertTrue(equalService.equals(service));
    }

    @Test
    public void testDifferentNotNull() {
        Service equalService = new Service("service://other/item", "service://other/collection/");
        assertFalse(service.equals(equalService));
        assertFalse(equalService.equals(service));
    }

    @Test
    public void testDifferentNull() {
        Service equalService = new Service(null, null);
        assertFalse(service.equals(equalService));
        assertFalse(equalService.equals(service));
    }

}

