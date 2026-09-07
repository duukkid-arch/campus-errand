package com.campus.errand.module.task;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("task")
public class Task {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskNo;
    private Integer type;
    private String title;
    private String remark;
    private String pickupAddr;
    private String deliverAddr;
    private BigDecimal reward;
    private LocalDateTime expectTime;
    private Long publisherId;
    private Long riderId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime acceptTime;
    private LocalDateTime pickupTime;
    private LocalDateTime finishTime;
}
