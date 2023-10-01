package io.github.douglasliebl.api.services.impl;

import io.github.douglasliebl.api.domain.User;
import io.github.douglasliebl.api.repositories.UserRepository;
import io.github.douglasliebl.api.services.UserService;
import io.github.douglasliebl.api.services.exceptions.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public User findById(Long id) {
        Optional<User> response = repository.findById(id);
        return response.orElseThrow(() -> new ObjectNotFoundException("Object not found"));
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }
}
