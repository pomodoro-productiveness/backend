package com.igorgorbunov3333.timer.service.tag.report;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportDto;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportRowDto;
import com.igorgorbunov3333.timer.service.tag.TagToTagsFromPomodoroMappingsBuilder;
import com.igorgorbunov3333.timer.service.util.PomodoroChronoUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TagDurationReporter {

    private final TagToTagsFromPomodoroMappingsBuilder tagToTagsFromPomodoroMappingsBuilder;

    public List<TagDurationReportDto> report(List<PomodoroDto> pomodoro) {
        if (CollectionUtils.isEmpty(pomodoro)) {
            return List.of();
        }

        return buildReports(pomodoro);
    }

    private List<TagDurationReportDto> buildReports(List<PomodoroDto> pomodoro) {
        Map<String, Set<String>> tagToMappedTags = tagToTagsFromPomodoroMappingsBuilder.buildTagMappings(pomodoro);

        return tagToMappedTags.keySet().stream()
                .map(tag -> buildSingleReport(tagToMappedTags, tag, pomodoro))
                .collect(Collectors.toList());
    }

    private TagDurationReportDto buildSingleReport(Map<String, Set<String>> tagToMappedTags,
                                                   String mainTag,
                                                   List<PomodoroDto> pomodoro) {
        List<PomodoroDto> tagPomodoro = filterPomodoro(pomodoro, p -> mapToTagNamesSet(p).contains(mainTag));

        return buildSingleReport(tagToMappedTags, mainTag, pomodoro, tagPomodoro);
    }

    private TagDurationReportDto buildSingleReport(Map<String, Set<String>> tagToMappedTags,
                                                   String tag,
                                                   List<PomodoroDto> allPomodoro,
                                                   List<PomodoroDto> tagPomodoro) {
        TagDurationReportRowDto mainTagReportRow = buildReportRow(tag, tagPomodoro);
        List<TagDurationReportRowDto> mappedTagsReportRows = buildMappedTagsReportRows(tagToMappedTags, tag, allPomodoro, tagPomodoro);

        return new TagDurationReportDto(mainTagReportRow, mappedTagsReportRows);
    }

    private List<TagDurationReportRowDto> buildMappedTagsReportRows(Map<String, Set<String>> tagToMappedTags,
                                                                    String tag,
                                                                    List<PomodoroDto> pomodoro,
                                                                    List<PomodoroDto> tagPomodoro) {
        Set<Set<String>> mappedTagGroups = buildMappedTagGroups(tagToMappedTags, tag, tagPomodoro);

        return buildMappedTagsReportRows(pomodoro, tag, mappedTagGroups);
    }

    private List<TagDurationReportRowDto> buildMappedTagsReportRows(List<PomodoroDto> pomodoro,
                                                                    String mainTag,
                                                                    Set<Set<String>> mappedTagsGroups) {
        return mappedTagsGroups.stream()
                .map(mappedTagGroup -> buildMappedTagReportRow(mappedTagGroup, mainTag, pomodoro))
                .collect(Collectors.toList());
    }

    private TagDurationReportRowDto buildMappedTagReportRow(Set<String> mappedTags,
                                                            String mainTag,
                                                            List<PomodoroDto> pomodoro) {
        String mappedTagsNames = buildMappedTagNames(mainTag, mappedTags);
        List<PomodoroDto> filteredPomodoro = filterPomodoro(pomodoro, singlePomodoro -> mapToTagNamesSet(singlePomodoro).equals(mappedTags));

        return buildReportRow(mappedTagsNames, filteredPomodoro);
    }

    private List<PomodoroDto> filterPomodoro(List<PomodoroDto> pomodoro, Predicate<PomodoroDto> predicate) {
        return pomodoro.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    private TagDurationReportRowDto buildReportRow(String tag, List<PomodoroDto> pomodoro) {
        long duration = calculateDurationInSeconds(pomodoro);
        return new TagDurationReportRowDto(tag, duration);
    }

    private Set<Set<String>> buildMappedTagGroups(Map<String, Set<String>> tagToMappedTags,
                                                  String tag,
                                                  List<PomodoroDto> pomodoro) {
        return pomodoro.stream()
                .map(this::mapToTagNamesSet)
                .filter(pomodoroTagGroup -> !Collections.disjoint(tagToMappedTags.get(tag), pomodoroTagGroup))
                .collect(Collectors.toSet());
    }

    private Set<String> mapToTagNamesSet(PomodoroDto p) {
        return p.getTags().stream()
                .map(PomodoroTagDto::getName)
                .collect(Collectors.toSet());
    }

    private long calculateDurationInSeconds(List<PomodoroDto> pomodoro) {
        return pomodoro.stream()
                .mapToLong(PomodoroChronoUtil::getStartEndTimeDifferenceInSeconds)
                .sum();
    }

    private String buildMappedTagNames(String mainTag, Set<String> mappedTags) {
        Set<String> tagsNotEqualToMainTag = mappedTags.stream()
                .filter(s -> !s.equals(mainTag))
                .collect(Collectors.toSet());

        return "#" + String.join(" #", tagsNotEqualToMainTag);
    }

}
