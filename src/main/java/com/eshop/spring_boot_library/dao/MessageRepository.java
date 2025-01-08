package com.eshop.spring_boot_library.dao;

import com.eshop.spring_boot_library.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
