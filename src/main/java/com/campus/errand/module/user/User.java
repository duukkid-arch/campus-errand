package com.campus.errand.module.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String openid;
    private String nickname;
    private String avatar;
    private String phone;
    private Integer role;
    private Integer riderStatus;
    private Integer finishCount;
    private LocalDateTime createTime;
}
