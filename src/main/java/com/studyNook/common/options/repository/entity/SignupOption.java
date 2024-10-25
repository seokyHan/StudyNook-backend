package com.studyNook.common.options.repository.entity;

import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@QueryEntity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SIGNUP_OPTION")
public class SignupOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "option_type")
    private String optionType;

    @Column(name = "value")
    private String value;

    @Column(name = "category")
    private String category;
}
