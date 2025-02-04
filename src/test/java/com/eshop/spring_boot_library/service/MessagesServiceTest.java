package com.eshop.spring_boot_library.service;

import com.eshop.spring_boot_library.dao.MessageRepository;
import com.eshop.spring_boot_library.entity.Message;
import com.eshop.spring_boot_library.requestmodels.AdminQuestionRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagesServiceTest {

    @Mock
    private MessageRepository messageRepository;

    private MessagesService messagesService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        messagesService = new MessagesService(messageRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }


    @Test
    void postMessage_Success() {
        //Given
        String userEmail = "test@email.com";
        Message messageRequest = new Message("Test Title","Test Question");

        //When
        when(messageRepository.save(any(Message.class))).thenReturn(messageRequest);
        messagesService.postMessage(messageRequest, userEmail);

        //Then
        verify(messageRepository).save(any(Message.class));
        verify(messageRepository, times(1))
                .save(argThat(m -> m.getTitle().equals("Test Title") &&
                        m.getQuestion().equals("Test Question") &&
                        m.getUserEmail().equals(userEmail)
                ));
    }

    @Test
    void putMessage_Success() throws Exception{
        //Given
        Long messageId = 1L;
        String adminEmail = "admin@example.com";
        String response = "Test Response";

        Message existingMessage= new Message("Original Title","Original Question");
        existingMessage.setId(messageId);

        AdminQuestionRequest adminQuestionRequest = new AdminQuestionRequest();
        adminQuestionRequest.setId(messageId);
        adminQuestionRequest.setResponse(response);

        //When
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(existingMessage));
        when(messageRepository.save(any(Message.class))).thenReturn(existingMessage);
        messagesService.putMessage(adminQuestionRequest, adminEmail);

        //Then
        verify(messageRepository).findById(messageId);
        verify(messageRepository).save(argThat(m -> m.getAdminEmail()
                .equals(adminEmail) &&
                m.getResponse().equals(response) &&
                m.getClosed()
        ));
    }

    @Test
    void putMessage_ThrowsException_WhenMessageNotFound() {
        //Given
        Long messageId = 1L;
        String adminEmail = "admin@example.com";

        AdminQuestionRequest adminQuestionRequest = new AdminQuestionRequest();
        adminQuestionRequest.setId(messageId);
        adminQuestionRequest.setResponse("Test Response");

        //When
         when(messageRepository.findById(messageId)).thenReturn(Optional.empty());
         Exception exception = assertThrows(Exception.class, () -> {
             messagesService.putMessage(adminQuestionRequest, adminEmail);
         });

         //Then
        assertEquals("Message does not exist", exception.getMessage());
        verify(messageRepository, never()).save(any(Message.class));
        verify(messageRepository, times(1)).findById(messageId);
    }

    @Test
    void postMessage_ValidatesMessageFields(){
        //Given
        String userEmail = "test@email.com";
        Message messageRequest = new Message("Test Title","Test Question");

        //When
        messagesService.postMessage(messageRequest, userEmail);

        //Then
        verify(messageRepository).save(argThat(m -> {
            assertNotNull(m.getTitle());
            assertNotNull(m.getQuestion());
            assertNotNull(m.getUserEmail());
            assertEquals("Test Title", messageRequest.getTitle());
            assertEquals("Test Question", messageRequest.getQuestion());
            assertNotEquals(userEmail, messageRequest.getUserEmail());
            return true;
        }));
    }
    @Test
    void putMessage_UpdatesAllRequiredFields() throws Exception {
        //Given
        Long messageId = 1L;
        String adminEmail = "admin@example.com";
        String response = "Test Response";

        Message existingMessage= new Message("Original Title","Original Question");
        existingMessage.setId(messageId);

        AdminQuestionRequest adminQuestionRequest = new AdminQuestionRequest();
        adminQuestionRequest.setId(messageId);
        adminQuestionRequest.setResponse(response);

        //When
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(existingMessage));
        when(messageRepository.save(any(Message.class))).thenReturn(existingMessage);
        messagesService.putMessage(adminQuestionRequest, adminEmail);

        //Then
        verify(messageRepository).findById(messageId);
        verify(messageRepository).save(argThat(m -> {
            assertEquals("Test Response", m.getResponse());
            assertEquals("admin@example.com", m.getAdminEmail());
            assertTrue(m.getClosed());
            return true;
        }));
    }
}