package com.istic.kahoot.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SHORT")
public class ShortTextQuestion extends Question {

    @Column(length = 512)
    private String expectedRegex;

    public boolean matches(String userText) {
        if (expectedRegex == null || userText == null) return false;
        return java.util.regex.Pattern.compile(expectedRegex).matcher(userText).matches();
    }
}