CREATE DATABASE IF NOT EXISTS ecommerce_platform;
CREATE DATABASE IF NOT EXISTS ecommerce_platform_payment_service;
CREATE DATABASE IF NOT EXISTS ecommerce_platform_notification_service;

CREATE USER IF NOT EXISTS 'ecommerce_user'@'%' IDENTIFIED BY 'ecommerce_pass_2026';

GRANT ALL PRIVILEGES ON ecommerce_platform.* TO 'ecommerce_user'@'%';
GRANT ALL PRIVILEGES ON ecommerce_platform_payment_service.* TO 'ecommerce_user'@'%';
GRANT ALL PRIVILEGES ON ecommerce_platform_notification_service.* TO 'ecommerce_user'@'%';

FLUSH PRIVILEGES;
