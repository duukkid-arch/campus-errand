-- 校园抢单助手 建表脚本
-- 容器第一次启动时会自动执行本文件（挂在 /docker-entrypoint-initdb.d）
SET NAMES utf8mb4;

-- ============ 1. 用户表 ============
-- 学生和骑手是同一批人，用 role 区分，不单开骑手表
CREATE TABLE IF NOT EXISTS `user` (
  id            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  openid        VARCHAR(64)  NOT NULL COMMENT '微信 openid',
  nickname      VARCHAR(50)  NOT NULL DEFAULT '',
  avatar        VARCHAR(255) NOT NULL DEFAULT '',
  phone         VARCHAR(20)  NOT NULL DEFAULT '',
  role          TINYINT      NOT NULL DEFAULT 1 COMMENT '1普通用户 2骑手 3管理员',
  rider_status  TINYINT      NOT NULL DEFAULT 0 COMMENT '0休息中 1接单中（仅骑手用）',
  finish_count  INT          NOT NULL DEFAULT 0 COMMENT '完成单数',
  create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============ 2. 任务表（核心）============
CREATE TABLE IF NOT EXISTS task (
  id            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  task_no       VARCHAR(32)  NOT NULL COMMENT '业务单号，对外展示用',
  type          TINYINT      NOT NULL COMMENT '1代取快递 2带饭 3代买 4其他',
  title         VARCHAR(50)  NOT NULL,
  remark        VARCHAR(255) NOT NULL DEFAULT '',
  pickup_addr   VARCHAR(100) NOT NULL COMMENT '取件地点',
  deliver_addr  VARCHAR(100) NOT NULL COMMENT '送达地点',
  reward        DECIMAL(6,2) NOT NULL COMMENT '悬赏金额',
  expect_time   DATETIME     NULL COMMENT '期望送达时间',
  publisher_id  BIGINT UNSIGNED NOT NULL COMMENT '发单人 id',
  rider_id      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0表示未接单',
  status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1待接单 2已接单 3配送中 4已完成 5已取消',
  create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  accept_time   DATETIME     NULL,
  pickup_time   DATETIME     NULL,
  finish_time   DATETIME     NULL,
  UNIQUE KEY uk_task_no (task_no),
  KEY idx_pool (status, create_time),           -- 订单池：按状态筛 + 按时间排
  KEY idx_rider (rider_id, status),             -- 骑手看自己的任务
  KEY idx_publisher (publisher_id, create_time) -- 学生看自己发的单
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- ============ 3. 任务状态流转日志 ============
-- 每次状态变更插一条，谁在什么时候把单子推到哪一步全程可回溯
CREATE TABLE IF NOT EXISTS task_status_log (
  id            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  task_id       BIGINT UNSIGNED NOT NULL,
  from_status   TINYINT      NOT NULL,
  to_status     TINYINT      NOT NULL,
  event         VARCHAR(20)  NOT NULL COMMENT 'ACCEPT/PICKUP/FINISH/CANCEL/GIVEUP/TIMEOUT',
  operator_id   BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0表示系统',
  operator_role VARCHAR(16)  NOT NULL COMMENT '操作者角色 USER/RIDER/SYSTEM/ADMIN',
  remark        VARCHAR(255) NOT NULL DEFAULT '',
  create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_task (task_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务状态流转日志';

-- ============ 4. 常用地址 ============
CREATE TABLE IF NOT EXISTS address (
  id           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id      BIGINT UNSIGNED NOT NULL,
  label        VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '标签，如宿舍/教学楼',
  detail       VARCHAR(100) NOT NULL,
  is_default   TINYINT      NOT NULL DEFAULT 0,
  create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='常用地址';

-- ============ 5. 评价 ============
CREATE TABLE IF NOT EXISTS review (
  id            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  task_id       BIGINT UNSIGNED NOT NULL,
  from_user_id  BIGINT UNSIGNED NOT NULL,
  to_user_id    BIGINT UNSIGNED NOT NULL,
  score         TINYINT      NOT NULL COMMENT '1~5 分',
  content       VARCHAR(255) NOT NULL DEFAULT '',
  create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_task (task_id),
  KEY idx_to_user (to_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价';

-- 放一条测试用户，方便第一周联调时当发单人
INSERT INTO `user` (openid, nickname, role) VALUES ('test_openid_001', '测试同学', 1)
ON DUPLICATE KEY UPDATE nickname = nickname;
