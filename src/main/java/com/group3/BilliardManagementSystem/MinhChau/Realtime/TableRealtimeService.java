package com.group3.BilliardManagementSystem.MinhChau.Realtime;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TableRealtimeService {

    private final List<SseEmitter> emitters = new ArrayList<>();

    public SseEmitter subscribe() {

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    public void sendTableUpdate(Object data) {

        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : emitters) {

            try {

                emitter.send(
                        SseEmitter.event()
                                .name("table-update")
                                .data(data)
                );

            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }

        emitters.removeAll(deadEmitters);
    }
}