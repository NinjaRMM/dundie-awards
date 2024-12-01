package com.ninjaone.dundie_awards.model;

import java.util.List;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor 
@Entity
@Table(name = "organizations")
public class Organization {
	
	public final static Organization GARFIELD = Organization.builder().name("Garfield").blocked(false).blockedBy(null).build();
	public final static Organization FRAJOLA = Organization.builder().name("Frajola").blocked(false).blockedBy(null).build();
	public final static Organization SQUANCHY = Organization.builder().name("Squanchy").blocked(false).blockedBy(null).build();
	public final static Organization TOM = Organization.builder().name("Tom").blocked(false).blockedBy(null).build();
	public final static Organization HELLO_KITTY = Organization.builder().name("Hello Kitty").blocked(false).blockedBy(null).build();

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(name = "name")
	private String name;

	@ToString.Exclude
	@Schema(hidden = true)
	@OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
	@Fetch(value = FetchMode.SELECT)
	@JsonIgnore
	private List<Employee> employees;
	
	@Column(name = "blocked")
	private boolean blocked;
	
	@Column(name = "blockedBy")
	private String blockedBy;
	
	@Version
    private int version;

}
