-- Fix seed data for Windows MySQL CLI (ASCII-safe, avoids UTF-8 encoding errors)
-- Run this AFTER tables already exist

SET NAMES utf8mb4;
USE community;

-- Ensure charset
ALTER DATABASE community CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE sys_org  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE sys_user CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE sys_dict CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE sys_log  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Org (id=1). Name can be Chinese once charset is fixed.
DELETE FROM sys_user;
DELETE FROM sys_dict;
DELETE FROM sys_org;

INSERT INTO sys_org (id, parent_id, name, code, sort) VALUES
(1, NULL, CONCAT(0xE7A4BE, 0xE58CBA), 'community', 0);
-- Above hex is unreliable for CONCAT bytes. Use plain ASCII then update via app,
-- OR use the lines below which are ASCII-only:

-- Prefer ASCII seed (recommended for Command Line Client):
DELETE FROM sys_org;
INSERT INTO sys_org (id, parent_id, name, code, sort) VALUES
(1, NULL, 'Community', 'community', 0);

INSERT INTO sys_user (username, real_name, phone, password, org_id, role, status, deleted) VALUES
('admin', 'Admin', '13800000000',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sLTTplI5N6.',
 1, 'admin', 'enabled', 0);

INSERT INTO sys_dict (dict_type, dict_name, dict_code, sort, remark) VALUES
('handle_type', 'Handle Type', 'phone', 1, 'phone contact'),
('handle_type', 'Handle Type', 'visit', 2, 'on-site visit');

-- Verify
SELECT id, name, code FROM sys_org;
SELECT id, username, real_name, role FROM sys_user;
SELECT id, dict_type, dict_name, dict_code FROM sys_dict;
