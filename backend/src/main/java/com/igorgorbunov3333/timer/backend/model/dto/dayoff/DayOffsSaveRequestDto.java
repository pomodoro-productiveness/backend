package com.igorgorbunov3333.timer.backend.model.dto.dayoff;

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
