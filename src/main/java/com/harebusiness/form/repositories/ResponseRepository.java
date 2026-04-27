package com.harebusiness.form.repositories;

import com.harebusiness.form.models.Form;
import com.harebusiness.form.models.Response;
import com.harebusiness.form.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
    boolean existsByFormAndUser(Form form, User user);
    List<Response> findAllByFormId(Long formId);
}
