package com.campus.errand.module.task;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {


    @Update("""
            UPDATE task
            SET rider_id = #{riderId},
                status = 2,
                accept_time = NOW()
            WHERE id = #{taskId}
              AND status = 1
            """)
    int acceptTask(
            @Param("taskId") Long taskId,
            @Param("riderId") Long riderId
    );


    @Update("""
            UPDATE task
            SET status = 3,
                pickup_time = NOW()
            WHERE id = #{taskId}
              AND rider_id = #{riderId}
              AND status = 2
            """)
    int pickupTask(
            @Param("taskId") Long taskId,
            @Param("riderId") Long riderId
    );
}
