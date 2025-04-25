package org.unitedlands.utils;

import net.md_5.bungee.api.ChatColor;

public class MessageFormatter {

    public static String getWithPrefix(String text) {
        return ChatColor.RED + "U" + ChatColor.WHITE + "L " + ChatColor.GRAY + "Dungeons " + ChatColor.DARK_GRAY + "» "
                + ChatColor.GRAY + " " + text;
    }


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
            sb.append(minutes).append("min ");
        sb.append(seconds).append("s");

        return sb.toString().trim();
    }
}
