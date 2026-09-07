package com.campus.errand.module.auth;

import lombok.Data;

@Data
public class MockLoginRequest {
    private String code;
    private String nickname;
}
