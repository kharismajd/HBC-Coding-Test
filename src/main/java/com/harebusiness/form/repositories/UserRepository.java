package com.harebusiness.form.repositories;

import com.harebusiness.form.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdAndIsDeletedFalse(Long id);

    Optional<User> findByEmailAndIsDeletedFalse(String email);
}
