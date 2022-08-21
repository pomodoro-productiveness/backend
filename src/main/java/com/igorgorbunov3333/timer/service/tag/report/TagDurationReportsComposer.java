package com.igorgorbunov3333.timer.service.tag.report;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportDto;
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
public class TagDurationReportsComposer {

    private static final String TOTAL_REPORT_ROW_NAME = "Total";

    private final AllTagsDurationReporter allTagsDurationReporter;

    public List<TagDurationReportDto> compose(List<PomodoroDto> pomodoro) {
        List<TagDurationReportDto> reports = allTagsDurationReporter.reportForEachTag(pomodoro);

        Map<String, TagDurationReportDto> tagsToReports = reports.stream()
                .collect(Collectors.toMap(report ->
                        report.getMainTagReportRow().getTag(), Function.identity()));

        List<TagDurationReportDto> rootReports = getRootReports(reports, tagsToReports);

        List<TagDurationReportDto> mergedReports = mergeReports(tagsToReports, rootReports);

        List<TagDurationReportRowDto> allRows = collectAllReportRows(mergedReports);

        Map<TagDurationReportRowDto, List<ParentWithChildrenContainer>> rowsToParentRows = new HashMap<>();
        for (TagDurationReportRowDto reportRow : allRows) {
            List<ParentWithChildrenContainer> parentsWithChildren = rowsToParentRows.get(reportRow);

            if (CollectionUtils.isEmpty(parentsWithChildren)) {
                parentsWithChildren = new ArrayList<>();
            }

            parentsWithChildren.add(new ParentWithChildrenContainer(reportRow.getParentRow(), reportRow.getSubRows()));

            rowsToParentRows.put(reportRow, parentsWithChildren);
        }

        for (TagDurationReportDto rootReport : mergedReports) {
            rowsToParentRows.put(rootReport.getMainTagReportRow(), List.of(new ParentWithChildrenContainer(null, rootReport.getMappedTagsReportRows())));
        }

        List<TagDurationReportDto> composedMergedReports = new ArrayList<>();
        for (TagDurationReportDto mergedReport : mergedReports) {
            List<TagDurationReportRowDto> fixedRows = clearDuplicatesAndFixRowsDuration(mergedReport.getMappedTagsReportRows(), rowsToParentRows);
            composedMergedReports.add(new TagDurationReportDto(mergedReport.getMainTagReportRow(), fixedRows));
        }

        composedMergedReports.sort(Comparator.comparing(r -> r.getMainTagReportRow().getTag()));

        TagDurationReportDto totalReport = buildTotalReport(composedMergedReports);
        composedMergedReports.add(totalReport);

        return composedMergedReports;
    }

    private List<TagDurationReportDto> getRootReports(List<TagDurationReportDto> reports,
                                                      Map<String, TagDurationReportDto> tagsToReports) {
        List<TagDurationReportDto> rootReports = new LinkedList<>();
        Map<Long, TagDurationReportDto> durationToReports = new HashMap<>();
        for (TagDurationReportDto report : reports) {
            if (isRootReport(report, tagsToReports)) {
                long reportDuration = report.getMainTagReportRow().getDuration();

                if (durationToReports.containsKey(reportDuration)) {
                    TagDurationReportDto reportWithSameDuration = durationToReports.get(reportDuration);

                    rootReports.remove(reportWithSameDuration);

                    TagDurationReportRowDto reportWithSameDurationMainRow = reportWithSameDuration.getMainTagReportRow();
                    reportWithSameDurationMainRow.setTag(reportWithSameDurationMainRow.getTag() + " #" + report.getMainTagReportRow().getTag());
                    reportWithSameDuration = new TagDurationReportDto(reportWithSameDurationMainRow, reportWithSameDuration.getMappedTagsReportRows());

                    rootReports.add(reportWithSameDuration);
                } else {
                    rootReports.add(report);
                    durationToReports.put(reportDuration, report);
                }
            }
        }

        return rootReports;
    }

    private boolean isRootReport(TagDurationReportDto report, Map<String, TagDurationReportDto> tagsToReports) {
        return report.getMappedTagsReportRows().stream()
                .allMatch(row -> isRootRow(report, tagsToReports, row));
    }

    private boolean isRootRow(TagDurationReportDto report,
                              Map<String, TagDurationReportDto> tagsToReports,
                              TagDurationReportRowDto reportRow) {
        return mapToTagNames(reportRow).stream()
                .map(tagName -> tagsToReports.get(tagName).getMainTagReportRow())
                .allMatch(row -> row.getDuration() <= report.getMainTagReportRow().getDuration());
    }

    private List<TagDurationReportDto> mergeReports(Map<String, TagDurationReportDto> tagsToReports,
                                                    List<TagDurationReportDto> reports) {
        List<TagDurationReportDto> mergedReports = new ArrayList<>();
        for (TagDurationReportDto rootReport : reports) {
            TagDurationReportRowDto mainRow = rootReport.getMainTagReportRow();
            List<TagDurationReportRowDto> mappedRows =
                    mapSubRows(rootReport.getMainTagReportRow(), rootReport.getMappedTagsReportRows(), tagsToReports, new HashMap<>());
            TagDurationReportDto mergedReport = new TagDurationReportDto(mainRow, mappedRows);
            mergedReports.add(mergedReport);
        }

        return mergedReports;
    }

    public List<TagDurationReportRowDto> mapSubRows(TagDurationReportRowDto mainRow,
                                                    List<TagDurationReportRowDto> mappedRows,
                                                    Map<String, TagDurationReportDto> tagsToReports,
                                                    Map<String, ProcessedTagsContainer> processedTags) {
        List<TagDurationReportRowDto> subRows = new ArrayList<>();

        if (CollectionUtils.isEmpty(mappedRows)) {
            return List.of();
        }

        for (TagDurationReportRowDto row : mappedRows) {
            Set<String> tagNames = mapToTagNames(row);

            Queue<TagDurationReportRowDto> rows = getRowsByNames(tagsToReports, tagNames);

            List<TagDurationReportRowDto> currentRowSubRows = new LinkedList<>();
            for (TagDurationReportRowDto currentRow : rows) {
                if (isAlreadyProcessed(currentRow, processedTags, tagsToReports)) {
                    continue;
                }

                String currentRowTagName = currentRow.getTag();

                if (currentRow.getDuration() < mainRow.getDuration()) {
                    if (!currentRowSubRows.isEmpty()) {
                        TagDurationReportRowDto previousRow = currentRowSubRows.get(currentRowSubRows.size() - 1);

                        if (previousRow.getDuration() == currentRow.getDuration()) {
                            previousRow.setTag(previousRow.getTag() + " #" + currentRowTagName);
                            continue;
                        }
                    }

                    long duration = calculateDuration(currentRow, mainRow, tagsToReports);

                    currentRow = new TagDurationReportRowDto(currentRowTagName, duration, currentRow.getSubRows(), mainRow);
                    currentRowSubRows.add(currentRow);

                    List<TagDurationReportRowDto> currentTagNameMappedReportRows = tagsToReports.get(currentRowTagName)
                            .getMappedTagsReportRows();
                    List<TagDurationReportRowDto> currentRowSubRowsSubRows =
                            mapSubRows(currentRow, currentTagNameMappedReportRows, tagsToReports, processedTags);
                    currentRow.setSubRows(currentRowSubRowsSubRows);
                    processedTags.put(currentRowTagName, new ProcessedTagsContainer(currentRowTagName, duration));
                }
            }
            subRows.addAll(currentRowSubRows);
        }

        return subRows;
    }

    private LinkedList<TagDurationReportRowDto> getRowsByNames(Map<String, TagDurationReportDto> tagsToRows, Set<String> tagNames) {
        return tagNames.stream()
                .map(tagName -> tagsToRows.get(tagName).getMainTagReportRow())
                .sorted(Comparator.comparing(TagDurationReportRowDto::getDuration).reversed())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private long calculateDuration(TagDurationReportRowDto currentRow,
                                   TagDurationReportRowDto mainRow,
                                   Map<String, TagDurationReportDto> tagsToRows) {
        List<TagDurationReportRowDto> mappedRows = tagsToRows.get(currentRow.getTag()).getMappedTagsReportRows();

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
                                       Map<String, TagDurationReportDto> tagsToReports) {
        ProcessedTagsContainer processedTagsContainer = processedTags.get(targetRow.getTag());

        long targetRowDuration = getTargetRowDuration(targetRow.getTag(), tagsToReports);

        return processedTagsContainer != null
                && processedTagsContainer.getDuration() == targetRowDuration;
    }

    private long getTargetRowDuration(String targetTagName, Map<String, TagDurationReportDto> tagsToReports) {
        String[] tags = targetTagName.split(" #");

        return tagsToReports.get(tags[0])
                .getMainTagReportRow()
                .getDuration();
    }

    private Set<String> mapToTagNames(TagDurationReportRowDto mappedReportRow) {
        return Arrays.stream(mappedReportRow.getTag().split(" "))
                .map(tag -> tag.replace("#", ""))
                .collect(Collectors.toSet());
    }

    private List<TagDurationReportRowDto> collectAllReportRows(List<TagDurationReportDto> reports) {
        List<TagDurationReportRowDto> firstLevelRows = reports.stream()
                .flatMap(report -> report.getMappedTagsReportRows().stream())
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

        if (!CollectionUtils.isEmpty(parentRow.getSubRows())) {
            for (TagDurationReportRowDto child : parentRow.getSubRows()) {
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

            if (!CollectionUtils.isEmpty(row.getSubRows())) {
                List<TagDurationReportRowDto> fixedChildRows = clearDuplicatesAndFixRowsDuration(row.getSubRows(), rowToParentRows);
                fixedRow = new TagDurationReportRowDto(rowTagName, row.getDuration(), fixedChildRows, row.getParentRow());
            }

            List<ParentWithChildrenContainer> parentWithChildrenContainers = rowToParentRows.get(row);
            if (!CollectionUtils.isEmpty(parentWithChildrenContainers) && parentWithChildrenContainers.size() > 1) {
                if (CollectionUtils.isEmpty(row.getSubRows())
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
        return containersWithSameParentAmount > 1L && CollectionUtils.isEmpty(row.getSubRows());
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
                findRowsWithSameTagWithNestingLevels(tagName, row.getSubRows(), nestingLevel + 1, pairs);
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

    private TagDurationReportDto buildTotalReport(List<TagDurationReportDto> reports) {
        long duration = 0;
        for (TagDurationReportDto report : reports) {
            duration += report.getMainTagReportRow().getDuration();
        }

        TagDurationReportRowDto totalReportRow =
                new TagDurationReportRowDto(TOTAL_REPORT_ROW_NAME, duration, Collections.emptyList(), null);

        return new TagDurationReportDto(totalReportRow, List.of());
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
