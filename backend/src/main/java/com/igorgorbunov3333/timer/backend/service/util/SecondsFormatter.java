package com.igorgorbunov3333.timer.backend.service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecondsFormatter {

    public static String formatInMinutes(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds - (minutes * 60);
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    public static String formatInHours(long seconds) {
        long hours = seconds / 3600;
        long secondsForMinutes = seconds - (hours * 3600);
        long minutes = secondsForMinutes / 60;
        long remainingSeconds = secondsForMinutes - (minutes * 60);
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

}
