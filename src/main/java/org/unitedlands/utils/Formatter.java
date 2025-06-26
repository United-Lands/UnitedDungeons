package org.unitedlands.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.unitedlands.utils.annotations.Info;

public class Formatter {
    public static String formatDuration(long millis) {
        long seconds = millis / 1000 % 60;
        long minutes = millis / (1000 * 60) % 60;
        long hours = millis / (1000 * 60 * 60) % 24;
        long days = millis / (1000 * 60 * 60 * 24);

        StringBuilder sb = new StringBuilder();
        if (days > 0)
            sb.append(days).append("d ");
        if (hours > 0 || days > 0)
            sb.append(hours).append("h ");
        if (minutes > 0 || hours > 0 || days > 0)
            sb.append(minutes).append("m ");
        sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    public static List<String> getSortedCompletions(String input, List<String> options) {
        List<String> completions = Arrays.asList("");
        if (options != null) {
            completions = options.stream().filter(s -> s.toLowerCase().startsWith(input.toLowerCase()))
                    .collect(Collectors.toList());
            Collections.sort(completions);
        }
        return completions;
    }

    public static String getFieldValuesString(Class<?> clazz, Object o) {
        List<String> stringFields = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            var info = fields[i].getAnnotation(Info.class);
            if (info != null) {
                try {
                    var value = fields[i].get(o);
                    stringFields.add("§l§6" + fields[i].getName() + ": §r§7" + value);
                } catch (Exception ex) {
                    Logger.logError(ex.getMessage());
                    continue;
                }
            }
        }
        return String.join(" | ", stringFields);
    }
}
