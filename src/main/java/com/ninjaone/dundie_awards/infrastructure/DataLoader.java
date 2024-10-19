package com.ninjaone.dundie_awards.infrastructure;

import com.ninjaone.dundie_awards.employee.Employee;
import com.ninjaone.dundie_awards.employee.EmployeeRepository;
import com.ninjaone.dundie_awards.organization.AwardsCache;
import com.ninjaone.dundie_awards.organization.Organization;
import com.ninjaone.dundie_awards.organization.OrganizationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final OrganizationRepository organizationRepository;
    private final AwardsCache awardsCache;

    public DataLoader(EmployeeRepository employeeRepository, OrganizationRepository organizationRepository, AwardsCache awardsCache) {
        this.awardsCache = awardsCache;
        this.employeeRepository = employeeRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public void run(String... args) {
        // uncomment to reseed data
        // employeeRepository.deleteAll();
        // organizationRepository.deleteAll();

        if (employeeRepository.count() == 0) {
            Organization organizationPikashu = new Organization("Pikashu");
            organizationRepository.save(organizationPikashu);
            List<Employee> pikashuEmployees = List.of(
                    Employee.builder()
                            .firstName("John")
                            .lastName("Doe")
                            .organization(organizationPikashu)
                            .build(),
                    Employee.builder()
                            .firstName("Jane")
                            .lastName("Smith")
                            .organization(organizationPikashu)
                            .build(),
                    Employee.builder()
                            .firstName("Creed")
                            .lastName("Braton")
                            .organization(organizationPikashu)
                            .build()
            );
            employeeRepository.saveAll(pikashuEmployees);

            Organization organizationSquanchy = new Organization("Squanchy");
            organizationRepository.save(organizationSquanchy);

            List<Employee> squanchyEmployees = List.of(
                    Employee.builder()
                            .firstName("Michael")
                            .lastName("Scott")
                            .organization(organizationSquanchy)
                            .build(),
                    Employee.builder()
                            .firstName("Dwight")
                            .lastName("Schrute")
                            .organization(organizationSquanchy)
                            .build(),
                    Employee.builder()
                            .firstName("Jim")
                            .lastName("Halpert")
                            .organization(organizationSquanchy)
                            .build(),
                    Employee.builder()
                            .firstName("Pam")
                            .lastName("Beesley")
                            .organization(organizationSquanchy)
                            .build()
            );

            employeeRepository.saveAll(squanchyEmployees);
        }

        int totalAwards = employeeRepository.findAll().stream()
                .mapToInt(Employee::getDundieAwards)
                .sum();
        this.awardsCache.setTotalAwards(totalAwards);
    }
}
