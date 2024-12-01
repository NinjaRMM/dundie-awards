package com.ninjaone.dundie_awards.repository;

import java.util.List;

/*
 * Queries whose performance was not good on tests where deprecated
 */
public interface EmployeeRepositoryCustomized {

	@Deprecated
	int increaseAwardsToEmployees(List<Long> employeeIds);

	@Deprecated
	int increaseAwardsToEmployees(Long organizationId);

	int increaseAwardsToEmployeesNative(Long organizationId);
	
	@Deprecated
	void createTemporaryTable(String tableName);

	@Deprecated
    void insertIntoTemporaryTable(String tableName, Long id, Integer dundieAwardsOriginal);

	@Deprecated
    void updateEmployeesFromTemporaryTable(String tableName);

	@Deprecated
    void dropTemporaryTable(String tableName);

	int decreaseAwardsToEmployeesNative(Long organizationId);
	
	int updateDundieAwards(Long employeeId, int dundieAwards);

}
