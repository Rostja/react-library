package com.eshop.spring_boot_library.service;

import com.eshop.spring_boot_library.dao.ReviewRepository;
import com.eshop.spring_boot_library.entity.Review;
import com.eshop.spring_boot_library.requestmodels.ReviewRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    private ReviewService reviewService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        reviewService = new ReviewService(reviewRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void postReview_SuccessfulWithDescription() throws Exception {
        // Arrange
        String userEmail = "test@example.com";
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setBookId(1L);
        reviewRequest.setRating(4.5);
        reviewRequest.setReviewDescription(Optional.of("Great book!"));

        when(reviewRepository.findByUserEmailAndBookId(userEmail, 1L)).thenReturn(null);

        // Act
        reviewService.postReview(userEmail, reviewRequest);

        // Assert
        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewCaptor.capture());

        Review savedReview = reviewCaptor.getValue();
        assertEquals(1L, savedReview.getBookId());
        assertEquals(4.5, savedReview.getRating());
        assertEquals(userEmail, savedReview.getUserEmail());
        assertEquals("Great book!", savedReview.getReviewDescription());
        assertEquals(Date.valueOf(LocalDate.now()), savedReview.getDate());
    }

    @Test
    void postReview_SuccessfulWithoutDescription() throws Exception {
        // Arrange
        String userEmail = "test@example.com";
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setBookId(1L);
        reviewRequest.setRating(4.5);
        reviewRequest.setReviewDescription(Optional.empty());

        when(reviewRepository.findByUserEmailAndBookId(userEmail, 1L)).thenReturn(null);

        // Act
        reviewService.postReview(userEmail, reviewRequest);

        // Assert
        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewCaptor.capture());

        Review savedReview = reviewCaptor.getValue();
        assertEquals(1L, savedReview.getBookId());
        assertEquals(4.5, savedReview.getRating());
        assertEquals(userEmail, savedReview.getUserEmail());
        assertNull(savedReview.getReviewDescription());
    }

    @Test
    void postReview_ThrowsException_WhenReviewAlreadyExists() {
        // Arrange
        String userEmail = "test@example.com";
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setBookId(1L);

        Review existingReview = new Review();
        when(reviewRepository.findByUserEmailAndBookId(userEmail, 1L)).thenReturn(existingReview);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            reviewService.postReview(userEmail, reviewRequest);
        });

        assertEquals("Review already created", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void userReviewListed_ReturnsTrue_WhenReviewExists() {
        // Arrange
        String userEmail = "test@example.com";
        Long bookId = 1L;
        Review existingReview = new Review();

        when(reviewRepository.findByUserEmailAndBookId(userEmail, bookId)).thenReturn(existingReview);

        // Act
        boolean result = reviewService.userReviewListed(userEmail, bookId);

        // Assert
        assertTrue(result);
        verify(reviewRepository).findByUserEmailAndBookId(userEmail, bookId);
    }

    @Test
    void userReviewListed_ReturnsFalse_WhenReviewDoesNotExist() {
        // Arrange
        String userEmail = "test@example.com";
        Long bookId = 1L;

        when(reviewRepository.findByUserEmailAndBookId(userEmail, bookId)).thenReturn(null);

        // Act
        boolean result = reviewService.userReviewListed(userEmail, bookId);

        // Assert
        assertFalse(result);
        verify(reviewRepository).findByUserEmailAndBookId(userEmail, bookId);
    }

    @Test
    void postReview_ValidatesAllFields() throws Exception {
        // Arrange
        String userEmail = "test@example.com";
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setBookId(1L);
        reviewRequest.setRating(5.0);
        reviewRequest.setReviewDescription(Optional.of("Detailed review"));

        when(reviewRepository.findByUserEmailAndBookId(userEmail, 1L)).thenReturn(null);

        // Act
        reviewService.postReview(userEmail, reviewRequest);

        // Assert
        verify(reviewRepository).save(argThat(review ->
                review.getBookId().equals(1L) &&
                        review.getRating() == 5.0 &&
                        review.getUserEmail().equals(userEmail) &&
                        review.getReviewDescription().equals("Detailed review") &&
                        review.getDate() != null
        ));
    }
}