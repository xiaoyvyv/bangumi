package com.xiaoyv.subtitle.api.subtitle.ass;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


import com.xiaoyv.subtitle.api.subtitle.common.SubtitleLine;
import com.xiaoyv.subtitle.api.utils.StringUtils;

/**
 * Contain the subtitle text, their timings, and how it should be displayed. The fields
 * which appear in each Dialogue line are defined by a Format: line, which must appear
 * before any events in the section. The format line specifies how SSA will interpret all
 * following Event lines.
 * <p>
 * The field names must be spelled correctly, and are as follows:
 * <p>
 * Marked, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text
 * <p>
 * The last field will always be the Text field, so that it can contain commas. The format
 * line allows new fields to be added to the script format in future, and yet allow old
 * versions of the software to read the fields it recognises - even if the field order is
 * changed.
 */
public class Events extends SubtitleLine<ASSTime> {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -6706119890451628726L;

	/**
	 * Format declaration
	 */
	public static final String FORMAT_STRING = "Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text";

	/**
	 * New line separator
	 */
	private static final String ESCAPED_RETURN = "\\N";

	/**
	 * Dialog
	 */
	public static final String DIALOGUE = "Dialogue: ";

	/**
	 * Separator
	 */
	public static final String SEP = ",";

	/**
	 * Subtitles having different layer number will be ignored during the collusion
	 * detection.
	 * 
	 * Higher numbered layers will be drawn over the lower numbered.
	 */
	private int layer;

	/**
	 * Style name. If it is "Default", then your own *Default style will be subtituted.
	 * 
	 * However, the Default style used by the script author IS stored in the script even
	 * though SSA ignores it - so if you want to use it, the information is there - you
	 * could even change the Name in the Style definition line, so that it will appear in
	 * the list of "script" styles.
	 */
	private String style;

	/**
	 * Character name. This is the name of the character who speaks the dialogue. It is
	 * for information only, to make the script is easier to follow when editing/timing.
	 */
	private String name = StringUtils.EMPTY;

	/**
	 * 4-figure Left Margin override. The values are in pixels. All zeroes means the
	 * default margins defined by the style are used.
	 */
	private String marginL = "0000";

	/**
	 * 4-figure Right Margin override. The values are in pixels. All zeroes means the
	 * default margins defined by the style are used.
	 */
	private String marginR = "0000";

	/**
	 * 4-figure Bottom Margin override. The values are in pixels. All zeroes means the
	 * default margins defined by the style are used.
	 */
	private String marginV = "0000";

	/**
	 * Transition Effect. This is either empty, or contains information for one of the
	 * three transition effects implemented in SSA v4.x
	 * 
	 * The effect names are case sensitive and must appear exactly as shown. The effect
	 * names do not have quote marks around them.
	 * 
	 * "Scroll up;y1;y2;delay[;fadeawayheight]"means that the text/picture will scroll up
	 * the screen. The parameters after the words "Scroll up" are separated by semicolons.
	 * 
	 * “Banner;delay” means that text will be forced into a single line, regardless of
	 * length, and scrolled from right to left accross the screen.
	 */
	private String effect = StringUtils.EMPTY;

	/**
	 * Constructor
	 */
	public Events(String style, ASSTime time, ArrayList<String> textLines) {
		this.style = style;
		this.time = time;
		this.textLines = textLines;
	}

	/**
	 * Constructor
	 * 
	 */
	public Events() {
		super();
		this.style = StringUtils.EMPTY;
		this.time = new ASSTime();
	}

	@NonNull
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(DIALOGUE);

		sb.append(this.layer).append(SEP);
		sb.append(ASSTime.format(this.time.getStart())).append(SEP);
		sb.append(ASSTime.format(this.time.getEnd())).append(SEP);
		sb.append(this.style).append(SEP);
		sb.append(this.name).append(SEP);
		sb.append(this.marginL).append(SEP);
		sb.append(this.marginR).append(SEP);
		sb.append(this.marginV).append(SEP);
		sb.append(this.effect).append(SEP);
		this.textLines.forEach(tl -> sb.append(tl).append(ESCAPED_RETURN));

		return StringUtils.removeEnd(sb.toString(), ESCAPED_RETURN);
	}

	// ===================== getter and setter start =====================

	public int getLayer() {
		return this.layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public String getStyle() {
		return this.style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMarginL() {
		return this.marginL;
	}

	public void setMarginL(String marginL) {
		this.marginL = marginL;
	}

	public String getMarginR() {
		return this.marginR;
	}

	public void setMarginR(String marginR) {
		this.marginR = marginR;
	}

	public String getMarginV() {
		return this.marginV;
	}

	public void setMarginV(String marginV) {
		this.marginV = marginV;
	}

	public String getEffect() {
		return this.effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}
}
