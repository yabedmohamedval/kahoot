package com.istic.kahoot.dto;

import java.util.List;

public class DraftQuestion {
    // "MCQ" | "TF" | "SHORT"
    public String type;
    public String label;
    public Integer orderIndex;         // peut être null
    // MCQ
    public Boolean multiSelect;        // MCQ
    public List<DraftChoice> choices; // MCQ
    // TF
    public Boolean correct;            // TF
    // SHORT
    public String expectedRegex;       // SHORT
}
