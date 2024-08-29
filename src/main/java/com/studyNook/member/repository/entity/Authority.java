package com.studyNook.member.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "authority_name", length = 30)
    private String authorityName;

    @OneToMany
    @JoinColumn(name = "authority_id", referencedColumnName = "id")
    @JsonBackReference(value = "authority")
    private Set<MemberAuthority> authorities;
}
