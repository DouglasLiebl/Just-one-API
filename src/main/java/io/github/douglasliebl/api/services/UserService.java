package io.github.douglasliebl.api.services;

import io.github.douglasliebl.api.domain.User;

import java.util.List;

public interface UserService {

    User findById(Long id);

    List<User> findAll();
}
