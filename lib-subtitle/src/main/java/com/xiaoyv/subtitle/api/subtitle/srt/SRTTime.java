package com.xiaoyv.subtitle.api.subtitle.srt;

import androidx.annotation.NonNull;

import com.xiaoyv.subtitle.api.subtitle.common.SubtitleTime;

import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoField;

import java.util.Locale;

public class SRTTime extends SubtitleTime {

    private static final long serialVersionUID = -5787808223967579723L;

    public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(SRTTime.PATTERN);
    public static final String PATTERN = "HH:mm:ss,SSS";
    private static final String TS_PATTERN = "%02d:%02d:%02d,%03d";
    public static final String DELIMITER = " --> ";

    public SRTTime() {
        super();
    }

    public SRTTime(LocalTime start, LocalTime end) {

        super(start, end);
    }

    @NonNull
    @Override
    public String toString() {
        return format(this.start) + DELIMITER + format(this.end);
    }

    /**
     * Convert a <code>LocalTime</code> to string
     *
     * @param time: the time to format
     * @return the formatted time
     */
    public static String format(LocalTime time) {

        int hr = time.get(ChronoField.HOUR_OF_DAY);
        int min = time.get(ChronoField.MINUTE_OF_HOUR);
        int sec = time.get(ChronoField.SECOND_OF_MINUTE);
        int ms = time.get(ChronoField.MILLI_OF_SECOND);

        return String.format(Locale.getDefault(), TS_PATTERN, hr, min, sec, ms);
    }

    /**
     * Convert a string pattern to a Local time
     *
     * @param times SRTTime.PATTERN
     */
    public static LocalTime fromString(String times) {
        return LocalTime.parse(times.replace('.', ',').trim(), FORMATTER);
    }
}
