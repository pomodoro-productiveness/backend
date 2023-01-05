package com.igorgorbunov3333.timer.backend.service.mapper;

import com.igorgorbunov3333.timer.backend.model.dto.message.MessageDto;
import com.igorgorbunov3333.timer.backend.model.entity.enums.MessagePeriod;
import com.igorgorbunov3333.timer.backend.model.entity.message.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "messagePeriod", target = "messagePeriod", qualifiedByName = "messagePeriod")
    Message toEntity(MessageDto dto);

    @Named("messagePeriod")
    static MessagePeriod mapMessagePeriodField(String messagePeriod) {
        return MessagePeriod.from(messagePeriod);
    }

}
