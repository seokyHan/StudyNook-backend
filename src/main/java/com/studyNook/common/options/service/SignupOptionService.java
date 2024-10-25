package com.studyNook.common.options.service;

import com.studyNook.common.options.dto.SignupOptionDto;
import com.studyNook.common.options.repository.SignupOptionRepository;
import com.studyNook.common.options.repository.entity.SignupOption;
import com.studyNook.common.options.types.OptionType;
import com.studyNook.common.options.types.SkillCategoriesType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.studyNook.common.options.types.OptionType.*;

@Service
@RequiredArgsConstructor
public class SignupOptionService {

    private final SignupOptionRepository signupOptionRepository;

    public SignupOptionDto findAll () {
        List<SignupOption> options = signupOptionRepository.findAll();

        List<String> jobOptions = filterOptionsByType(options, JOB);
        List<String> experienceOptions = filterOptionsByType(options, EXPERIENCE);
        List<SignupOptionDto.SkillCategories> skillCategories = getSkillCategories(options, SKILL);
        List<String> stepTitles = filterOptionsByType(options, STEP_TITLE);
        List<String> stateOptions = filterOptionsByType(options, STATE);


        return new SignupOptionDto(jobOptions, experienceOptions, skillCategories, stepTitles, stateOptions);
    }

    private List<String> filterOptionsByType(List<SignupOption> options, OptionType type){
        return options.stream()
                .filter(option -> StringUtils.equalsIgnoreCase(type.getType(), option.getOptionType()))
                .map(SignupOption::getValue)
                .collect(Collectors.toList());
    }

    private List<SignupOptionDto.SkillCategories> getSkillCategories(List<SignupOption> options, OptionType type) {
        return Arrays.stream(SkillCategoriesType.values())
                .map(category -> {
                    List<String> skills = options.stream()
                            .filter(option -> StringUtils.equalsIgnoreCase(type.getType(), option.getOptionType()) &&
                                    StringUtils.equalsIgnoreCase(category.getName(), option.getCategory()))
                            .map(SignupOption::getValue)
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
