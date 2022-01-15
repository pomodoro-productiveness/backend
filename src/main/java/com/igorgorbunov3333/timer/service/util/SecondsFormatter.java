package com.igorgorbunov3333.timer.service.util;

import org.springframework.stereotype.Component;

@Component
public class SecondsFormatter {

    public String formatInMinutes(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds - (minutes * 60);
        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    public String formatInHours(long seconds) {
        long hours = seconds / 3600;
        long secondsForMinutes = seconds - (hours * 3600);
        long minutes = secondsForMinutes / 60;
        long remainingSeconds = secondsForMinutes - (minutes * 60);
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

}
