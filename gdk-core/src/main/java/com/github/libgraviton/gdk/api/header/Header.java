package com.github.libgraviton.gdk.api.header;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Header implements Iterable<String> {

    private List<String> values;

    public Header(Header header, String value) {
        values = header.values;
        values.add(value);
    }

    public Header() {
        this(new ArrayList<String>());
    }

    public Header(List<String> values) {
        this.values = values;
    }

    public boolean contains(String value) {
        return values.contains(value);
    }

    public String get(int index) {
        return values.get(index);
    }

    public List<String> all() {
        return new ArrayList<>(values);
    }

    @Override
    public Iterator<String> iterator() {
        return values.iterator();
    }

}
