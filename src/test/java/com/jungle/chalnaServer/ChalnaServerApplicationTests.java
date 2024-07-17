package com.jungle.chalnaServer;

import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageRequest;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class ChalnaServerApplicationTests {

    @Autowired
    ChatService chatService;

//    @Test
    public void chatTest() throws InterruptedException, ExecutionException {
        int numberOfThreads = 1000;
        int numberOfIterations = 1;
        long startTime = System.nanoTime();
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<List<Long>>> futures = new ArrayList<>();

        Long memberId = 1L;
        Long chatRoomId = 2L;



        for (int i = 0; i < numberOfThreads; i++) {
            futures.add(executorService.submit(new MessageSenderTask(chatService, numberOfIterations,memberId,chatRoomId)));
        }

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.MINUTES);

        Long endTime = System.nanoTime();
        List<List<Long>> allExecutionTimes = new ArrayList<>();
        for (Future<List<Long>> future : futures) {
            allExecutionTimes.add(future.get());
        }

        double averageTime = allExecutionTimes.stream()
                .mapToDouble(executionTimes ->
                        executionTimes.stream()
                        .mapToLong(Long::longValue)
                        .average().orElse(0.0))
                .average().orElse(0.0);
        long maxTime = allExecutionTimes.stream()
                .mapToLong(executionTimes ->
                        executionTimes.stream()
                                .mapToLong(Long::longValue)
                                .max().orElse(0L))
                .max().orElse(0L);

        Long totalTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        System.out.println("Average execution time: " + averageTime + " ms");
        System.out.println("Max execution time: " + maxTime + " ms");
        System.out.println("Total execution Time: " + totalTime + " ms");
    }

    static class MessageSenderTask implements Callable<List<Long>> {

        private final ChatService chatService;
        private final int numberOfIterations;
        private final Long memberId;
        private final Long chatRoomId;

        public MessageSenderTask(ChatService chatService, int numberOfIterations,Long memberId,Long chatRoomId) {
            this.chatService = chatService;
            this.numberOfIterations = numberOfIterations;
            this.memberId = memberId;
            this.chatRoomId = chatRoomId;
        }

        @Override
        public List<Long> call() {
            List<Long> executionTimes = new ArrayList<>();

            for (int i = 0; i < numberOfIterations; i++) {
                long startTime = System.nanoTime();

                ChatMessageRequest.SEND send = new ChatMessageRequest.SEND(ChatMessage.MessageType.CHAT, "test");
                chatService.sendMessage(memberId, chatRoomId, send);

                long endTime = System.nanoTime();
                long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

                executionTimes.add(duration);
            }

            return executionTimes;
        }
    }

}
