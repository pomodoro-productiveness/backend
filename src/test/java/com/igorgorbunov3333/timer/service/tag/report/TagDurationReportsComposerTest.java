package com.igorgorbunov3333.timer.service.tag.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportRowDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagDurationReportsComposerTest {

    @InjectMocks
    private TagDurationReportsComposer testee;
    @Mock
    private AllTagsDurationReporter allTagsDurationReporter;

    @Test
    void compose_whenReportsPresent_ThenCompose() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String stringReports = getStringFromJson("data/service/tag/report/TagDurationReportsComposer_yearly_reports_input.json");
        List<TagDurationReportRowDto> reportsRows = objectMapper.readValue(stringReports, new TypeReference<>() {
        });

        when(allTagsDurationReporter.reportForEachTag(List.of())).thenReturn(reportsRows);

        String stringExpected = getStringFromJson("data/service/tag/report/TagDurationReportsComposer_yearly_reports_expected.json");

        List<TagDurationReportRowDto> expected = objectMapper.readValue(stringExpected, new TypeReference<>() {
        });

        List<TagDurationReportRowDto> actual = testee.compose(List.of());

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    private String getStringFromJson(String path) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(path);

        return new BufferedReader(new InputStreamReader(is))
                .lines()
                .collect(Collectors.joining(StringUtils.LF));
    }

}
