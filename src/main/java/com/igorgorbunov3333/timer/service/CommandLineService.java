package com.igorgorbunov3333.timer.service;

import com.igorgorbunov3333.timer.model.entity.Pomodoro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
public class CommandLineService {

    @Autowired
    private PomodoroService pomodoroService;
    @Autowired
    private SecondsFormatterService secondsFormatterService;

    public void start() {
        Scanner sc = new Scanner(System.in);

        System.out.println("1. start");
        System.out.println("2. stop");
        System.out.println("3. current time");
        System.out.println("4. pomadoros today");
        System.out.println("5. pomadoros today extended");
        System.out.println("6. pomadoros for the last month");

        while (true) {
            int c = sc.nextInt();
            gotoChoice(c);
            if (c == 7)
                break;
        }
        sc.close();
    }

    private void gotoChoice(int c) {
        switch (c) {
            case 1:
                pomodoroService.starPomodoro();
                break;
            case 2:
                pomodoroService.stopPomodoro();
                break;
            case 3:
                int seconds = pomodoroService.getPomodoroTime();
                String formattedTime = secondsFormatterService.format(seconds);
                System.out.println(formattedTime);
                break;
            case 4:
                System.out.println(pomodoroService.getPomodorosInDay());
                break;
            case 5:
                pomodoroService.getPomodorosInDayExtended().stream()
                        .map(this::mapTimestamp)
                        .forEach(System.out::println);
                break;
            case 6:
                Map<LocalDate, List<Pomodoro>> datesToPomadoros = pomodoroService.getPomodorosInMonthExtended();
                for (Map.Entry<LocalDate, List<Pomodoro>> entry : datesToPomadoros.entrySet()) {
                    System.out.println();
                    System.out.println(entry.getKey());
                    System.out.println("pomodoros in day - " + entry.getValue().size());
                    for (Pomodoro pomodoro : entry.getValue()) {
                        String pomodoroRow = mapTimestamp(pomodoro);
                        String pomodoroDuration = secondsFormatterService.format(pomodoro.startEndTimeDifferenceInSeconds());
                        System.out.println(pomodoroRow + " - " + pomodoroDuration);
                    }
                }
                break;
        }
    }

    private String mapTimestamp(Pomodoro pomodoro) {
        String startTimeString = format(pomodoro.getStartTime());
        String endTimeString = format(pomodoro.getEndTime());
        return String.join(" : ", List.of(startTimeString, endTimeString));
    }

    private String format(LocalDateTime startTime) {
        int hours = startTime.getHour();
        int minutes = startTime.getMinute();
        int seconds = startTime.getSecond();
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }

}
