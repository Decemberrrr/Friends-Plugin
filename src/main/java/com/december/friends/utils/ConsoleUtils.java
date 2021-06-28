package com.december.friends.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ConsoleUtils {
    public static void log(String string) {
        Bukkit.getConsoleSender().sendMessage(Color.translate(string));
    }
}
