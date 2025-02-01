package com.ninjaone.dundie_awards;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;

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
            Organization organizationPikashu = Organization.builder().name("Pikashu").build();
            organizationRepository.save(organizationPikashu);

            employeeRepository.save(Employee.builder().firstName("John").lastName("Doe").organization(organizationPikashu).build());
            employeeRepository.save(Employee.builder().firstName("Jane").lastName("Smith").organization(organizationPikashu).build());
            employeeRepository.save(Employee.builder().firstName("Creed").lastName("Braton").organization(organizationPikashu).build());

            Organization organizationSquanchy = Organization.builder().name("Squanchy").build();
            organizationRepository.save(organizationSquanchy);

            employeeRepository.save(Employee.builder().firstName("Michael").lastName("Scott").organization(organizationSquanchy).build());
            employeeRepository.save(Employee.builder().firstName("Dwight").lastName("Schrute").organization(organizationSquanchy).build());
            employeeRepository.save(Employee.builder().firstName("Jim").lastName("Halpert").organization(organizationSquanchy).build());
            employeeRepository.save(Employee.builder().firstName("Pam").lastName("Beesley").organization(organizationSquanchy).build());
        }

        int totalAwards = employeeRepository.findAll().stream()
                .mapToInt(employee -> Objects.requireNonNullElse(employee.getDundieAwards(), 0))
                .sum();
        this.awardsCache.setTotalAwards(totalAwards);
    }
}
