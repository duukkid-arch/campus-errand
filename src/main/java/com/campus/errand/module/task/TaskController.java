package com.campus.errand.module.task;

import com.campus.errand.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @PostMapping
    public Result<Task> createTask(
            @RequestBody CreateTaskRequest request,
            HttpServletRequest httpRequest) {

        Long userId =
                (Long) httpRequest.getAttribute("userId");

        return Result.ok(
                taskService.createTask(userId, request)
        );
    }


    @GetMapping("/pool")
    public Result<List<Task>> getTaskPool() {

        return Result.ok(
                taskService.getTaskPool()
        );
    }


    @PostMapping("/{id}/accept")
    public Result<Task> acceptTask(
            @PathVariable("id") Long taskId,
            HttpServletRequest httpRequest) {


        Long riderId =
                (Long) httpRequest.getAttribute("userId");


        return Result.ok(
                taskService.acceptTask(taskId, riderId)
        );
    }


    /**
     * 开始配送
     * 状态：
     * 2 已接单
     * ↓
     * 3 配送中
     */
    @PostMapping("/{id}/pickup")
    public Result<Task> pickupTask(
            @PathVariable("id") Long taskId,
            HttpServletRequest httpRequest) {


        Long riderId =
                (Long) httpRequest.getAttribute("userId");


        return Result.ok(
                taskService.pickupTask(taskId, riderId)
        );
    }
}
