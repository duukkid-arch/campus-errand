package com.campus.errand.module.task;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateTaskRequest {

    private Integer type;
    private String title;
    private String remark;
    private String pickupAddr;
    private String deliverAddr;
    private BigDecimal reward;
    private LocalDateTime expectTime;
}
