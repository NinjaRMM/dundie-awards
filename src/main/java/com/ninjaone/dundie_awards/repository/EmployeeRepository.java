package com.ninjaone.dundie_awards.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ninjaone.dundie_awards.model.Employee;


/*
 * Queries whose performance was not good on tests where deprecated
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>,  EmployeeRepositoryCustomized {
	
	@Query("SELECT e FROM Employee e WHERE e.organization.id NOT IN :ids")
	List<Employee> findAllByOrganizationIdsNotIn(@Param("ids") List<Long> ids);


    @Query(value = "SELECT e.id FROM Employee e WHERE e.organization.id = :organizationId")
    List<Long> findEmployeeIdsByOrganizationId(@Param("organizationId") long organizationId);
    
    @Query(value = "SELECT STRING_AGG(CONCAT(e.id, ',', e.dundie_awards), '|') " +
            "FROM employees e " +
            "WHERE e.organization_id = :organizationId", 
    nativeQuery = true)
    String findConcatenatedEmployeeDataByOrganizationIdNative(@Param("organizationId") long organizationId);

    
    int countByOrganizationId(Long id);


	/*
	 * Unsude methods created to compare performance
	 */
	@Deprecated
	@Query(value = "SELECT e.id FROM employees e WHERE e.organization_id = :organizationId", nativeQuery = true)
	List<Long> findEmployeeIdsByOrganizationIdNative(@Param("organizationId") long organizationId);
	
	@Deprecated
	@Query(value = "SELECT e.id FROM employees e WHERE e.organization_id = :organizationId LIMIT :limit OFFSET :offset", nativeQuery = true)
	List<Long> findEmployeeIdsByOrganizationIdWithPagination(
			@Param("organizationId") long organizationId,
			@Param("offset") int offset,
			@Param("limit") int limit
			);
    
}

