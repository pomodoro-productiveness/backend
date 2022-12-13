package com.igorgorbunov3333.timer.service.tag.report;

import com.igorgorbunov3333.timer.model.dto.pomodoro.PomodoroDto;
import com.igorgorbunov3333.timer.model.dto.tag.PomodoroTagDto;
import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportRowDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagDurationReportsComposerTest {

    private final static LocalDateTime INITIAL_DATE_TIME = LocalDate.of(2020, 1, 1).atStartOfDay();

    @InjectMocks
    private TagDurationReportsComposer testee;

    @Test
    void compose_WhenReportsPresent_ThenCompose() {
        List<PomodoroDto> pomodoro = new ArrayList<>();
        pomodoro.addAll(buildPomodoro(5, List.of("work", "meeting")));
        pomodoro.addAll(buildPomodoro(20, List.of("work", "outsourceCompany", "clientCompany", "meeting")));
        pomodoro.addAll(buildPomodoro(7, List.of("work", "outsourceCompany", "clientCompany", "trainings")));
        pomodoro.addAll(buildPomodoro(30, List.of("work", "outsourceCompany", "clientCompany")));

        TagDurationReportRowDto work_meetingMappedExpected = new TagDurationReportRowDto(
                "meeting",
                6_000L,
                new ArrayList<>()
        );
        TagDurationReportRowDto outsourceCompanyClientCompany_meetingMappedExpected = new TagDurationReportRowDto(
                "meeting",
                24_000L,
                new ArrayList<>()
        );
        TagDurationReportRowDto outsourceCompanyClientCompany_trainingsMappedExpected = new TagDurationReportRowDto(
                "trainings",
                8_400L,
                new ArrayList<>()
        );
        TagDurationReportRowDto work_outsourceCompanyClientCompanyMappedExpected = new TagDurationReportRowDto(
                "outsourceCompany #clientCompany",
                68_400L,
                new ArrayList<>(List.of(
                        outsourceCompanyClientCompany_meetingMappedExpected,
                        outsourceCompanyClientCompany_trainingsMappedExpected
                ))
        );
        TagDurationReportRowDto workExpected = new TagDurationReportRowDto(
                "work",
                74_400L,
                new ArrayList<>(List.of(
                        work_outsourceCompanyClientCompanyMappedExpected,
                        work_meetingMappedExpected
                ))
        );
        TagDurationReportRowDto totalExpected = new TagDurationReportRowDto(
                "Total",
                74_400L,
                new ArrayList<>()
        );

        List<TagDurationReportRowDto> actual = testee.compose(pomodoro);

        assertThat(actual).containsExactlyInAnyOrderElementsOf(List.of(workExpected, totalExpected));
    }

    private List<PomodoroDto> buildPomodoro(int amount, List<String> tags) {
        List<PomodoroDto> mockedPomodoro = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            mockedPomodoro.add(buildSinglePomodoro(i, tags));
        }

        return mockedPomodoro;
    }

    private PomodoroDto buildSinglePomodoro(int number, List<String> tags) {
        LocalDateTime start = INITIAL_DATE_TIME.plusHours(number);
        LocalDateTime end = INITIAL_DATE_TIME.plusHours(number).plusMinutes(20L);

        List<PomodoroTagDto> pomodoroTags = new ArrayList<>();
        for (String tag : tags) {
            PomodoroTagDto pomodoroTagDto = new PomodoroTagDto(tag, false);
            pomodoroTags.add(pomodoroTagDto);
        }

        return new PomodoroDto(
                null,
                ZonedDateTime.of(start, ZoneId.systemDefault()),
                ZonedDateTime.of(end, ZoneId.systemDefault()),
                false,
                Collections.emptyList(),
                pomodoroTags
        );
    }

//    @Test
//    void compose_WhenReportsPresentAndTwoOrMoreParentsHasChildrenWithDeepNesting_ThenCompose() {
//        TagDurationReportRowDto pomodoro_backendMappedTagDurationReportRow = new TagDurationReportRowDto(
//                "#backend",
//                5_400L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto pomodoro_javaBackendMappedTagDurationReportRows = new TagDurationReportRowDto(
//                "#Java #backend",
//                25_200L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto pomodoro_pythonBackendMappedTagDurationReportRows = new TagDurationReportRowDto(
//                "#python #backend",
//                3_600L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto pomodoro_designBackendMappedTagDurationReportRows = new TagDurationReportRowDto(
//                "#design #backend",
//                1_800L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto pomodoroTagDurationReportRow = new TagDurationReportRowDto(
//                "pomodoro",
//                36_000L,
//                new ArrayList<>(List.of(
//                        pomodoro_backendMappedTagDurationReportRow,
//                        pomodoro_javaBackendMappedTagDurationReportRows,
//                        pomodoro_pythonBackendMappedTagDurationReportRows,
//                        pomodoro_designBackendMappedTagDurationReportRows
//                ))
//        );
//        //----------------------------------------
//        TagDurationReportRowDto backend_pomodoroMappedTagDurationReportRow = new TagDurationReportRowDto(
//                "#pomodoro",
//                5_400L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto backend_javaPomodoroMappedTagDurationReportRows = new TagDurationReportRowDto(
//                "#Java #pomodoro",
//                25_200L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto backend_pythonPomodoroMappedTagDurationReportRows = new TagDurationReportRowDto(
//                "#python #pomodoro",
//                3_600L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto backend_designPomodoroMappedTagDurationReportRows = new TagDurationReportRowDto(
//                "#design #pomodoro",
//                1_800L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto backendTagDurationReportRow = new TagDurationReportRowDto(
//                "backend",
//                36_000L,
//                new ArrayList<>(List.of(
//                        backend_pomodoroMappedTagDurationReportRow,
//                        backend_javaPomodoroMappedTagDurationReportRows,
//                        backend_pythonPomodoroMappedTagDurationReportRows,
//                        backend_designPomodoroMappedTagDurationReportRows
//                ))
//        );
//        //--------------------------------
//        TagDurationReportRowDto java_pomodoroBackendMappedTagDurationReportRows = new TagDurationReportRowDto(
//                "#pomodoro #backend",
//                25_200L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto javaTagDurationReportRow = new TagDurationReportRowDto(
//                "Java",
//                25_200L,
//                new ArrayList<>(List.of(
//                        java_pomodoroBackendMappedTagDurationReportRows
//                ))
//        );
//        //-----------------------
//        TagDurationReportRowDto design_pomodoroBackendMappedTagDurationReportRows = new TagDurationReportRowDto(
//                "#pomodoro #backend",
//                1_800L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto designTagDurationReportRow = new TagDurationReportRowDto(
//                "design",
//                1_800L,
//                new ArrayList<>(List.of(
//                        design_pomodoroBackendMappedTagDurationReportRows
//                ))
//        );
//        //----------------------
//        TagDurationReportRowDto python_pomodoroBackendMappedTagDurationReportRows = new TagDurationReportRowDto(
//                "backend #pomodoro",
//                3_600L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto pythonTagDurationReportRow = new TagDurationReportRowDto(
//                "python",
//                3_600L,
//                new ArrayList<>(List.of(
//                        python_pomodoroBackendMappedTagDurationReportRows
//                ))
//        );
//
//        when(allTagsDurationReporter.reportForEachTag(List.of()))
//                .thenReturn(List.of(
//                        pomodoroTagDurationReportRow,
//                        backendTagDurationReportRow,
//                        javaTagDurationReportRow,
//                        designTagDurationReportRow,
//                        pythonTagDurationReportRow
//                ));
//
//        List<TagDurationReportRowDto> actual = testee.compose(List.of());
//
//        TagDurationReportRowDto javaRowExpected = new TagDurationReportRowDto(
//                "Java",
//                25_200L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto pythonRowExpected = new TagDurationReportRowDto(
//                "python",
//                3_600L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto designRowExpected = new TagDurationReportRowDto(
//                "design",
//                1_800L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto pomodoroBackendRowExpected = new TagDurationReportRowDto(
//                "backend #pomodoro",
//                36_000L,
//                new ArrayList<>(List.of(
//                        javaRowExpected,
//                        pythonRowExpected,
//                        designRowExpected
//                ))
//        );
//        TagDurationReportRowDto totalRowExpected = new TagDurationReportRowDto(
//                "Total",
//                36_000L,
//                new ArrayList<>()
//        );
//
//        assertThat(actual).containsExactlyInAnyOrderElementsOf(
//                List.of(pomodoroBackendRowExpected, totalRowExpected)
//        );
//    }
//
//    @Test
//    void compose_WhenRootRowsHasSameDuration_ThenRowsShouldBeAtRootPositionAndNotBePartOfOtherRows() {
//        TagDurationReportRowDto work_Company1Company2MappedTagDurationReportRow = new TagDurationReportRowDto(
//                "#Company1 #Company2",
//                64_800L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto work_Company1Company2MeetingMappedTagDurationReportRow = new TagDurationReportRowDto(
//                "#Company1 #Company2 #meeting",
//                25_200L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto workTagDurationReportRow = new TagDurationReportRowDto(
//                "work",
//                90_000L,
//                new ArrayList<>(List.of(
//                        work_Company1Company2MappedTagDurationReportRow,
//                        work_Company1Company2MeetingMappedTagDurationReportRow
//                ))
//        );
//        //-------------------------------------------
//        TagDurationReportRowDto company1_Company2WorkMappedTagDurationReportRow = new TagDurationReportRowDto(
//                "#Company2 #work",
//                64_800L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto company1_Company2MeetingWorkMappedTagDurationReportRow = new TagDurationReportRowDto(
//                "#Company2 #meeting #work",
//                25_200L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto company1TagDurationReportRow = new TagDurationReportRowDto(
//                "Company1",
//                90_000L,
//                new ArrayList<>(List.of(
//                        company1_Company2WorkMappedTagDurationReportRow,
//                        company1_Company2MeetingWorkMappedTagDurationReportRow
//                ))
//        );
//        //-------------------------------------------
//        TagDurationReportRowDto company2_Company1WorkMappedTagDurationReportRow = new TagDurationReportRowDto(
//                "#Company1 #work",
//                64_800L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto company2_Company1MeetingWorkMappedTagDurationReportRow = new TagDurationReportRowDto(
//                "#Company1 #meeting #work",
//                25_200L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto company2TagDurationReportRow = new TagDurationReportRowDto(
//                "Company2",
//                90_000L,
//                new ArrayList<>(List.of(
//                        company2_Company1WorkMappedTagDurationReportRow,
//                        company2_Company1MeetingWorkMappedTagDurationReportRow
//                ))
//        );
//        //-------------------------------------------
//        TagDurationReportRowDto meeting_Company1Company2WorkMappedTagDurationReportRow = new TagDurationReportRowDto(
//                "#Company1 #Company2 #work",
//                25_200L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto meeting_pomodoroMappedTagDurationReportRow = new TagDurationReportRowDto(
//                "#pomodoro",
//                2_400L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto meetingTagDurationReportRow = new TagDurationReportRowDto(
//                "meeting",
//                27_600L,
//                new ArrayList<>(List.of(
//                        meeting_Company1Company2WorkMappedTagDurationReportRow,
//                        meeting_pomodoroMappedTagDurationReportRow
//                ))
//        );
//        //-------------------------------------------
//        TagDurationReportRowDto pomodoro_meetingTagDurationReportRow = new TagDurationReportRowDto(
//                "#meeting",
//                2_400L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto pomodoro_javaBackendTagDurationReportRow = new TagDurationReportRowDto(
//                "#Java #backend",
//                15_600L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto pomodoroTagDurationReportRow = new TagDurationReportRowDto(
//                "pomodoro",
//                18_000L,
//                new ArrayList<>(List.of(
//                        pomodoro_meetingTagDurationReportRow,
//                        pomodoro_javaBackendTagDurationReportRow
//                ))
//        );
//        //-------------------------------------------
//        TagDurationReportRowDto java_pomodoroBackendTagDurationReportRow = new TagDurationReportRowDto(
//                "#pomodoro #backend",
//                15_600L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto javaTagDurationReportRow = new TagDurationReportRowDto(
//                "Java",
//                15_600L,
//                new ArrayList<>(List.of(
//                        java_pomodoroBackendTagDurationReportRow
//                ))
//        );
//        //-------------------------------------------
//        TagDurationReportRowDto backend_pomodoroJavaTagDurationReportRow = new TagDurationReportRowDto(
//                "#pomodoro #Java",
//                15_600L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto backendTagDurationReportRow = new TagDurationReportRowDto(
//                "backend",
//                15_600L,
//                new ArrayList<>(List.of(
//                        backend_pomodoroJavaTagDurationReportRow
//                ))
//        );
//
//        when(allTagsDurationReporter.reportForEachTag(List.of()))
//                .thenReturn(List.of(
//                        workTagDurationReportRow,
//                        company1TagDurationReportRow,
//                        company2TagDurationReportRow,
//                        meetingTagDurationReportRow,
//                        pomodoroTagDurationReportRow,
//                        javaTagDurationReportRow,
//                        backendTagDurationReportRow
//                ));
//
//        List<TagDurationReportRowDto> actual = testee.compose(List.of());
//
//        TagDurationReportRowDto workCompaniesMeetingRowExpected = new TagDurationReportRowDto(
//                "#meeting",
//                10_800L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto workCompaniesRowExpected = new TagDurationReportRowDto(
//                "work #Company1 #Company2",
//                36_000L,
//                new ArrayList<>(List.of(workCompaniesMeetingRowExpected))
//        );
//        TagDurationReportRowDto pomodoroMeetingRowExpected = new TagDurationReportRowDto(
//                "#meeting",
//                2_400L,
//                new ArrayList<>()
//        );
//        TagDurationReportRowDto pomodoroRowExpected = new TagDurationReportRowDto(
//                "#pomodoro",
//                2_400L,
//                new ArrayList<>(List.of(pomodoroMeetingRowExpected))
//        );
//        TagDurationReportRowDto totalRowExpected = new TagDurationReportRowDto(
//                "Total",
//                38_400L,
//                new ArrayList<>()
//        );
//
//        assertThat(actual).containsExactlyInAnyOrderElementsOf(
//                List.of(workCompaniesRowExpected, pomodoroRowExpected, totalRowExpected)
//        );
//    }

}
