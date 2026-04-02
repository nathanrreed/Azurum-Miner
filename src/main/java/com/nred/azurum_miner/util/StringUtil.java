package com.nred.azurum_miner.util;

public class StringUtil {
    public static String safeSubstring(String text, int start, int end) { // TODO use or remove
        if (text.length() < start) return "";
        return text.substring(start, Math.min(end, text.length()));
    }
}