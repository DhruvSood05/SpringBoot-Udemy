package in.dhruvsood.fitnessstudiomanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;


    @OneToOne
    @JoinColumn(
            name = "head_trainer_id",
            nullable = false,
            unique = true


    )
    private Trainer headTrainer;


    @ManyToMany
    @JoinTable(
            name = "trainer_zone",
            joinColumns = {
                    @JoinColumn(name = "zone_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "trainer_id")
            }
    )
    private Set<Trainer> trainers = new HashSet<>();
}
