package com.campus.errand.module.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.errand.common.BizException;
import com.campus.errand.module.user.User;
import com.campus.errand.module.user.UserMapper;
import com.campus.errand.security.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public LoginResponse mockLogin(MockLoginRequest request) {

        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new BizException("code不能为空");
        }

        String openid = "mock_openid_" + request.getCode().trim();

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getOpenid, openid)
        );

        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname(
                    request.getNickname() == null
                            || request.getNickname().isBlank()
                            ? "新用户"
                            : request.getNickname()
            );
            user.setAvatar("");
            user.setPhone("");
            user.setRole(1);
            user.setRiderStatus(0);
            user.setFinishCount(0);

            userMapper.insert(user);
        }

        String token = jwtUtil.generateToken(user.getId());

        return new LoginResponse(token, user);
    }
}
