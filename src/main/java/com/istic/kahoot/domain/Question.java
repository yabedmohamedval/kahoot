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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="q_type", discriminatorType = DiscriminatorType.STRING)
@Table(name="question", uniqueConstraints = {
        @UniqueConstraint(name="uk_question_quiz_order", columnNames = {"quiz_id", "order_index"})
})
public abstract class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    protected String  label;
    @Column(name = "order_index", nullable = false)
    protected Integer orderIndex;
    private int timeLimitSeconds = 20;
    @Column(nullable = false)
    protected Integer points = 100;

    @Column(name = "q_type", insertable = false, updatable = false)
    @Setter(lombok.AccessLevel.NONE)
    private String qType;


    @ManyToOne(optional=false,  fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}
