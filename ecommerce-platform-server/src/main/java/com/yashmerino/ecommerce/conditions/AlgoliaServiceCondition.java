package com.yashmerino.ecommerce.conditions;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Condition applied for creation of algolia service.
 */
public class AlgoliaServiceCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String propertyValue = context.getEnvironment().getProperty("algolia.usage");
        return Boolean.parseBoolean(propertyValue); // Return true if the property is "true"
    }
}
