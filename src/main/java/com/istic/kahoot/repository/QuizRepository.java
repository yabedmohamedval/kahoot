package com.istic.kahoot.repository;

import com.istic.kahoot.domain.Quiz;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @EntityGraph(attributePaths = "owner")
    List<Quiz> findAll();
    @Query("""
  select q.id, count(que)
  from Quiz q left join q.questions que
  where q.id in :ids
  group by q.id
""")
    List<Object[]> countQuestionsByQuizIds(@Param("ids") List<Long> ids);

    @Query("""
  select distinct q
  from Quiz q
  left join fetch q.owner
  left join fetch q.questions qs
  where q.id = :id
""")
    Optional<Quiz> findDetailsById(Long id);

}
