package com.xiaoyv.subtitle.api.subtitle.ass;

import androidx.annotation.NonNull;

import com.xiaoyv.subtitle.api.subtitle.common.TimedTextFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * The class <code>ASSSub</code> represents a SubStation Alpha subtitle
 */
public class ASSSub implements TimedTextFile<Events> {

    /**
     * Serial
     */
    private static final long serialVersionUID = 8812933867812351549L;


    /**
     * Format
     */
    public static final String FORMAT = "Format";

    /**
     * Events section
     */
    private static final String EVENTS = "[Events]";

    /**
     * Styles section
     */
    private static final String V4_STYLES = "[V4+ Styles]";

    /**
     * Script info section
     */
    private static final String SCRIPT_INFO = "[Script Info]";

    /**
     * Line separator
     */
    private static final String NEW_LINE = "\n";

    /**
     * Key / Value info separator. Ex : "Color: red"
     */
    public static final String SEP = ": ";

    /**
     * Subtitle name
     */
    private String filename;

    /**
     * Headers and general information about the script
     */
    private ScriptInfo scriptInfo = new ScriptInfo();

    /**
     * Style definitions required by the script
     */
    private List<V4Style> style = new ArrayList<>();

    /**
     * Events for the script - all the subtitles, comments, pictures, sounds, movies and
     * commands
     */
    private Set<Events> events = new TreeSet<>();

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // [Script Info]
        sb.append(SCRIPT_INFO).append(NEW_LINE).append(this.scriptInfo.toString());
        sb.append(NEW_LINE).append(NEW_LINE);

        // [V4 Styles]
        sb.append(V4_STYLES).append(NEW_LINE);
        sb.append(FORMAT).append(SEP).append(V4Style.FORMAT_STRING).append(NEW_LINE);
        this.style.forEach(s -> sb.append(s.toString()).append(NEW_LINE));
        sb.append(NEW_LINE);

        // [Events]
        sb.append(EVENTS).append(NEW_LINE);
        sb.append(FORMAT).append(SEP).append(Events.FORMAT_STRING).append(NEW_LINE);
        this.events.forEach(e -> sb.append(e.toString()).append(NEW_LINE));

        return sb.toString();
    }

    /**
     * Get the ass file as an input stream
     *
     * @return the file
     */
    public InputStream toInputStream() {
        return new ByteArrayInputStream(toString().getBytes());
    }

    // ===================== getter and setter start =====================

    public ScriptInfo getScriptInfo() {
        return this.scriptInfo;
    }

    public void setScriptInfo(ScriptInfo scriptInfo) {
        this.scriptInfo = scriptInfo;
    }

    public List<V4Style> getStyle() {
        return this.style;
    }

    public void setStyle(List<V4Style> style) {
        this.style = style;
    }

    public Set<Events> getEvents() {
        return this.events;
    }

    public void setEvents(Set<Events> events) {
        this.events = events;
    }

    public String getFilename() {
        return this.filename;
    }

    @Override
    public void setFileName(@NonNull String fileName) {
        this.filename = fileName;
    }

    @NonNull
    @Override
    public String getFileName() {
        return this.filename;
    }

    @NonNull
    @Override
    public Set<Events> getTimedLines() {
        return this.events;
    }
}
