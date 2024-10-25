package com.studyNook.common.options.repository;

import com.studyNook.common.options.repository.entity.SignupOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignupOptionRepository extends JpaRepository<SignupOption, Long> {
}
