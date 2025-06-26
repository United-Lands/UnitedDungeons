package org.unitedlands.utils;

import org.unitedlands.UnitedDungeons;

public class Logger {

    private static final UnitedDungeons plugin;

    static {
        plugin = UnitedDungeons.getPlugin(UnitedDungeons.class);
    }

    public static void log(String message) {
        plugin.getLogger().info(message);
    }

        public static void logError(String message) {
        plugin.getLogger().severe(message);
    }
}