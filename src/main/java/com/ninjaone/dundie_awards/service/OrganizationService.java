package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.dto.OrganizationDTO;
import com.ninjaone.dundie_awards.exception.NotFoundException;
import com.ninjaone.dundie_awards.mapper.OrganizationMapper;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;


    public OrganizationDTO findById(Long id) {
        Optional<Organization> orgEntity = organizationRepository.findById(id);
        if(orgEntity.isPresent()) {
            return OrganizationMapper.toDTO(orgEntity.get());
        } else {
            throw new NotFoundException("Organization not found");
        }
    }
}
