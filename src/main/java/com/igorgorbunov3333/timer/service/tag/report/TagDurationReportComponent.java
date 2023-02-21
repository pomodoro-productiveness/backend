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
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
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
                .collect(Collectors.toList());

        List<TagDurationReportRowDto> rootRows = new ArrayList<>();

        Queue<TagDuration> tagDurationQueue = new PriorityQueue<>(tagDurationList);

        buildTree(rootRows, tagDurationQueue.poll(), tagDurationQueue, tagDurationList, pomodoroTagsToDurations, new HashMap<>());

        TagDurationReportRowDto totalRow = buildTotalRow(rootRows);
        rootRows.add(totalRow);

        return rootRows;
    }

    private void buildTree(List<TagDurationReportRowDto> rootRows,
                           TagDuration tagDuration,
                           Queue<TagDuration> tagDurations,
                           List<TagDuration> tagDurationList,
                           Map<List<String>, Long> pomodoroTagsToDurations,
                           Map<String, Long> leftoverTags) {
        if (rootRows.isEmpty()) {
            addNewRoot(rootRows, tagDuration);
        } else {
            boolean updated = false;
            for (TagDurationReportRowDto rootRow : rootRows) {
                Set<String> rootTags = mapToTagNames(rootRow);
                UpdatingResult updatingResult = updateTree(rootRow, null, pomodoroTagsToDurations, tagDuration.getTag(), new HashSet<>(rootTags), tagDurationList, leftoverTags);

                if (updatingResult.getLeftoverTag() != null) {
                    leftoverTags.put(tagDuration.getTag(), updatingResult.getLeftoverDuration());
                    tagDurations.add(new TagDuration(updatingResult.getLeftoverTag(), updatingResult.leftoverDuration));
                }

                if (updatingResult.isUpdated()) {
                    updated = true;
                }
            }

            if (!updated) {
                addNewRoot(rootRows, tagDuration);
            }
        }

        if (tagDurations.isEmpty()) {
            return;
        }

        buildTree(rootRows, tagDurations.poll(), tagDurations, tagDurationList, pomodoroTagsToDurations, leftoverTags);
    }

    private UpdatingResult updateTree(TagDurationReportRowDto row,
                                      TagDurationReportRowDto parent,
                                      Map<List<String>, Long> pomodoroTagsToDurations,
                                      String currentTag,
                                      Set<String> alreadyPassedTags,
                                      List<TagDuration> tagDurationList,
                                      Map<String, Long> leftOverTagsWithDuration) {
        Set<String> rowTags = mapToTagNames(row);

        boolean notHaveCommonMappings = true;
        for (String rowTag : rowTags) {
            for (Map.Entry<List<String>, Long> entry : pomodoroTagsToDurations.entrySet()) {
                if (!currentTag.equals(rowTag) && new HashSet<>(entry.getKey()).containsAll(Set.of(currentTag, rowTag))) {
                    notHaveCommonMappings = false;
                    break;
                }
            }
        }

        if (notHaveCommonMappings) {
            return new UpdatingResult(null, null, false);
        }

        rowTags.addAll(alreadyPassedTags);

        Set<List<String>> tagsToRemove = new HashSet<>();

        Map<List<String>, Long> pomodoroTagsToDurationsForCalculation = new HashMap<>();

        for (Map.Entry<List<String>, Long> entry : pomodoroTagsToDurations.entrySet()) {
            for (TagDurationReportRowDto tagDurationRow : row.getAllRows()) {
                if (new HashSet<>(entry.getKey()).containsAll(mapToTagNames(tagDurationRow))) {
                    pomodoroTagsToDurationsForCalculation.put(entry.getKey(), entry.getValue());
                }
            }
        }

        long duration = calculateDuration(pomodoroTagsToDurationsForCalculation, currentTag, rowTags, tagsToRemove);

        if (parent != null) {
            boolean currentRowDurationMoreThenParent =
                    isCurrentRowDurationMoreThanParent(parent, row, currentTag, duration);

            if (currentRowDurationMoreThenParent) {
                TagDurationReportRowDto currentRowSibling = parent.getChildren().stream()
                        .filter(r -> r.getTag().equals(currentTag))
                        .findFirst()
                        .orElse(null);

                long siblingDuration = currentRowSibling == null ? 0L : currentRowSibling.getDuration();

                TagDurationReportRowDto newChild = new TagDurationReportRowDto(
                        currentTag,
                        row.getDuration() + siblingDuration,
                        new ArrayList<>(List.of(row))
                );

                parent.getChildren().remove(currentRowSibling);
                parent.addChild(newChild);
                parent.getChildren().remove(row);

                return new UpdatingResult(currentTag, siblingDuration, true);
            }
        }

        Map<List<String>, Long> pomodoroTagsToDurationsCopy = new HashMap<>(pomodoroTagsToDurations);

        pomodoroTagsToDurationsCopy.keySet()
                .removeAll(tagsToRemove);

        if (duration == row.getDuration()) {
            row.setTag(String.join(" #", row.getTag(), currentTag));
            return new UpdatingResult(null, null, true);
        }

        List<TagDurationReportRowDto> children = new ArrayList<>(row.getChildren());

        boolean childNotUpdated = true;
        for (TagDurationReportRowDto child : children) {
            if (child.getDuration() == duration) {
                childNotUpdated = false;
                if (!child.getTag().equals(currentTag)) {
                    child.setTag(String.join(" #", child.getTag(), currentTag));
                    children.remove(child);
                    break;
                }
            }
        }

        boolean childAdded = false;
        String leftoverTag = null;
        Long leftoverDuration = null;

        TagDuration currentTagDuration = tagDurationList.stream()
                .filter(td -> td.getTag().equals(currentTag))
                .findFirst()
                .orElse(null);

        if (duration > 0L && childNotUpdated) {

            if (currentTagDuration != null) {
                leftoverTag = currentTag;
                leftoverDuration = currentTagDuration.getDuration() - duration;
            }

            addChildToRow(row, currentTag, duration);
            childAdded = true;
        }

        if (CollectionUtils.isEmpty(children)) {
            if (currentTagDuration != null && currentTagDuration.getDuration() > duration) {
                Long tagLeftoverDuration = leftOverTagsWithDuration.get(currentTag);
                long updatedLeftoverDuration = 0L;

                if (tagLeftoverDuration == null) {
                    updatedLeftoverDuration = currentTagDuration.getDuration() - duration;
                } else {
                    updatedLeftoverDuration -= tagLeftoverDuration;
                }

                tagDurationList.remove(currentTagDuration);
                tagDurationList.add(new TagDuration(currentTagDuration.getTag(), updatedLeftoverDuration));

                if (updatedLeftoverDuration == 0L) {
                    return new UpdatingResult(null, null, true);
                } else {
                    return new UpdatingResult(currentTag, updatedLeftoverDuration, true);
                }

            }

            return new UpdatingResult(null, null, true);
        }

        if (!childNotUpdated) {
            return new UpdatingResult(null, null, true);
        }

        boolean updated = false;
        for (TagDurationReportRowDto child : children) {
            UpdatingResult updatingResult = updateTree(child, row, pomodoroTagsToDurationsCopy, currentTag, rowTags, tagDurationList, leftOverTagsWithDuration);
            String childLeftoverTag = updatingResult.getLeftoverTag();

            if (childLeftoverTag != null) {
                leftoverTag = childLeftoverTag;

                long currentTagDurationDuration = currentTagDuration == null ? 0L : currentTagDuration.getDuration();
                if (leftoverDuration != null) {
                    leftoverDuration = currentTagDurationDuration - leftoverDuration - updatingResult.getLeftoverDuration();
                } else {
                    leftoverDuration = updatingResult.getLeftoverDuration();
                }
            }

            if (updatingResult.isUpdated()) {
                updated = true;
            }
        }

        boolean result = updated || childAdded;

        if (leftoverDuration != null && leftoverDuration > 0L) {
            return new UpdatingResult(leftoverTag, leftoverDuration, result);
        }

        return new UpdatingResult(null, null, result);
    }

    private boolean isCurrentRowDurationMoreThanParent(TagDurationReportRowDto parentRow,
                                                       TagDurationReportRowDto row,
                                                       String currentTag,
                                                       long duration) {
        List<TagDurationReportRowDto> children = parentRow.getChildren();

        long rowRelatedDuration = children.stream()
                .filter(r -> r.getTag().equals(row.getTag()))
                .mapToLong(TagDurationReportRowDto::getDuration)
                .sum();

        long currentRowDuration = children.stream()
                .filter(r -> r.getTag().equals(currentTag))
                .mapToLong(TagDurationReportRowDto::getDuration)
                .sum() + duration;

        return currentRowDuration > rowRelatedDuration;
    }

    private long calculateDuration(Map<List<String>, Long> pomodoroTagsToDurations,
                                   String currentTag,
                                   Set<String> passedTags,
                                   Set<List<String>> tagsToRemove) {
        long duration = 0L;
        for (Map.Entry<List<String>, Long> entry : pomodoroTagsToDurations.entrySet()) {
            if (!entry.getKey().contains(currentTag)) {
                continue;
            }

            Set<String> neighbours = new HashSet<>(entry.getKey());
            neighbours.removeAll(passedTags);
            neighbours.remove(currentTag);

            if (CollectionUtils.isEmpty(neighbours)) {
                duration += entry.getValue();
                tagsToRemove.add(entry.getKey());
                continue;
            }

            if (includeInDurationCalculation(pomodoroTagsToDurations, passedTags, neighbours, currentTag)) {
                duration += entry.getValue();
                tagsToRemove.add(entry.getKey());
            }
        }

        return duration;
    }

    private boolean includeInDurationCalculation(Map<List<String>, Long> pomodoroTagsToDurations,
                                                 Set<String> passedTags,
                                                 Set<String> neighbours,
                                                 String currentTag) {
        return isEveryNeighbourMeetAnyOfPassedTags(pomodoroTagsToDurations, neighbours, passedTags)
                && isNeighboursDurationNotMoreThanCurrentTag(pomodoroTagsToDurations, passedTags, neighbours, currentTag);
    }

    private boolean isEveryNeighbourMeetAnyOfPassedTags(Map<List<String>, Long> pomodoroTagsToDurations,
                                                        Set<String> neighbours,
                                                        Set<String> passedTags) {
        for (String neighbour : neighbours) {
            for (Map.Entry<List<String>, Long> entry : pomodoroTagsToDurations.entrySet()) {
                if (entry.getKey().contains(neighbour) && containsAnyOfPassedTags(passedTags, entry)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean containsAnyOfPassedTags(Set<String> passedTags, Map.Entry<List<String>, Long> entry) {
        for (String tag : passedTags) {
            if (entry.getKey().contains(tag)) {
                return true;
            }
        }

        return false;
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
        if (tagDuration == null) {
            return;
        }

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

    private TagDurationReportRowDto buildTotalRow(List<TagDurationReportRowDto> rootRows) {
        long duration = rootRows.stream()
                .mapToLong(TagDurationReportRowDto::getDuration)
                .sum();

        return new TagDurationReportRowDto(TOTAL_REPORT_ROW_NAME, duration, Collections.emptyList());
    }

    private Set<String> mapToTagNames(TagDurationReportRowDto mappedReportRow) {
        return Arrays.stream(mappedReportRow.getTag().split(" "))
                .map(name -> name.replace("#", ""))
                .collect(Collectors.toSet());
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
                return 0;
            } else {
                return 1;
            }
        }
    }

    @Getter
    @AllArgsConstructor
    private static class UpdatingResult {

        private final String leftoverTag;
        private final Long leftoverDuration;
        private final boolean updated;

    }

}
