package com.igorgorbunov3333.timer.backend.controller.message;

import com.igorgorbunov3333.timer.backend.controller.util.RestPathUtil;
import com.igorgorbunov3333.timer.backend.model.dto.message.MessageDto;
import com.igorgorbunov3333.timer.backend.service.message.MessageComponent;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@AllArgsConstructor
@RequestMapping(RestPathUtil.COMMON + "/messages")
public class MessageController {

    private final MessageComponent messageComponent;

    @GetMapping
    public boolean isExistByDate(@RequestParam LocalDate date) {
        return messageComponent.isExistByDate(date);
    }

    @PostMapping
    public void save(@RequestBody MessageDto messageDto) {
        messageComponent.save(messageDto);
    }

}
