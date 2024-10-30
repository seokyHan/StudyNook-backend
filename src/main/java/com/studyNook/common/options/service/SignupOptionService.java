package com.studyNook.common.options.service;

import com.studyNook.common.options.dto.SignupOptionDto;
import com.studyNook.common.options.repository.CodeRepository;
import com.studyNook.common.options.repository.entity.Code;
import com.studyNook.common.options.types.OptionType;
import com.studyNook.common.options.types.SkillCategoriesType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.studyNook.common.options.types.OptionType.*;

@Service
@RequiredArgsConstructor
public class SignupOptionService {

    private final CodeRepository codeRepository;

    public SignupOptionDto findSignupOptions () {
        List<Code> options = codeRepository.findSignupOptions();

        var jobOptions = filterOptionsByType(options, JOB, SignupOptionDto.JobOptions::new);
        var experienceOptions = filterOptionsByType(options, EXPERIENCE, SignupOptionDto.ExperienceOptions::new);
        var skillCategories = getSkillCategories(options, SKILL);
        var stateOptions = filterOptionsByType(options, STATE, SignupOptionDto.StateOptions::new);
        var stepTitles = filterOptionsByType(options, STEP_TITLE, SignupOptionDto.StepTitles::new);


        return new SignupOptionDto(jobOptions, experienceOptions, skillCategories, stepTitles, stateOptions);
    }

    /**
     *
     * BiFunction<String, String, T> mapper
     * BiFunction 인터페이스:자바의 java.util.function 패키지에 정의된 함수형 인터페이스.
     * 두 개의 입력을 받아 하나의 출력을 반환하는 함수로, 제네릭을 사용하여 입력 타입과 출력 타입을 정의.
     * 구문: BiFunction<T, U, R> 형태로 정의되며, 여기서 T와 U는 입력 타입, R은 출력 타입을 나타냄. 이 경우 T는 SignupOptionDto.JobOptions와 같은 DTO 타입.
     * 사용 목적: mapper는 filterOptionsByType 메소드에서 필터링된 Code 객체의 id와 codeName을 인자로 받아 해당 DTO 타입의 객체를 생성하는 데 사용.
     *
     */
    private <T> List<T> filterOptionsByType(List<Code> options, OptionType type, BiFunction<String, String, T> mapper) {
        return options.stream()
                .filter(option -> StringUtils.equalsIgnoreCase(type.getCode(), option.getGroupId()))
                .map(option -> mapper.apply(option.getCodeId(), option.getCodeNm()))
                .collect(Collectors.toList());
    }

    private List<SignupOptionDto.SkillCategories> getSkillCategories(List<Code> options, OptionType type) {
        return Arrays.stream(SkillCategoriesType.values())
                .map(category -> {
                    List<SignupOptionDto.Skills> skills = options.stream()
                            .filter(option -> StringUtils.equalsIgnoreCase(type.getCode(), option.getGroupId()) &&
                                    StringUtils.equalsIgnoreCase(category.getName(), option.getRemark()))
                            .map(option -> new SignupOptionDto.Skills(option.getCodeId(), option.getCodeNm()))
                            .collect(Collectors.toList());
                    return new SignupOptionDto.SkillCategories(
                            category.getName(),
                            skills,
                            category.getImageSrc()
                    );
                })
                .collect(Collectors.toList());
    }
}
