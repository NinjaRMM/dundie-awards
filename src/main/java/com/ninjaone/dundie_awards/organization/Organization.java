package com.ninjaone.dundie_awards.organization;

import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(NONE)
    private long id;

    @Column(name = "name")
    private String name;

    public Organization(String name) {
        this.name = name;
    }
}
