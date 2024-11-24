package com.ninjaone.dundie_awards;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;

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
        	
        	List<Organization> organizations = List.of(
        			Organization.builder().name("Pikashu").build(),
        			Organization.builder().name("Squanchy").build()
        			);
        	organizationRepository.saveAll(organizations);

            List<Employee> pikashuEmployees = List.of(
            		Employee.builder().firstName("John").lastName("Doe").organization(organizations.get(0)).build(),
            		Employee.builder().firstName("Jane").lastName("Smith").organization(organizations.get(0)).build(),
            		Employee.builder().firstName("Creed").lastName("Braton").organization(organizations.get(0)).build()
            		);
            employeeRepository.saveAll(pikashuEmployees);

            List<Employee> squanchyEmployees = List.of(
            	    new Employee("Michael", "Scott", organizations.get(1)),
            	    new Employee("Dwight", "Schrute", organizations.get(1)),
            	    new Employee("Jim", "Halpert", organizations.get(1)),
            	    new Employee("Pam", "Beesley", organizations.get(1))
            	);
        	employeeRepository.saveAll(squanchyEmployees);
        }

        int totalAwards = employeeRepository.findAll().stream()
                .mapToInt(Employee::getDundieAwards)
                .sum();
        
        this.awardsCache.setTotalAwards(totalAwards);
    }
}
