package com.istic.kahoot.config;

import com.istic.kahoot.domain.*;
import com.istic.kahoot.repository.AppUserRepository;
import com.istic.kahoot.repository.QuizRepository;
import jakarta.persistence.EntityManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seed(    AppUserRepository users,
                               QuizRepository quizzes,
                               PasswordEncoder encoder,
                               EntityManager em) {
        return args -> {
            var teacher = users.findByUsername("teacher").orElseGet(() -> {
                var u = new AppUser();
                u.setUsername("teacher");
                u.setEmail("teacher@demo.test");
                u.setRole(Role.TEACHER);
                u.setPasswordHash(encoder.encode("pwTeacher"));
                return users.save(u);
            });

            var player = users.findByUsername("player").orElseGet(() -> {
                var v = new AppUser();
                v.setUsername("player");
                v.setEmail("player@demo.test");
                v.setRole(Role.PLAYER);
                v.setPasswordHash(encoder.encode("pwPlayer"));
                return users.save(v);
            });

            if (quizzes.count() == 0) {
                var quiz = new Quiz();
                quiz.setTitle("Demo Quiz");
                quiz.setOwner(teacher);
                quiz = quizzes.save(quiz);

                var q1 = new TrueFalseQuestion();
                q1.setLabel("La Terre est ronde ?");
                q1.setOrderIndex(1);
                q1.setCorrect(true);

                var q2 = new MCQQuestion();
                q2.setLabel("Couleurs primaires ?");
                q2.setOrderIndex(2);
                q2.setMultiSelect(true);
                var c1 = new Choice();
                c1.setLabel("Rouge");
                c1.setCorrectAnswer(true);
                var c2 = new Choice();
                c2.setLabel("Vert");
                c2.setCorrectAnswer(false);
                var c3 = new Choice();
                c2.setLabel("Bleu");
                c2.setCorrectAnswer(true);
                q2.addChoice(c1); q2.addChoice(c2); q2.addChoice(c3);


                var q3 = new ShortTextQuestion();
                q3.setLabel("Capitale du Japon ?");
                q3.setOrderIndex(3);
                q3.setExpectedRegex("(?i)tokyo");

                quiz.addQuestion(q1);
                quiz.addQuestion(q2);
                quiz.addQuestion(q3);

                quizzes.save(quiz);

            }
        };
    }
}
