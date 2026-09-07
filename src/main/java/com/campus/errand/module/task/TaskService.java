package com.campus.errand.module.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.errand.common.BizException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TaskService {

    private final TaskMapper taskMapper;
    private final TaskStatusLogMapper taskStatusLogMapper;

    public TaskService(
            TaskMapper taskMapper,
            TaskStatusLogMapper taskStatusLogMapper) {
        this.taskMapper = taskMapper;
        this.taskStatusLogMapper = taskStatusLogMapper;
    }

    @Transactional
    public Task createTask(Long userId, CreateTaskRequest request) {

        if (request.getType() == null
                || request.getType() < 1
                || request.getType() > 4) {
            throw new BizException("任务类型不正确");
        }

        if (request.getTitle() == null
                || request.getTitle().isBlank()) {
            throw new BizException("任务标题不能为空");
        }

        if (request.getPickupAddr() == null
                || request.getPickupAddr().isBlank()) {
            throw new BizException("取件地点不能为空");
        }

        if (request.getDeliverAddr() == null
                || request.getDeliverAddr().isBlank()) {
            throw new BizException("送达地点不能为空");
        }

        if (request.getReward() == null
                || request.getReward().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("悬赏金额必须大于0");
        }

        Task task = new Task();

        task.setTaskNo(generateTaskNo());
        task.setType(request.getType());
        task.setTitle(request.getTitle());
        task.setRemark(
                request.getRemark() == null
                        ? ""
                        : request.getRemark()
        );
        task.setPickupAddr(request.getPickupAddr());
        task.setDeliverAddr(request.getDeliverAddr());
        task.setReward(request.getReward());
        task.setExpectTime(request.getExpectTime());

        task.setPublisherId(userId);
        task.setRiderId(0L);
        task.setStatus(1);

        taskMapper.insert(task);

        TaskStatusLog log = new TaskStatusLog();
        log.setTaskId(task.getId());
        log.setFromStatus(0);
        log.setToStatus(1);
        log.setEvent("CREATE");
        log.setOperatorId(userId);
        log.setOperatorRole("PUBLISHER");
        log.setRemark("发布任务");

        taskStatusLogMapper.insert(log);

        return task;
    }

    public List<Task> getTaskPool() {
        return taskMapper.selectList(
                new LambdaQueryWrapper<Task>()
                        .eq(Task::getStatus, 1)
                        .orderByDesc(Task::getCreateTime)
        );
    }

    @Transactional
    public Task acceptTask(Long taskId, Long riderId) {

        Task task = taskMapper.selectById(taskId);

        if (task == null) {
            throw new BizException("任务不存在");
        }

        if (task.getPublisherId().equals(riderId)) {
            throw new BizException("不能接自己发布的任务");
        }

        int affectedRows = taskMapper.acceptTask(taskId, riderId);

        if (affectedRows == 0) {
            throw new BizException("任务已被抢走或当前不可接单");
        }

        TaskStatusLog log = new TaskStatusLog();
        log.setTaskId(taskId);
        log.setFromStatus(1);
        log.setToStatus(2);
        log.setEvent("ACCEPT");
        log.setOperatorId(riderId);
        log.setOperatorRole("RIDER");
        log.setRemark("骑手接单");

        taskStatusLogMapper.insert(log);

        return taskMapper.selectById(taskId);
    }


    @Transactional
    public Task pickupTask(Long taskId, Long riderId) {

        Task task = taskMapper.selectById(taskId);

        if (task == null) {
            throw new BizException("任务不存在");
        }

        if (!task.getRiderId().equals(riderId)) {
            throw new BizException("只有接单骑手可以开始配送");
        }

        int affectedRows = taskMapper.pickupTask(taskId, riderId);

        if (affectedRows == 0) {
            throw new BizException("任务当前状态不能开始配送");
        }

        TaskStatusLog log = new TaskStatusLog();

        log.setTaskId(taskId);
        log.setFromStatus(2);
        log.setToStatus(3);
        log.setEvent("PICKUP");
        log.setOperatorId(riderId);
        log.setOperatorRole("RIDER");
        log.setRemark("骑手开始配送");

        taskStatusLogMapper.insert(log);

        return taskMapper.selectById(taskId);
    }



    private String generateTaskNo() {
        return "T" + System.currentTimeMillis();
    }

}
