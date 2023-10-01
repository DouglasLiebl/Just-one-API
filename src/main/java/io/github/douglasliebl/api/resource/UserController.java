package io.github.douglasliebl.api.resource;

import io.github.douglasliebl.api.domain.dto.UserDTO;
import io.github.douglasliebl.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    static final String ID = "/{id}";

    private final UserService service;
    private final ModelMapper mapper;

    @GetMapping(value = ID)
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(mapper.map(service.findById(id), UserDTO.class));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAllUsers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.findAll()
                        .stream()
                        .map(x -> mapper.map(x, UserDTO.class)).toList());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO request) {
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(ID)
                .buildAndExpand(service.create(request).getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = ID)
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @RequestBody UserDTO request) {
        request.setId(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(mapper.map(service.update(request), UserDTO.class));
    }

    @DeleteMapping(value = ID)
    public ResponseEntity<UserDTO> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
