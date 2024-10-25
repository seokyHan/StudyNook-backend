package com.studyNook.common.options.dto;

import java.util.List;

public record SignupOptionDto(List<String> jobOptions,
                              List<String> experienceOptions,
                              List<SkillCategories> skillCategories,
                              List<String> stepTitles,
                              List<String> stateOptions) {

    public record SkillCategories(String name,
                                  List<String> skills,
                                  String imageSrc) {}

}
