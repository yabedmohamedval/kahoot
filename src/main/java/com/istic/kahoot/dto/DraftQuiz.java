package com.istic.kahoot.dto;

import java.util.List;

public class DraftQuiz {
    public String title;
    public String description;
    public Integer timePerQuestionSec;
    public List<DraftQuestion> questions;
}
