package com.istic.kahoot.controller;


import com.istic.kahoot.dto.QuizDto;
import com.istic.kahoot.service.QuizService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.*;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private record CreateQuizReq(String title) {}

    private final QuizService service;
    public QuizController(QuizService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER','PLAYER')")
    public List<QuizDto> list() {
        return service.list();
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public QuizDto create(@RequestBody Map<String,String> body, Authentication auth) {
        String title = body.getOrDefault("title", "(no title)");
        return service.create(title, auth.getName());
    }
}
