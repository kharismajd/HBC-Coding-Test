package com.harebusiness.form.repositories;

import com.harebusiness.form.models.AllowedDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllowedDomainRepository extends JpaRepository<AllowedDomain, Long> {

}
