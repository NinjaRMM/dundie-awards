package com.ninjaone.dundie_awards.model;

import jakarta.persistence.*;

@Entity
@Table(name = "organizations")
@lombok.Data
@lombok.EqualsAndHashCode(of = {"id", "name"})
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Builder(builderClassName = "Builder")
public class Organization {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name")
  private String name;
}
