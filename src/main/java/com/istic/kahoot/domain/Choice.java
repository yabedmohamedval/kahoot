package com.istic.kahoot.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Choice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;
    @Column(name = "correct_answer", nullable = false)
    private Boolean correctAnswer = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private MCQQuestion question;

    public Choice(String label, boolean correct) {
        this.label = label;
        this.correctAnswer = correct;
    }


}
