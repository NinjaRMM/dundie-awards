package com.ninjaone.dundie_awards.validation;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

@Component
public class SpringValidatorFactoryBean extends LocalValidatorFactoryBean {
	
	private final ApplicationContext applicationContext;

    public SpringValidatorFactoryBean(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @Override
    protected void postProcessConfiguration(jakarta.validation.Configuration<?> configuration) {
        configuration.constraintValidatorFactory(
            new SpringConstraintValidatorFactory(applicationContext.getAutowireCapableBeanFactory())
        );
    }
}