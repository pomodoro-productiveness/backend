package com.igorgorbunov3333.timer.service.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CurrentDayService {

    public LocalDate getCurrentDay() {
        return LocalDate.now();
    }

}
