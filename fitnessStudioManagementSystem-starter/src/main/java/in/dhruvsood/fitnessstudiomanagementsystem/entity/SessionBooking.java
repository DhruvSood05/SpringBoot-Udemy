package in.dhruvsood.fitnessstudiomanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SessionBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime sessionTime;

    @Column(nullable = false)
    private String reason;

    @ManyToOne
    @JoinColumn(name = "member_id",
            nullable = false)
    private Member member;


    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

}
