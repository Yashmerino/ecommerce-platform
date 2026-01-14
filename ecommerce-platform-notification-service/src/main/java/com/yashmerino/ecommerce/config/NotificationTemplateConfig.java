package com.yashmerino.ecommerce.config;

import com.yashmerino.ecommerce.service.NotificationTemplate;
import com.yashmerino.ecommerce.utils.NotificationType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Notification template configuration.
 */
@Configuration
public class NotificationTemplateConfig {

    /**
     * Implementation of notification templates bean.
     *
     * @param templates the existing templates.
     *
     * @return the notification templates mapped by notification types.
     */
    @Bean
    public Map<NotificationType, NotificationTemplate> notificationTemplates(List<NotificationTemplate> templates) {
        return templates.stream()
                .collect(Collectors.toMap(NotificationTemplate::getType, Function.identity()));
    }
}
