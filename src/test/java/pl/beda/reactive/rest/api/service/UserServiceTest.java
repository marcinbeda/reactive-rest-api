package pl.beda.reactive.rest.api.service;

import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import pl.beda.reactive.rest.api.dto.input.InputUserDto;
import pl.beda.reactive.rest.api.model.User;
import pl.beda.reactive.rest.api.repository.UserRepository;
import pl.beda.reactive.rest.api.utils.ResponseUtils;

import static org.mockito.Mockito.doReturn;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoutingContext event;

    @Mock
    private HttpServerRequest request;

    @Mock
    private HttpServerResponse response;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        doReturn(request).when(event).request();
        doReturn(response).when(event).response();
    }

    @Test
    @DisplayName("User creation.")
    void givenUserDataWhenTryToCreateUserThenCreateUserSuccessfully() {
        try (MockedStatic<ResponseUtils> responseUtilsMockedStatic = Mockito.mockStatic(ResponseUtils.class)) {
            responseUtilsMockedStatic.when(() -> ResponseUtils.buildNoContentResponse(event)).thenAnswer((Answer<Void>) invocation -> null);

            Mockito.when(userRepository.findByLogin("test"))
                    .thenReturn(Future.future(jsonObjectPromise -> jsonObjectPromise.complete(null)));

            Mockito.when(userRepository.insert(Mockito.any(User.class)))
                    .thenReturn(Future.future(jsonObjectPromise -> jsonObjectPromise.complete("6569be83f00edd19430eb77c")));

            UserService userService = new UserService(userRepository);

            userService.create(event, new InputUserDto("test", "Examplepassword1."));

            responseUtilsMockedStatic.verify(() -> ResponseUtils.buildNoContentResponse(event));
        }
    }

    @Test
    @DisplayName("User credentials check.")
    void givenUserCredentialsWhenLoginThenAuthSuccessfully() {
        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(16, 32, 1, 60000, 10);
        String password = "Examplepassword1.";

        Mockito.when(userRepository.findByLogin("test"))
                .thenReturn(Future.future(jsonObjectPromise -> jsonObjectPromise.complete(new JsonObject().put("password", encoder.encode(password)))));

        Mockito.when(userRepository.insert(Mockito.any(User.class)))
                .thenReturn(Future.future(jsonObjectPromise -> jsonObjectPromise.complete("6569be83f00edd19430eb77c")));

        UserService userService = new UserService(userRepository);

        userService.validateCredentials(event, new InputUserDto("test", password));

        Mockito.verify(event).next();
    }

}
