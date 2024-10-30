package com.studyNook.common.options.dto;

import java.util.List;

public record SignupOptionDto(List<JobOptions> jobOptions,
                              List<ExperienceOptions> experienceOptions,
                              List<SkillCategories> skillCategories,
                              List<StepTitles> stepTitles,
                              List<StateOptions> stateOptions) {

    public record JobOptions(String id, String codeName){}
    public record ExperienceOptions(String id, String codeName){}
    public record StepTitles(String id, String codeName){}
    public record StateOptions(String id, String codeName){}

    public record SkillCategories(String name,
                                  List<Skills> skills,
                                  String imageSrc) {}

    public record Skills(String id, String codeName){}

}
