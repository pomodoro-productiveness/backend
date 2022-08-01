package com.igorgorbunov3333.timer.service.tag;

import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagProviderTest {

    @InjectMocks
    private TagProvider testee;

    @Mock
    private TagService tagService;

    @Test
    void provideTags_WhenTagsExist_ThenNumberedSortedListProvided() {
        PomodoroTagDto tag1 = mock(PomodoroTagDto.class);
        when(tag1.getName()).thenReturn("fourthLevelTag");

        PomodoroTagDto tag2 = mock(PomodoroTagDto.class);
        when(tag2.getName()).thenReturn("thirdLevelTag1");

        PomodoroTagDto tag3 = mock(PomodoroTagDto.class);
        when(tag3.getName()).thenReturn("thirdLevelTag2");

        PomodoroTagDto tag4 = mock(PomodoroTagDto.class);
        when(tag4.getName()).thenReturn("secondLevelTag1");

        PomodoroTagDto tag5 = mock(PomodoroTagDto.class);
        when(tag5.getName()).thenReturn("secondLevelTag2");

        PomodoroTagDto tag6 = mock(PomodoroTagDto.class);
        when(tag6.getName()).thenReturn("secondLevelTag3");

        PomodoroTagDto tag7 = mock(PomodoroTagDto.class);
        when(tag7.getName()).thenReturn("firstLevelTag1");

        PomodoroTagDto tag8 = mock(PomodoroTagDto.class);
        when(tag8.getName()).thenReturn("firstLevelTag2");

        List<PomodoroTagDto> tags = new ArrayList<>(List.of(tag1, tag2, tag3,  tag4, tag5, tag6, tag7, tag8));
        tags.sort(Comparator.comparing(PomodoroTagDto::getName));
        when(tagService.getSortedTags(false)).thenReturn(tags);

        Map<Integer, PomodoroTagDto> actual = testee.provide();

        assertThat(actual).extractingFromEntries(entry -> Tuple.tuple(entry.getKey(), entry.getValue().getName()))
                .containsExactlyElementsOf(List.of(
                        tuple(1, "firstLevelTag1"),
                        tuple(2, "firstLevelTag2"),
                        tuple(3, "fourthLevelTag"),
                        tuple(4, "secondLevelTag1"),
                        tuple(5, "secondLevelTag2"),
                        tuple(6, "secondLevelTag3"),
                        tuple(7, "thirdLevelTag1"),
                        tuple(8, "thirdLevelTag2")
                ));
    }

}