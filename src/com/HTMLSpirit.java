package com;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLSpirit {
	private static final String REGEX_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
	private static final String REGEX_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
	private static final String REGEX_html = "<[^>]+>";

	public static String delHTMLTag(String htmlStr) {
		Pattern p_script = Pattern.compile(REGEX_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // Script tag filter
		
		Pattern p_style = Pattern.compile(REGEX_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // Style tag filter

		Pattern p_html = Pattern.compile(REGEX_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // HTML filter

		return htmlStr;
	}
}
