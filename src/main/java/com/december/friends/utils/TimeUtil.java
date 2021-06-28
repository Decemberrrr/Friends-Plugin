package com.december.friends.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtil {
    public static String getDurationBreakdown(long millis) {
        if(millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days != 0) {
            sb.append(days);
            sb.append(" Days ");
        }
        if (hours != 0) {
            sb.append(hours);
            sb.append(" Hours ");
        }
        if (minutes != 0) {
            sb.append(minutes);
            sb.append(" Minutes ");
        }
        sb.append(seconds);
        sb.append(" Seconds");

        return(sb.toString());
    }
}
