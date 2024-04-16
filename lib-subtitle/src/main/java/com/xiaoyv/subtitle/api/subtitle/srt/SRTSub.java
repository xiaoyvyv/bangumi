package com.xiaoyv.subtitle.api.subtitle.srt;

import androidx.annotation.NonNull;

import java.util.Set;
import java.util.TreeSet;

import com.xiaoyv.subtitle.api.subtitle.common.TimedLine;
import com.xiaoyv.subtitle.api.subtitle.common.TimedTextFile;

/**
 * Class <SRTLine> represents an SRT file, meandin a complete set of subtitle lines
 */
public class SRTSub implements TimedTextFile<SRTLine> {

    private static final long serialVersionUID = -2909833999376537734L;

    private String fileName;
    private Set<SRTLine> lines = new TreeSet<>();

    // ======================== Public methods ==========================

    public void add(SRTLine line) {

        this.lines.add(line);
    }

    public void remove(TimedLine line) {

        this.lines.remove(line);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.lines.forEach(sb::append);
        return sb.toString();
    }

    // ===================== getter and setter start =====================

    public Set<SRTLine> getLines() {
        return this.lines;
    }

    @NonNull
    @Override
    public Set<SRTLine> getTimedLines() {
        return this.lines;
    }

    public void setLines(Set<SRTLine> lines) {
        this.lines = lines;
    }

    @NonNull
    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public void setFileName(@NonNull String fileName) {
        this.fileName = fileName;
    }

}
