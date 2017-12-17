package com.qci.pickem.model.mysportsfeeds;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Team {

    @JsonProperty("ID")
    private Integer id;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Abbreviation")
    private String abbreviation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
}
