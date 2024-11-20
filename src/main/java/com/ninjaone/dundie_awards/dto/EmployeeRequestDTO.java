package com.ninjaone.dundie_awards.dto;

public class EmployeeRequestDTO {

    private String firstName;
    private String lastName;
    private Integer dundieAwards=0;
    private Long organizationId;

    public EmployeeRequestDTO() {}

    public EmployeeRequestDTO(String firstName, String lastName, Integer dundieAwards, Long organizationId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dundieAwards = dundieAwards;
        this.organizationId = organizationId;
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

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
