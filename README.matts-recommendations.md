# Matt's Recommendations for this Project/Codebase

Below are individual sections categorizing the recommendations I have for this project.

## Github Project-Level
 1. Enabled Dependency Graphing in the repo
 2. Enabled branch protection with code quality checks
 3. Setup API Tokens for Github Actions to be able to interact with the project

## CI/CD
 1. Added Github Actions to execute Gradle build and test
 2. Added Gradle jacoco plugin and Github Actions to perform Test Coverage analysis

## General Application
1. References springdoc in config, but doesn't include it in gradle
    - Added springdoc to gradle build, swagger-ui now present
2. No security on REST endpoints
3. No real tests
    - Added `EmployeeControllerTest`
4. Added lombok for cleaner class instance usage
    - Removed boilerplate getter/setter/constructor code

## REST Functionality

### EmployeeController
1. No way to update `Employee`'s `organziation` on an existing record
    - fixed
2. No way to update `Employee`'s `dundieAwards` count on an existing record
    - fixed
3. Imports the following Spring Components but does not use them
    - `ActivityRepository`
    - `MessageBroker`
    - `AwardsCache`
4. Controller has too many concerns if there's supposed to be caching, etc.
    - Controllers should just be a demarcation layer b/t a backend service and the appropriate HTTP/REST responses
    - Something like caching should be abstracted out in another layer
    - The `@Cacheable` annotation can be used to apply built-in caching mechansims from Spring-Data

### No controller for `Organization`s

## Larger Initiatives
### Refactor with clean-architecture design
- Improves Domain-Driven design paradigms
- Improves component decoupling and therefore testing
- Makes future refactorings less challenging
- Allows for more natural decisioning when organizing new code/features