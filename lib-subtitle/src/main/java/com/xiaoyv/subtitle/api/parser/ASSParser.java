package com.xiaoyv.subtitle.api.parser;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.xiaoyv.subtitle.api.parser.exception.InvalidAssSubException;
import com.xiaoyv.subtitle.api.subtitle.ass.ASSSub;
import com.xiaoyv.subtitle.api.subtitle.ass.ASSTime;
import com.xiaoyv.subtitle.api.subtitle.ass.Events;
import com.xiaoyv.subtitle.api.subtitle.ass.ScriptInfo;
import com.xiaoyv.subtitle.api.subtitle.ass.V4Style;
import com.xiaoyv.subtitle.api.utils.ColorUtils;
import com.xiaoyv.subtitle.api.utils.NumberUtils;
import com.xiaoyv.subtitle.api.utils.PropertyUtils;
import com.xiaoyv.subtitle.api.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

/**
 * Parse SSA/ASS subtitles
 */
public class ASSParser extends BaseParser<Events, ASSSub> {

    /**
     * Comments: lines that start with this character are ignored
     */
    private static final String COMMENTS_MARK = ";";

    @Override
    protected void parse(@NonNull BufferedReader br, @NonNull ASSSub sub) throws IOException, InvalidAssSubException {
        String line = readFirstTextLine(br);

        if (line != null && !("[script info]").equalsIgnoreCase(line.trim())) {
            throw new InvalidAssSubException("The line that says “[Script Info]” must be the first line in the script.");
        }

        // [Script Info]
        sub.setScriptInfo(parseScriptInfo(br));

        while ((line = readFirstTextLine(br)) != null) {
            if (line.matches("(?i:^\\[v.*styles\\+?]$)")) {
                // [V4+ Styles]
                sub.setStyle(parseStyle(br));
            } else if (line.equalsIgnoreCase("[events]")) {
                // [Events]
                sub.setEvents(parseEvents(br));
            }
        }

        if (sub.getStyle().isEmpty()) {
            throw new InvalidAssSubException("Missing style definition");
        }

        if (sub.getEvents().isEmpty()) {
            throw new InvalidAssSubException("No text line found");
        }
    }


    /**
     * Parse the events section from the reader. <br/>
     * <p>
     * Example of events section:
     *
     * <pre>
     * [Events]
     * Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text
     * Dialogue: 0,0:02:30.84,0:02:34.70,StlyeOne,,0000,0000,0000,,A text line
     * Dialogue: 0,0:02:34.92,0:02:37.54,StyleTwo,,0000,0000,0000,,Another text line
     * </pre>
     *
     * @param br: the buffered reader
     */
    private static Set<Events> parseEvents(BufferedReader br) throws IOException, InvalidAssSubException {
        String[] eventsFormat = findFormat(br, "events");

        Set<Events> events = new TreeSet<>();
        String line = readFirstTextLine(br);

        while (line != null && !line.startsWith("[")) {

            if (line.startsWith(Events.DIALOGUE) && !line.startsWith(COMMENTS_MARK)) {
                String info = findInfo(line, Events.DIALOGUE);
                String[] dialogLine = StringUtils.splitByWholeSeparatorPreserveAllTokens(info, Events.SEP);
                if (dialogLine == null) dialogLine = new String[]{};

                // The last field will always be the Text field, so that it can contain
                // commas.
                int lengthDialog = dialogLine.length;
                int lengthFormat = eventsFormat.length;

                if (lengthDialog < lengthFormat) {
                    throw new InvalidAssSubException("Incorrect dialog line : " + info);
                }

                if (lengthDialog > lengthFormat) {
                    // The text field contains commas
                    StringJoiner joiner = new StringJoiner(Events.SEP);
                    for (int i = lengthFormat - 1; i < lengthDialog; i++) {
                        joiner.add(dialogLine[i]);
                    }
                    dialogLine[lengthFormat - 1] = joiner.toString();
                    dialogLine = Arrays.copyOfRange(dialogLine, 0, lengthFormat);
                }

                events.add(parseDialog(eventsFormat, dialogLine));
            }

            line = markAndRead(br);

        }

        reset(br, line);

        return events;
    }

    /**
     * Parse the style section from the reader. <br/>
     * <p>
     * Example of style section:
     *
     * <pre>
     * [V4+ Styles]
     * Format: Name,Fontname,Fontsize,PrimaryColour,SecondaryColour,OutlineColour
     * Style: StyleOne,Arial,16,64250,16777215,0
     * Style: StyleTwo,Arial,16,16383999,16777215,0
     * </pre>
     *
     * @param br: the buffered reader
     * @throws IOException
     * @throws InvalidAssSubException
     */
    private static List<V4Style> parseStyle(BufferedReader br) throws IOException, InvalidAssSubException {
        String[] styleFormat = findFormat(br, "styles");

        List<V4Style> styles = new ArrayList<>();
        String line = readFirstTextLine(br);
        int index = 1;
        while (line != null && !line.startsWith("[")) {
            if (line.startsWith(V4Style.STYLE) && !line.startsWith(COMMENTS_MARK)) {
                String[] textLine = line.split(":");
                if (textLine.length > 1) {
                    String[] styleLine = textLine[1].split(V4Style.SEP);
                    styles.add(parseV4Style(styleFormat, styleLine, index));
                    index++;
                }
            }

            line = markAndRead(br);
        }

        reset(br, line);

        return styles;
    }

    /**
     * Return the Events object from text dialog line
     *
     * @param eventsFormat: the format definition
     * @param dialogLine:   the dialog line
     * @return the Events object
     * @throws InvalidAssSubException
     */
    private static Events parseDialog(String[] eventsFormat, String[] dialogLine) throws InvalidAssSubException {

        Events events = new Events();

        for (int i = 0; i < eventsFormat.length; i++) {
            String property = StringUtils.unCapitalize(eventsFormat[i].trim());
            String value = dialogLine[i].trim();

            try {
                switch (property) {
                    case "start":
                        events.getTime().setStart(ASSTime.fromString(value));
                        break;
                    case "end":
                        events.getTime().setEnd(ASSTime.fromString(value));
                        break;
                    case "text":
                        List<String> textLines = Arrays.asList(value.split("\\\\N"));
                        events.setTextLines(new ArrayList<>(textLines));
                        break;
                    default:
                        String error = callProperty(events, property, value);
                        if (error != null) {
                            throw new InvalidAssSubException("Invalid property (" + property + ") " + value);
                        }
                        break;
                }
            } catch (Exception e) {
                throw new InvalidAssSubException("Invalid time for property " + property + " : " + value);
            }

        }

        return events;
    }

    /**
     * Return the V4Style object from text style line
     *
     * @param styleFormat: format line
     * @param styleLine:   the style line
     * @param lineIndex:   the line index
     * @return the style object
     * @throws InvalidAssSubException
     */
    private static V4Style parseV4Style(String[] styleFormat, String[] styleLine, int lineIndex)
            throws InvalidAssSubException {

        String message = "Style at index " + lineIndex + ": ";

        if (styleFormat.length != styleLine.length) {
            throw new InvalidAssSubException(message + "does not match style definition");
        }

        V4Style style = new V4Style();
        for (int i = 0; i < styleFormat.length; i++) {
            String property = StringUtils.unCapitalize(styleFormat[i].trim());
            String value = styleLine[i].trim();

            if (property.toLowerCase().contains("colour")) {
                // Colors can be number (bgr) or string (&HBBGGRR or &HAABBGGRR)
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    int bgr = getBGR(value);
                    if (bgr != -1) {
                        value = Integer.toString(bgr);
                    }
                }
            }

            String error = callProperty(style, property, value);

            if (error != null) {
                throw new InvalidAssSubException(message + error);
            }
        }

        if (TextUtils.isEmpty(style.getName())) {
            throw new InvalidAssSubException(message + " missing name");
        }

        return style;
    }

    /**
     * Get the BGR code from the &HBBGGRR or &HAABBGGRR pattern
     *
     * @param value: the value to convert
     * @return the bgr code
     */
    private static int getBGR(String value) {

        int length = value.length();
        int bgr = -1;
        if (length == 10) {
            // From ASS
            bgr = ColorUtils.HAABBGGRRToBGR(value);
        } else if (length == 8) {
            // From SSA
            bgr = ColorUtils.HBBGGRRToBGR(value);
        }
        return bgr;
    }

    /**
     * Parse the script info section from the reader. <br/>
     * <p>
     * Example of script info section:
     *
     * <pre>
     * [Script Info]
     * ScriptType: v4.00+
     * Collisions: Normal
     * Timer: 100,0000
     * Title: My movie title
     * </pre>
     *
     * @param br: the buffered reader
     * @throws IOException
     * @throws InvalidAssSubException
     */
    private static ScriptInfo parseScriptInfo(BufferedReader br) throws IOException, InvalidAssSubException {

        ScriptInfo scriptInfo = new ScriptInfo();
        String line = readFirstTextLine(br);

        while (line != null && !line.startsWith("[")) {

            if (!line.startsWith(COMMENTS_MARK)) {

                String[] split = line.split(ScriptInfo.SEP);
                if (split.length > 1) {
                    String property = StringUtils.deleteWhitespace(split[0]);
                    property = StringUtils.unCapitalize(property);

                    StringJoiner joiner = new StringJoiner(ScriptInfo.SEP);
                    for (int i = 1; i < split.length; i++) {
                        joiner.add(split[i]);
                    }
                    String value = joiner.toString().trim();

                    String error = callProperty(scriptInfo, property, value);

                    if (error != null) {
                        throw new InvalidAssSubException("Script info : " + error);
                    }

                }

            }

            line = markAndRead(br);
        }

        reset(br, line);

        return scriptInfo;
    }

    /**
     * Call a specific property of an object with reflection
     *
     * @param object:   the object to set a property
     * @param property: the property to define
     * @param value:    the value to set
     * @return the error message if an error has occured, null otherwise
     */
    private static String callProperty(Object object, String property, String value) {
        String error = null;
        try {
            PropertyUtils.PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(object, property);

            if (descriptor != null) {
                String type = descriptor.getType().getSimpleName();
                switch (type) {
                    case "String":
                        PropertyUtils.setProperty(object, property, value);
                        break;
                    case "int":
                        PropertyUtils.setProperty(object, property, NumberUtils.toInt(value));
                        break;
                    case "boolean":
                        boolean boolValue = NumberUtils.toInt(value) == -1;
                        PropertyUtils.setProperty(object, property, boolValue);
                        break;
                    case "double":
                        double doubleValue = NumberUtils.toDouble(value.replace(",", ".").trim());
                        PropertyUtils.setProperty(object, property, doubleValue);
                        break;
                    case "float":
                        double floatValue = NumberUtils.toFloat(value.replace(",", ".").trim());
                        PropertyUtils.setProperty(object, property, floatValue);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            Log.wtf("ASSParser", e);

            error = e.toString();
            // Property not supported, do nothing
        }

        return error;
    }

    /**
     * Get the format string definition
     *
     * @param br:          the buffered reader
     * @param sectionName: the name of the section to parse
     * @return the format string definition
     * @throws IOException
     * @throws InvalidAssSubException
     */
    private static String[] findFormat(BufferedReader br, String sectionName) throws IOException,
            InvalidAssSubException {

        String line = readFirstTextLine(br);
        if (TextUtils.isEmpty(line)) {
            throw new InvalidAssSubException("Missing format definition in " + sectionName + " section");
        }
        if (!line.trim().startsWith(ASSSub.FORMAT)) {
            String capitalized = StringUtils.capitalize(sectionName);
            throw new InvalidAssSubException(capitalized + " definition must start with 'Format' line");
        }
        return Objects.requireNonNull(findInfo(line, ASSSub.FORMAT)).split(V4Style.SEP);
    }

    /**
     * Find the information after ":" in a text line
     *
     * @param line:   the line
     * @param search: the information to search
     * @return info or null if the info is empty / not found
     */
    private static String findInfo(String line, String search) {

        String info = null;
        String sep = ":";
        if (line.trim().toLowerCase().startsWith(search.toLowerCase()) && line.indexOf(sep) > 0) {
            info = line.substring(line.indexOf(sep) + 1).trim();
        }
        return TextUtils.isEmpty(info) ? null : info;
    }

}
