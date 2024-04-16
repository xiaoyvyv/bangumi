package com.xiaoyv.subtitle.api.subtitle.common;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;

public class SubtitleLine<T extends TimedObject> implements TimedLine {

    /**
     * Serial Id
     */
    private static final long serialVersionUID = 288560648398584309L;

    /**
     * Subtitle Text. This is the actual text which will be displayed as a subtitle
     * onscreen.
     */
    protected ArrayList<String> textLines = new ArrayList<>();

    /**
     * Timecodes
     */
    protected T time;

    /**
     * Comparator that only compare timings
     */
    public static Comparator<TimedLine> timeComparator = Comparator.comparing(TimedLine::getTime);

    /**
     * Constructor
     */
    public SubtitleLine() {
        super();
    }

    /**
     * Constructor
     */
    public SubtitleLine(T time) {

        super();
        this.time = time;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        TimedLine other = (TimedLine) obj;
        return compareTo(other) == 0;
    }

    @Override
    public int compare(TimedLine o1, TimedLine o2) {

        return o1.compareTo(o2);
    }

    @Override
    public int compareTo(TimedLine o) {

        int compare = this.time.compareTo(o.getTime());
        if (compare == 0) {
            String thisText = String.join(",", this.textLines);
            String otherText = String.join(",", o.getTextLines());
            compare = thisText.compareTo(otherText);
        }

        return compare;
    }

    // ===================== getter and setter start =====================

    @NonNull
    @Override
    public T getTime() {
        return this.time;
    }

    public void setTime(T time) {
        this.time = time;
    }

    @NonNull
    @Override
    public ArrayList<String> getTextLines() {
        return this.textLines;
    }

    public void setTextLines(ArrayList<String> textLines) {
        this.textLines = textLines;
    }

}
