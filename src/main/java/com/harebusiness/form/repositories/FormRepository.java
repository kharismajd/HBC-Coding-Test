package com.harebusiness.form.repositories;

import com.harebusiness.form.models.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {

    boolean existsBySlug(String slug);
}
