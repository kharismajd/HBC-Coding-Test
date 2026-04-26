package com.harebusiness.form.repositories;

import com.harebusiness.form.models.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {

    boolean existsBySlug(String slug);

    List<Form> findAllByCreatorIdOrderByIdDesc(Long creatorId);

    Optional<Form> findBySlug(String slug);
}
