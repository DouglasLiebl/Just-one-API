package io.github.douglasliebl.api.services.impl;

import io.github.douglasliebl.api.domain.User;
import io.github.douglasliebl.api.domain.dto.UserDTO;
import io.github.douglasliebl.api.repositories.UserRepository;
import io.github.douglasliebl.api.services.UserService;
import io.github.douglasliebl.api.services.exceptions.DataIntegrityViolationException;
import io.github.douglasliebl.api.services.exceptions.ObjectNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class UserServiceImplTest {

    UserService service;

    @MockBean
    UserRepository repository;

    @MockBean
    ModelMapper mapper;

    @BeforeEach
    public void setUp() {
        this.service = new UserServiceImpl(repository, mapper);
    }

    @Test
    @DisplayName("Should return a user")
    public void whenFindByIdThenReturnAnUserInstance() {
        // given
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@gmail.com")
                .password("password").build();

        // when
        Mockito.when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        User response = service.findById(1L);

        // then
        assertNotNull(response);
        assertEquals(User.class, response.getClass());
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getPassword(), response.getPassword());
    }

    @Test
    @DisplayName("Should throw an Exception when cannot found user")
    public void whenFindByIdThenReturnAnObjectNotFoundException() {
        // given
        Long id = 1L;

        // when
        Mockito.when(repository.findById(Mockito.anyLong()))
                .thenThrow(new ObjectNotFoundException("Object not found"));

        Throwable exception = Assertions
                .catchThrowable(() -> service.findById(id));

        // then
        assertEquals(ObjectNotFoundException.class, exception.getClass());
        assertEquals("Object not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should return an list of all users")
    public void whenFindAllThenReturnAnListOfUsers() {
        // given
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@gmail.com")
                .password("password").build();

        // when
        Mockito.when(repository.findAll())
                .thenReturn(List.of(user));

        List<User> response = service.findAll();

        // then
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(User.class,  response.get(0).getClass());
        assertEquals(1L, response.get(0).getId());
    }

    @Test
    @DisplayName("Should create a user")
    public void whenCreateThenReturnSuccess() {
        // given
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@gmail.com")
                .password("password").build();

        UserDTO dto = UserDTO.builder()
                .name("User")
                .email("user@gmail.com")
                .password("password").build();

        // when
        Mockito.when(repository.save(Mockito.any()))
                .thenReturn(user);

        User response = service.create(dto);

        // then
        assertNotNull(response);
        assertEquals(User.class, response.getClass());
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getPassword(), response.getPassword());
    }

    @Test
    @DisplayName("Should throw an Exception when email already used")
    public void whenCreateThenReturnAnDataIntegrityValidationException() {
        // given
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@gmail.com")
                .password("password").build();

        // when
        Mockito.when(repository.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(user));

        Throwable exception = Assertions
                .catchThrowable(() -> service.create(UserDTO.builder().email("user@gmail.com").build()));

        // then
        assertEquals(DataIntegrityViolationException.class, exception.getClass());
        assertEquals("Email already used", exception.getMessage());
    }

    @Test
    @DisplayName("Should update an user")
    public void whenUpdateThenReturnSuccess() {
        // given
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@gmail.com")
                .password("password").build();

        UserDTO dto = UserDTO.builder()
                .name("User")
                .email("user@gmail.com")
                .password("password").build();

        // when
        Mockito.when(repository.save(Mockito.any()))
                .thenReturn(user);

        User response = service.update(dto);

        // then
        assertNotNull(response);
        assertEquals(User.class, response.getClass());
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getPassword(), response.getPassword());
    }

    @Test
    @DisplayName("Should throw an exception when email already used to update")
    public void whenUpdateThenReturnAnDataIntegrityValidationException() {
        // given
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@gmail.com")
                .password("password").build();

        // when
        Mockito.when(repository.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(user));

        Throwable exception = Assertions
                .catchThrowable(() -> service.update(UserDTO.builder().id(2L).email("user@gmail.com").build()));

        // then
        assertEquals(DataIntegrityViolationException.class, exception.getClass());
        assertEquals("Email already used", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete a user")
    public void whenDeleteWithSuccess() {
        // given
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@gmail.com")
                .password("password").build();

        // when
        Mockito.when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> service.delete(1L));

        // then
        Mockito.verify(repository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw a exception when deleting not registered user")
    public void whenDeleteThenReturnAnObjectNotFoundException() {
        // given
        Long id = 1L;

        // when
        Throwable exception = Assertions
                .catchThrowable(() -> service.delete(id));

        // then
        Mockito.verify(repository, Mockito.never()).deleteById(id);
        assertEquals(ObjectNotFoundException.class, exception.getClass());
        assertEquals("Object not found", exception.getMessage());
    }
}