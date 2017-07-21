package network.marble.game.mode.survivalgames.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public enum FontFormat {
    BLACK("0", 1),
    DARK_BLUE("1", 2),
    DARK_GREEN("2", 2),
    DARK_AQUA("3", 2),
    DARK_RED("4", 2),
    PURPLE("5", 2),
    ORANGE("6", 0),
    GREY("7", 1),
    DARK_GREY("8", 1),
    BLUE("9", 2),
    GREEN("a", 0),
    AQUA("b", 0),
    RED("c", 0),
    PINK("d", 0),
    YELLOW("e", 0),
    WHITE("f", 1),
    RANDOM("k", 3),
    BOLD("l", 3),
    STRIKE("m", 3),
    UNDERLINED("n", 3),
    ITALICS("o", 3),
    RESET("r", 3);
    
    private final String value;
    private final int type;
    private static final Map<String, String> translate;
    private static final String COLOR_PREFIX_CHARACTER = "\u00a7";
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf("&") + "[0-9A-FK-OR]");
    
    private FontFormat(String value, int type) {
        this.value = COLOR_PREFIX_CHARACTER + value;
        this.type = type;
    }
    
    public String toString() {
        return this.value;
    }
    
    static {
        translate = new HashMap<String, String>();
        createMap();
    }
    
    private static void createMap() {
        translate.put("&0", COLOR_PREFIX_CHARACTER + "0");
        translate.put("&1", COLOR_PREFIX_CHARACTER + "1");
        translate.put("&2", COLOR_PREFIX_CHARACTER + "2");
        translate.put("&3", COLOR_PREFIX_CHARACTER + "3");
        translate.put("&4", COLOR_PREFIX_CHARACTER + "4");
        translate.put("&5", COLOR_PREFIX_CHARACTER + "5");
        translate.put("&6", COLOR_PREFIX_CHARACTER + "6");
        translate.put("&7", COLOR_PREFIX_CHARACTER + "7");
        translate.put("&8", COLOR_PREFIX_CHARACTER + "8");
        translate.put("&9", COLOR_PREFIX_CHARACTER + "9");
        translate.put("&a", COLOR_PREFIX_CHARACTER + "a");
        translate.put("&b", COLOR_PREFIX_CHARACTER + "b");
        translate.put("&c", COLOR_PREFIX_CHARACTER + "c");
        translate.put("&d", COLOR_PREFIX_CHARACTER + "d");
        translate.put("&e", COLOR_PREFIX_CHARACTER + "e");
        translate.put("&f", COLOR_PREFIX_CHARACTER + "f");
        translate.put("&k", COLOR_PREFIX_CHARACTER + "k");
        translate.put("&l", COLOR_PREFIX_CHARACTER + "l");
        translate.put("&m", COLOR_PREFIX_CHARACTER + "m");
        translate.put("&n", COLOR_PREFIX_CHARACTER + "n");
        translate.put("&o", COLOR_PREFIX_CHARACTER + "o");
        translate.put("&r", COLOR_PREFIX_CHARACTER + "r");
    }
    
    public static String translateString(String value) {
        for (String code : translate.keySet()) {
            value = value.replace(code, translate.get(code));
        }
        
        return value;
    }

    public static String translateString(String value, boolean unicode) {
        String message = translateString(value);

        if (unicode) {
            message = replaceUnicodeEscapes(message);
        }

        return message;
    }
    
    public static String stripColor(final String input) {
        if (input == null) {
            return null;
        }
        
        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }
    
    public static String rainbow(String text, boolean includeShades, boolean includeDarks) {
        StringBuilder builder = new StringBuilder();
        int readcount = 0;
        
        List<FontFormat> colors = new ArrayList<>();
        for (FontFormat format : FontFormat.values()) {
            if (format.type == 0) {
                colors.add(format);
            }

            if (includeShades && format.type == 1) {
                colors.add(format);
            }

            if (includeDarks && format.type == 2) {
                colors.add(format);
            }
        }
        
        while (readcount < text.length()) {
            for (int i = 0; i <= colors.size(); i++) {
                if (i == colors.size()) {
                    i = 0;
                }
                
                while (text.charAt(readcount) == ' ') {
                    builder.append(text.charAt(readcount));
                    readcount += 1;
                }
                
                builder.append(colors.get(i)).append(text.charAt(readcount));
                readcount += 1;

                if (readcount == text.length()) {
                    break;
                }
            }
        }
        return builder.toString();
    }
    
    public static List<String> splitLine(String line, int length) {
        return splitLine(line, length, "");
    }
    
    public static List<String> splitLine(String line, int length, String append) {
        List<String> lines = new ArrayList<>();
        
        if (length >= line.length()) {
            lines.add(append + line);
            return lines;
        }
        
        while (line.length() > 0) {
            if (line.length() >= length) {
                String l = line.substring(0, length);
                l = l.substring(0, l.lastIndexOf(" ") + 1);
                lines.add(append + l);
                line = line.substring(l.lastIndexOf(" ") + 1);
            } else {
                lines.add(append + line);
                break;
            }
        }
        
        return lines;
    }

    private static String replaceUnicodeEscapes(String message) {
        while (message.contains("\\u")) {
            String code = message.substring(message.indexOf("\\u") + 2, message.indexOf("\\u") + 6);
            int icode = Integer.parseInt(code, 16);
            char ccode = (char) icode;
            message = message.replace("\\u" + code, ccode + "");
        }

        return message;
    }
}