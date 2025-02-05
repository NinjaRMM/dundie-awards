package com.ninjaone.dundie_awards.repository;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @JpaQuery("update Employee e set e.dundieAwards = e.dundieAwards + {awardCount} where e.organization = {organization}")
    int giveDundieAwardsByOrganization(Organization organization, int awardCount);
}
