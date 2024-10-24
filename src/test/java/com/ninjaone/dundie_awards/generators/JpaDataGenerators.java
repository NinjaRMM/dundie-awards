package com.ninjaone.dundie_awards.generators;

import com.ninjaone.dundie_awards.employee.Employee;
import com.ninjaone.dundie_awards.organization.Organization;

public abstract class JpaDataGenerators {

    public static Employee generateDbEmployee() {
        return new Employee("Ash",
                "Ketchum",
                generateDbOrganization());
    }

    public static Organization generateDbOrganization() {
        return new Organization(
                "Trainers"
        );
    }
}
