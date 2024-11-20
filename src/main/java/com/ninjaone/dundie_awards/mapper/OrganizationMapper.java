package com.ninjaone.dundie_awards.mapper;

import com.ninjaone.dundie_awards.dto.OrganizationDTO;
import com.ninjaone.dundie_awards.model.Organization;

public class OrganizationMapper {

    public static OrganizationDTO toDTO(Organization organization) {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(organization.getId());
        dto.setName(organization.getName());
        return dto;
    }

    public static Organization toEntity(OrganizationDTO organizationDTO) {
        Organization organization = new Organization();
        organization.setId(organizationDTO.getId());
        organization.setName(organizationDTO.getName());
        return organization;
    }
}
