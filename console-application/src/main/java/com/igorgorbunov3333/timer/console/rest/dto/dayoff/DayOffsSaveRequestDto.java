package com.igorgorbunov3333.timer.console.rest.dto.dayoff;

import com.igorgorbunov3333.timer.console.rest.dto.DayOffDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class DayOffsSaveRequestDto {

    @NotNull
    @NotEmpty
    private List<DayOffDto> dayOffs;

}