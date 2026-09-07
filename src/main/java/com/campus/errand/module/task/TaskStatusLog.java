package com.campus.errand.module.task;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task_status_log")
public class TaskStatusLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;
    private Integer fromStatus;
    private Integer toStatus;
    private String event;
    private Long operatorId;
    private String operatorRole;
    private String remark;
    private LocalDateTime createTime;
}
