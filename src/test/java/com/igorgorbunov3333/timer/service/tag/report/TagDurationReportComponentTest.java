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

@ExtendWith(MockitoExtension.class)
class TagDurationReportComponentTest {

    private final static LocalDateTime INITIAL_DATE_TIME = LocalDate.of(2020, 1, 1).atStartOfDay();

    @InjectMocks
    private TagDurationReportComponent testee;

    @Test
    void buildReport_WhenReportsPresent_ThenBuildReport() {
        List<PomodoroDto> pomodoro = new ArrayList<>();
        pomodoro.addAll(buildPomodoro(5, List.of("work", "meeting")));
        pomodoro.addAll(buildPomodoro(20, List.of("work", "outsourceCompany", "clientCompany", "meeting")));
        pomodoro.addAll(buildPomodoro(7, List.of("work", "outsourceCompany", "clientCompany", "trainings")));
        pomodoro.addAll(buildPomodoro(30, List.of("work", "outsourceCompany", "clientCompany")));

        List<TagDurationReportRowDto> actual = testee.buildReport(pomodoro);

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


        assertThat(actual).containsExactlyInAnyOrderElementsOf(List.of(workExpected, totalExpected));
    }

    @Test
    void buildReport_WhenReportsPresentAndTwoOrMoreParentsHasChildrenWithDeepNesting_ThenBuildReport() {
        List<PomodoroDto> pomodoro = new ArrayList<>();
        pomodoro.addAll(buildPomodoro(21, List.of("backend", "pomodoro", "Java")));
        pomodoro.addAll(buildPomodoro(3, List.of("backend", "pomodoro", "python")));
        pomodoro.addAll(buildPomodoro(2, List.of("backend", "pomodoro", "design")));
        pomodoro.addAll(buildPomodoro(4, List.of("backend", "pomodoro")));

        List<TagDurationReportRowDto> actual = testee.buildReport(pomodoro);

        TagDurationReportRowDto javaRowExpected = new TagDurationReportRowDto(
                "Java",
                25_200L,
                new ArrayList<>()
        );
        TagDurationReportRowDto pythonRowExpected = new TagDurationReportRowDto(
                "python",
                3_600L,
                new ArrayList<>()
        );
        TagDurationReportRowDto designRowExpected = new TagDurationReportRowDto(
                "design",
                2_400L,
                new ArrayList<>()
        );
        TagDurationReportRowDto pomodoroBackendRowExpected = new TagDurationReportRowDto(
                "backend #pomodoro",
                36_000L,
                new ArrayList<>(List.of(
                        javaRowExpected,
                        pythonRowExpected,
                        designRowExpected
                ))
        );
        TagDurationReportRowDto totalRowExpected = new TagDurationReportRowDto(
                "Total",
                36_000L,
                new ArrayList<>()
        );

        assertThat(actual).containsExactlyInAnyOrderElementsOf(
                List.of(pomodoroBackendRowExpected, totalRowExpected)
        );
    }

    @Test
    void buildReport_WhenRootRowsHasSameDuration_ThenRowsShouldBeAtRootPositionAndNotBePartOfOtherRows() {
        List<PomodoroDto> pomodoro = new ArrayList<>();
        pomodoro.addAll(buildPomodoro(21, List.of("work", "Company1", "Company2")));
        pomodoro.addAll(buildPomodoro(9, List.of("work", "Company1", "Company2", "meeting")));
        pomodoro.addAll(buildPomodoro(1, List.of("work", "education")));

        pomodoro.addAll(buildPomodoro(8, List.of("pomodoro", "Java", "backend")));
        pomodoro.addAll(buildPomodoro(3, List.of("pomodoro", "meeting", "cicd")));
        pomodoro.addAll(buildPomodoro(2, List.of("pomodoro", "meeting", "architecture")));
        pomodoro.addAll(buildPomodoro(1, List.of("pomodoro", "meeting")));

        pomodoro.addAll(buildPomodoro(4, List.of("education", "book")));
        pomodoro.addAll(buildPomodoro(2, List.of("education", "spring", "book")));
        pomodoro.addAll(buildPomodoro(1, List.of("education", "database", "book")));

        List<TagDurationReportRowDto> actual = testee.buildReport(pomodoro);

        TagDurationReportRowDto workCompanies_MeetingRowExpected = new TagDurationReportRowDto(
                "meeting",
                10_800L,
                new ArrayList<>()
        );
        TagDurationReportRowDto work_CompaniesRowExpected = new TagDurationReportRowDto(
                "Company1 #Company2",
                36_000L,
                new ArrayList<>(List.of(workCompanies_MeetingRowExpected))
        );
        TagDurationReportRowDto work_EducationRowExpected = new TagDurationReportRowDto(
                "education",
                1_200L,
                new ArrayList<>()
        );
        TagDurationReportRowDto workRowExpected = new TagDurationReportRowDto(
                "work",
                37_200L,
                new ArrayList<>(List.of(work_CompaniesRowExpected, work_EducationRowExpected))
        );

        TagDurationReportRowDto pomodoroJavaBackendRowExpected = new TagDurationReportRowDto(
                "backend #Java",
                9_600L,
                new ArrayList<>()
        );
        TagDurationReportRowDto meetingCicdRowExpected = new TagDurationReportRowDto(
                "cicd",
                3_600L,
                new ArrayList<>()
        );
        TagDurationReportRowDto meetingArchitectureRowExpected = new TagDurationReportRowDto(
                "architecture",
                2_400L,
                new ArrayList<>()
        );
        TagDurationReportRowDto pomodoroMeetingRowExpected = new TagDurationReportRowDto(
                "meeting",
                7_200L,
                new ArrayList<>(List.of(meetingCicdRowExpected, meetingArchitectureRowExpected))
        );
        TagDurationReportRowDto pomodoroRowExpected = new TagDurationReportRowDto(
                "pomodoro",
                16_800L,
                new ArrayList<>(List.of(pomodoroJavaBackendRowExpected, pomodoroMeetingRowExpected))
        );

        TagDurationReportRowDto book_springRowExpected = new TagDurationReportRowDto(
                "spring",
                2_400L,
                new ArrayList<>()
        );
        TagDurationReportRowDto book_databaseRowExpected = new TagDurationReportRowDto(
                "database",
                1_200L,
                new ArrayList<>()
        );
        TagDurationReportRowDto educationBookRowExpected = new TagDurationReportRowDto(
                "book #education",
                8_400L,
                new ArrayList<>(List.of(book_springRowExpected, book_databaseRowExpected))
        );

        TagDurationReportRowDto totalRowExpected = new TagDurationReportRowDto(
                "Total",
                62_400L,
                new ArrayList<>()
        );

        assertThat(actual).containsExactlyInAnyOrderElementsOf(
                List.of(workRowExpected, pomodoroRowExpected, totalRowExpected, educationBookRowExpected)
        );
    }

    @Test
    void buildReport_WhenReportBuilt_ThenNoLeftoversInReport() {
        List<PomodoroDto> pomodoro = new ArrayList<>();
        pomodoro.addAll(buildPomodoro(9, List.of("work", "Company1", "Company2")));
        pomodoro.addAll(buildPomodoro(2, List.of("work", "Company1", "Company2", "meeting")));
        pomodoro.addAll(buildPomodoro(2, List.of("education", "English", "Friends", "tvSeries")));
        pomodoro.addAll(buildPomodoro(1, List.of("education", "EduCompany", "html/css")));

        List<TagDurationReportRowDto> actual = testee.buildReport(pomodoro);

        TagDurationReportRowDto workCompany_MeetingRowExpected = new TagDurationReportRowDto(
                "meeting",
                2_400L,
                new ArrayList<>()
        );
        TagDurationReportRowDto workCompanyRowExpected = new TagDurationReportRowDto(
                "work #Company1 #Company2",
                13_200L,
                new ArrayList<>(List.of(workCompany_MeetingRowExpected))
        );

        TagDurationReportRowDto education_englishFriendsTvSeries = new TagDurationReportRowDto(
                "tvSeries #English #Friends",
                2_400L,
                new ArrayList<>()
        );
        TagDurationReportRowDto education_eduCompanyHtmlCss = new TagDurationReportRowDto(
                "html/css #EduCompany",
                1_200L,
                new ArrayList<>()
        );
        TagDurationReportRowDto educationRowExpected = new TagDurationReportRowDto(
                "education",
                3_600L,
                new ArrayList<>(List.of(education_englishFriendsTvSeries, education_eduCompanyHtmlCss))
        );

        TagDurationReportRowDto totalRowExpected = new TagDurationReportRowDto(
                "Total",
                16_800L,
                new ArrayList<>()
        );

        assertThat(actual).containsExactlyInAnyOrderElementsOf(
                List.of(workCompanyRowExpected, educationRowExpected, totalRowExpected)
        );
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

}
