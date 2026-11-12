package in.dhruvsood.fitnessstudiomanagementsystem;

import in.dhruvsood.fitnessstudiomanagementsystem.entity.SessionBooking;
import in.dhruvsood.fitnessstudiomanagementsystem.service.SessionBookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class SessionBookingTest {
    @Autowired
    private SessionBookingService sessionBookingService;

    @Test
    void testCreateSessionBooking() {
        SessionBooking sessionBooking = SessionBooking.builder()
                .sessionTime(LocalDateTime.of(2025, 11, 1, 14, 12, 34))
                .reason("Personal training session")
                .build();

        SessionBooking updatedSessionBooking = sessionBookingService.createNewSessionBooking(
                sessionBooking,
                2L,
                2L
        );

        System.out.println(updatedSessionBooking);
    }
}
