package com.study.day06integration.controller;



import com.study.day06integration.dto.StreamChunk;
import com.study.day06integration.service.ChatService;
import com.study.day06integration.service.HelpdeskService;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class AiController {
    private final ChatService chatService;
    private final HelpdeskService helpdeskService;

    public AiController(ChatService chatService, HelpdeskService helpdeskService) {
        this.chatService = chatService;
        this.helpdeskService = helpdeskService;
    }

    @GetMapping("/api/stream-console")
    private Flux<String> streamConsole(@RequestParam String question) {
        return chatService.askStream(question)
                .doOnNext(System.out::println)
                .doOnComplete(() -> System.out.println(" 스트림 완료 "));
    }

    // 토큰이 도착하는대로 흘려 보냄
    // 브라우저가 EventSource.로 소비하도록


    @GetMapping("/api//chat/stream")
    private Flux<ServerSentEvent<StreamChunk>> helpdeskStream(@RequestParam String question,
                                                      @RequestParam String conversationId) {
        Flux<ServerSentEvent<StreamChunk>> token =  helpdeskService.chatStream(question, conversationId)
                .map(chunk -> ServerSentEvent.builder(new StreamChunk(chunk)).build());

        Mono<ServerSentEvent<StreamChunk>> done = Mono.just(ServerSentEvent.<StreamChunk>builder(new StreamChunk("")).event("done").build());

        return token.concatWith(done);
    }
}
