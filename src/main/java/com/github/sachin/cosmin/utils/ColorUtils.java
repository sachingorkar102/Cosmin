package com.github.sachin.cosmin.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {
    
    private static final String NORMAL_HEX_REGEX = "(\\{([0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F])\\})";
    private static final String GRADIENT_HEX_REGEX = "(\\{#([0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F])\\})(.*?)(\\{#\\/([0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F])\\})";
    public static final Pattern NORMAL_HEX_PATTERN = Pattern.compile(NORMAL_HEX_REGEX);
    public static final Pattern GRADIENT_HEX_PATTERN = Pattern.compile(GRADIENT_HEX_REGEX);


    public static String applyColor(String str){
        str = ChatColor.translateAlternateColorCodes('&',str);
        Matcher gradientMatcher = GRADIENT_HEX_PATTERN.matcher(str);
        Matcher normalMatcher = NORMAL_HEX_PATTERN.matcher(str);
        if(gradientMatcher.matches()){
            StringBuffer sb = new StringBuffer();
            HexColor start = new HexColor(gradientMatcher.group(2));
            HexColor end = new HexColor(gradientMatcher.group(5));
            gradientMatcher.appendReplacement(sb, HexColor.applyGradient(gradientMatcher.group(3).replace(gradientMatcher.group(1), "").replace(gradientMatcher.group(4), ""), start, end));
            gradientMatcher.appendTail(sb);
            return sb.toString().replace("&", String.valueOf(ChatColor.COLOR_CHAR));
        }
        while(normalMatcher.find()){
            HexColor color = new HexColor(normalMatcher.group(2));
            str = str.replace(normalMatcher.group(1), ChatColor.RESET+color.toColorCode());
            return str.replace("&", String.valueOf(ChatColor.COLOR_CHAR));
        }
        return str;
    }
}
