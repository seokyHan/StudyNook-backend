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
@Table(name = "CODE_GROUP")
public class CodeGroup extends BaseTimeEntity {

    @Id
    @Column(name = "group_id")
    private String groupId;

    @Column(name = "group_nm")
    private String groupNm;

    @Column(name = "use_yn")
    private String useYn;

    @Column(name = "remark")
    private String remark;

}
