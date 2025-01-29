# Matt's Recommendations for this Project/Codebase

Below are individual sections categorizing the recommendations I have for this project.

## Github Project-Level
 1. Enabled Dependency Graphing in the repo
 2. Enabled branch protection with code quality checks

## CI/CD
 1. Added Github Actions to execute Gradle build and test
 2. Added Gradle jacoco plugin and Github Actions to perform Test Coverage analysis


## Application
 1. References springdoc in config, but doesn't include it in gradle
 2. No security

## Larger Initiatives
 1. Refactor with clean-architecture design
    - Improves Domain-Driven design paradigms
    - Improves component decoupling and therefore testing
    - Makes future refactorings less challenging
    - Allows for more natural decisioning when organizing new code/features