package io.github.douglasliebl.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.douglasliebl.api.domain.User;
import io.github.douglasliebl.api.domain.dto.UserDTO;
import io.github.douglasliebl.api.services.UserService;
import io.github.douglasliebl.api.services.exceptions.DataIntegrityViolationException;
import io.github.douglasliebl.api.services.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    static final String USER_API = "/user";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService service;

    @MockBean
    ModelMapper mapper;

    @Test
    @DisplayName("Should obtain user details")
    public void whenFindByIdThenReturnAnUser() throws Exception {
        // given
        Long id = 1L;
        User user = User.builder()
                .id(id)
                .name("User")
                .email("user@gmail.com")
                .password("password").build();

        UserDTO dto = UserDTO.builder()
                .id(id)
                .name("User")
                .email("user@gmail.com")
                .password("password").build();

        BDDMockito.given(service.findById(id))
                .willReturn(user);
        BDDMockito.given(mapper.map(Mockito.any(), Mockito.any()))
                .willReturn(dto);

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(USER_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value(user.getName()))
                .andExpect(jsonPath("email").value(user.getEmail()));
    }

    @Test
    @DisplayName("Should throw an Exception when user cannot be found")
    public void whenFindByIdThenReturnAnObjectNotFoundException() throws Exception {
        // given
        Long id = 1L;

        BDDMockito.given(service.findById(id))
                .willThrow(new ObjectNotFoundException("Object not found"));

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(USER_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("error").value("Object not found"))
                .andExpect(jsonPath("path").value("/user/1"));
    }

    @Test
    @DisplayName("Should return all users")
    public void whenFindAllThenReturnSuccess() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .build();

        UserDTO dto = UserDTO.builder()
                .id(1L)
                .build();

        BDDMockito.given(service.findAll())
                .willReturn(List.of(user));
        BDDMockito.given(mapper.map(Mockito.any(), Mockito.any()))
                .willReturn(dto);

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(USER_API)
                .accept(MediaType.APPLICATION_JSON);

        List<User> response = service.findAll();

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
        assertNotNull(response);
    }

    @Test
    @DisplayName("Should create a user")
    public void whenCreateUserThenSuccess() throws Exception {
        // given
        UserDTO dto = UserDTO.builder().build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(service.create(Mockito.any(UserDTO.class)))
                .willReturn(User.builder().id(1L).build());

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(USER_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should throw an exception when trying to register an already used email")
    public void whenCreateUserThenReturnDataIntegrityViolationException() throws Exception {
        // given
        UserDTO dto = UserDTO.builder()
                .email("user@email.com").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(service.create(Mockito.any(UserDTO.class)))
                .willThrow(new DataIntegrityViolationException("email already used"));

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(USER_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("email already used"))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("path").value("/user"));
    }

    @Test
    @DisplayName("Should update a user details")
    public void whenUpdateThenSuccess() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .name("User2")
                .build();

        UserDTO dto = UserDTO.builder()
                .id(1L)
                .name("User2")
                .build();

        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(service.update(Mockito.any(UserDTO.class)))
                .willReturn(user);
        BDDMockito.given(mapper.map(Mockito.any(), Mockito.any()))
                .willReturn(dto);

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(USER_API.concat("/" + 1L))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(dto.getId()))
                .andExpect(jsonPath("name").value(dto.getName()))
                .andExpect(jsonPath("email").isEmpty());
    }

    @Test
    @DisplayName("Should throw an exception when trying to register an already used email")
    public void whenUpdateThenReturnDataIntegrityViolationException() throws Exception {
        // given
        UserDTO dto = UserDTO.builder()
                .id(1L)
                .email("user@email.com").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(service.update(Mockito.any(UserDTO.class)))
                .willThrow(new DataIntegrityViolationException("email already used"));

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(USER_API.concat("/" + 1L))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("email already used"))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("path").value("/user/1"));
    }

    @Test
    @DisplayName("Should delete a user")
    public void whenDeleteThenReturnSuccess() throws Exception {
        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(USER_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
        Mockito.verify(service, Mockito.times(1)).delete(1L);
    }
}