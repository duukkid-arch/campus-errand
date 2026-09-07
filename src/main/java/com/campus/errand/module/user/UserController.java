package com.campus.errand.module.user;

import com.campus.errand.common.BizException;
import com.campus.errand.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public Result<User> me(HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        User user = userMapper.selectById(userId);

        if (user == null) {
            throw new BizException("用户不存在");
        }

        return Result.ok(user);
    }
}
