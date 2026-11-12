-- 社区民情收集平台 - 完整建表脚本
-- 系统管理表 + 工单表
-- Windows 命令行建议先执行 SET NAMES utf8mb4;

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS community DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE community;

-- 先删依赖表，再删主表
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
CREATE TABLE sys_org (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  parent_id   BIGINT       NULL,
  name        VARCHAR(100) NOT NULL,
  code        VARCHAR(64)  NOT NULL,
  sort        INT          NOT NULL DEFAULT 0,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_code (code),
  KEY idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE sys_user (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  username    VARCHAR(50)  NOT NULL,
  real_name   VARCHAR(50)  NOT NULL,
  phone       VARCHAR(11)  NOT NULL,
  password    VARCHAR(100) NOT NULL,
  org_id      BIGINT       NOT NULL,
  role        VARCHAR(20)  NOT NULL,
  status      VARCHAR(20)  NOT NULL DEFAULT 'enabled',
  deleted     TINYINT      NOT NULL DEFAULT 0,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_username (username),
  KEY idx_phone (phone),
  KEY idx_org (org_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- 数据字典
-- ----------------------------
CREATE TABLE sys_dict (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  dict_type   VARCHAR(64)  NOT NULL,
  dict_name   VARCHAR(100) NOT NULL,
  dict_code   VARCHAR(64)  NOT NULL,
  sort        INT          NOT NULL DEFAULT 0,
  remark      VARCHAR(255) NULL,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_type_code (dict_type, dict_code),
  KEY idx_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- 操作日志
-- ----------------------------
CREATE TABLE sys_log (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  operator    VARCHAR(50)  NOT NULL,
  type        VARCHAR(20)  NOT NULL,
  module      VARCHAR(50)  NOT NULL,
  content     VARCHAR(500) NOT NULL,
  ip          VARCHAR(64)  NULL,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_operator (operator),
  KEY idx_type (type),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- 工单主表
-- ----------------------------
CREATE TABLE work_order (
  id              BIGINT       PRIMARY KEY AUTO_INCREMENT,
  reporter        VARCHAR(50)  NOT NULL,
  phone           VARCHAR(11)  NOT NULL,
  address         VARCHAR(255) NOT NULL,
  description     TEXT         NOT NULL,
  images          TEXT         NULL,
  status          VARCHAR(20)  NOT NULL DEFAULT 'pending',
  handle_type     VARCHAR(20)  NULL,
  handle_result   TEXT         NULL,
  handle_time     DATETIME     NULL,
  handler_id      BIGINT       NULL,
  handler_name    VARCHAR(50)  NULL,
  complete_time   DATETIME     NULL,
  reject_reason   TEXT         NULL,
  audit_time      DATETIME     NULL,
  auditor_id      BIGINT       NULL,
  auditor_name    VARCHAR(50)  NULL,
  create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_status (status),
  KEY idx_phone (phone),
  KEY idx_reporter (reporter),
  KEY idx_create_time (create_time),
  KEY idx_complete_time (complete_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- 工单处理记录
-- ----------------------------
CREATE TABLE work_order_handle_record (
  id             BIGINT      PRIMARY KEY AUTO_INCREMENT,
  work_order_id  BIGINT      NOT NULL,
  handle_type    VARCHAR(20) NOT NULL,
  handle_result  TEXT        NOT NULL,
  handler_id     BIGINT      NULL,
  handler_name   VARCHAR(50) NULL,
  create_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_work_order_id (work_order_id),
  KEY idx_handler_id (handler_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- 工单审核记录
-- ----------------------------
CREATE TABLE work_order_audit_record (
  id             BIGINT      PRIMARY KEY AUTO_INCREMENT,
  work_order_id  BIGINT      NOT NULL,
  audit_result   VARCHAR(20) NOT NULL,
  reject_reason  TEXT        NULL,
  auditor_id     BIGINT      NULL,
  auditor_name   VARCHAR(50) NULL,
  create_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_work_order_id (work_order_id),
  KEY idx_auditor_id (auditor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- 初始化数据
-- ----------------------------
INSERT INTO sys_org (id, parent_id, name, code, sort) VALUES
(1, NULL, 'Community', 'community', 0);

INSERT INTO sys_user (username, real_name, phone, password, org_id, role, status, deleted) VALUES
('admin', 'Admin', '13800000000',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sLTTplI5N6.',
 1, 'admin', 'enabled', 0);

INSERT INTO sys_dict (dict_type, dict_name, dict_code, sort, remark) VALUES
('handle_type', 'Handle Type', 'phone', 1, 'phone contact'),
('handle_type', 'Handle Type', 'visit', 2, 'on-site visit'),
('work_order_status', 'Work Order Status', 'pending', 1, '待处理'),
('work_order_status', 'Work Order Status', 'auditing', 2, '审核中'),
('work_order_status', 'Work Order Status', 'done', 3, '已处理'),
('user_role', 'User Role', 'admin', 1, '管理员'),
('user_role', 'User Role', 'staff', 2, '工作人员');

INSERT INTO work_order (reporter, phone, address, description, images, status, create_time) VALUES
('Zhang San', '13800138000', '1号楼3单元502', '小区路灯不亮', '', 'pending', NOW()),
('Li Si', '13800138001', '2号楼门口', '下水道堵塞', '', 'auditing', NOW()),
('Wang Wu', '13800138002', '3号楼外墙', '墙面脱落', '', 'done', NOW());
