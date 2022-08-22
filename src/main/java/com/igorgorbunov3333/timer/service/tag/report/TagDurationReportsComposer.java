package com.igorgorbunov3333.timer.service.tag.report;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportRowDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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
        List<TagDurationReportRowDto> reportRows = allTagsDurationReporter.reportForEachTag(pomodoro);

        Map<String, TagDurationReportRowDto> tagsToReportRows = reportRows.stream()
                .collect(Collectors.toMap(TagDurationReportRowDto::getTag, Function.identity()));

        List<TagDurationReportRowDto> rootReportRows = getRootReportRows(reportRows, tagsToReportRows);

        List<TagDurationReportRowDto> mergedReportRows = mergeReportRows(tagsToReportRows, rootReportRows);

        List<TagDurationReportRowDto> allRows = collectAllReportRows(mergedReportRows);

        Map<TagDurationReportRowDto, List<ParentWithChildrenContainer>> rowsToParentRows = new HashMap<>();
        for (TagDurationReportRowDto reportRow : allRows) {
            List<ParentWithChildrenContainer> parentsWithChildren = rowsToParentRows.get(reportRow);

            if (CollectionUtils.isEmpty(parentsWithChildren)) {
                parentsWithChildren = new ArrayList<>();
            }

            ParentWithChildrenContainer parentWithChildrenContainer =
                    new ParentWithChildrenContainer(reportRow.getParentRow(), reportRow.getMappedRows());
            parentsWithChildren.add(parentWithChildrenContainer);

            rowsToParentRows.put(reportRow, parentsWithChildren);
        }

        for (TagDurationReportRowDto rootReport : mergedReportRows) {
            ParentWithChildrenContainer parentWithChildrenContainer =
                    new ParentWithChildrenContainer(null, rootReport.getMappedRows());
            rowsToParentRows.put(rootReport, List.of(parentWithChildrenContainer));
        }

        List<TagDurationReportRowDto> composedMergedReportsRows = new ArrayList<>();
        for (TagDurationReportRowDto mergedReportRow : mergedReportRows) {
            List<TagDurationReportRowDto> fixedRows =
                    clearDuplicatesAndFixRowsDuration(mergedReportRow.getMappedRows(), rowsToParentRows);
            mergedReportRow.setMappedRows(fixedRows);
            composedMergedReportsRows.add(mergedReportRow);
        }

        composedMergedReportsRows.sort(Comparator.comparing(TagDurationReportRowDto::getTag));

        TagDurationReportRowDto totalReportRow = buildTotalReportRow(composedMergedReportsRows);
        composedMergedReportsRows.add(totalReportRow);

        return composedMergedReportsRows;
    }

    private List<TagDurationReportRowDto> getRootReportRows(List<TagDurationReportRowDto> reportRows,
                                                            Map<String, TagDurationReportRowDto> tagsToReportRows) {
        List<TagDurationReportRowDto> rootRows = new LinkedList<>();
        Map<Long, TagDurationReportRowDto> durationToReportRows = new HashMap<>();
        for (TagDurationReportRowDto row : reportRows) {
            if (isRootReportRow(row, tagsToReportRows)) {
                long reportDuration = row.getDuration();

                if (durationToReportRows.containsKey(reportDuration)) {
                    TagDurationReportRowDto reportWithSameDuration = durationToReportRows.get(reportDuration);

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

    private boolean isRootReportRow(TagDurationReportRowDto reportRow,
                                    Map<String, TagDurationReportRowDto> tagsToReportRows) {
        return reportRow.getMappedRows().stream()
                .allMatch(row -> isRootReportRow(reportRow, tagsToReportRows, row));
    }

    private boolean isRootReportRow(TagDurationReportRowDto reportRow,
                                    Map<String, TagDurationReportRowDto> tagsToReportRows,
                                    TagDurationReportRowDto mappedRow) {
        return mapToTagNames(mappedRow).stream()
                .map(tagsToReportRows::get)
                .allMatch(row -> row.getDuration() <= reportRow.getDuration());
    }

    private List<TagDurationReportRowDto> mergeReportRows(Map<String, TagDurationReportRowDto> tagsToReportRows,
                                                          List<TagDurationReportRowDto> reportRows) {
        List<TagDurationReportRowDto> mergedReportRows = new ArrayList<>();
        for (TagDurationReportRowDto rootReportRow : reportRows) {
            List<TagDurationReportRowDto> mappedRows =
                    mapSubRows(rootReportRow, rootReportRow.getMappedRows(), tagsToReportRows, new HashMap<>());
            rootReportRow.setMappedRows(mappedRows);
            mergedReportRows.add(rootReportRow);
        }

        return mergedReportRows;
    }

    public List<TagDurationReportRowDto> mapSubRows(TagDurationReportRowDto mainReportRow,
                                                    List<TagDurationReportRowDto> mappedReportRows,
                                                    Map<String, TagDurationReportRowDto> tagsToReportRows,
                                                    Map<String, ProcessedTagsContainer> processedTags) {
        List<TagDurationReportRowDto> subRows = new ArrayList<>();

        if (CollectionUtils.isEmpty(mappedReportRows)) {
            return List.of();
        }

        for (TagDurationReportRowDto row : mappedReportRows) {
            Set<String> tagNames = mapToTagNames(row);

            Queue<TagDurationReportRowDto> rows = getRowsByNames(tagsToReportRows, tagNames);

            List<TagDurationReportRowDto> currentRowSubRows = new LinkedList<>();
            for (TagDurationReportRowDto currentRow : rows) {
                if (isAlreadyProcessed(currentRow, processedTags, tagsToReportRows)) {
                    continue;
                }

                String currentRowTagName = currentRow.getTag();

                if (currentRow.getDuration() < mainReportRow.getDuration()) {
                    if (!currentRowSubRows.isEmpty()) {
                        TagDurationReportRowDto previousRow = currentRowSubRows.get(currentRowSubRows.size() - 1);

                        if (previousRow.getDuration() == currentRow.getDuration()) {
                            previousRow.setTag(previousRow.getTag() + " #" + currentRowTagName);
                            continue;
                        }
                    }

                    long duration = calculateDuration(currentRow, mainReportRow, tagsToReportRows);

                    currentRow = new TagDurationReportRowDto(currentRowTagName, duration, currentRow.getMappedRows(), mainReportRow);
                    currentRowSubRows.add(currentRow);

                    List<TagDurationReportRowDto> currentTagNameMappedReportRows = tagsToReportRows.get(currentRowTagName)
                            .getMappedRows();
                    List<TagDurationReportRowDto> currentRowSubRowsSubRows =
                            mapSubRows(currentRow, currentTagNameMappedReportRows, tagsToReportRows, processedTags);
                    currentRow.setMappedRows(currentRowSubRowsSubRows);
                    processedTags.put(currentRowTagName, new ProcessedTagsContainer(currentRowTagName, duration));
                }
            }
            subRows.addAll(currentRowSubRows);
        }

        return subRows;
    }

    private LinkedList<TagDurationReportRowDto> getRowsByNames(Map<String, TagDurationReportRowDto> tagsToRows,
                                                               Set<String> tagNames) {
        return tagNames.stream()
                .map(tagsToRows::get)
                .sorted(Comparator.comparing(TagDurationReportRowDto::getDuration).reversed())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private long calculateDuration(TagDurationReportRowDto currentRow,
                                   TagDurationReportRowDto mainRow,
                                   Map<String, TagDurationReportRowDto> tagsToRows) {
        List<TagDurationReportRowDto> mappedRows = tagsToRows.get(currentRow.getTag()).getMappedRows();

        long duration = 0L;

        for (TagDurationReportRowDto mappedRow : mappedRows) {
            Set<String> tagNames = mapToTagNames(mappedRow);
            List<TagDurationReportRowDto> rows = getRowsByNames(tagsToRows, tagNames);

            boolean containMainRow = false;
            for (TagDurationReportRowDto subRow : rows) {
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

    private boolean isAlreadyProcessed(TagDurationReportRowDto targetRow,
                                       Map<String, ProcessedTagsContainer> processedTags,
                                       Map<String, TagDurationReportRowDto> tagsToReportRows) {
        ProcessedTagsContainer processedTagsContainer = processedTags.get(targetRow.getTag());

        long targetRowDuration = getTargetRowDuration(targetRow.getTag(), tagsToReportRows);

        return processedTagsContainer != null
                && processedTagsContainer.getDuration() == targetRowDuration;
    }

    private long getTargetRowDuration(String targetTagName, Map<String, TagDurationReportRowDto> tagsToReportRows) {
        String[] tags = targetTagName.split(" #");

        return tagsToReportRows.get(tags[0])
                .getDuration();
    }

    private Set<String> mapToTagNames(TagDurationReportRowDto mappedReportRow) {
        return Arrays.stream(mappedReportRow.getTag().split(" "))
                .map(tag -> tag.replace("#", ""))
                .collect(Collectors.toSet());
    }

    private List<TagDurationReportRowDto> collectAllReportRows(List<TagDurationReportRowDto> reportsRows) {
        List<TagDurationReportRowDto> firstLevelRows = reportsRows.stream()
                .flatMap(report -> report.getMappedRows().stream())
                .collect(Collectors.toList());

        List<TagDurationReportRowDto> allRows = firstLevelRows.stream()
                .map(this::collectAllRows)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        allRows.addAll(firstLevelRows);

        return allRows;
    }

    private List<TagDurationReportRowDto> collectAllRows(TagDurationReportRowDto parentRow) {
        List<TagDurationReportRowDto> allRows = new ArrayList<>();

        if (!CollectionUtils.isEmpty(parentRow.getMappedRows())) {
            for (TagDurationReportRowDto child : parentRow.getMappedRows()) {
                allRows.add(child);
                allRows.addAll(collectAllRows(child));
            }
        }

        return allRows;
    }

    private List<TagDurationReportRowDto> clearDuplicatesAndFixRowsDuration(List<TagDurationReportRowDto> children,
                                                                            Map<TagDurationReportRowDto, List<ParentWithChildrenContainer>> rowToParentRows) {
        List<TagDurationReportRowDto> fixedRows = new ArrayList<>();
        for (TagDurationReportRowDto row : children) {
            TagDurationReportRowDto fixedRow = row;

            String rowTagName = row.getTag();

            if (!CollectionUtils.isEmpty(row.getMappedRows())) {
                List<TagDurationReportRowDto> fixedChildRows = clearDuplicatesAndFixRowsDuration(row.getMappedRows(), rowToParentRows);
                fixedRow = new TagDurationReportRowDto(rowTagName, row.getDuration(), fixedChildRows, row.getParentRow());
            }

            List<ParentWithChildrenContainer> parentWithChildrenContainers = rowToParentRows.get(row);
            if (!CollectionUtils.isEmpty(parentWithChildrenContainers) && parentWithChildrenContainers.size() > 1) {
                if (CollectionUtils.isEmpty(row.getMappedRows())
                        && isCurrentRowWithSameParentAsOthersAndCurrentWithoutChildren(row, parentWithChildrenContainers)) {
                    continue;
                }

                if (isMoreSeniorParentAndLongerDuration(row, rowToParentRows)) {
                    TagDurationReportRowDto parentRow = row.getParentRow();
                    List<ParentWithChildrenContainer> parentRowParentWithChildrenContainers = rowToParentRows.get(parentRow);

                    List<TagDurationReportRowDto> siblings = parentRowParentWithChildrenContainers.stream()
                            .flatMap(container -> container.getChildren().stream())
                            .collect(Collectors.toList());

                    TagDurationReportRowDto closestRowWithSameTagAmongSiblings =
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

    private boolean isCurrentRowWithSameParentAsOthersAndCurrentWithoutChildren(TagDurationReportRowDto row,
                                                                                List<ParentWithChildrenContainer> containers) {
        long containersWithSameParentAmount = containers.stream()
                .filter(container -> container.getParent().equals(row.getParentRow()))
                .count();
        return containersWithSameParentAmount > 1L && CollectionUtils.isEmpty(row.getMappedRows());
    }

    private boolean isMoreSeniorParentAndLongerDuration(TagDurationReportRowDto row,
                                                        Map<TagDurationReportRowDto, List<ParentWithChildrenContainer>> rowToParentRows) {
        TagDurationReportRowDto currentRowParent = row.getParentRow();

        return rowToParentRows.get(row).stream()
                .noneMatch(container -> isMoreSenior(container.getParent(), currentRowParent));
    }

    private boolean isMoreSenior(TagDurationReportRowDto target, TagDurationReportRowDto comparedTo) {
        return calculateLevel(target) < calculateLevel(comparedTo);
    }

    private TagDurationReportRowDto findClosestRowWithSameTagAmongSiblings(String tagName,
                                                                           List<TagDurationReportRowDto> siblings) {
        siblings = siblings.stream()
                .filter(s -> !s.getTag().equals(tagName))
                .collect(Collectors.toList());
        List<Pair<TagDurationReportRowDto, Integer>> pairs = new ArrayList<>();
        findRowsWithSameTagWithNestingLevels(tagName, siblings, 0, pairs);

        if (!CollectionUtils.isEmpty(pairs)) {
            pairs.sort(Comparator.comparing(Pair::getRight));
            return pairs.get(0).getLeft();
        }

        return null;
    }

    private void findRowsWithSameTagWithNestingLevels(String tagName,
                                                      List<TagDurationReportRowDto> rows,
                                                      int nestingLevel,
                                                      List<Pair<TagDurationReportRowDto, Integer>> pairs) {
        if (!CollectionUtils.isEmpty(rows)) {
            for (TagDurationReportRowDto row : rows) {
                if (row.getTag().equals(tagName)) {
                    pairs.add(Pair.of(row, nestingLevel));
                }
                findRowsWithSameTagWithNestingLevels(tagName, row.getMappedRows(), nestingLevel + 1, pairs);
            }
        }
    }

    private int calculateLevel(TagDurationReportRowDto row) {
        int count = 0;
        while (row.getParentRow() != null) {
            ++count;
            row = row.getParentRow();
        }

        return count;
    }

    private TagDurationReportRowDto buildTotalReportRow(List<TagDurationReportRowDto> reportRows) {
        long duration = 0;
        for (TagDurationReportRowDto report : reportRows) {
            duration += report.getDuration();
        }

        return new TagDurationReportRowDto(TOTAL_REPORT_ROW_NAME, duration, Collections.emptyList(), null);
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

        private final TagDurationReportRowDto parent;
        private final List<TagDurationReportRowDto> children;

    }

}
