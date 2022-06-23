package com.igorgorbunov3333.timer.model.entity.pomodoro;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PomodoroTagTest {

    @Test
    void isRelative_WhenDeepNestingAndTagPresentAmongChildrenTags_ThenReturnTrue() {
        String thirdLevelChildTagName = "thirdLevelChild";

        PomodoroTag thirdLevelChild = new PomodoroTag(8L, thirdLevelChildTagName, null, List.of(), false);

        PomodoroTag secondLevelChild1 = new PomodoroTag(4L, "secondLevelChild1", null, List.of(), false);
        PomodoroTag secondLevelChild2 = new PomodoroTag(5L, "secondLevelChild2", null, List.of(), false);
        PomodoroTag secondLevelChild3 = new PomodoroTag(6L, "secondLevelChild3", null, List.of(), false);
        PomodoroTag secondLevelChild4 = new PomodoroTag(7L, "secondLevelChild4", null, List.of(thirdLevelChild), false);

        PomodoroTag firstLevelChild1 = new PomodoroTag(2L, "firstLevelChild1", null, List.of(secondLevelChild1, secondLevelChild2), false);
        PomodoroTag firstLevelChild2 = new PomodoroTag(3L, "firstLevelChild2", null, List.of(secondLevelChild3, secondLevelChild4), false);

        PomodoroTag testee = new PomodoroTag(1L, "education", null, List.of(firstLevelChild1, firstLevelChild2), false);

        boolean actual = testee.isRelative(thirdLevelChildTagName);

        assertThat(actual).isTrue();
    }

    @Test
    void isRelative_WhenDeepNestingAndNoTagAmongChildTags_ThenReturnFalse() {
        PomodoroTag thirdLevelChild = new PomodoroTag(8L, "thirdLevelChild", null, List.of(), false);

        PomodoroTag secondLevelChild1 = new PomodoroTag(4L, "secondLevelChild1", null, List.of(), false);
        PomodoroTag secondLevelChild2 = new PomodoroTag(5L, "secondLevelChild2", null, List.of(), false);
        PomodoroTag secondLevelChild3 = new PomodoroTag(6L, "secondLevelChild3", null, List.of(), false);
        PomodoroTag secondLevelChild4 = new PomodoroTag(7L, "secondLevelChild4", null, List.of(thirdLevelChild), false);

        PomodoroTag firstLevelChild1 = new PomodoroTag(2L, "firstLevelChild1", null, List.of(secondLevelChild1, secondLevelChild2), false);
        PomodoroTag firstLevelChild2 = new PomodoroTag(3L, "firstLevelChild2", null, List.of(secondLevelChild3, secondLevelChild4), false);

        PomodoroTag testee = new PomodoroTag(1L, "education", null, List.of(firstLevelChild1, firstLevelChild2), false);

        boolean actual = testee.isRelative("someTag");

        assertThat(actual).isFalse();
    }

    @Test
    void isRelative_WhenTagEqualsToRootTagName_ThenReturnTrue() {
        String parentTag = "education";

        PomodoroTag thirdLevelChild = new PomodoroTag(8L, "thirdLevelChild", null, List.of(), false);

        PomodoroTag secondLevelChild1 = new PomodoroTag(4L, "secondLevelChild1", null, List.of(), false);
        PomodoroTag secondLevelChild2 = new PomodoroTag(5L, "secondLevelChild2", null, List.of(), false);
        PomodoroTag secondLevelChild3 = new PomodoroTag(6L, "secondLevelChild3", null, List.of(), false);
        PomodoroTag secondLevelChild4 = new PomodoroTag(7L, "secondLevelChild4", null, List.of(thirdLevelChild), false);

        PomodoroTag firstLevelChild1 = new PomodoroTag(2L, "firstLevelChild1", null, List.of(secondLevelChild1, secondLevelChild2), false);
        PomodoroTag firstLevelChild2 = new PomodoroTag(3L, "firstLevelChild2", null, List.of(secondLevelChild3, secondLevelChild4), false);

        PomodoroTag testee = new PomodoroTag(1L, parentTag, null, List.of(firstLevelChild1, firstLevelChild2), false);

        boolean actual = testee.isRelative(parentTag);

        assertThat(actual).isTrue();
    }

}
