package com.igorgorbunov3333.timer.service.tag.report;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportRowDto;
import com.igorgorbunov3333.timer.service.util.PomodoroChronoUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TagDurationReportsComposer {

    private static final String TOTAL_REPORT_ROW_NAME = "Total"; //TODO: add total row

    public List<TagDurationReportRowDto> compose(List<PomodoroDto> pomodoroList) {
        Map<List<String>, Long> pomodoroTagsToDurations = new HashMap<>();

        for (PomodoroDto pomodoro : pomodoroList) {
            List<String> sortedTags = pomodoro.getTags().stream()
                    .map(PomodoroTagDto::getName)
                    .sorted()
                    .toList();

            long pomodoroDuration = PomodoroChronoUtil.getStartEndTimeDifferenceInSeconds(pomodoro);

            Long duration = pomodoroTagsToDurations.get(sortedTags) == null
                    ? pomodoroDuration
                    : pomodoroTagsToDurations.get(sortedTags) + pomodoroDuration;

            pomodoroTagsToDurations.put(sortedTags, duration);
        }

        Set<String> uniqueTags = pomodoroTagsToDurations.keySet().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        Map<String, TagDuration> tagDurations = new HashMap<>();

        for (String tag : uniqueTags) {
            for (Map.Entry<List<String>, Long> entry : pomodoroTagsToDurations.entrySet()) {
                if (entry.getKey().contains(tag)) {
                    long tagDuration = tagDurations.get(tag) == null
                            ? entry.getValue()
                            : tagDurations.get(tag).getDuration() + entry.getValue();

                    tagDurations.put(tag, new TagDuration(tag, tagDuration));
                }
            }
        }

        List<TagDuration> tagDurationList = tagDurations.values().stream()
                .sorted(Comparator.comparing(TagDuration::getDuration).reversed())
                .toList();

        List<TagDurationReportRowDto> rootRows = new ArrayList<>();

        buildTree(rootRows, tagDurationList, pomodoroTagsToDurations);

        return rootRows;
    }

    private void buildTree(List<TagDurationReportRowDto> rootRows,
                           List<TagDuration> tagDurationList,
                           Map<List<String>, Long> pomodoroTagsToDurations) {
        for (TagDuration tagDuration : tagDurationList) {
            if (rootRows.isEmpty()) {
                addNewRoot(rootRows, tagDuration);
            } else {
                boolean updated = false;
                for (TagDurationReportRowDto rootRow : rootRows) {
                    Set<String> rootTags = mapToTagNames(rootRow);
                    if (updateTree(rootRow, pomodoroTagsToDurations, tagDuration.getTag(), new HashSet<>(rootTags))) {
                        updated = true;
                    }
                }

                if (!updated) {
                    addNewRoot(rootRows, tagDuration);
                }
            }
        }
    }

    private boolean updateTree(TagDurationReportRowDto row,
                               Map<List<String>, Long> pomodoroTagsToDurations,
                               String currentTag,
                               Set<String> alreadyPassedTags) {
        Set<String> rowTags = mapToTagNames(row);

        rowTags.addAll(alreadyPassedTags);

        long duration = 0L;

        Set<List<String>> tagsToRemove = new HashSet<>();
        for (Map.Entry<List<String>, Long> entry : pomodoroTagsToDurations.entrySet()) {
            if (!entry.getKey().contains(currentTag)) {
                continue;
            }

            Set<String> neighbours = new HashSet<>(entry.getKey());
            neighbours.removeAll(rowTags);
            neighbours.remove(currentTag);

            if (CollectionUtils.isEmpty(neighbours)) {
                duration += entry.getValue();
                tagsToRemove.add(entry.getKey());
            } else {
                if (isNeighboursDurationNotMoreThanCurrentTag(pomodoroTagsToDurations, rowTags, neighbours, currentTag)) {
                    duration += entry.getValue();
                    tagsToRemove.add(entry.getKey());
                }
            }
        }

        Map<List<String>, Long> pomodoroTagsToDurationsCopy = new HashMap<>(pomodoroTagsToDurations);

        pomodoroTagsToDurationsCopy.keySet()
                .removeAll(tagsToRemove);

        if (duration == row.getDuration()) {
            row.setTag(String.join(" #", row.getTag(), currentTag));
        }

        List<TagDurationReportRowDto> children = new ArrayList<>(row.getChildren());

        boolean childNotUpdated = true;
        for (TagDurationReportRowDto child : children) {
            if (child.getDuration() == duration) {
                child.setTag(String.join(" #", child.getTag(), currentTag));
                childNotUpdated = false;
                children.remove(child);
                break;
            }
        }

        if (duration > 0L && childNotUpdated) {
            addChildToRow(row, currentTag, duration);
        }

        if (CollectionUtils.isEmpty(children)) {
            return true;
        }

        boolean updated = false;
        for (TagDurationReportRowDto child : children) {
            if (updateTree(child, pomodoroTagsToDurationsCopy, currentTag, rowTags)) {
                updated = true;
            }
        }

        return updated;
    }

    private boolean isNeighboursDurationNotMoreThanCurrentTag(Map<List<String>, Long> pomodoroTagsToDurations,
                                                              Set<String> alreadyPassedTags,
                                                              Set<String> neighbours,
                                                              String currentTag) {
        long duration = 0L;
        for (Map.Entry<List<String>, Long> entry : pomodoroTagsToDurations.entrySet()) {
            Set<String> currentTagAndAlreadyPassedTags = new HashSet<>(alreadyPassedTags);
            currentTagAndAlreadyPassedTags.add(currentTag);

            if (new HashSet<>(entry.getKey()).containsAll(currentTagAndAlreadyPassedTags)) {
                duration += entry.getValue();
            }
        }

        for (String neighbourTag : neighbours) {
            long neighbourDuration = 0L;
            for (Map.Entry<List<String>, Long> entry : pomodoroTagsToDurations.entrySet()) {
                Set<String> neighbourAndAlreadyPassedTags = new HashSet<>(alreadyPassedTags);
                neighbourAndAlreadyPassedTags.add(neighbourTag);

                if (new HashSet<>(entry.getKey()).containsAll(neighbourAndAlreadyPassedTags)) {
                    neighbourDuration += entry.getValue();
                }
            }
            if (neighbourDuration > duration) {
                return false;
            }
        }

        return true;
    }

    private void addNewRoot(List<TagDurationReportRowDto> rootRows, TagDuration tagDuration) {
        TagDurationReportRowDto newRow = new TagDurationReportRowDto(
                tagDuration.getTag(),
                tagDuration.getDuration(),
                new ArrayList<>()
        );

        rootRows.add(newRow);
    }

    private void addChildToRow(TagDurationReportRowDto row, String currentTag, long duration) {
        TagDurationReportRowDto newChild = new TagDurationReportRowDto(
                currentTag,
                duration,
                new ArrayList<>()
        );

        row.addChild(newChild);
    }

    private Set<String> mapToTagNames(TagDurationReportRowDto mappedReportRow) {
        return Arrays.stream(mappedReportRow.getTag().split(" "))
                .map(tag -> tag.replace("#", ""))
                .collect(Collectors.toSet());
    }

    @Getter
    @AllArgsConstructor
    private static class TagDuration {

        private final String tag;
        private long duration;

    }

}
