package com.igorgorbunov3333.timer.service.tag.report;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportRowDto;
import com.igorgorbunov3333.timer.service.util.PomodoroChronoUtil;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class TagDurationReportComponent {

    private static final String TOTAL_REPORT_ROW_NAME = "Total";

    public List<TagDurationReportRowDto> buildReport(List<PomodoroDto> pomodoroList) {
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

        TreeSet<TagDuration> tagDurationQueue = new TreeSet<>(tagDurationList);
        buildTree(rootRows, new ArrayList<>(), null, tagDurationQueue, pomodoroTagsToDurations);

        TagDurationReportRowDto totalRow = buildTotalRow(rootRows);
        rootRows.add(totalRow);

        return rootRows;
    }

    private void buildTree(List<TagDurationReportRowDto> rootRows,
                           List<TagDurationReportRowDto> visitedNodes,
                           TagDurationReportRowDto currentRow,
                           TreeSet<TagDuration> tagDurations,
                           Map<List<String>, Long> pomodoroTagsToDurations) {
        if (tagDurations.isEmpty()) {
            return;
        }

        TagDuration rowTagDuration = new TreeSet<>(new ArrayList<>(tagDurations)).iterator().next();
        if (rowTagDuration.getDuration() == 0L) {
            tagDurations.remove(rowTagDuration);
            buildTree(rootRows, visitedNodes, currentRow, tagDurations, pomodoroTagsToDurations);
            return;
        }

        if (currentRow == null) {
            TagDurationReportRowDto newRootRow = new TagDurationReportRowDto(
                    rowTagDuration.getTag(),
                    rowTagDuration.getDuration(),
                    new ArrayList<>()
            );

            currentRow = newRootRow;

            tagDurations.remove(rowTagDuration);

            rootRows.add(newRootRow);

            visitedNodes.add(newRootRow);

            while (!tagDurations.isEmpty()) {
                if (tagDurations.stream()
                        .allMatch(td -> td.getDuration() == 0L)) {
                    return;
                }

                buildTree(rootRows, new ArrayList<>(List.of(currentRow)), newRootRow, tagDurations, pomodoroTagsToDurations);
            }
        }

        Map<List<String>, Long> relatedTagDurations = new HashMap<>();
        Set<List<String>> candidatesToRemove = new HashSet<>();
        for (Map.Entry<List<String>, Long> entry : pomodoroTagsToDurations.entrySet()) {
            Set<String> visitedNodeTags = visitedNodes.stream()
                    .flatMap(td -> mapToTagNames(td).stream())
                    .collect(Collectors.toSet());

            String currentRowTag = currentRow.getTag();
            Set<String> intermediateTags = new HashSet<>();
            if (currentRow.getAllRows().stream().map(TagDurationReportRowDto::getTag).collect(Collectors.toSet()).contains(rowTagDuration)) {
                intermediateTags = new HashSet<>(currentRow.getAllRows().stream().map(TagDurationReportRowDto::getTag).collect(Collectors.toList()));
                intermediateTags.remove(currentRowTag);
                intermediateTags.remove(rowTagDuration.getTag());
            }

            if (new HashSet<>(entry.getKey()).containsAll(visitedNodeTags)
                    && new HashSet<>(entry.getKey()).contains(rowTagDuration.getTag())
                    && new HashSet<>(entry.getKey()).stream().noneMatch(intermediateTags::contains)) {
                relatedTagDurations.put(entry.getKey(), entry.getValue());
                candidatesToRemove.add(entry.getKey());
            }
        }

        long currentTagDuration = relatedTagDurations.values().stream()
                .mapToLong(l -> l)
                .sum();

        if (currentTagDuration == 0L) {
            return;
        }

        Set<String> otherRelatedTags = new HashSet<>();
        for (Map.Entry<List<String>, Long> entry : relatedTagDurations.entrySet()) {
            Set<String> currentEntryTags = new HashSet<>(entry.getKey());
            currentEntryTags.removeAll(visitedNodes.stream().map(TagDurationReportRowDto::getTag).collect(Collectors.toSet()));
            currentEntryTags.remove(rowTagDuration.getTag());

            otherRelatedTags.addAll(currentEntryTags);
        }

        Set<String> tagsWithDurationMoreThanCurrent = new HashSet<>();
        for (String otherRelatedTag : otherRelatedTags) {
            long otherRelatedTagDuration = 0L;
            for (Map.Entry<List<String>, Long> entry : pomodoroTagsToDurations.entrySet()) {
                if (new HashSet<>(entry.getKey()).containsAll(visitedNodes.stream().map(TagDurationReportRowDto::getTag).collect(Collectors.toSet()))
                        && new HashSet<>(entry.getKey()).contains(otherRelatedTag)) {
                    otherRelatedTagDuration += entry.getValue();
                }
            }
            if (otherRelatedTagDuration > currentTagDuration) {
                tagsWithDurationMoreThanCurrent.add(otherRelatedTag);
            }
        }

        if (!tagsWithDurationMoreThanCurrent.isEmpty()) {
            candidatesToRemove = new HashSet<>();
            Map<List<String>, Long> mapToCalculateUpdatedDuration = new HashMap<>();
            for (String tagWithDurationMoreThanCurrent : tagsWithDurationMoreThanCurrent) {
                for (Map.Entry<List<String>, Long> entry : relatedTagDurations.entrySet()) {
                    if (!new HashSet<>(entry.getKey()).contains(tagWithDurationMoreThanCurrent)) {
                        mapToCalculateUpdatedDuration.put(entry.getKey(), entry.getValue());
                        candidatesToRemove.add(entry.getKey());
                    }
                }
            }

            currentTagDuration = mapToCalculateUpdatedDuration.values().stream()
                    .mapToLong(l -> l)
                    .sum();
        }

        long leftoverDuration = rowTagDuration.getDuration() - currentTagDuration;
        rowTagDuration.setDuration(leftoverDuration);

        TagDurationReportRowDto newRow = null;
        if (currentTagDuration == currentRow.getDuration()) {
            currentRow.setTag(currentRow.getTag().concat(" #").concat(rowTagDuration.getTag()));
        } else {
            newRow = new TagDurationReportRowDto(
                    rowTagDuration.getTag(),
                    currentTagDuration,
                    new ArrayList<>()
            );
            currentRow.addChild(newRow);
        }

        TreeSet<TagDuration> newRowChildren = new TreeSet<>();
        if (newRow != null) {
            newRowChildren =
                    getChildren(newRow, visitedNodes, relatedTagDurations, tagsWithDurationMoreThanCurrent, tagDurations);
        }

        if (!newRowChildren.isEmpty()) {
            visitedNodes.add(newRow);

            for (TagDuration child : newRowChildren) {
                String childTagName = newRowChildren.iterator().next().getTag();
                buildTree(rootRows, visitedNodes, newRow, newRowChildren, pomodoroTagsToDurations);
                newRowChildren = newRowChildren.stream()
                        .filter(c -> !c.getTag().equals(childTagName))
                        .collect(Collectors.toCollection(TreeSet::new));
            }
            visitedNodes.remove(newRow);
        }

        Set<String> visitedTagNames = visitedNodes.stream()
                .flatMap(node -> mapToTagNames(node).stream())
                .collect(Collectors.toSet());
        visitedTagNames.add(rowTagDuration.getTag());
        Set<List<String>> entriesToRemove = candidatesToRemove.stream()
                .filter(visitedTagNames::containsAll)
                .collect(Collectors.toSet());

        entriesToRemove.forEach(pomodoroTagsToDurations::remove);
    }

    private TreeSet<TagDuration> getChildren(TagDurationReportRowDto newRow,
                                             List<TagDurationReportRowDto> visitedNodes,
                                             Map<List<String>, Long> pomodoroTagsToDurations,
                                             Set<String> tagsWithDurationMoreThanCurrent,
                                             TreeSet<TagDuration> tagDurations) {
        Set<TagDuration> children = new HashSet<>();
        for (Map.Entry<List<String>, Long> entry : pomodoroTagsToDurations.entrySet()) {
            Set<String> neighbours = new HashSet<>(entry.getKey());
            visitedNodes.forEach(vn -> neighbours.remove(vn.getTag()));
            neighbours.remove(newRow.getTag());

            if (!neighbours.isEmpty() && !tagsWithDurationMoreThanCurrent.containsAll(neighbours)) {
                children.addAll(tagDurations.stream().filter(td -> neighbours.contains(td.getTag())).collect(Collectors.toSet()));
            }
        }

        return new TreeSet<>(children);
    }

    private Set<String> mapToTagNames(TagDurationReportRowDto mappedReportRow) {
        return Arrays.stream(mappedReportRow.getTag().split(" "))
                .map(name -> name.replace("#", ""))
                .collect(Collectors.toSet());
    }

    private TagDurationReportRowDto buildTotalRow(List<TagDurationReportRowDto> rootRows) {
        long duration = rootRows.stream()
                .mapToLong(TagDurationReportRowDto::getDuration)
                .sum();

        return new TagDurationReportRowDto(TOTAL_REPORT_ROW_NAME, duration, Collections.emptyList());
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    private static class TagDuration implements Comparable<TagDuration> {

        private final String tag;
        @Setter
        private long duration;

        @Override
        public int compareTo(TagDuration o) {
            if (o.getDuration() - this.duration < 0) {
                return -1;
            } else if (o.getDuration() - this.duration == 0) {
                return this.tag.compareTo(o.getTag());
            } else {
                return 1;
            }
        }
    }

}
