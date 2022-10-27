package com.igorgorbunov3333.timer.backend.service.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.igorgorbunov3333.timer.backend.model.dto.pomodoro.PomodoroDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class TagToTagsFromPomodoroMappingsBuilderTest {

    @InjectMocks
    private TagToTagsFromPomodoroMappingsBuilder testee;

    @Test
    void buildTagMappings_WhenPomodoroExists_ThenMap() throws JsonProcessingException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("data/service/tag/TagToTagsFromPomodoroMappingsBuilder_weekly_pomodoro.json");
        String stringPomodoro = new BufferedReader(new InputStreamReader(is))
                .lines()
                .collect(Collectors.joining(StringUtils.LF));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List<PomodoroDto> pomodoro = objectMapper.readValue(stringPomodoro, new TypeReference<>() {});

        Map<String, Set<String>> actual = testee.buildTagMappings(pomodoro);

        assertThat(actual).containsExactlyInAnyOrderEntriesOf(
                Map.of("education", Set.of("book", "SpringSecurity", "algorithms", "html/css", "educativePlatform", "EducativeCompany"),
                "algorithms", Set.of("education", "educativePlatform"),
                "SpringSecurity", Set.of("book", "education"),
                "html/css", Set.of("education", "EducativeCompany"),
                "book", Set.of("education", "SpringSecurity"),
                "pomodoro", Set.of(),
                "educativePlatform", Set.of("education", "algorithms"),
                "EducativeCompany", Set.of("education", "html/css")
        ));
    }

}
