package com.campus.errand.module.ping;

import com.campus.errand.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 连通测试接口。第一周唯一的目标就是让它被前端和 curl 打通。
 */
@RestController
@RequestMapping("/api")
public class PingController {

    @GetMapping("/ping")
    public Result<Map<String, Object>> ping() {
        Map<String, Object> data = new HashMap<>();
        data.put("msg", "pong");
        data.put("time", System.currentTimeMillis());
        return Result.ok(data);
    }
}
