package com.eshop.spring_boot_library.dao;

import com.eshop.spring_boot_library.entity.History;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class HistoryRepositoryTest {

    @Autowired
    private HistoryRepository historyRepository;

    private History history1;
    private History history2;
    private History history3;

    @BeforeEach
    void setUp() {
        history1 = new History(
                "user1@example.com",
                "2024-01-01",
                "2024-02-05",
                "Book 1",
                "Author 1",
                "Description 1",
                "image1.jpg"
        );

        history2 = new History(
                "user1@example.com",
                "2024-04-02",
                "2024-06-02",
                "Book 2",
                "Author 2",
                "Description 2",
                "image2.jpg"
        );

        history3 = new History(
                "user2@example.com",
                "2024-02-23",
                "2024-07-14",
                "Book 3",
                "Author 3",
                "Description 3",
                "image3.jpg"
        );


        historyRepository.saveAll(List.of(history1, history2, history3));
    }

    @AfterEach
    void tearDown() {
        historyRepository.deleteAll();
    }

    @Test
    void findBooksByUserEmail() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<History> user1History = historyRepository.findBooksByUserEmail("user1@example.com", pageable);
        Page<History> user2History = historyRepository.findBooksByUserEmail("user2@example.com", pageable);
        Page<History> nonExistentUserHistory = historyRepository.findBooksByUserEmail("nonexistent@example.com", pageable);

        // Then
        assertEquals(2, user1History.getTotalElements());
        assertEquals(1, user2History.getTotalElements());
        assertEquals(0, nonExistentUserHistory.getTotalElements());
    }

    @Test
    void testPagination() {
        //Given
        for (int i = 0; i < 15; i++) {
            History history = new History(
                    "user3@example.com",
                    "2024-01-01",
                    "2024-02-01",
                    "Book " + i,
                    "Author " + i,
                    "Description " + i,
                    "image" + i + ".jpg"
            );
            historyRepository.save(history);
        }

        //WHen
        Pageable firstPage = PageRequest.of(0, 5);
        Pageable secondPage = PageRequest.of(1, 5);

        Page<History> firstPageResult = historyRepository.findBooksByUserEmail("user3@example.com", firstPage);
        Page<History> secondPageResult = historyRepository.findBooksByUserEmail("user3@example.com", secondPage);

        //Then
        assertEquals(15, firstPageResult.getTotalElements());
        assertEquals(5, secondPageResult.getContent().size());
        assertEquals("Book 4", firstPageResult.getContent().get(4).getTitle());
        assertEquals("Book 9", secondPageResult.getContent().get(4).getTitle());
        assertTrue(firstPageResult.hasNext());
        assertEquals(3, firstPageResult.getTotalPages());
        assertFalse(secondPageResult.isEmpty());
    }

    @Test
    void testHistoryCreation() {
        // Given
        History newHistory = new History(
                "newuser@example.com",
                "2024-01-04",
                "2024-02-04",
                "New Book",
                "New Author",
                "New Description",
                "newimage.jpg"
        );

        // When
        History savedHistory = historyRepository.save(newHistory);

        // Then
        assertNotNull(savedHistory.getId());
        assertEquals("newuser@example.com", savedHistory.getUserEmail());
        assertEquals("New Book", savedHistory.getTitle());
    }

    @Test
    void testHistoryUpdate() {
        // Given
        History historyToUpdate = historyRepository.findById(history1.getId()).orElseThrow();
        String updatedTitle = "Updated Book Title";

        // When
        historyToUpdate.setTitle(updatedTitle);
        History updatedHistory = historyRepository.save(historyToUpdate);

        // Then
        assertEquals(updatedTitle, updatedHistory.getTitle());
    }

    @Test
    void testHistoryDeletion() {
        // Given
        Long historyId = history1.getId();

        // When
        historyRepository.deleteById(historyId);
        Optional<History> deletedHistory = historyRepository.findById(historyId);

        // Then
        assertFalse(deletedHistory.isPresent());
    }

    @Test
    void testHistoryFields() {
        // Given
        History history = historyRepository.findById(history1.getId()).orElseThrow();

        // Then
        assertNotNull(history.getId());
        assertEquals("user1@example.com", history.getUserEmail());
        assertEquals("2024-01-01", history.getCheckoutDate());
        assertEquals("2024-02-05", history.getReturnedDate());
        assertEquals("Book 1", history.getTitle());
        assertEquals("Author 1", history.getAuthor());
        assertEquals("Description 1", history.getDescription());
        assertEquals("image1.jpg", history.getImg());
    }

}