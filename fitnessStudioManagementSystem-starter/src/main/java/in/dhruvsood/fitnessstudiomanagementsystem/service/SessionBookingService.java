package in.dhruvsood.fitnessstudiomanagementsystem.service;

import in.dhruvsood.fitnessstudiomanagementsystem.entity.Member;
import in.dhruvsood.fitnessstudiomanagementsystem.entity.SessionBooking;
import in.dhruvsood.fitnessstudiomanagementsystem.entity.Trainer;
import in.dhruvsood.fitnessstudiomanagementsystem.repository.MemberRepository;
import in.dhruvsood.fitnessstudiomanagementsystem.repository.SessionBookingRepository;
import in.dhruvsood.fitnessstudiomanagementsystem.repository.TrainerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionBookingService {


    private final SessionBookingRepository sessionBookingRepository;
    private final TrainerRepository trainerRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public SessionBooking createNewSessionBooking(
            SessionBooking sessionBooking,
            Long memberId,
            Long trainerId
    ) {

        Member member = memberRepository.findById(memberId).orElseThrow();
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow();

        sessionBooking.setMember(member);
        sessionBooking.setTrainer(trainer);

        return sessionBookingRepository.save(sessionBooking);

    }
}
