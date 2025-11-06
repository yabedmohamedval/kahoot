package com.istic.kahoot.web;

import com.istic.kahoot.domain.Quiz;
import com.istic.kahoot.dto.DraftQuiz;
import com.istic.kahoot.dto.QuizDto;
import com.istic.kahoot.repository.QuizRepository;
import com.istic.kahoot.service.QuizDraftService;
import com.istic.kahoot.service.QuizService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/quizzes")
public class ViewController {

    private final QuizService service;
    private final QuizRepository quizRepo;
    private final QuizDraftService draftService;
    private final ObjectMapper mapper;

    public ViewController(QuizService service, QuizRepository quizRepo, QuizDraftService draftService, ObjectMapper mapper) {
        this.service = service;
        this.quizRepo = quizRepo;
        this.draftService = draftService;
        this.mapper = mapper;
    }

    // Page liste — /quizzes
    @GetMapping
    @PreAuthorize("hasAnyRole('PLAYER','TEACHER')")
    public String quizzes(Model model) {
        model.addAttribute("quizzes", service.list());
        return "quizzes"; // templates/quizzes.html
    }

    // Page création — GET form


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PLAYER','TEACHER')")
    public String quizDetails(@PathVariable Long id, Model model) {
        Quiz quiz = service.getDetails(id);
        model.addAttribute("quiz", quiz);
        return "quiz_detail";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('TEACHER')")
    public ModelAndView newQuizForm() {
        return new ModelAndView("quiz_new");
    }
    // Soumission création — POST form
    // Réception du brouillon JSON et création en base
    @PostMapping("/new")
    @PreAuthorize("hasRole('TEACHER')")
    public String createFromDraft(@RequestParam("draftJson") String draftJson,
                                  Authentication auth) throws Exception {
        // 1) parse JSON -> DraftQuiz
        DraftQuiz draft = mapper.readValue(draftJson, DraftQuiz.class);

        // 2) création + persistance, avec owner = user connecté
        Long quizId = draftService.createFromDraft(draft, auth.getName());

        // 3) redirection vers la fiche du quiz ou la liste
        return "redirect:/quizzes/" + quizId + "?created";
    }
}
