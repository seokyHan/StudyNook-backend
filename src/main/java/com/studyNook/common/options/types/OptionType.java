package com.studyNook.common.options.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OptionType {
    JOB("job"),
    EXPERIENCE("experience"),
    SKILL("skill"),
    STEP_TITLE("stepTitle"),
    STATE("state");

    private final String type;
}
