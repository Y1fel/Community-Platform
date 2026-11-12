-- MySQL 8+ migration for the community quiz, score ranking, and prize exchange module.
-- Safe to run more than once: it only creates missing tables and inserts missing seed rows.

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS quiz_question (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  content     VARCHAR(500) NOT NULL,
  options     TEXT         NOT NULL,
  answer      VARCHAR(10)  NOT NULL,
  category    VARCHAR(50)  NOT NULL DEFAULT 'general',
  difficulty  INT          NOT NULL DEFAULT 1,
  score       INT          NOT NULL DEFAULT 10,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_quiz_question_content (content),
  KEY idx_qq_category (category),
  KEY idx_qq_difficulty (difficulty)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_score (
  id          BIGINT      PRIMARY KEY AUTO_INCREMENT,
  phone       VARCHAR(11) NOT NULL,
  real_name   VARCHAR(50) NOT NULL,
  score       INT         NOT NULL DEFAULT 0,
  total_quiz  INT         NOT NULL DEFAULT 0,
  correct_num INT         NOT NULL DEFAULT 0,
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_us_phone (phone),
  KEY idx_us_score (score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS quiz_record (
  id          BIGINT      PRIMARY KEY AUTO_INCREMENT,
  phone       VARCHAR(11) NOT NULL,
  question_id BIGINT      NOT NULL,
  user_answer VARCHAR(10) NOT NULL,
  is_correct  TINYINT     NOT NULL DEFAULT 0,
  score       INT         NOT NULL DEFAULT 0,
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_qr_phone (phone),
  KEY idx_qr_question_id (question_id),
  KEY idx_qr_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS prize (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(100) NOT NULL,
  description VARCHAR(255) NULL,
  image_url   VARCHAR(255) NULL,
  cost        INT          NOT NULL DEFAULT 0,
  stock       INT          NOT NULL DEFAULT 0,
  status      VARCHAR(20)  NOT NULL DEFAULT 'active',
  sort        INT          NOT NULL DEFAULT 0,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_prize_name (name),
  KEY idx_prize_cost (cost),
  KEY idx_prize_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS exchange_record (
  id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
  phone       VARCHAR(11)  NOT NULL,
  real_name   VARCHAR(50)  NOT NULL,
  prize_id    BIGINT       NOT NULL,
  prize_name  VARCHAR(100) NOT NULL,
  cost        INT          NOT NULL DEFAULT 0,
  status      VARCHAR(20)  NOT NULL DEFAULT 'pending',
  remark      VARCHAR(255) NULL,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_er_phone (phone),
  KEY idx_er_prize_id (prize_id),
  KEY idx_er_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO quiz_question (content, options, answer, category, difficulty, score) VALUES
('中国的首都是哪个城市？', 'A.上海|||B.北京|||C.广州|||D.深圳', 'B', 'general', 1, 10),
('以下哪个是垃圾分类中的有害垃圾？', 'A.废纸|||B.电池|||C.塑料瓶|||D.剩菜', 'B', 'environment', 1, 10),
('居民身份证共有多少位数字？', 'A.15位|||B.16位|||C.18位|||D.20位', 'C', 'general', 1, 10),
('乘坐电梯时应该靠哪侧站立？', 'A.左侧|||B.右侧|||C.中间|||D.随意', 'B', 'civilization', 1, 10),
('火警报警电话是多少？', 'A.110|||B.120|||C.119|||D.122', 'C', 'safety', 1, 10),
('以下哪种行为是不文明行为？', 'A.排队等候|||B.随地吐痰|||C.遵守交通规则|||D.垃圾分类', 'B', 'civilization', 1, 10),
('社区服务热线通常是多少？', 'A.12345|||B.114|||C.110|||D.96128', 'A', 'community', 1, 10),
('以下哪种物品可以回收利用？', 'A.一次性筷子|||B.旧报纸|||C.陶瓷碎片|||D.口香糖', 'B', 'environment', 1, 10),
('遇到火灾时应该怎么做？', 'A.乘坐电梯|||B.用水灭火|||C.捂住口鼻弯腰逃生|||D.开窗呼救', 'C', 'safety', 2, 15),
('社会主义核心价值观中，属于国家层面的是？', 'A.自由平等公正法治|||B.富强民主文明和谐|||C.爱国敬业诚信友善|||D.以上都是', 'B', 'general', 2, 15),
('垃圾分类分为哪四大类？', 'A.可回收物、有害垃圾、厨余垃圾、其他垃圾|||B.废纸、塑料、玻璃、金属|||C.生活垃圾、工业垃圾、建筑垃圾、医疗垃圾|||D.以上都不对', 'A', 'environment', 2, 15),
('文明社区建设的目标是什么？', 'A.环境优美|||B.秩序优良|||C.服务优质|||D.以上都是', 'D', 'community', 2, 15),
('家庭用电安全中，以下哪种做法是正确的？', 'A.用湿手触摸电器|||B.超负荷用电|||C.定期检查电线|||D.私拉乱接电线', 'C', 'safety', 2, 15),
('社区居民应该遵守的基本准则是什么？', 'A.居民公约|||B.法律法规|||C.社会公德|||D.以上都是', 'D', 'community', 1, 10),
('以下哪种植物适合室内种植？', 'A.仙人掌|||B.夹竹桃|||C.滴水观音|||D.以上都不适合', 'A', 'environment', 1, 10);

INSERT IGNORE INTO prize (name, description, cost, stock, status, sort) VALUES
('环保购物袋', '可重复使用的环保购物袋一个', 50, 50, 'active', 1),
('社区纪念品', '社区定制纪念品一份', 100, 30, 'active', 2),
('保温杯', '不锈钢保温杯一个', 200, 20, 'active', 3),
('雨伞', '折叠晴雨伞一把', 300, 15, 'active', 4),
('书籍', '社区推荐书籍一本', 150, 25, 'active', 5),
('绿植', '精美盆栽绿植一盆', 180, 20, 'active', 6),
('厨房套装', '厨房用品套装', 500, 10, 'active', 7),
('运动手环', '智能运动手环一个', 800, 5, 'active', 8);
