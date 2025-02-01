package com.eshop.spring_boot_library.dao;

import com.eshop.spring_boot_library.entity.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    private Message message1;
    private Message message2;
    private Message message3;

    @BeforeEach
    void setUp() {
        message1 = new Message("Title 1", "Question 1");
        message1.setUserEmail("user1@example.com");
        message1.setAdminEmail("admin@example.com");
        message1.setResponse("Response 1");
        message1.setClosed(false);

        message2 = new Message("Title 2", "Question 2");
        message2.setUserEmail("user1@example.com");
        message2.setAdminEmail("admin@example.com");
        message2.setResponse("Response 2");
        message2.setClosed(true);

        message3 = new Message("Title 3", "Question 3");
        message3.setUserEmail("user2@example.com");
        message3.setAdminEmail("admin@example.com");
        message3.setResponse("Response 3");
        message3.setClosed(false);

        // Save test data
        messageRepository.saveAll(List.of(message1, message2, message3));
    }

    @AfterEach
    void tearDown() {
        messageRepository.deleteAll();
    }

    @Test
    void findByUserEmail() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Message> userMessages = messageRepository.findByUserEmail("user1@example.com", pageable);

        // Then
        assertEquals(2, userMessages.getTotalElements());
        assertTrue(userMessages.getContent().stream()
                .allMatch(message -> message.getUserEmail().equals("user1@example.com")));
    }

    @Test
    void findByUserEmail_NonExistingUser_ShouldReturnEmptyPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Message> userMessages = messageRepository.findByUserEmail("nonexistent@example.com", pageable);

        // Then
        assertEquals(0, userMessages.getTotalElements());
        assertTrue(userMessages.getContent().isEmpty());
    }

    @Test
    void findByClosed_OpenMessages_ShouldReturnOnlyOpenMessages() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Message> openMessages = messageRepository.findByClosed(false, pageable);

        // Then
        assertEquals(2, openMessages.getTotalElements());
        assertTrue(openMessages.getContent().stream()
                .noneMatch(Message::getClosed));
    }

    @Test
    void findByClosed_ClosedMessages_ShouldReturnOnlyClosedMessages() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Message> closedMessages = messageRepository.findByClosed(true, pageable);

        // Then
        assertEquals(1, closedMessages.getTotalElements());
        assertTrue(closedMessages.getContent().stream()
                .allMatch(Message::getClosed));
    }

    @Test
    void createMessage_ShouldSaveAndGenerateId() {
        // Given
        Message newMessage = new Message("New Title", "New Question");
        newMessage.setUserEmail("user3@example.com");
        newMessage.setClosed(false);

        // When
        Message savedMessage = messageRepository.save(newMessage);

        // Then
        assertNotNull(savedMessage.getId());
        assertEquals("New Title", savedMessage.getTitle());
        assertEquals("New Question", savedMessage.getQuestion());
    }
    @Test
    void updateMessage_ShouldModifyExistingMessage() {
        // Given
        Message messageToUpdate = messageRepository.findById(message1.getId()).orElseThrow();
        String newResponse = "Updated Response";

        // When
        messageToUpdate.setResponse(newResponse);
        messageToUpdate.setClosed(true);
        Message updatedMessage = messageRepository.save(messageToUpdate);

        // Then
        assertEquals(newResponse, updatedMessage.getResponse());
        assertTrue(updatedMessage.getClosed());
    }

    @Test
    void pagination_ShouldReturnCorrectPageSize() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Message> messagePage = messageRepository.findAll(pageable);

        // Then
        assertEquals(2, messagePage.getContent().size());
        assertEquals(3, messagePage.getTotalElements());
        assertEquals(2, messagePage.getTotalPages());
    }

    @Test
    void findByUserEmail_WithPagination_ShouldReturnCorrectPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);

        // When
        Page<Message> userMessages = messageRepository.findByUserEmail("user1@example.com", pageable);

        // Then
        assertEquals(1, userMessages.getContent().size());
        assertEquals(2, userMessages.getTotalElements());
        assertEquals(2, userMessages.getTotalPages());
    }

}