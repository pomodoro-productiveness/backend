package com.igorgorbunov3333.timer.service;

import org.springframework.stereotype.Component;

@Component
public class SecondsFormatterService {

    public String format(long seconds) {
        long minutes = seconds / 60;
        long leftover = seconds - (minutes * 60);
        return String.format("%d:%02d", minutes, leftover);
    }

}
