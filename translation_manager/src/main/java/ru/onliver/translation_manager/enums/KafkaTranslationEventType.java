package ru.onliver.translation_manager.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum KafkaTranslationEventType {
    TRANSLATION_STARTED("translation_started"),
    TRANSLATION_ENDED_PLANNED("translation_ended_planned"),
    TRANSLATION_ENDED_MANUAL("translation_ended_manual"),
    TRANSLATION_ENDED_EMERGENCY("translation_ended_emergency"),
    TRANSLATION_FINISHED_PLANNED("translation_finished_planned"),
    TRANSLATION_FINISHED_ABORTED("translation_finished_aborted"),;

    private final String code;

    KafkaTranslationEventType(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    @JsonValue
    public String getCode() { return code; }

    @JsonCreator
    public static KafkaTranslationEventType fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow();
    }
}
