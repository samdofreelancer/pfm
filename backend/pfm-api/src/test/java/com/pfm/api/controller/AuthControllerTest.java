package com.pfm.api.controller;

import com.pfm.application.auth.command.DeleteUserCommand;
import com.pfm.application.auth.handler.ChangePasswordHandler;
import com.pfm.application.auth.handler.DeleteUserHandler;
import com.pfm.application.auth.handler.GetProfileHandler;
import com.pfm.application.auth.handler.LoginHandler;
import com.pfm.application.auth.handler.RefreshTokenHandler;
import com.pfm.application.auth.handler.RegisterHandler;
import com.pfm.application.auth.handler.UpdateProfileHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private LoginHandler loginHandler;

    @Mock
    private RegisterHandler registerHandler;

    @Mock
    private RefreshTokenHandler refreshTokenHandler;

    @Mock
    private DeleteUserHandler deleteUserHandler;

    @Mock
    private GetProfileHandler getProfileHandler;

    @Mock
    private UpdateProfileHandler updateProfileHandler;

    @Mock
    private ChangePasswordHandler changePasswordHandler;

    @InjectMocks
    private AuthController controller;

    @Test
    void deleteUser_ShouldDeleteCurrentAuthenticatedUserOnly() {
        var response = controller.deleteUser();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deleteUserHandler).handle(new DeleteUserCommand());
    }
}
