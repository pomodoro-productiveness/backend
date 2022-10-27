package com.igorgorbunov3333.timer.backend.service.pomodoro.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.period.YearlyPomodoroDto;
import com.igorgorbunov3333.timer.backend.model.entity.pomodoro.Pomodoro;
import com.igorgorbunov3333.timer.backend.repository.DayOffRepository;
import com.igorgorbunov3333.timer.backend.repository.PomodoroRepository;
import com.igorgorbunov3333.timer.backend.repository.PomodoroTagRepository;
import com.igorgorbunov3333.timer.backend.service.mapper.PomodoroMapper;
import com.igorgorbunov3333.timer.backend.service.mapper.TagMapper;
import com.igorgorbunov3333.timer.backend.service.pomodoro.period.PomodoroByMonthsDivider;
import com.igorgorbunov3333.timer.backend.service.period.WeekPeriodHelper;
import com.igorgorbunov3333.timer.backend.service.tag.TagService;
import com.igorgorbunov3333.timer.backend.service.tag.impl.DefaultTagService;
import com.igorgorbunov3333.timer.backend.service.util.CurrentTimeService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {YearlyPomodoroProvider.class,
        PomodoroMapper.class,
        DefaultTagService.class,
        PomodoroByMonthsDivider.class,
        MonthlyPomodoroProvider.class,
        TagMapper.class,
        WeeklyPomodoroProvider.class,
        WeekPeriodHelper.class
})
class YearlyPomodoroProviderIT {

    @Autowired
    private YearlyPomodoroProvider testee;

    @MockBean
    private CurrentTimeService currentTimeService;
    @MockBean
    private PomodoroRepository pomodoroRepository;
    @MockBean
    private PomodoroTagRepository pomodoroTagRepository;
    @MockBean
    private DayOffRepository dayOffRepository;

    @Autowired
    private PomodoroMapper pomodoroMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private PomodoroByMonthsDivider pomodoroByMonthsDivider;
    @Autowired
    private MonthlyPomodoroProvider monthlyPomodoroProvider;

    @Test
    void provideCurrentYearPomodoro_WhenPomodoroPresent_ThenReturnYearlyData() throws JsonProcessingException {
        String stringYearlyPomodoro = getStringFromFile("data/service/pomodoro/provider/YearlyPomodoroProvider_yearly_pomodoro_initial_data.json");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List<Pomodoro> pomodoro = objectMapper.readValue(stringYearlyPomodoro, new TypeReference<>() {});

        when(pomodoroRepository.findByStartTimeAfterAndEndTimeBeforeOrderByStartTime(any(), any()))
                .thenReturn(pomodoro);
        when(currentTimeService.getCurrentDateTime()).thenReturn(LocalDate.of(2022, 9, 27).atStartOfDay());

        String yearlyPomodoroExpected = getStringFromFile("data/service/pomodoro/provider/YearlyPomodoroProvider_yearly_pomodoro_expected.json");
        YearlyPomodoroDto expected = objectMapper.readValue(yearlyPomodoroExpected, YearlyPomodoroDto.class);

        YearlyPomodoroDto actual = testee.provideCurrentYearPomodoro(null);

        assertThat(actual).isEqualTo(expected);
    }

    private String getStringFromFile(String path) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(path);
        return new BufferedReader(new InputStreamReader(is))
                .lines()
                .collect(Collectors.joining(StringUtils.LF));
    }

}
