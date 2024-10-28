package com.studyNook.common.options.repository;

import com.studyNook.common.options.repository.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CodeRepository extends JpaRepository<Code, String> {

    @Query(value = "SELECT n FROM Code n WHERE n.useYn = 'Y' AND n.groupId like 'L0%' ")
    List<Code> findSignupOptions();
}
