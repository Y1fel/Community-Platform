-- H2 兼容版建表脚本（MySQL 模式）
-- 移除 MySQL 特有的 CREATE DATABASE / USE / SET NAMES 语法

DROP TABLE IF EXISTS work_order_audit_record;
DROP TABLE IF EXISTS work_order_handle_record;
DROP TABLE IF EXISTS work_order;
DROP TABLE IF EXISTS sys_log;
DROP TABLE IF EXISTS sys_dict;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_org;

-- ----------------------------
-- 组织表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_org (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  parent_id   BIGINT       NULL,
  name        VARCHAR(100) NOT NULL,
  code        VARCHAR(64)  NOT NULL,
  sort        INT          NOT NULL DEFAULT 0,
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_org_code ON sys_org(code);
CREATE INDEX IF NOT EXISTS idx_org_parent ON sys_org(parent_id);

-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_user (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  username    VARCHAR(50)  NOT NULL,
  real_name   VARCHAR(50)  NOT NULL,
  phone       VARCHAR(11)  NOT NULL,
  password    VARCHAR(100) NOT NULL,
  org_id      BIGINT       NOT NULL,
  role        VARCHAR(20)  NOT NULL,
  status      VARCHAR(20)  NOT NULL DEFAULT 'enabled',
  deleted     TINYINT      NOT NULL DEFAULT 0,
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_user_username ON sys_user(username);
CREATE INDEX IF NOT EXISTS idx_user_phone ON sys_user(phone);
CREATE INDEX IF NOT EXISTS idx_user_org ON sys_user(org_id);

-- ----------------------------
-- 数据字典
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_dict (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  dict_type   VARCHAR(64)  NOT NULL,
  dict_name   VARCHAR(100) NOT NULL,
  dict_code   VARCHAR(64)  NOT NULL,
  sort        INT          NOT NULL DEFAULT 0,
  remark      VARCHAR(255) NULL,
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_dict_type_code ON sys_dict(dict_type, dict_code);
CREATE INDEX IF NOT EXISTS idx_dict_type ON sys_dict(dict_type);

-- ----------------------------
-- 操作日志
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_log (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  operator    VARCHAR(50)  NOT NULL,
  type        VARCHAR(20)  NOT NULL,
  module      VARCHAR(50)  NOT NULL,
  content     VARCHAR(500) NOT NULL,
  ip          VARCHAR(64)  NULL,
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_log_operator ON sys_log(operator);
CREATE INDEX IF NOT EXISTS idx_log_type ON sys_log(type);
CREATE INDEX IF NOT EXISTS idx_log_create_time ON sys_log(create_time);

-- ----------------------------
-- 工单主表
-- ----------------------------
CREATE TABLE IF NOT EXISTS work_order (
  id              BIGINT       PRIMARY KEY AUTO_INCREMENT,
  reporter        VARCHAR(50)  NOT NULL,
  phone           VARCHAR(11)  NOT NULL,
  address         VARCHAR(255) NOT NULL,
  description     TEXT         NOT NULL,
  images          TEXT         NULL,
  status          VARCHAR(20)  NOT NULL DEFAULT 'pending',
  handle_type     VARCHAR(20)  NULL,
  handle_result   TEXT         NULL,
  handle_time     TIMESTAMP    NULL,
  handler_id      BIGINT       NULL,
  handler_name    VARCHAR(50)  NULL,
  complete_time   TIMESTAMP    NULL,
  reject_reason   TEXT         NULL,
  audit_time      TIMESTAMP    NULL,
  auditor_id      BIGINT       NULL,
  auditor_name    VARCHAR(50)  NULL,
  create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_wo_status ON work_order(status);
CREATE INDEX IF NOT EXISTS idx_wo_phone ON work_order(phone);
CREATE INDEX IF NOT EXISTS idx_wo_reporter ON work_order(reporter);
CREATE INDEX IF NOT EXISTS idx_wo_create_time ON work_order(create_time);
CREATE INDEX IF NOT EXISTS idx_wo_complete_time ON work_order(complete_time);

-- ----------------------------
-- 工单处理记录
-- ----------------------------
CREATE TABLE IF NOT EXISTS work_order_handle_record (
  id             BIGINT      PRIMARY KEY AUTO_INCREMENT,
  work_order_id  BIGINT      NOT NULL,
  handle_type    VARCHAR(20) NOT NULL,
  handle_result  TEXT        NOT NULL,
  handler_id     BIGINT      NULL,
  handler_name   VARCHAR(50) NULL,
  create_time    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_whr_work_order_id ON work_order_handle_record(work_order_id);
CREATE INDEX IF NOT EXISTS idx_whr_handler_id ON work_order_handle_record(handler_id);

-- ----------------------------
-- 工单审核记录
-- ----------------------------
CREATE TABLE IF NOT EXISTS work_order_audit_record (
  id             BIGINT      PRIMARY KEY AUTO_INCREMENT,
  work_order_id  BIGINT      NOT NULL,
  audit_result   VARCHAR(20) NOT NULL,
  reject_reason  TEXT        NULL,
  auditor_id     BIGINT      NULL,
  auditor_name   VARCHAR(50) NULL,
  create_time    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_war_work_order_id ON work_order_audit_record(work_order_id);
CREATE INDEX IF NOT EXISTS idx_war_auditor_id ON work_order_audit_record(auditor_id);

-- ----------------------------
-- 题目表
-- ----------------------------
CREATE TABLE IF NOT EXISTS quiz_question (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  content     TEXT         NOT NULL,
  options     TEXT         NOT NULL,
  answer      VARCHAR(10)  NOT NULL,
  category    VARCHAR(50)  NOT NULL DEFAULT 'general',
  difficulty  INT          NOT NULL DEFAULT 1,
  score       INT          NOT NULL DEFAULT 10,
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_qq_category ON quiz_question(category);
CREATE INDEX IF NOT EXISTS idx_qq_difficulty ON quiz_question(difficulty);

-- ----------------------------
-- 用户积分表
-- ----------------------------
CREATE TABLE IF NOT EXISTS user_score (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  phone       VARCHAR(11)  NOT NULL,
  real_name   VARCHAR(50)  NOT NULL,
  score       INT          NOT NULL DEFAULT 0,
  total_quiz  INT          NOT NULL DEFAULT 0,
  correct_num INT          NOT NULL DEFAULT 0,
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_us_phone ON user_score(phone);
CREATE INDEX IF NOT EXISTS idx_us_score ON user_score(score);

-- ----------------------------
-- 答题记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS quiz_record (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  phone       VARCHAR(11)  NOT NULL,
  question_id BIGINT       NOT NULL,
  user_answer VARCHAR(10)  NOT NULL,
  is_correct  TINYINT      NOT NULL DEFAULT 0,
  score       INT          NOT NULL DEFAULT 0,
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_qr_phone ON quiz_record(phone);
CREATE INDEX IF NOT EXISTS idx_qr_question_id ON quiz_record(question_id);
CREATE INDEX IF NOT EXISTS idx_qr_create_time ON quiz_record(create_time);

-- ----------------------------
-- 奖品表
-- ----------------------------
CREATE TABLE IF NOT EXISTS prize (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(100) NOT NULL,
  description VARCHAR(255) NULL,
  image_url   VARCHAR(255) NULL,
  cost        INT          NOT NULL DEFAULT 0,
  stock       INT          NOT NULL DEFAULT 0,
  status      VARCHAR(20)  NOT NULL DEFAULT 'active',
  sort        INT          NOT NULL DEFAULT 0,
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_prize_cost ON prize(cost);
CREATE INDEX IF NOT EXISTS idx_prize_status ON prize(status);

-- ----------------------------
-- 兑换记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS exchange_record (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  phone       VARCHAR(11)  NOT NULL,
  real_name   VARCHAR(50)  NOT NULL,
  prize_id    BIGINT       NOT NULL,
  prize_name  VARCHAR(100) NOT NULL,
  cost        INT          NOT NULL DEFAULT 0,
  status      VARCHAR(20)  NOT NULL DEFAULT 'pending',
  remark      VARCHAR(255) NULL,
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_er_phone ON exchange_record(phone);
CREATE INDEX IF NOT EXISTS idx_er_prize_id ON exchange_record(prize_id);
CREATE INDEX IF NOT EXISTS idx_er_status ON exchange_record(status);
