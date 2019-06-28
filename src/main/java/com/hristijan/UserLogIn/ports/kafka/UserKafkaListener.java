package com.hristijan.UserLogIn.ports.kafka;

import com.hristijan.UserLogIn.model.User;
import com.hristijan.UserLogIn.repository.jpa.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserKafkaListener {

    private final UserRepository userRepository;

    public UserKafkaListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "USER_TOPIC", groupId = "user_group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUser(User user){
        this.userRepository.save(user);
    }
}
