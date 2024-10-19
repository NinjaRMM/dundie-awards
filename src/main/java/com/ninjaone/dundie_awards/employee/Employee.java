package com.ninjaone.dundie_awards.employee;

import com.ninjaone.dundie_awards.infrastructure.AbstractEntity;
import com.ninjaone.dundie_awards.organization.Organization;
import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class Employee extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(NONE)
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "dundie_awards")
    private int dundieAwards;

    @ManyToOne
    private Organization organization;

    public Employee(String firstName, String lastName, Organization organization) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.organization = organization;
    }
}
