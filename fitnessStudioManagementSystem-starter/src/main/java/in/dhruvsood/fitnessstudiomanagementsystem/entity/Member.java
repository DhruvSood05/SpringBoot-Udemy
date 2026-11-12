package in.dhruvsood.fitnessstudiomanagementsystem.entity;

import in.dhruvsood.fitnessstudiomanagementsystem.entity.type.MembershipTier;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@ToString
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate birthDate;

    private String email;

    private String gender;

    // default is ordinal
    @Enumerated(value = EnumType.STRING)
    private MembershipTier membershipTier;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "membership_plan_id", unique = true)
    private MembershipPlan membershipPlan;


    @OneToMany(mappedBy = "member")
    private Set<SessionBooking> bookings = new HashSet<>();
}
