package com.harebusiness.form.models;

import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

public class AuthenticatedUser extends User {
    private final com.harebusiness.form.models.User userEntity;

    public AuthenticatedUser(com.harebusiness.form.models.User userEntity) {
        super(userEntity.getId().toString(), userEntity.getPassword(), new ArrayList<>());
        this.userEntity = userEntity;
    }

    public com.harebusiness.form.models.User getUserEntity() {
        return userEntity;
    }

    public Long getId() {
        return userEntity.getId();
    }
}
