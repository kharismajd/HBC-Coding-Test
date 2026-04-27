package com.harebusiness.form.repositories;

import com.harebusiness.form.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByIdAndFormId(Long id, Long formId);
}
