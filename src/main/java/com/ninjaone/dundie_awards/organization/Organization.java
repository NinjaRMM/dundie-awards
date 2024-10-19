package com.ninjaone.dundie_awards.organization;

import com.ninjaone.dundie_awards.infrastructure.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "organizations")
public class Organization extends AbstractEntity {

    @Column(name = "name")
    private String name;
}
