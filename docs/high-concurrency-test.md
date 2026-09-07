# 高并发抢单测试记录

## 测试目标

验证多个骑手同时抢同一个任务时，系统是否能够保证任务数据一致性。


## 技术方案

采用 MySQL CAS 条件更新实现抢单控制。


核心 SQL：

```sql
UPDATE task
SET rider_id = ?,
    status = 2,
    accept_time = NOW()
WHERE id = ?
AND status = 1;


试数据

任务：

task_id = 2
status = 1
rider_id = 0

初始状态：

待接单
未分配骑手
测试过程
骑手4请求

接口：

POST /api/tasks/2/accept

结果：

affectedRows = 1

数据库变化：

rider_id = 4
status = 2

结果：

抢单成功。

骑手5请求

接口：

POST /api/tasks/2/accept

结果：

affectedRows = 0

返回：

任务已被抢走或当前不可接单

结果：

抢单失败。

测试结论

CAS机制有效避免：

重复抢单
多骑手绑定同一任务
数据状态冲突

当前方案满足基础生产环境并发一致性要求。