package in.dhruvsood.fitnessstudiomanagementsystem.service;

import in.dhruvsood.fitnessstudiomanagementsystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

}
