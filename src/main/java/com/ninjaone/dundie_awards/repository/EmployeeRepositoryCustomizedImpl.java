package com.ninjaone.dundie_awards.repository;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/*
 * Queries whose performance was not good on tests where deprecated
 */
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
    
    @Override
    @Transactional
    public int decreaseAwardsToEmployeesNative(Long organizationId) {
        if (organizationId == null) {
            return 0;
        }

        String query = "UPDATE employees SET dundie_awards = dundie_awards - 1 WHERE organization_id = :organizationId";
        return entityManager.createNativeQuery(query)
                            .setParameter("organizationId", organizationId)
                            .executeUpdate();
    }
    
    @Override
    @Transactional
    @Deprecated
    public void createTemporaryTable(String uuid) {
        String normalizedTableName = "temp_" + uuid.replace("-", "_");

        String createTableQuery = String.format("""
            CREATE TEMPORARY TABLE %s (
                id BIGINT PRIMARY KEY,
                dundie_awards_original INT
            )
            """, normalizedTableName);

        String createIndexQuery = String.format("""
            CREATE INDEX idx_temp_table_id ON %s (id)
            """, normalizedTableName);

        entityManager.createNativeQuery(createTableQuery).executeUpdate();
        entityManager.createNativeQuery(createIndexQuery).executeUpdate();
    }

    @Override
    @Transactional
    @Deprecated
    public void insertIntoTemporaryTable(String uuid, Long id, Integer dundieAwardsOriginal) {
        String normalizedTableName = "temp_" + uuid.replace("-", "_");

        String query = String.format("""
            INSERT INTO %s (id, dundie_awards_original)
            VALUES (:id, :dundieAwardsOriginal)
            """, normalizedTableName);

        entityManager.createNativeQuery(query)
                     .setParameter("id", id)
                     .setParameter("dundieAwardsOriginal", dundieAwardsOriginal)
                     .executeUpdate();
    }

    @Override
    @Transactional
    @Deprecated
    public void updateEmployeesFromTemporaryTable(String uuid) {
        String normalizedTableName = "temp_" + uuid.replace("-", "_");

        String query = String.format("""
            UPDATE employees e
            SET dundie_awards = (
                SELECT t.dundie_awards_original
                FROM %s t
                WHERE t.id = e.id
            )
            WHERE EXISTS (
                SELECT 1
                FROM %s t
                WHERE t.id = e.id
            )
            """, normalizedTableName, normalizedTableName);

        entityManager.createNativeQuery(query).executeUpdate();
    }


    @Override
    @Transactional
    @Deprecated
    public void dropTemporaryTable(String uuid) {
        String normalizedTableName = "temp_" + uuid.replace("-", "_");

        String query = String.format("""
            DROP TABLE %s
            """, normalizedTableName);

        entityManager.createNativeQuery(query).executeUpdate();
    }
    
    @Override
    @Transactional
    public int updateDundieAwards(Long employeeId, int dundieAwards) {
        if (employeeId == null) {
            return 0;
        }

        String query = """
            UPDATE employees
            SET dundie_awards = :dundieAwards
            WHERE id = :employeeId
            """;

        return entityManager.createNativeQuery(query)
                            .setParameter("employeeId", employeeId)
                            .setParameter("dundieAwards", dundieAwards)
                            .executeUpdate();
    }

}
