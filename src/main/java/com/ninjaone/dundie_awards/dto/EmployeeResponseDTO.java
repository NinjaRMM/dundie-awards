package com.ninjaone.dundie_awards.dto;

public class EmployeeResponseDTO {

    private long id;
    private String firstName;
    private String lastName;
    private Integer dundieAwards;
    private OrganizationDTO organization;

    public EmployeeResponseDTO() {}

    public EmployeeResponseDTO(long id, String firstName, String lastName, Integer dundieAwards, OrganizationDTO organization) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dundieAwards = dundieAwards;
        this.organization = organization;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getDundieAwards() {
        return dundieAwards;
    }

    public void setDundieAwards(Integer dundieAwards) {
        this.dundieAwards = dundieAwards;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }
}
