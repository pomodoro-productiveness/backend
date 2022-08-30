package com.igorgorbunov3333.timer.service.console.printer;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.service.console.printer.util.ListOfItemsPrinter;
import com.igorgorbunov3333.timer.service.util.PomodoroChronoUtil;
import com.igorgorbunov3333.timer.service.util.SecondsFormatter;
import com.igorgorbunov3333.timer.service.util.TagUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PomodoroPrinter {

    public void print(Map<Integer, PomodoroDto> numberToPomodoro) {
        Map<PomodoroDto, List<PomodoroTagDto>> rearrangedPomodoroToTags =
                rearrange(new ArrayList<>(numberToPomodoro.values()));

        List<PomodoroDto> pomodoroWithRearrangedTags = new ArrayList<>();
        for (Map.Entry<PomodoroDto, List<PomodoroTagDto>> entry : rearrangedPomodoroToTags.entrySet()) {
            PomodoroDto currentPomodoro = entry.getKey();
            PomodoroDto updatedPomodoro = new PomodoroDto(
                    currentPomodoro.getId(),
                    currentPomodoro.getStartTime(),
                    currentPomodoro.getEndTime(),
                    currentPomodoro.isSavedAutomatically(),
                    currentPomodoro.getPomodoroPauses(),
                    entry.getValue()
            );
            pomodoroWithRearrangedTags.add(updatedPomodoro);
        }

        Map<Integer, PomodoroDto> numberedPomodoroWithRearrangedTags = new LinkedHashMap<>();
        for (Map.Entry<Integer, PomodoroDto> entry : numberToPomodoro.entrySet()) {
            int pomodoroIndex = pomodoroWithRearrangedTags.indexOf(entry.getValue());
            PomodoroDto singlePomodoroWithRearrangedTags = pomodoroWithRearrangedTags.get(pomodoroIndex);
            numberedPomodoroWithRearrangedTags.put(entry.getKey(), singlePomodoroWithRearrangedTags);
        }

        Function<PomodoroDto, String> pomodoroExtractorFunction = this::mapToString;

        ListOfItemsPrinter.print(numberedPomodoroWithRearrangedTags, pomodoroExtractorFunction);
    }

    private Map<PomodoroDto, List<PomodoroTagDto>> rearrange(List<PomodoroDto> pomodoro) {
        Map<PomodoroDto, List<PomodoroTagDto>> rearrangedPomodoroToTagsMap = getPomodoroWithTagsWithMainTagAtStart(pomodoro);

        for (PomodoroDto currentPomodoro : pomodoro) {
            List<PomodoroTagDto> currentPomodoroTags = rearrangedPomodoroToTagsMap.get(currentPomodoro);
            Map<Integer, PomodoroTagDto> currentPomodoroNumberedTags = mapTagsToNumbers(currentPomodoroTags);

            for (int i = pomodoro.indexOf(currentPomodoro) + 1; i < pomodoro.size(); i++) {
                PomodoroDto pomodoroToRearrangeTags = pomodoro.get(i);
                List<PomodoroTagDto> pomodoroTagsToRearrange = rearrangedPomodoroToTagsMap.get(pomodoroToRearrangeTags);

                if (pomodoroTagsToRearrange.size() > rearrangedPomodoroToTagsMap.get(currentPomodoro).size()) {
                    List<PomodoroTagDto> rearrangedTags = rearrangeTags(pomodoroTagsToRearrange, currentPomodoroNumberedTags);

                    rearrangedPomodoroToTagsMap.put(pomodoroToRearrangeTags, rearrangedTags);
                } else {
                    rearrangedPomodoroToTagsMap.put(pomodoroToRearrangeTags, pomodoroTagsToRearrange);

                    List<PomodoroTagDto> rearrangedTags = rearrangeTags(currentPomodoroTags, mapTagsToNumbers(rearrangedPomodoroToTagsMap.get(pomodoroToRearrangeTags)));
                    rearrangedPomodoroToTagsMap.put(currentPomodoro, rearrangedTags);
                }
            }
        }

        return rearrangedPomodoroToTagsMap;
    }

    private Map<PomodoroDto, List<PomodoroTagDto>> getPomodoroWithTagsWithMainTagAtStart(List<PomodoroDto> pomodoro) {
        Map<PomodoroDto, List<PomodoroTagDto>> rearrangedPomodoroToTagsMap = new LinkedHashMap<>();
        for (PomodoroDto singlePomodoro : pomodoro) {
            List<PomodoroTagDto> pomodoroTags = singlePomodoro.getTags();

            List<PomodoroTagDto> rearrangedTags = new ArrayList<>();
            if (!Set.of(TagUtil.TAG_EDUCATION, TagUtil.TAG_WORK).contains(pomodoroTags.get(0).getName())
                    && containsTagNames(pomodoroTags, TagUtil.TAG_EDUCATION, TagUtil.TAG_WORK)) {
                int mainTagIndex = getMainTagIndex(pomodoroTags);
                if (mainTagIndex != -1) {
                    rearrangedTags.add(pomodoroTags.get(mainTagIndex));
                    fillWithRemainingTags(rearrangedTags, pomodoroTags, mainTagIndex);
                }
            } else {
                fillWithRemainingTags(rearrangedTags, pomodoroTags, -1);
            }

            rearrangedPomodoroToTagsMap.put(singlePomodoro, rearrangedTags);
        }

        return rearrangedPomodoroToTagsMap;
    }

    private int getMainTagIndex(List<PomodoroTagDto> tags) {
        for (PomodoroTagDto tag : tags) {
            if (tag.getName().equals(TagUtil.TAG_EDUCATION) || tag.getName().equals(TagUtil.TAG_WORK)) {
                return tags.indexOf(tag);
            }
        }

        return -1;
    }

    private void fillWithRemainingTags(List<PomodoroTagDto> rearrangedTags,
                                       List<PomodoroTagDto> pomodoroTags,
                                       int mainTagIndex) {
        for (PomodoroTagDto tag : pomodoroTags) {
            if (pomodoroTags.indexOf(tag) != mainTagIndex) {
                rearrangedTags.add(tag);
            }
        }
    }

    private Map<Integer, PomodoroTagDto> mapTagsToNumbers(List<PomodoroTagDto> tags) {
        Map<Integer, PomodoroTagDto> tagsToNumbers = new LinkedHashMap<>();

        int count = 0;
        for (PomodoroTagDto tag : tags) {
            tagsToNumbers.put(++count, tag);
        }

        return tagsToNumbers;
    }

    private boolean containsTagNames(List<PomodoroTagDto> tags, String... tagNames) {
        boolean contains = false;
        for (String name : tagNames) {
            if (tags.stream()
                    .anyMatch(tag -> tag.getName().equals(name))) {
                contains = true;
            }
        }
        return contains;
    }

    private List<PomodoroTagDto> rearrangeTags(List<PomodoroTagDto> tagsFromCurrentPomodoro,
                                               Map<Integer, PomodoroTagDto> numberedTagsFromPomodoroAbove) {
        List<PomodoroTagDto> rearrangedTags = new ArrayList<>();

        int tagsFromCurrentPomodoroIndex = 0;
        for (Map.Entry<Integer, PomodoroTagDto> entry : numberedTagsFromPomodoroAbove.entrySet()) {
            String tagName = entry.getValue().getName();
            int tagNumber = getTagNumberByName(tagsFromCurrentPomodoro, tagName);
            if (containsTagNames(tagsFromCurrentPomodoro, tagName) && entry.getKey() < tagNumber) {
                rearrangedTags.add(entry.getValue());
            } else {
                if (tagsFromCurrentPomodoroIndex < tagsFromCurrentPomodoro.size()) {
                    PomodoroTagDto currentTag = tagsFromCurrentPomodoro.get(tagsFromCurrentPomodoroIndex++);
                    rearrangedTags.add(currentTag);
                } else {
                    break;
                }
            }
        }

        if (tagsFromCurrentPomodoro.size() > numberedTagsFromPomodoroAbove.size()) {
            for (PomodoroTagDto tag : tagsFromCurrentPomodoro) {
                if (!rearrangedTags.contains(tag)) {
                    rearrangedTags.add(tag);
                }
            }
        }

        return rearrangedTags;
    }

    private int getTagNumberByName(List<PomodoroTagDto> tags, String tagName) {
        int number = -1;

        int counter = 0;
        for (PomodoroTagDto tag : tags) {
            ++counter;
            if (tag.getName().equals(tagName)) {
                number = counter;
                break;
            }
        }

        return number;
    }

    private String mapToString(PomodoroDto pomodoro) {
        String pomodoroPeriod = buildPomodoroStartEndTime(pomodoro);
        long pomodoroStartEndTimeDifference = PomodoroChronoUtil.getStartEndTimeDifferenceInSeconds(pomodoro);
        String pomodoroDuration = SecondsFormatter.formatInMinutes(pomodoroStartEndTimeDifference);
        String formattedPomodoroPeriodAndDuration = "time - "
                .concat(pomodoroPeriod)
                .concat(" | ")
                .concat("duration - ")
                .concat(pomodoroDuration);

        String tagsLine = buildTagsLine(pomodoro);

        return formattedPomodoroPeriodAndDuration
                .concat(" | ")
                .concat("tags: ")
                .concat(tagsLine);
    }

    private String buildPomodoroStartEndTime(PomodoroDto pomodoro) {
        String startTimeString = buildDateTime(pomodoro.getStartTime().toLocalDateTime());
        String endTimeString = buildDateTime(pomodoro.getEndTime().toLocalDateTime());

        return String.join(" : ", List.of(startTimeString, endTimeString));
    }

    private String buildTagsLine(PomodoroDto pomodoro) {
        String tagsLine = StringUtils.EMPTY;
        if (!CollectionUtils.isEmpty(pomodoro.getTags())) {
            tagsLine = "#" + pomodoro.getTags().stream()
                    .map(PomodoroTagDto::getName)
                    .collect(Collectors.joining(" #"));
        }
        return tagsLine;
    }

    private String buildDateTime(LocalDateTime startTime) {
        int hours = startTime.getHour();
        int minutes = startTime.getMinute();
        int seconds = startTime.getSecond();

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
