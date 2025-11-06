package com.istic.kahoot.mapper;

import com.istic.kahoot.domain.Quiz;
import com.istic.kahoot.dto.QuizDto;

public class QuizMapper {
    public static QuizDto toDto(Quiz q) {
        String owner = (q.getOwner() != null) ? q.getOwner().getUsername() : null;
        int count = (q.getQuestions() != null) ? q.getQuestions().size() : 0;
        return new QuizDto(
                q.getId(),
                q.getTitle(),
                q.getDescription(),
                q.getTimePerQuestionSec(),
                owner,
                count
        );    }
}
