package com.igorgorbunov3333.timer.service.tag.report;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportRowDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        TagDurationReportRowDto totalReportRow = buildTotalReportRow(mergedReportRows);
        mergedReportRows.add(totalReportRow);

        return mergedReportRows;
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
            List<TagDurationReportRowDto> mappedRows = mapSubRows(rootReportRow, tagsToReportRows, new HashMap<>());
            rootReportRow.setMappedRows(mappedRows);
            mergedReportRows.add(rootReportRow);
        }

        return mergedReportRows;
    }

    public List<TagDurationReportRowDto> mapSubRows(TagDurationReportRowDto mainReportRow,
                                                    Map<String, TagDurationReportRowDto> tagsToReportRows,
                                                    Map<String, List<ProcessedTagsContainer>> processedTags) {
        List<TagDurationReportRowDto> subRows = new ArrayList<>();

        TagDurationReportRowDto tagDurationReportRow = tagsToReportRows.get(mainReportRow.getTag());

        if (tagDurationReportRow == null || CollectionUtils.isEmpty(tagDurationReportRow.getMappedRows())) {
            return List.of();
        }

        List<TagDurationReportRowDto> mappedRows = tagDurationReportRow.getMappedRows();
        mappedRows.sort(Comparator.comparing(TagDurationReportRowDto::getDuration).reversed());

        for (TagDurationReportRowDto row : tagDurationReportRow.getMappedRows()) {
            Set<String> tagNames = mapToTagNames(row);

            Queue<TagDurationReportRowDto> rows = getRowsByNames(tagsToReportRows, tagNames);

            List<TagDurationReportRowDto> currentRowSubRows = new LinkedList<>();
            for (TagDurationReportRowDto currentRow : rows) {
                long currentRowDuration = calculateDuration(currentRow, mainReportRow, tagsToReportRows, processedTags);

                if (isAlreadyProcessed(currentRow, processedTags, currentRowDuration)) {
                    continue;
                }

                String currentRowTagName = currentRow.getTag();

                if (currentRowDuration < mainReportRow.getDuration()) {
                    if (!currentRowSubRows.isEmpty()) {
                        TagDurationReportRowDto previousRow = currentRowSubRows.get(currentRowSubRows.size() - 1);

                        if (previousRow.getDuration() == currentRowDuration) {
                            String updatedTag = previousRow.getTag() + " #" + currentRowTagName;
                            previousRow.setTag(updatedTag);

                            List<ProcessedTagsContainer> processedTagsWithTagName = processedTags.get(previousRow.getTag());

                            if (!CollectionUtils.isEmpty(processedTagsWithTagName)) {
                                Optional<ProcessedTagsContainer> processedTagWithDurationOpt = processedTagsWithTagName.stream()
                                        .filter(tag -> tag.getDuration() == previousRow.getDuration())
                                        .findFirst();

                                if (processedTagWithDurationOpt.isPresent()) {
                                    List<ProcessedTagsContainer> processedContainers = processedTags.get(currentRowTagName);

                                    if (!CollectionUtils.isEmpty(processedContainers)) {
                                        processedContainers.add(new ProcessedTagsContainer(currentRowTagName, previousRow.getDuration()));
                                    } else {
                                        processedContainers = new ArrayList<>(List.of(new ProcessedTagsContainer(currentRowTagName, previousRow.getDuration())));
                                    }

                                    processedTags.put(currentRowTagName, processedContainers);
                                }
                            } else {
                                processedTags.put(currentRowTagName, new ArrayList<>(List.of(new ProcessedTagsContainer(currentRowTagName, previousRow.getDuration()))));
                            }

                            continue;
                        }
                    }

                    currentRow = new TagDurationReportRowDto(currentRowTagName, currentRowDuration, currentRow.getMappedRows());
                    currentRowSubRows.add(currentRow);

                    List<TagDurationReportRowDto> currentRowSubRowsSubRows = mapSubRows(currentRow, tagsToReportRows, processedTags);
                    currentRow.setMappedRows(currentRowSubRowsSubRows);

                    List<ProcessedTagsContainer> processedTagContainers = processedTags.get(currentRowTagName);

                    ProcessedTagsContainer newContainer = new ProcessedTagsContainer(currentRowTagName, currentRowDuration);
                    if (!CollectionUtils.isEmpty(processedTagContainers)) {
                        processedTagContainers.add(newContainer);
                    } else {
                        processedTags.put(currentRowTagName, new ArrayList<>(List.of(newContainer)));
                    }
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
                                   Map<String, TagDurationReportRowDto> tagsToRows,
                                   Map<String, List<ProcessedTagsContainer>> processedTags) {
        List<TagDurationReportRowDto> mappedRows = tagsToRows.get(currentRow.getTag()).getMappedRows();

        if (isCurrentRowParentToMainRow(currentRow, mainRow, tagsToRows)) {
            return tagsToRows.get(currentRow.getTag()).getDuration();
        }

        long duration = 0L;

        for (TagDurationReportRowDto mappedRow : mappedRows) {
            Set<String> tagNames = mapToTagNames(mappedRow);
            List<TagDurationReportRowDto> rows = getRowsByNames(tagsToRows, tagNames);

            boolean containMainRow = false;
            for (TagDurationReportRowDto mapped : rows) {
                if (mapped.getTag().equals(mainRow.getTag())) {
                    containMainRow = true;
                    break;
                }
            }

            if (containMainRow) {
                duration += mappedRow.getDuration();
            }
        }

        List<ProcessedTagsContainer> processedTagsWithSameName = processedTags.get(currentRow.getTag());

        long finalDuration = duration;
        if (!CollectionUtils.isEmpty(processedTagsWithSameName)) {
            long processedTagsDuration = processedTagsWithSameName.stream()
                    .map(ProcessedTagsContainer::getDuration)
                    .reduce(0L, Long::sum);

            finalDuration -= processedTagsDuration;
        }

        return finalDuration;
    }

    private boolean isCurrentRowParentToMainRow(TagDurationReportRowDto currentRow,
                                                TagDurationReportRowDto mainRow,
                                                Map<String, TagDurationReportRowDto> tagsToRows) {
        return tagsToRows.get(currentRow.getTag()).getDuration() > tagsToRows.get(mainRow.getTag()).getDuration();
    }

    private boolean isAlreadyProcessed(TagDurationReportRowDto targetRow,
                                       Map<String, List<ProcessedTagsContainer>> processedTags,
                                       long targetRowDuration) {
        if (targetRowDuration == 0L) {
            return true;
        }

        List<ProcessedTagsContainer> processedTagContainers = processedTags.get(targetRow.getTag());

        return !CollectionUtils.isEmpty(processedTagContainers)
                && anyOfAlreadyProcessedTagContainersHasSameDuration(processedTagContainers, targetRowDuration);
    }

    private boolean anyOfAlreadyProcessedTagContainersHasSameDuration(List<ProcessedTagsContainer> processedTagsContainers,
                                                                      long targetRowDuration) {
        for (ProcessedTagsContainer processedContainer : processedTagsContainers) {
            if (processedContainer.getDuration() == targetRowDuration) {
                return true;
            }
        }

        return false;
    }

    private Set<String> mapToTagNames(TagDurationReportRowDto mappedReportRow) {
        return Arrays.stream(mappedReportRow.getTag().split(" "))
                .map(tag -> tag.replace("#", ""))
                .collect(Collectors.toSet());
    }

    private TagDurationReportRowDto buildTotalReportRow(List<TagDurationReportRowDto> reportRows) {
        long duration = 0;
        for (TagDurationReportRowDto report : reportRows) {
            duration += report.getDuration();
        }

        return new TagDurationReportRowDto(TOTAL_REPORT_ROW_NAME, duration, new ArrayList<>());
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode(of = "processedTag")
    private static class ProcessedTagsContainer {

        private final String processedTag;
        private final long duration;

    }

}
