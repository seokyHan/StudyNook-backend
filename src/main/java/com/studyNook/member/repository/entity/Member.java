package com.studyNook.member.repository.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.querydsl.core.annotations.QueryEntity;
import com.studyNook.oauth2.types.SocialType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@QueryEntity
@Getter
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

    @Column(name = "nickName")
    private String nickName;

    @Column(name = "activated")
    private boolean activated;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    @JsonManagedReference(value = "member-authority")
    private List<MemberAuthority> authorities;

    @Enumerated(EnumType.STRING)
    @Column(name = "SOCIAL_TYPE")
    private SocialType socialType;

    @Column(name = "SOCIAL_ID")
    private String socialId;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Builder
    public Member(String email,
                  String password,
                  String name,
                  String nickName,
                  boolean activated,
                  List<MemberAuthority> authorities,
                  SocialType socialType,
                  String socialId,
                  String imageUrl) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickName = nickName;
        this.activated = activated;
        this.authorities = authorities;
        this.socialType = socialType;
        this.socialId = socialId;
        this.imageUrl = imageUrl;
    }

}
