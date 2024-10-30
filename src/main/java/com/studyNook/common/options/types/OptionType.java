package com.studyNook.common.options.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OptionType {
    EXPERIENCE("L01"),
    JOB("L02"),
    SKILL("L03"),
    STATE("L04"),
    STEP_TITLE("L05");

    private final String code;
}
