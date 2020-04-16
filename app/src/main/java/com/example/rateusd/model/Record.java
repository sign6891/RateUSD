package com.example.rateusd.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Record", strict = false)
public class Record {

    @Element(name = "Value")
    private String value;
    @Element(name = "Nominal")
    private String nominal;


    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }
}
