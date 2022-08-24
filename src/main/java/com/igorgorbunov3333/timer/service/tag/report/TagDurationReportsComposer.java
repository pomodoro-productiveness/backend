package com.igorgorbunov3333.timer.service.tag.report;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportRowDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
//TODO: refactor
public class TagDurationReportsComposer {

    private static final String TOTAL_REPORT_ROW_NAME = "Total";

    private final AllTagsDurationReporter allTagsDurationReporter;

    public List<TagDurationReportRowDto> compose(List<PomodoroDto> pomodoro) {
        List<TagDurationReportRowDtoClone> reportRows = allTagsDurationReporter.reportForEachTag(pomodoro).stream()
                .map(this::mapToClone)
                .collect(Collectors.toList());

        Map<String, TagDurationReportRowDtoClone> tagsToReportRows = reportRows.stream()
                .collect(Collectors.toMap(TagDurationReportRowDtoClone::getTag, Function.identity()));

        List<TagDurationReportRowDtoClone> rootReportRows = getRootReportRows(reportRows, tagsToReportRows);

        List<TagDurationReportRowDtoClone> mergedReportRows = mergeReportRows(tagsToReportRows, rootReportRows);

        List<TagDurationReportRowDtoClone> allRows = collectAllReportRows(mergedReportRows);

        Map<TagDurationReportRowDtoClone, List<ParentWithChildrenContainer>> rowsToParentRows = new HashMap<>();
        for (TagDurationReportRowDtoClone reportRow : allRows) {
            List<ParentWithChildrenContainer> parentsWithChildren = rowsToParentRows.get(reportRow);

            if (CollectionUtils.isEmpty(parentsWithChildren)) {
                parentsWithChildren = new ArrayList<>();
            }

            ParentWithChildrenContainer parentWithChildrenContainer =
                    new ParentWithChildrenContainer(reportRow.getParentRow(), reportRow.getMappedRows());
            parentsWithChildren.add(parentWithChildrenContainer);

            rowsToParentRows.put(reportRow, parentsWithChildren);
        }

        for (TagDurationReportRowDtoClone rootReport : mergedReportRows) {
            ParentWithChildrenContainer parentWithChildrenContainer =
                    new ParentWithChildrenContainer(null, rootReport.getMappedRows());
            rowsToParentRows.put(rootReport, List.of(parentWithChildrenContainer));
        }

        List<TagDurationReportRowDtoClone> composedMergedReportsRows = new ArrayList<>();
        for (TagDurationReportRowDtoClone mergedReportRow : mergedReportRows) {
            List<TagDurationReportRowDtoClone> fixedRows =
                    clearDuplicatesAndFixRowsDuration(mergedReportRow.getMappedRows(), rowsToParentRows);
            mergedReportRow.setMappedRows(fixedRows);
            composedMergedReportsRows.add(mergedReportRow);
        }

        composedMergedReportsRows.sort(Comparator.comparing(TagDurationReportRowDtoClone::getTag));

        TagDurationReportRowDtoClone totalReportRow = buildTotalReportRow(composedMergedReportsRows);
        composedMergedReportsRows.add(totalReportRow);

        return mapToRows(composedMergedReportsRows);
    }

    private TagDurationReportRowDtoClone mapToClone(TagDurationReportRowDto row) {
        List<TagDurationReportRowDto> mappedRows = row.getMappedRows();

        List<TagDurationReportRowDtoClone> mappedCloneRows = new ArrayList<>();
        for (TagDurationReportRowDto mappedRow : mappedRows) {
            mappedCloneRows.add(new TagDurationReportRowDtoClone(mappedRow.getTag(), mappedRow.getDuration(), List.of(), null));
        }

        return new TagDurationReportRowDtoClone(row.getTag(), row.getDuration(), mappedCloneRows, null);
    }

    private List<TagDurationReportRowDtoClone> getRootReportRows(List<TagDurationReportRowDtoClone> reportRows,
                                                                 Map<String, TagDurationReportRowDtoClone> tagsToReportRows) {
        List<TagDurationReportRowDtoClone> rootRows = new LinkedList<>();
        Map<Long, TagDurationReportRowDtoClone> durationToReportRows = new HashMap<>();
        for (TagDurationReportRowDtoClone row : reportRows) {
            if (isRootReportRow(row, tagsToReportRows)) {
                long reportDuration = row.getDuration();

                if (durationToReportRows.containsKey(reportDuration)) {
                    TagDurationReportRowDtoClone reportWithSameDuration = durationToReportRows.get(reportDuration);

                    rootRows.remove(reportWithSameDuration);

                    reportWithSameDuration.setTag(reportWithSameDuration.getTag() + " #" + row.getTag());

                    rootRows.add(reportWithSameDuration);
                } else {
                    rootRows.add(row);
                    durationToReportRows.put(reportDuration, row);
                }
            }
        }

        return rootRows;
    }

    private boolean isRootReportRow(TagDurationReportRowDtoClone reportRow,
                                    Map<String, TagDurationReportRowDtoClone> tagsToReportRows) {
        return reportRow.getMappedRows().stream()
                .allMatch(row -> isRootReportRow(reportRow, tagsToReportRows, row));
    }

    private boolean isRootReportRow(TagDurationReportRowDtoClone reportRow,
                                    Map<String, TagDurationReportRowDtoClone> tagsToReportRows,
                                    TagDurationReportRowDtoClone mappedRow) {
        return mapToTagNames(mappedRow).stream()
                .map(tagsToReportRows::get)
                .allMatch(row -> row.getDuration() <= reportRow.getDuration());
    }

    private List<TagDurationReportRowDtoClone> mergeReportRows(Map<String, TagDurationReportRowDtoClone> tagsToReportRows,
                                                               List<TagDurationReportRowDtoClone> reportRows) {
        List<TagDurationReportRowDtoClone> mergedReportRows = new ArrayList<>();
        for (TagDurationReportRowDtoClone rootReportRow : reportRows) {
            List<TagDurationReportRowDtoClone> mappedRows =
                    mapSubRows(rootReportRow, rootReportRow.getMappedRows(), tagsToReportRows, new HashMap<>());
            rootReportRow.setMappedRows(mappedRows);
            mergedReportRows.add(rootReportRow);
        }

        return mergedReportRows;
    }

    public List<TagDurationReportRowDtoClone> mapSubRows(TagDurationReportRowDtoClone mainReportRow,
                                                         List<TagDurationReportRowDtoClone> mappedReportRows,
                                                         Map<String, TagDurationReportRowDtoClone> tagsToReportRows,
                                                         Map<String, ProcessedTagsContainer> processedTags) {
        List<TagDurationReportRowDtoClone> subRows = new ArrayList<>();

        if (CollectionUtils.isEmpty(mappedReportRows)) {
            return List.of();
        }

        for (TagDurationReportRowDtoClone row : mappedReportRows) {
            Set<String> tagNames = mapToTagNames(row);

            Queue<TagDurationReportRowDtoClone> rows = getRowsByNames(tagsToReportRows, tagNames);

            List<TagDurationReportRowDtoClone> currentRowSubRows = new LinkedList<>();
            for (TagDurationReportRowDtoClone currentRow : rows) {
                if (isAlreadyProcessed(currentRow, processedTags, tagsToReportRows)) {
                    continue;
                }

                String currentRowTagName = currentRow.getTag();

                if (currentRow.getDuration() < mainReportRow.getDuration()) {
                    if (!currentRowSubRows.isEmpty()) {
                        TagDurationReportRowDtoClone previousRow = currentRowSubRows.get(currentRowSubRows.size() - 1);

                        if (previousRow.getDuration() == currentRow.getDuration()) {
                            previousRow.setTag(previousRow.getTag() + " #" + currentRowTagName);
                            continue;
                        }
                    }

                    long duration = calculateDuration(currentRow, mainReportRow, tagsToReportRows);

                    currentRow = new TagDurationReportRowDtoClone(currentRowTagName, duration, currentRow.getMappedRows(), mainReportRow);
                    currentRowSubRows.add(currentRow);

                    List<TagDurationReportRowDtoClone> currentTagNameMappedReportRows = tagsToReportRows.get(currentRowTagName)
                            .getMappedRows();
                    List<TagDurationReportRowDtoClone> currentRowSubRowsSubRows =
                            mapSubRows(currentRow, currentTagNameMappedReportRows, tagsToReportRows, processedTags);
                    currentRow.setMappedRows(currentRowSubRowsSubRows);
                    processedTags.put(currentRowTagName, new ProcessedTagsContainer(currentRowTagName, duration));
                }
            }
            subRows.addAll(currentRowSubRows);
        }

        return subRows;
    }

    private LinkedList<TagDurationReportRowDtoClone> getRowsByNames(Map<String, TagDurationReportRowDtoClone> tagsToRows,
                                                                    Set<String> tagNames) {
        return tagNames.stream()
                .map(tagsToRows::get)
                .sorted(Comparator.comparing(TagDurationReportRowDtoClone::getDuration).reversed())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private long calculateDuration(TagDurationReportRowDtoClone currentRow,
                                   TagDurationReportRowDtoClone mainRow,
                                   Map<String, TagDurationReportRowDtoClone> tagsToRows) {
        List<TagDurationReportRowDtoClone> mappedRows = tagsToRows.get(currentRow.getTag()).getMappedRows();

        long duration = 0L;

        for (TagDurationReportRowDtoClone mappedRow : mappedRows) {
            Set<String> tagNames = mapToTagNames(mappedRow);
            List<TagDurationReportRowDtoClone> rows = getRowsByNames(tagsToRows, tagNames);

            boolean containMainRow = false;
            for (TagDurationReportRowDtoClone subRow : rows) {
                if (subRow.getTag().equals(mainRow.getTag())) {
                    containMainRow = true;
                    break;
                }
            }

            if (containMainRow) {
                duration += mappedRow.getDuration();
            }
        }

        return duration;
    }

    private boolean isAlreadyProcessed(TagDurationReportRowDtoClone targetRow,
                                       Map<String, ProcessedTagsContainer> processedTags,
                                       Map<String, TagDurationReportRowDtoClone> tagsToReportRows) {
        ProcessedTagsContainer processedTagsContainer = processedTags.get(targetRow.getTag());

        long targetRowDuration = getTargetRowDuration(targetRow.getTag(), tagsToReportRows);

        return processedTagsContainer != null
                && processedTagsContainer.getDuration() == targetRowDuration;
    }

    private long getTargetRowDuration(String targetTagName, Map<String, TagDurationReportRowDtoClone> tagsToReportRows) {
        String[] tags = targetTagName.split(" #");

        return tagsToReportRows.get(tags[0])
                .getDuration();
    }

    private Set<String> mapToTagNames(TagDurationReportRowDtoClone mappedReportRow) {
        return Arrays.stream(mappedReportRow.getTag().split(" "))
                .map(tag -> tag.replace("#", ""))
                .collect(Collectors.toSet());
    }

    private List<TagDurationReportRowDtoClone> collectAllReportRows(List<TagDurationReportRowDtoClone> reportsRows) {
        List<TagDurationReportRowDtoClone> firstLevelRows = reportsRows.stream()
                .flatMap(report -> report.getMappedRows().stream())
                .collect(Collectors.toList());

        List<TagDurationReportRowDtoClone> allRows = firstLevelRows.stream()
                .map(this::collectAllRows)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        allRows.addAll(firstLevelRows);

        return allRows;
    }

    private List<TagDurationReportRowDtoClone> collectAllRows(TagDurationReportRowDtoClone parentRow) {
        List<TagDurationReportRowDtoClone> allRows = new ArrayList<>();

        if (!CollectionUtils.isEmpty(parentRow.getMappedRows())) {
            for (TagDurationReportRowDtoClone child : parentRow.getMappedRows()) {
                allRows.add(child);
                allRows.addAll(collectAllRows(child));
            }
        }

        return allRows;
    }

    private List<TagDurationReportRowDtoClone> clearDuplicatesAndFixRowsDuration(List<TagDurationReportRowDtoClone> children,
                                                                                 Map<TagDurationReportRowDtoClone, List<ParentWithChildrenContainer>> rowToParentRows) {
        List<TagDurationReportRowDtoClone> fixedRows = new ArrayList<>();
        for (TagDurationReportRowDtoClone row : children) {
            TagDurationReportRowDtoClone fixedRow = row;

            String rowTagName = row.getTag();

            if (!CollectionUtils.isEmpty(row.getMappedRows())) {
                List<TagDurationReportRowDtoClone> fixedChildRows = clearDuplicatesAndFixRowsDuration(row.getMappedRows(), rowToParentRows);
                fixedRow = new TagDurationReportRowDtoClone(rowTagName, row.getDuration(), fixedChildRows, row.getParentRow());
            }

            List<ParentWithChildrenContainer> parentWithChildrenContainers = rowToParentRows.get(row);
            if (!CollectionUtils.isEmpty(parentWithChildrenContainers) && parentWithChildrenContainers.size() > 1) {
                if (CollectionUtils.isEmpty(row.getMappedRows())
                        && isCurrentRowWithSameParentAsOthersAndCurrentWithoutChildren(row, parentWithChildrenContainers)) {
                    continue;
                }

                if (isMoreSeniorParentAndLongerDuration(row, rowToParentRows)) {
                    TagDurationReportRowDtoClone parentRow = row.getParentRow();
                    List<ParentWithChildrenContainer> parentRowParentWithChildrenContainers = rowToParentRows.get(parentRow);

                    List<TagDurationReportRowDtoClone> siblings = parentRowParentWithChildrenContainers.stream()
                            .flatMap(container -> container.getChildren().stream())
                            .collect(Collectors.toList());

                    TagDurationReportRowDtoClone closestRowWithSameTagAmongSiblings =
                            findClosestRowWithSameTagAmongSiblings(rowTagName, siblings);

                    if (closestRowWithSameTagAmongSiblings != null) {
                        long updatedDuration = row.getDuration() - closestRowWithSameTagAmongSiblings.getDuration();

                        if (updatedDuration < 1L) {
                            continue;
                        }

                        fixedRow.setDuration(updatedDuration);
                    }
                }
            }

            fixedRows.add(fixedRow);
        }

        return fixedRows;
    }

    private boolean isCurrentRowWithSameParentAsOthersAndCurrentWithoutChildren(TagDurationReportRowDtoClone row,
                                                                                List<ParentWithChildrenContainer> containers) {
        long containersWithSameParentAmount = containers.stream()
                .filter(container -> container.getParent().equals(row.getParentRow()))
                .count();
        return containersWithSameParentAmount > 1L && CollectionUtils.isEmpty(row.getMappedRows());
    }

    private boolean isMoreSeniorParentAndLongerDuration(TagDurationReportRowDtoClone row,
                                                        Map<TagDurationReportRowDtoClone, List<ParentWithChildrenContainer>> rowToParentRows) {
        TagDurationReportRowDtoClone currentRowParent = row.getParentRow();

        return rowToParentRows.get(row).stream()
                .noneMatch(container -> isMoreSenior(container.getParent(), currentRowParent));
    }

    private boolean isMoreSenior(TagDurationReportRowDtoClone target, TagDurationReportRowDtoClone comparedTo) {
        return calculateLevel(target) < calculateLevel(comparedTo);
    }

    private TagDurationReportRowDtoClone findClosestRowWithSameTagAmongSiblings(String tagName,
                                                                                List<TagDurationReportRowDtoClone> siblings) {
        siblings = siblings.stream()
                .filter(s -> !s.getTag().equals(tagName))
                .collect(Collectors.toList());
        List<Pair<TagDurationReportRowDtoClone, Integer>> pairs = new ArrayList<>();
        findRowsWithSameTagWithNestingLevels(tagName, siblings, 0, pairs);

        if (!CollectionUtils.isEmpty(pairs)) {
            pairs.sort(Comparator.comparing(Pair::getRight));
            return pairs.get(0).getLeft();
        }

        return null;
    }

    private void findRowsWithSameTagWithNestingLevels(String tagName,
                                                      List<TagDurationReportRowDtoClone> rows,
                                                      int nestingLevel,
                                                      List<Pair<TagDurationReportRowDtoClone, Integer>> pairs) {
        if (!CollectionUtils.isEmpty(rows)) {
            for (TagDurationReportRowDtoClone row : rows) {
                if (row.getTag().equals(tagName)) {
                    pairs.add(Pair.of(row, nestingLevel));
                }
                findRowsWithSameTagWithNestingLevels(tagName, row.getMappedRows(), nestingLevel + 1, pairs);
            }
        }
    }

    private int calculateLevel(TagDurationReportRowDtoClone row) {
        int count = 0;
        while (row.getParentRow() != null) {
            ++count;
            row = row.getParentRow();
        }

        return count;
    }

    private TagDurationReportRowDtoClone buildTotalReportRow(List<TagDurationReportRowDtoClone> reportRows) {
        long duration = 0;
        for (TagDurationReportRowDtoClone report : reportRows) {
            duration += report.getDuration();
        }

        return new TagDurationReportRowDtoClone(TOTAL_REPORT_ROW_NAME, duration, Collections.emptyList(), null);
    }

    private List<TagDurationReportRowDto> mapToRows(List<TagDurationReportRowDtoClone> clones) {
        if (CollectionUtils.isEmpty(clones)) {
            return List.of();
        }

        List<TagDurationReportRowDto> rows = new ArrayList<>();
        for (TagDurationReportRowDtoClone clone : clones) {
            List<TagDurationReportRowDto> mappedRows = mapToRows(clone.getMappedRows());
            TagDurationReportRowDto row = new TagDurationReportRowDto(clone.getTag(), clone.getDuration(), mappedRows);
            rows.add(row);
        }

        return rows;
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode(of = "processedTag")
    private static class ProcessedTagsContainer {

        private final String processedTag;
        private final long duration;

    }

    @Getter
    @AllArgsConstructor
    private static class ParentWithChildrenContainer {

        private final TagDurationReportRowDtoClone parent;
        private final List<TagDurationReportRowDtoClone> children;

    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode(of = "tag")
    @ToString(of = {"tag", "duration"})
    private static class TagDurationReportRowDtoClone {

        @Setter
        private String tag;
        @Setter
        private long duration;

        @Setter
        private List<TagDurationReportRowDtoClone> mappedRows;

        private TagDurationReportRowDtoClone parentRow;
    }

}
