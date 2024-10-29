package com.studyNook.common.options.repository.entity;

import com.querydsl.core.annotations.QueryEntity;
import com.studyNook.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@QueryEntity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "CODE")
public class Code extends BaseTimeEntity {

    @Id
    @Column(name = "code_id")
    private String codeId;

    @Column(name = "group_id")
    private String groupId;

    @Column(name = "code_nm")
    private String codeNm;

    @Column(name = "use_yn")
    private String useYn;

    @Column(name = "remark")
    private String remark;
}
