package com.campus.errand.module.auth;

import com.campus.errand.common.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/mock-login")
    public Result<LoginResponse> mockLogin(
            @RequestBody MockLoginRequest request) {

        return Result.ok(authService.mockLogin(request));
    }
}
