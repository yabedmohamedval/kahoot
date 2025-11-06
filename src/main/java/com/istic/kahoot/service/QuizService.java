package com.istic.kahoot.service;

import com.istic.kahoot.domain.MCQQuestion;
import com.istic.kahoot.domain.Quiz;
import com.istic.kahoot.domain.Role;
import com.istic.kahoot.dto.QuizDto;
import com.istic.kahoot.mapper.QuizMapper;
import com.istic.kahoot.repository.AppUserRepository;
import com.istic.kahoot.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class QuizService {

    private final QuizRepository repo;
    private final AppUserRepository users;


    public QuizService(QuizRepository repo, AppUserRepository users) {
        this.repo = repo;
        this.users = users;
    }

    public List<QuizDto> list() {
        var quizzes = repo.findAll();
        var ids = quizzes.stream().map(Quiz::getId).toList();

        Map<Long, Integer> counts = new HashMap<>();
        if (!ids.isEmpty()) {
            for (Object[] row : repo.countQuestionsByQuizIds(ids)) {
                counts.put((Long) row[0], ((Long) row[1]).intValue());
            }
        }

        return quizzes.stream().map(q -> {
            String owner = (q.getOwner() != null) ? q.getOwner().getUsername() : null;
            int count = counts.getOrDefault(q.getId(), 0);
            return new QuizDto(
                    q.getId(),
                    q.getTitle(),
                    q.getDescription(),
                    q.getTimePerQuestionSec(),
                    owner,
                    count
            );
        }).toList();    }

    public QuizDto create(String title, String creatorUsername) {
        var owner = users.findByUsername(creatorUsername)
                .orElseThrow(() -> new IllegalArgumentException("Creator not found: " + creatorUsername));

        if (owner.getRole() != Role.TEACHER) {
            throw new AccessDeniedException("Only TEACHER can create quizzes");
        }
        Quiz q = new Quiz();
        q.setTitle(title);
        q.setOwner(owner);
        return QuizMapper.toDto(repo.save(q));
    }

    public Quiz getDetails(Long id) {
        Quiz quiz = repo.findDetailsById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        quiz.getQuestions().forEach(q -> {
            if (q instanceof MCQQuestion mcq) {
                mcq.getChoices().size();
            }
        });
        return quiz;
    }
}
