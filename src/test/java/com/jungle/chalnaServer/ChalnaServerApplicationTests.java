package com.jungle.chalnaServer;

import com.jungle.chalnaServer.domain.chat.repository.ChatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChalnaServerApplicationTests {

    @Autowired
    ChatRepository chatRepository;
    @Test
    void contextLoads() {
    }

}
