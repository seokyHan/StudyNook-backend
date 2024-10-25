package com.studyNook.common.options.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SkillCategoriesType {
    DEVELOP("개발", "/images/develop.png"),
    PLANNING("기획", "/images/planning.png"),
    DESIGN("디자인", "/images/design.png"),
    MARKETING("마케팅", "/images/marketing.png")
    ;

    private final String name;
    private final String imageSrc;
}
