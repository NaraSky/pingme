package com.lb.pingme.common.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

    public static String handleSpecialHtmlTag(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        content = content.replaceAll("&lt;br&gt;", "<br>");
        content = content.replaceAll("&lt;b&gt;", "<b>");
        return content;
    }

}