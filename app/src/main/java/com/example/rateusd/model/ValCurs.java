package com.example.rateusd.model;


import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "ValCurs", strict = false)
public class ValCurs {


    /* @ElementList(name = "Valute", inline = true)
     private List<Valute> valutes;

     public List<Valute> getValutes() {
         return valutes;
     }

     public void setValutes(List<Valute> valutes) {
         this.valutes = valutes;
     }*/
    @ElementList(name = "Record", inline = true)
    private List<Record> records;

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }
}
