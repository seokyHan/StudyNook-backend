package com.studyNook.member.repository;

import com.studyNook.member.repository.entity.Member;
import com.studyNook.oauth2.types.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
    Optional<Member> findByEmail(String email);
}
