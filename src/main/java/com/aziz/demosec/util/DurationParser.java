package com.aziz.demosec.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses free-text duration strings that doctors enter in PrescriptionItem.duration.
 *
 * Supported formats (case-insensitive):
 *   "7 days"  "7d"  "7"
 *   "2 weeks" "2w"
 *   "1 month" "1m"
 *   "3 months"
 *
 * Returns Optional.empty() for unrecognised strings so the caller can skip safely.
 */
@Slf4j
@UtilityClass
public class DurationParser {

    private static final Pattern PATTERN = Pattern.compile(
            "^\\s*(\\d+)\\s*(d(?:ays?)?|w(?:eeks?)?|m(?:onths?)?)?\\s*$",
            Pattern.CASE_INSENSITIVE
    );

    public static Optional<LocalDate> resolveEndDate(LocalDate startDate, String duration) {
        if (duration == null || duration.isBlank()) return Optional.empty();

        Matcher m = PATTERN.matcher(duration.trim());
        if (!m.matches()) {
            log.warn("[DurationParser] Unrecognised duration string: '{}'", duration);
            return Optional.empty();
        }

        int amount = Integer.parseInt(m.group(1));
        String unit = m.group(2) == null ? "d" : m.group(2).toLowerCase();

        Period period;
        if (unit.startsWith("w")) {
            period = Period.ofDays(amount * 7);
        } else if (unit.startsWith("m")) {
            period = Period.ofMonths(amount);
        } else {
            period = Period.ofDays(amount);   // default: days
        }

        return Optional.of(startDate.plus(period));
    }
}