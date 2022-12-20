package com.igorgorbunov3333.timer.backend.service.message;

import com.igorgorbunov3333.timer.backend.model.dto.message.MessageDto;
import com.igorgorbunov3333.timer.backend.model.entity.message.Message;
import com.igorgorbunov3333.timer.backend.repository.MessageRepository;
import com.igorgorbunov3333.timer.backend.service.mapper.MessageMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
@AllArgsConstructor
public class MessageComponent {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public boolean isExistByDate(@NonNull LocalDate date) {
        return messageRepository.existsByDate(date);
    }

    public void save(@NonNull MessageDto messageDto) {
        Message message = messageMapper.toEntity(messageDto);

        messageRepository.save(message);

        deleteOldMessages();
    }

    private void deleteOldMessages() {
        List<Message> messagesToRemove = messageRepository.findAll();
        messagesToRemove.sort(Comparator.comparing(Message::getDate));
        messagesToRemove.remove(messagesToRemove.size() - 1);
        messageRepository.deleteAll(messagesToRemove);
    }

}
