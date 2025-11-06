package com.istic.kahoot.domain;


import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("TF")
public class TrueFalseQuestion extends Question {

    @Column(nullable = true)
    private Boolean correct;
}
