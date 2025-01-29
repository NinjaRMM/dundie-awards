package com.ninjaone.dundie_awards.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
@lombok.Data
@lombok.Builder(builderClassName = "Builder")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "dundie_awards")
    private Integer dundieAwards;

    @ManyToOne
    private Organization organization;

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

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setDundieAwards(int dundieAwards){
        this.dundieAwards = dundieAwards;
    }

    public Integer getDundieAwards(){
        return dundieAwards;
    }
}