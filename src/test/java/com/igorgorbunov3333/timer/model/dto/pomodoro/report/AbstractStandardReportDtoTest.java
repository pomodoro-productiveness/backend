package com.igorgorbunov3333.timer.model.dto.pomodoro.report;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//TODO: make tests parameterized
class AbstractStandardReportDtoTest {

    @Test
    void getRatio_WhenActualMoreThanStandardTwoTimes_ThenReturn200Percents() {
        int standardAmount = 10;
        int differenceAmount = Integer.MAX_VALUE;
        int actualAmount = 20;

        TestStandardReportDto testee = new TestStandardReportDto(standardAmount, differenceAmount, actualAmount);

        assertThat(testee.getRatio()).isEqualTo(2.0D);
    }

    @Test
    void getRatio_WhenActualLessThanStandardTwoTimes_ThenReturn50Percents() {
        int standardAmount = 20;
        int differenceAmount = Integer.MAX_VALUE;
        int actualAmount = 10;

        TestStandardReportDto testee = new TestStandardReportDto(standardAmount, differenceAmount, actualAmount);

        assertThat(testee.getRatio()).isEqualTo(0.5D);
    }

    @Test
    void getRatio_WhenActualEqualsStandard_ThenReturn100Percents() {
        int standardAmount = 10;
        int differenceAmount = Integer.MAX_VALUE;
        int actualAmount = 10;

        TestStandardReportDto testee = new TestStandardReportDto(standardAmount, differenceAmount, actualAmount);

        assertThat(testee.getRatio()).isEqualTo(1.0D);
    }

    @Test
    void getRatio_WhenStandardIsZero_ThenReturn0Percents() {
        int standardAmount = 0;
        int differenceAmount = Integer.MAX_VALUE;
        int actualAmount = 5;

        TestStandardReportDto testee = new TestStandardReportDto(standardAmount, differenceAmount, actualAmount);

        assertThat(testee.getRatio()).isEqualTo(0.0D);
    }

    private static class TestStandardReportDto extends AbstractStandardReportDto {

        public TestStandardReportDto(int standardAmount, int differenceAmount, int actualAmount) {
            super(standardAmount, differenceAmount, actualAmount);
        }

    }

}
