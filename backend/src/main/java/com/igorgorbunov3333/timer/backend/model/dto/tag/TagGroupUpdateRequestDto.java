package com.igorgorbunov3333.timer.backend.model.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.Set;

@Getter
@AllArgsConstructor
public class TagGroupUpdateRequestDto {

    @Positive
    private final long tagGroupId;

    @NotEmpty
    private final Set<@NotBlank String> tags;

}
