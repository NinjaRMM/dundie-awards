package com.ninjaone.dundie_awards.repository;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class EmployeeRepositoryCustomizedImpl implements EmployeeRepositoryCustomized {

	@PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public int increaseAwardsToEmployees(List<Long> employeeIds) {
        if (employeeIds.isEmpty()) {
            return 0;
        }

        String query = "UPDATE Employee e SET e.dundieAwards = e.dundieAwards + 1 WHERE e.id IN :employeeIds";
        return entityManager.createQuery(query)
                            .setParameter("employeeIds", employeeIds)
                            .executeUpdate();
    }
    
    @Override
    @Transactional
    public int increaseAwardsToEmployees(Long organizationId) {
        if (null==organizationId) {
            return 0;
        }

        String query = "UPDATE Employee e SET e.dundieAwards = e.dundieAwards + 1 WHERE e.organization.id = :organizationId";
        return entityManager.createQuery(query)
                            .setParameter("organizationId", organizationId)
                            .executeUpdate();
    }
    
    @Override
    @Transactional
    public int increaseAwardsToEmployeesNative(Long organizationId) {
        if (organizationId == null) {
            return 0;
        }

        String query = "UPDATE employees SET dundie_awards = dundie_awards + 1 WHERE organization_id = :organizationId";
        return entityManager.createNativeQuery(query)
                            .setParameter("organizationId", organizationId)
                            .executeUpdate();
    }
}
