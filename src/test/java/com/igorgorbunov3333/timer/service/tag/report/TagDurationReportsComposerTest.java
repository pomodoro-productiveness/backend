package com.igorgorbunov3333.timer.service.tag.report;

import com.igorgorbunov3333.timer.model.dto.tag.report.TagDurationReportRowDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagDurationReportsComposerTest {

    @InjectMocks
    private TagDurationReportsComposer testee;
    @Mock
    private AllTagsDurationReporter allTagsDurationReporter;

    @Test
    void compose_whenReportsPresent_ThenCompose() {
        TagDurationReportRowDto meetingOutsourceClientMappedRows = new TagDurationReportRowDto(
                "#meeting #outsourceCompany #clientCompany",
                24_000L,
                Collections.emptyList()
        );
        TagDurationReportRowDto meetingMappedRow = new TagDurationReportRowDto(
                "#meeting",
                6_000L,
                Collections.emptyList()
        );
        TagDurationReportRowDto outsourceClientCompanyTrainingsMappedRows = new TagDurationReportRowDto(
                "#outsourceCompany #clientCompany #trainings",
                8_400L,
                Collections.emptyList()
        );
        TagDurationReportRowDto outsourceCompanyClientCompanyMappedRows = new TagDurationReportRowDto(
                "#outsourceCompany #clientCompany",
                36_000L,
                Collections.emptyList()
        );

        TagDurationReportRowDto workTagDurationReportRow = new TagDurationReportRowDto(
                "work",
                74_400L,
                List.of(
                        meetingOutsourceClientMappedRows,
                        meetingMappedRow,
                        outsourceClientCompanyTrainingsMappedRows,
                        outsourceCompanyClientCompanyMappedRows
                )
        );

        //-----------

        TagDurationReportRowDto workOutsourceCompanyClientCompanyMappedReportRows = new TagDurationReportRowDto(
                "#work #outsourceCompany #clientCompany",
                24_000L,
                Collections.emptyList()
        );

        TagDurationReportRowDto workMappedReportRow = new TagDurationReportRowDto(
                "#work",
                6_000L,
                Collections.emptyList()
        );

        TagDurationReportRowDto meetingTagDurationReportRow = new TagDurationReportRowDto(
                "meeting",
                30_000L,
                List.of(
                        workOutsourceCompanyClientCompanyMappedReportRows,
                        workMappedReportRow
                )
        );

        //---------

        TagDurationReportRowDto workMeetingOutsourceClientCompanyReportRows = new TagDurationReportRowDto(
                "#work #meeting #clientCompany",
                24_000L,
                Collections.emptyList()
        );

        TagDurationReportRowDto workClientCompanyTrainingsMappedReportRows = new TagDurationReportRowDto(
                "#work #clientCompany #trainings",
                8_400L,
                Collections.emptyList()
        );

        TagDurationReportRowDto workClientCompanyMappedReportRows = new TagDurationReportRowDto(
                "#work #clientCompany",
                36_000L,
                Collections.emptyList()
        );

        TagDurationReportRowDto outsourceCompanyReportRow = new TagDurationReportRowDto(
                "outsourceCompany",
                68_400L,
                List.of(
                        workMeetingOutsourceClientCompanyReportRows,
                        workClientCompanyTrainingsMappedReportRows,
                        workClientCompanyMappedReportRows

                )
        );

        //-----------

        TagDurationReportRowDto outsourceCompany_workMeetingOutsourceClientCompanyReportRows = new TagDurationReportRowDto(
                "#work #meeting #outsourceCompany",
                24_000L,
                Collections.emptyList()
        );

        TagDurationReportRowDto outsourceCompany_workClientCompanyTrainingsMappedReportRows = new TagDurationReportRowDto(
                "#work #outsourceCompany #trainings",
                8_400L,
                Collections.emptyList()
        );

        TagDurationReportRowDto outsourceCompany_workClientCompanyMappedReportRows = new TagDurationReportRowDto(
                "#work #outsourceCompany",
                36_000L,
                Collections.emptyList()
        );

        TagDurationReportRowDto clientCompanyReportRow = new TagDurationReportRowDto(
                "clientCompany",
                68_400L,
                List.of(
                        outsourceCompany_workMeetingOutsourceClientCompanyReportRows,
                        outsourceCompany_workClientCompanyTrainingsMappedReportRows,
                        outsourceCompany_workClientCompanyMappedReportRows
                )
        );

        //---------------

        TagDurationReportRowDto workOutsourceCompanyClientCompanyReportRows = new TagDurationReportRowDto(
                "#work #outsourceCompany #clientCompany",
                36_000L,
                Collections.emptyList()
        );

        TagDurationReportRowDto trainingsReportRow = new TagDurationReportRowDto(
                "trainings",
                68_400L,
                List.of(
                        workOutsourceCompanyClientCompanyReportRows
                )
        );

        when(allTagsDurationReporter.reportForEachTag(List.of()))
                .thenReturn(List.of(
                        workTagDurationReportRow,
                        meetingTagDurationReportRow,
                        outsourceCompanyReportRow,
                        clientCompanyReportRow,
                        trainingsReportRow
                ));

        TagDurationReportRowDto work_meetingMappedExpected = new TagDurationReportRowDto(
                "meeting",
                6_000L,
                Collections.emptyList()
        );

        TagDurationReportRowDto outsourceCompanyClientCompany_meetingMappedExpected = new TagDurationReportRowDto(
                "meeting",
                24_000L,
                Collections.emptyList()
        );

        TagDurationReportRowDto outsourceCompanyClientCompany_trainingsMappedExpected = new TagDurationReportRowDto(
                "trainings",
                8_400L,
                Collections.emptyList()
        );

        TagDurationReportRowDto work_outsourceCompanyClientCompanyMappedExpected = new TagDurationReportRowDto(
                "outsourceCompany #clientCompany",
                68_400L,
                List.of(
                        outsourceCompanyClientCompany_meetingMappedExpected,
                        outsourceCompanyClientCompany_trainingsMappedExpected
                )
        );

        TagDurationReportRowDto workExpected = new TagDurationReportRowDto(
                "work",
                7_4400L,
                List.of(
                        work_meetingMappedExpected,
                        work_outsourceCompanyClientCompanyMappedExpected
                )
        );

        TagDurationReportRowDto totalExpected = new TagDurationReportRowDto(
                "Total",
                74_400L,
                Collections.emptyList()
        );

        List<TagDurationReportRowDto> actual = testee.compose(List.of());

        assertThat(actual).containsExactlyInAnyOrderElementsOf(List.of(workExpected, totalExpected));
    }

}
