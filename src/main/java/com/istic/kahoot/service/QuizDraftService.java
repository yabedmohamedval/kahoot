package com.istic.kahoot.service;

import com.istic.kahoot.domain.*;
import com.istic.kahoot.dto.*;
import com.istic.kahoot.repository.AppUserRepository;
import com.istic.kahoot.repository.QuizRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class QuizDraftService {

    private final QuizRepository quizzes;
    private final AppUserRepository users;
    private final EntityManager em;

    public QuizDraftService(QuizRepository quizzes, AppUserRepository users, EntityManager em) {
        this.quizzes = quizzes;
        this.users = users;
        this.em = em;
    }

    public Long createFromDraft(DraftQuiz draft, String username) {
        // validations minimales
        if (draft == null || draft.title == null || draft.title.isBlank())
            throw new IllegalArgumentException("Le titre est requis");
        if (draft.questions == null || draft.questions.isEmpty())
            throw new IllegalArgumentException("Au moins une question est requise");

        // owner = user connecté, et il doit être TEACHER
        AppUser owner = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur inconnu: " + username));
        if (owner.getRole() != Role.TEACHER)
            throw new AccessDeniedException("Seuls les TEACHER peuvent créer des quiz");

        // création du quiz
        Quiz qz = new Quiz();
        qz.setTitle(draft.title.trim());
        qz.setDescription(draft.description == null ? null : draft.description.trim());
        qz.setTimePerQuestionSec(draft.timePerQuestionSec == null ? 20 : draft.timePerQuestionSec);
        qz.setOwner(owner);

        // ordre auto si non fourni : on itère et on affecte progressivement
        int auto = 1;

        for (DraftQuestion dq : draft.questions) {
            if (dq == null) continue;
            if (dq.label == null || dq.label.isBlank())
                throw new IllegalArgumentException("Chaque question doit avoir un label");

            int order = (dq.orderIndex != null && dq.orderIndex > 0) ? dq.orderIndex : auto++;

            switch (dq.type) {
                case "MCQ" -> {
                    MCQQuestion mcq = new MCQQuestion();
                    mcq.setLabel(dq.label.trim());
                    mcq.setOrderIndex(order);
                    mcq.setMultiSelect(Boolean.TRUE.equals(dq.multiSelect));
                    // choices (au moins 2)
                    if (dq.choices == null || dq.choices.size() < 2)
                        throw new IllegalArgumentException("Une MCQ doit avoir au moins 2 choix");

                    for (DraftChoice dc : dq.choices) {
                        if (dc == null || dc.label == null || dc.label.isBlank())
                            continue;
                        Choice ch = new Choice(dc.label.trim(), Boolean.TRUE.equals(dc.correctAnswer));
                        mcq.addChoice(ch); // gère le setQuestion
                    }
                    qz.addQuestion(mcq); // gère le setQuiz + cascade
                }
                case "TF" -> {
                    TrueFalseQuestion tf = new TrueFalseQuestion();
                    tf.setLabel(dq.label.trim());
                    tf.setOrderIndex(order);
                    tf.setCorrect(Boolean.TRUE.equals(dq.correct));
                    qz.addQuestion(tf);
                }
                case "SHORT" -> {
                    ShortTextQuestion st = new ShortTextQuestion();
                    st.setLabel(dq.label.trim());
                    st.setOrderIndex(order);
                    st.setExpectedRegex(dq.expectedRegex == null ? null : dq.expectedRegex.trim());
                    qz.addQuestion(st);
                }
                default -> throw new IllegalArgumentException("Type de question inconnu: " + dq.type);
            }
        }

        // persist (cascade PERSIST/REMOVE sur Quiz.questions + orphanRemoval)
        Quiz saved = quizzes.save(qz);
        em.flush(); // pour obtenir l'id immédiatement
        return saved.getId();
    }
}
