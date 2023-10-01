package io.github.douglasliebl.api.services;

import io.github.douglasliebl.api.domain.User;
import io.github.douglasliebl.api.domain.dto.UserDTO;

import java.util.List;

public interface UserService {

    User findById(Long id);

    List<User> findAll();

    User create(UserDTO request);

    User update(UserDTO request);

    void delete(Long id);
}
