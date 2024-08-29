package com.studyNook.member.repository.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@QueryEntity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    @JsonManagedReference(value = "member-authority")
    private List<MemberAuthority> authorities;

}
