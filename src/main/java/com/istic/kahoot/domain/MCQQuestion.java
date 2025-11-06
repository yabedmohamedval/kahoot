package com.istic.kahoot.domain;


import jakarta.persistence.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("MCQ")
public class MCQQuestion  extends Question{

    private Boolean multiSelect = Boolean.FALSE;

    @OneToMany(mappedBy = "question",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    private List<Choice> choices = new ArrayList<>();


    public void addChoice(Choice c) {
        c.setQuestion(this);
        this.choices.add(c);
    }

    public void removeChoice(Choice c) {
        this.choices.remove(c);
        c.setQuestion(null);
    }
}
