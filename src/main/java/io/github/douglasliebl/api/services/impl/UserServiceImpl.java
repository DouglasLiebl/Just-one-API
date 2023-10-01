package io.github.douglasliebl.api.services.impl;

import io.github.douglasliebl.api.domain.User;
import io.github.douglasliebl.api.domain.dto.UserDTO;
import io.github.douglasliebl.api.repositories.UserRepository;
import io.github.douglasliebl.api.services.UserService;
import io.github.douglasliebl.api.services.exceptions.DataIntegrityViolationException;
import io.github.douglasliebl.api.services.exceptions.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final ModelMapper mapper;

    @Override
    public User findById(Long id) {
        Optional<User> response = repository.findById(id);
        return response.orElseThrow(() -> new ObjectNotFoundException("Object not found"));
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User create(UserDTO request) {
        emailVerify(request);
        return repository.save(mapper.map(request, User.class));
    }

    @Override
    public User update(UserDTO request) {
        emailVerify(request);
        return repository.save(mapper.map(request, User.class));
    }

    @Override
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    private void emailVerify(UserDTO request) {
        Optional<User> user = repository.findByEmail(request.getEmail());

        if(user.isPresent() && !user.get().getEmail().equals(request.getEmail()))
            throw new DataIntegrityViolationException("Email already used");
    }
}
