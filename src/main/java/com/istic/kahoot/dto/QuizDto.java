package com.istic.kahoot.dto;

public record QuizDto(
        Long id,
        String title,
        String description,
        Integer timePerQuestionSec,
        String ownerUsername,
        int questionCount) {
}
