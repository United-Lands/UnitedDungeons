package org.unitedlands.utils;

import net.md_5.bungee.api.ChatColor;

public class MessageFormatter {
    public static String getWithPrefix(String text) {
        return ChatColor.RED + "U" + ChatColor.WHITE + "L " + ChatColor.GRAY + "Dungeons " + ChatColor.DARK_GRAY + "» "
                + ChatColor.GRAY + " " + text;
    }
}
