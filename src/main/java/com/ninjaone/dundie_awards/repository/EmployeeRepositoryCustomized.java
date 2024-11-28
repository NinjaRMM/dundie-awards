package com.ninjaone.dundie_awards.repository;

import java.util.List;

public interface EmployeeRepositoryCustomized {

	int increaseAwardsToEmployees(List<Long> employeeIds);

	int increaseAwardsToEmployees(Long organizationId);

	int increaseAwardsToEmployeesNative(Long organizationId);
}
