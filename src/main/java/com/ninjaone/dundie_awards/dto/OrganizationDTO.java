package com.ninjaone.dundie_awards.dto;

public class OrganizationDTO {

    private long id;
    private String name;

    public OrganizationDTO() {
    }

    public OrganizationDTO(long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
