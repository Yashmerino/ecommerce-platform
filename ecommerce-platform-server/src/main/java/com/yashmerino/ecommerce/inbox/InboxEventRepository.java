package com.yashmerino.ecommerce.inbox;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxEventRepository extends JpaRepository<InboxEvent, String> {
}