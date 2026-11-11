package in.dhruvsood.fitnessstudiomanagementsystem.entity;

import in.dhruvsood.fitnessstudiomanagementsystem.entity.type.MembershipTier;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
}
