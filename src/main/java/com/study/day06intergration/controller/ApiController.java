package com.study.day06intergration.controller;

import com.study.day06intergration.dto.HistoryMessage;
import com.study.day06intergration.dto.StreamChunk;
import com.study.day06intergration.service.ChatHistoryService;
import com.study.day06intergration.service.ChatService;
import com.study.day06intergration.service.HelpdeskService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class ApiController {
    private final ChatService chatService;
    private final HelpdeskService helpdeskService;
    private final ChatHistoryService chatHistoryService;

    public ApiController(ChatService chatService, HelpdeskService helpdeskService, ChatHistoryService chatHistoryService) {
        this.chatService = chatService;
        this.helpdeskService = helpdeskService;
        this.chatHistoryService = chatHistoryService;
    }

    @GetMapping("/api/stream-console")
    public Flux<String> streamConsole(@RequestParam String question) {
        return chatService.askStream(question)
                .doOnNext(token -> System.out.print(token))
                .doOnComplete(() -> System.out.print(" [stream complete]"));
        // 콘솔에서 확인
    }

    // 토큰이 도착하는대로 흘려 보냄.
    // 브라우저가 EventSOurce.로 소비하도록
    @GetMapping("/api/stream")
    public Flux<ServerSentEvent<StreamChunk>> stream(@RequestParam String question) {
        // 각 토큰들이 StreamChunk로 감싸진 JSON으로 직렬화
        Flux<ServerSentEvent<StreamChunk>> token = chatService.askStream(question)
                .map(chunk -> ServerSentEvent.builder(new StreamChunk(chunk)).build());
        // 완료신호. SSE가 끝난다. 연결을 닫음. 무한 재연결을 방지.
        Flux<ServerSentEvent<StreamChunk>> done = Mono.just(ServerSentEvent.<StreamChunk>builder(new StreamChunk("")).event("done").build())
                .flux();
        return token.concatWith(done);
    }

    // ------------------------------------------
    // 헬프데스크 서비스
    @GetMapping("/api/chat/stream-nohistroy")
    public Flux<ServerSentEvent<StreamChunk>> helpdeskStreamNoHistory(@RequestParam String question,
                                                     @RequestParam String conversationId) {
        Flux<ServerSentEvent<StreamChunk>> token = helpdeskService.chatStream(question, conversationId)
                .map(chunk -> ServerSentEvent.builder(new StreamChunk(chunk)).build());
        Flux<ServerSentEvent<StreamChunk>> done = Mono.just(ServerSentEvent.<StreamChunk>builder(new StreamChunk("")).event("done").build())
                .flux();
        return token.concatWith(done);
    }

    @GetMapping(value = "/api/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StreamChunk>> helpdeskStream(@RequestParam String question,
                                                             @RequestParam String conversationId) {
        // 프론트로부터 요청을 받음 -> 채팅 이력으로 저장
        chatHistoryService.save(conversationId, "user", question);

        // 답변을 저장할 객체. 문자열 덩어리를 이어서 붙인다.
        StringBuilder answer = new StringBuilder();

        // 이어붙인 문자열 덩어리 저장
        Flux<ServerSentEvent<StreamChunk>> token = helpdeskService.chatStream(question, conversationId)
                .doOnNext(answer::append) // 문자열을 이어붙임
                .map(chunk -> ServerSentEvent.builder(new StreamChunk(chunk)).build())
                .doOnComplete(() -> chatHistoryService.save(conversationId, "assistant", answer.toString()));

        // SSE는 데이타가 비면 이벤트를 전달하지 않음. 빈 값이라도 전달.
        // done 이벤트는 프론트엔드 브라우저 쪽 객체의 연결 닫는 신호.
        Flux<ServerSentEvent<StreamChunk>> done = Mono.
                just(ServerSentEvent.<StreamChunk>builder(new StreamChunk("")).event("done").build())
                .flux();
        return token.concatWith(done);
    }

    // 채팅 이력 가져오기. 메세지 윈도우 (20개)가 넘는 이력도 테이블에서 따로 가져온다.
    @GetMapping("/api/history")
    public List<HistoryMessage> history(@RequestParam String conversationId) {
        return chatHistoryService.history(conversationId);
    }


}
