package com.example.rateusd.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Valute", strict = false)
public class Valute {

    @Element(name = "NumCode")
    private String numcode;
    @Element(name = "CharCode")
    private String charcode;
    @Element(name = "Name")
    private String name;
    @Element(name = "Nominal")
    private String nominal;
    @Element(name = "Value")
    private String value;
}
