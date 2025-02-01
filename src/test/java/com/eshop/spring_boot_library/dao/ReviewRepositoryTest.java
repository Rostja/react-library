package com.eshop.spring_boot_library.dao;

import com.eshop.spring_boot_library.entity.Review;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    private Review review1;
    private Review review2;
    private Review review3;

    @BeforeEach
    void setUp() {
        // Initialize test reviews
        review1 = new Review();
        review1.setUserEmail("user1@example.com");
        review1.setRating(4.5);
        review1.setBookId(1L);
        review1.setReviewDescription("Great book!");

        review2 = new Review();
        review2.setUserEmail("user2@example.com");
        review2.setRating(3.5);
        review2.setBookId(1L);
        review2.setReviewDescription("Good read");

        review3 = new Review();
        review3.setUserEmail("user1@example.com");
        review3.setRating(5.0);
        review3.setBookId(2L);
        review3.setReviewDescription("Excellent!");

        // Save test data
        reviewRepository.saveAll(List.of(review1, review2, review3));
    }

    @Test
    void findByBookId_ShouldReturnReviewsForSpecificBook() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Long bookId = 1L;

        // When
        Page<Review> reviews = reviewRepository.findByBookId(bookId, pageable);

        // Then
        assertEquals(2, reviews.getTotalElements());
        assertTrue(reviews.getContent().stream()
                .allMatch(review -> review.getBookId().equals(bookId)));
    }

    @Test
    void findByBookId_NonExistingBook_ShouldReturnEmptyPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Long nonExistingBookId = 999L;

        // When
        Page<Review> reviews = reviewRepository.findByBookId(nonExistingBookId, pageable);

        // Then
        assertEquals(0, reviews.getTotalElements());
        assertTrue(reviews.getContent().isEmpty());
    }

    @Test
    void findByUserEmailAndBookId_ShouldReturnSpecificReview() {
        // Given
        String userEmail = "user1@example.com";
        Long bookId = 1L;

        // When
        Review foundReview = reviewRepository.findByUserEmailAndBookId(userEmail, bookId);

        // Then
        assertNotNull(foundReview);
        assertEquals(userEmail, foundReview.getUserEmail());
        assertEquals(bookId, foundReview.getBookId());
        assertEquals(4.5, foundReview.getRating());
    }

    @Test
    void findByUserEmailAndBookId_NonExisting_ShouldReturnNull() {
        // Given
        String nonExistingEmail = "nonexistent@example.com";
        Long bookId = 1L;

        // When
        Review foundReview = reviewRepository.findByUserEmailAndBookId(nonExistingEmail, bookId);

        // Then
        assertNull(foundReview);
    }

    @Test
    void deleteAllByBookId_ShouldRemoveAllReviewsForBook() {
        // Given
        Long bookId = 1L;

        // When
        reviewRepository.deleteAllByBookId(bookId);
        Page<Review> remainingReviews = reviewRepository.findByBookId(bookId, PageRequest.of(0, 10));

        // Then
        assertEquals(0, remainingReviews.getTotalElements());
        assertEquals(1, reviewRepository.count()); // Only review3 should remain
    }

    @Test
    void createReview_ShouldSaveAndGenerateId() {
        // Given
        Review newReview = new Review();
        newReview.setUserEmail("user3@example.com");
        newReview.setRating(4.0);
        newReview.setBookId(3L);
        newReview.setReviewDescription("Nice book");

        // When
        Review savedReview = reviewRepository.save(newReview);

        // Then
        assertNotNull(savedReview.getId());
        assertEquals("user3@example.com", savedReview.getUserEmail());
        assertEquals(4.0, savedReview.getRating());
        assertNotNull(savedReview.getDate()); // Check if creation timestamp is set
    }

    @Test
    void updateReview_ShouldModifyExistingReview() {
        // Given
        Review reviewToUpdate = reviewRepository.findById(review1.getId()).orElseThrow();
        double newRating = 3.0;
        String newDescription = "Updated review";

        // When
        reviewToUpdate.setRating(newRating);
        reviewToUpdate.setReviewDescription(newDescription);
        Review updatedReview = reviewRepository.save(reviewToUpdate);

        // Then
        assertEquals(newRating, updatedReview.getRating());
        assertEquals(newDescription, updatedReview.getReviewDescription());
    }

    @Test
    void findByBookId_WithPagination_ShouldReturnCorrectPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);
        Long bookId = 1L;

        // When
        Page<Review> reviews = reviewRepository.findByBookId(bookId, pageable);

        // Then
        assertEquals(1, reviews.getContent().size());
        assertEquals(2, reviews.getTotalElements());
        assertEquals(2, reviews.getTotalPages());
    }

    @Test
    void verifyReviewDate_ShouldBeSetAutomatically() {
        // Given
        Review newReview = new Review();
        newReview.setUserEmail("user4@example.com");
        newReview.setRating(4.0);
        newReview.setBookId(4L);
        newReview.setReviewDescription("Time test review");

        // When
        Review savedReview = reviewRepository.save(newReview);

        // Then
        assertNotNull(savedReview.getDate());
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll();
    }
}