package io.github.douglasliebl.api.resource.exception;

import io.github.douglasliebl.api.services.exceptions.DataIntegrityViolationException;
import io.github.douglasliebl.api.services.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class ResourceExceptionHandlerTest {

    @InjectMocks
    ResourceExceptionHandler resourceExceptionHandler;

    @Test
    @DisplayName("Should return an Response Entity")
    public void whenObjectNotFoundExceptionThenReturnAnResponseEntity() {
        // given
        ResponseEntity<StandardError> response = resourceExceptionHandler
                .handleObjectNotFoundException(
                        new ObjectNotFoundException("Object not found"),
                        new MockHttpServletRequest());

        // then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(StandardError.class, response.getBody().getClass());
        assertEquals("Object not found", response.getBody().getError());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Should return an Response Entity")
    public void whenDataIntegrityViolationExceptionThenReturnAnResponseEntity() {
        // given
        ResponseEntity<StandardError> response = resourceExceptionHandler
                .handleDataIntegrityViolationException(
                        new DataIntegrityViolationException("email already used"),
                        new MockHttpServletRequest());

        // then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(StandardError.class, response.getBody().getClass());
        assertEquals("email already used", response.getBody().getError());
        assertEquals(400, response.getBody().getStatus());
    }

}