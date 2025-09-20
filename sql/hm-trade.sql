/*
 Navicat Premium Data Transfer

 Source Server         : localhost-mysql
 Source Server Type    : MySQL
 Source Server Version : 80043
 Source Host           : localhost:3306
 Source Schema         : hm-trade

 Target Server Type    : MySQL
 Target Server Version : 80043
 File Encoding         : 65001

 Date: 20/09/2025 22:45:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` bigint NOT NULL COMMENT '订单id',
  `total_fee` int NOT NULL DEFAULT 0 COMMENT '总金额，单位为分',
  `payment_type` tinyint(1) UNSIGNED ZEROFILL NOT NULL COMMENT '支付类型，1、支付宝，2、微信，3、扣减余额',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '订单的状态，1、未付款 2、已付款,未发货 3、已发货,未确认 4、确认收货，交易成功 5、交易取消，订单关闭 6、交易结束，已评价',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  `consign_time` timestamp NULL DEFAULT NULL COMMENT '发货时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '交易完成时间',
  `close_time` timestamp NULL DEFAULT NULL COMMENT '交易关闭时间',
  `comment_time` timestamp NULL DEFAULT NULL COMMENT '评价时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `multi_key_status_time`(`status` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (123865420, 327900, 3, 2, 1, '2021-07-28 19:01:41', NULL, NULL, NULL, NULL, NULL, '2021-07-28 19:01:47');
INSERT INTO `order` VALUES (1654779387523936258, 135800, 3, 1, 1, '2023-05-06 17:25:24', NULL, NULL, NULL, NULL, NULL, '2023-05-06 17:25:24');
INSERT INTO `order` VALUES (1654782927348740097, 135800, 3, 1, 1, '2023-05-06 17:39:28', NULL, NULL, NULL, NULL, NULL, '2023-05-06 17:39:28');
INSERT INTO `order` VALUES (1658434251768471554, 120000, 3, 1, 1, '2023-05-16 19:28:32', NULL, NULL, NULL, NULL, NULL, '2023-05-16 19:28:32');
INSERT INTO `order` VALUES (1658453559437434882, 55400, 3, 1, 1, '2023-05-16 20:45:15', NULL, NULL, NULL, NULL, NULL, '2023-05-16 20:45:15');
INSERT INTO `order` VALUES (1659160216593354754, 156000, 3, 1, 1, '2023-05-18 19:33:16', NULL, NULL, NULL, NULL, NULL, '2023-05-18 19:33:16');
INSERT INTO `order` VALUES (1957637178322964482, 1342800, 3, 1, 1, '2025-08-19 10:54:09', NULL, NULL, NULL, NULL, NULL, '2025-08-19 10:54:09');
INSERT INTO `order` VALUES (1960234917862961154, 544000, 3, 1, 1, '2025-08-26 14:56:38', NULL, NULL, NULL, NULL, NULL, '2025-08-26 14:56:38');
INSERT INTO `order` VALUES (1960235026961002497, 544000, 3, 1, 1, '2025-08-26 14:57:04', NULL, NULL, NULL, NULL, NULL, '2025-08-26 14:57:04');
INSERT INTO `order` VALUES (1960238184713953282, 144600, 3, 1, 1, '2025-08-26 15:09:37', NULL, NULL, NULL, NULL, NULL, '2025-08-26 15:09:37');
INSERT INTO `order` VALUES (1960239006768177154, 144600, 3, 1, 1, '2025-08-26 15:12:53', NULL, NULL, NULL, NULL, NULL, '2025-08-26 15:12:53');
INSERT INTO `order` VALUES (1960240127096217601, 144600, 3, 1, 1, '2025-08-26 15:17:20', NULL, NULL, NULL, NULL, NULL, '2025-08-26 15:17:20');
INSERT INTO `order` VALUES (1960240433280409602, 144600, 3, 1, 1, '2025-08-26 15:18:33', NULL, NULL, NULL, NULL, NULL, '2025-08-26 15:18:33');
INSERT INTO `order` VALUES (1960245195199569922, 144600, 3, 1, 1, '2025-08-26 15:37:28', NULL, NULL, NULL, NULL, NULL, '2025-08-26 15:37:28');
INSERT INTO `order` VALUES (1960246354786213889, 144600, 3, 1, 1, '2025-08-26 15:42:05', NULL, NULL, NULL, NULL, NULL, '2025-08-26 15:42:05');
INSERT INTO `order` VALUES (1963081548585058306, 144600, 3, 1, 1, '2025-09-03 11:28:08', NULL, NULL, NULL, NULL, NULL, '2025-09-03 11:28:08');
INSERT INTO `order` VALUES (1963081578909876225, 144600, 3, 1, 1, '2025-09-03 11:28:15', NULL, NULL, NULL, NULL, NULL, '2025-09-03 11:28:15');
INSERT INTO `order` VALUES (1963081990362710018, 544000, 3, 1, 1, '2025-09-03 11:29:53', NULL, NULL, NULL, NULL, NULL, '2025-09-03 11:29:53');
INSERT INTO `order` VALUES (1963082657953300482, 9700, 3, 1, 1, '2025-09-03 11:32:32', NULL, NULL, NULL, NULL, NULL, '2025-09-03 11:32:32');
INSERT INTO `order` VALUES (1963083227896340481, 9700, 3, 1, 1, '2025-09-03 11:34:48', NULL, NULL, NULL, NULL, NULL, '2025-09-03 11:34:48');
INSERT INTO `order` VALUES (1963083838729609218, 544000, 3, 1, 1, '2025-09-03 11:37:14', NULL, NULL, NULL, NULL, NULL, '2025-09-03 11:37:14');
INSERT INTO `order` VALUES (1963085921901993986, 544000, 3, 1, 1, '2025-09-03 11:45:30', NULL, NULL, NULL, NULL, NULL, '2025-09-03 11:45:30');
INSERT INTO `order` VALUES (1963089068863008770, 544000, 3, 1, 1, '2025-09-03 11:58:01', '2025-09-03 11:58:59', NULL, NULL, NULL, NULL, '2025-09-03 12:06:07');
INSERT INTO `order` VALUES (1963096974022946817, 544000, 3, 1, 2, '2025-09-03 12:29:25', '2025-09-03 12:33:50', NULL, NULL, NULL, NULL, '2025-09-03 12:33:49');
INSERT INTO `order` VALUES (1963102318568681473, 544000, 3, 1, 2, '2025-09-03 12:50:40', '2025-09-03 12:51:47', NULL, NULL, NULL, NULL, '2025-09-03 12:51:47');
INSERT INTO `order` VALUES (1963104522797068289, 544000, 3, 1, 2, '2025-09-03 12:59:25', '2025-09-03 12:59:30', NULL, NULL, NULL, NULL, '2025-09-03 12:59:29');
INSERT INTO `order` VALUES (1963177963390775297, 544000, 3, 1, 1, '2025-09-03 17:51:15', NULL, NULL, NULL, NULL, NULL, '2025-09-03 17:51:15');
INSERT INTO `order` VALUES (1963177975889801217, 544000, 3, 1, 2, '2025-09-03 17:51:18', '2025-09-03 17:55:21', NULL, NULL, NULL, NULL, '2025-09-03 17:55:21');
INSERT INTO `order` VALUES (1964328387292430338, 1088000, 3, 1, 1, '2025-09-06 22:02:37', NULL, NULL, NULL, NULL, NULL, '2025-09-06 22:02:37');
INSERT INTO `order` VALUES (1964328395592957953, 1088000, 3, 1, 2, '2025-09-06 22:02:39', '2025-09-06 22:02:44', NULL, NULL, NULL, NULL, '2025-09-06 22:02:44');
INSERT INTO `order` VALUES (1964341061799641089, 544000, 3, 1, 1, '2025-09-06 22:52:59', NULL, NULL, NULL, NULL, NULL, '2025-09-06 22:52:59');
INSERT INTO `order` VALUES (1964341074000871425, 544000, 3, 1, 2, '2025-09-06 22:53:02', '2025-09-06 22:53:06', NULL, NULL, NULL, NULL, '2025-09-06 22:53:06');
INSERT INTO `order` VALUES (1964345234243002369, 639900, 3, 1, 1, '2025-09-06 23:09:34', NULL, NULL, NULL, NULL, NULL, '2025-09-06 23:09:34');
INSERT INTO `order` VALUES (1964345246389706754, 639900, 3, 1, 2, '2025-09-06 23:09:37', '2025-09-06 23:09:42', NULL, NULL, NULL, NULL, '2025-09-06 23:09:42');
INSERT INTO `order` VALUES (1964352336650829826, 639900, 3, 1, 1, '2025-09-06 23:37:47', NULL, NULL, NULL, NULL, NULL, '2025-09-06 23:37:47');
INSERT INTO `order` VALUES (1964352351804850177, 639900, 3, 1, 2, '2025-09-06 23:37:51', '2025-09-06 23:38:35', NULL, NULL, NULL, NULL, '2025-09-06 23:38:35');
INSERT INTO `order` VALUES (1964354679576461313, 735800, 3, 1, 1, '2025-09-06 23:47:06', NULL, NULL, NULL, NULL, NULL, '2025-09-06 23:47:06');
INSERT INTO `order` VALUES (1964357116009938945, 735800, 3, 1, 1, '2025-09-06 23:56:47', NULL, NULL, NULL, NULL, NULL, '2025-09-06 23:56:47');
INSERT INTO `order` VALUES (1964357127762378753, 735800, 3, 1, 1, '2025-09-06 23:56:49', NULL, NULL, NULL, NULL, NULL, '2025-09-06 23:56:49');
INSERT INTO `order` VALUES (1964359936197033985, 735800, 3, 1, 1, '2025-09-07 00:07:59', NULL, NULL, NULL, NULL, NULL, '2025-09-07 00:07:59');
INSERT INTO `order` VALUES (1964363403359801345, 735800, 3, 1, 1, '2025-09-07 00:21:46', NULL, NULL, NULL, NULL, NULL, '2025-09-07 00:21:46');
INSERT INTO `order` VALUES (1964363420292206593, 735800, 3, 1, 1, '2025-09-07 00:21:50', NULL, NULL, NULL, NULL, NULL, '2025-09-07 00:21:50');
INSERT INTO `order` VALUES (1964365696205344770, 735800, 3, 1, 1, '2025-09-07 00:30:53', NULL, NULL, NULL, NULL, NULL, '2025-09-07 00:30:53');
INSERT INTO `order` VALUES (1964365706900819970, 735800, 3, 1, 1, '2025-09-07 00:30:55', NULL, NULL, NULL, NULL, NULL, '2025-09-07 00:30:55');
INSERT INTO `order` VALUES (1964366619740753921, 735800, 3, 1, 2, '2025-09-07 00:34:32', '2025-09-07 00:35:11', NULL, NULL, NULL, NULL, '2025-09-07 00:35:11');
INSERT INTO `order` VALUES (1964588340079091713, 105600, 3, 1, 1, '2025-09-07 15:15:35', NULL, NULL, NULL, NULL, NULL, '2025-09-07 15:15:35');
INSERT INTO `order` VALUES (1964588414729314305, 105600, 3, 1, 1, '2025-09-07 15:15:53', NULL, NULL, NULL, NULL, NULL, '2025-09-07 15:15:53');
INSERT INTO `order` VALUES (1964588546208161793, 105600, 3, 1, 2, '2025-09-07 15:16:24', '2025-09-07 15:16:28', NULL, NULL, NULL, NULL, '2025-09-07 15:16:28');
INSERT INTO `order` VALUES (1964605975734890497, 12800, 3, 1, 1, '2025-09-07 16:25:40', NULL, NULL, NULL, NULL, NULL, '2025-09-07 16:25:40');
INSERT INTO `order` VALUES (1964605985868328961, 12800, 3, 1, 2, '2025-09-07 16:25:42', '2025-09-07 16:25:46', NULL, NULL, NULL, NULL, '2025-09-07 16:25:46');
INSERT INTO `order` VALUES (1964969054221987842, 9700, 3, 1, 1, '2025-09-08 16:28:24', NULL, NULL, NULL, NULL, NULL, '2025-09-08 16:28:24');
INSERT INTO `order` VALUES (1964969184882946050, 9700, 3, 1, 2, '2025-09-08 16:28:55', '2025-09-08 16:29:01', NULL, NULL, NULL, NULL, '2025-09-08 16:29:01');
INSERT INTO `order` VALUES (1964969499212476417, 9700, 3, 1, 1, '2025-09-08 16:30:10', NULL, NULL, NULL, NULL, NULL, '2025-09-08 16:30:10');
INSERT INTO `order` VALUES (1964985192813277185, 9700, 3, 1, 1, '2025-09-08 17:32:32', NULL, NULL, NULL, NULL, NULL, '2025-09-08 17:32:32');
INSERT INTO `order` VALUES (1964985240158580738, 9700, 3, 1, 2, '2025-09-08 17:32:43', '2025-09-08 17:32:47', NULL, NULL, NULL, NULL, '2025-09-08 17:32:46');
INSERT INTO `order` VALUES (1964989614704406529, 9700, 3, 1, 2, '2025-09-08 17:50:06', '2025-09-08 17:50:10', NULL, NULL, NULL, NULL, '2025-09-08 17:50:10');
INSERT INTO `order` VALUES (1964990375546957825, 9700, 3, 1, 1, '2025-09-08 17:53:07', NULL, NULL, NULL, NULL, NULL, '2025-09-08 17:53:07');
INSERT INTO `order` VALUES (1964990830951903234, 9700, 3, 1, 1, '2025-09-08 17:54:56', NULL, NULL, NULL, NULL, NULL, '2025-09-08 17:54:56');
INSERT INTO `order` VALUES (1964991241603624961, 9700, 3, 1, 5, '2025-09-08 17:56:34', NULL, NULL, '2025-09-08 17:56:46', '2025-09-08 17:56:45', NULL, '2025-09-08 17:56:46');
INSERT INTO `order` VALUES (1964991536563859458, 9700, 3, 1, 5, '2025-09-08 17:57:44', NULL, NULL, '2025-09-08 17:57:52', '2025-09-08 17:57:51', NULL, '2025-09-08 17:57:52');
INSERT INTO `order` VALUES (1964992033186230274, 9700, 3, 1, 5, '2025-09-08 17:59:43', NULL, NULL, '2025-09-08 17:59:44', '2025-09-08 17:59:44', NULL, '2025-09-08 17:59:44');
INSERT INTO `order` VALUES (1964992246433034242, 9700, 3, 1, 5, '2025-09-08 18:00:34', NULL, NULL, '2025-09-08 18:00:35', '2025-09-08 18:00:35', NULL, '2025-09-08 18:00:35');
INSERT INTO `order` VALUES (1964993021137760257, 9700, 3, 1, 5, '2025-09-08 18:03:38', NULL, NULL, '2025-09-08 18:03:40', '2025-09-08 18:03:40', NULL, '2025-09-08 18:03:40');
INSERT INTO `order` VALUES (1964994260168716290, 9700, 3, 1, 5, '2025-09-08 18:08:34', NULL, NULL, '2025-09-08 18:08:35', '2025-09-08 18:08:35', NULL, '2025-09-08 18:08:35');
INSERT INTO `order` VALUES (1964994864391761922, 9700, 3, 1, 5, '2025-09-08 18:10:58', NULL, NULL, '2025-09-08 18:11:00', '2025-09-08 18:11:00', NULL, '2025-09-08 18:11:00');
INSERT INTO `order` VALUES (1964995340470431746, 9700, 3, 1, 5, '2025-09-08 18:12:51', NULL, NULL, '2025-09-08 18:42:52', '2025-09-08 18:42:52', NULL, '2025-09-08 18:42:52');
INSERT INTO `order` VALUES (1968228945598455809, 63500, 3, 1, 5, '2025-09-17 16:22:03', NULL, NULL, '2025-09-17 16:52:12', '2025-09-17 16:52:12', NULL, '2025-09-17 16:52:12');
INSERT INTO `order` VALUES (1968229958292185089, 63500, 3, 1, 2, '2025-09-17 16:26:04', '2025-09-17 16:26:11', NULL, NULL, NULL, NULL, '2025-09-17 16:26:10');
INSERT INTO `order` VALUES (1969376217128329218, 13400, 3, 1, 5, '2025-09-20 20:20:54', NULL, NULL, '2025-09-20 20:50:57', '2025-09-20 20:50:57', NULL, '2025-09-20 20:50:57');
INSERT INTO `order` VALUES (1969376255380381697, 13400, 3, 1, 2, '2025-09-20 20:21:03', '2025-09-20 20:21:38', NULL, NULL, NULL, NULL, '2025-09-20 20:21:37');
INSERT INTO `order` VALUES (1969387388266962946, 462900, 3, 1, 1, '2025-09-20 21:05:17', NULL, NULL, NULL, NULL, NULL, '2025-09-20 21:05:17');
INSERT INTO `order` VALUES (1969387420424691713, 462900, 3, 1, 2, '2025-09-20 21:05:25', '2025-09-20 21:05:29', NULL, NULL, NULL, NULL, '2025-09-20 21:05:28');
INSERT INTO `order` VALUES (1969387669885116417, 95900, 3, 1, 2, '2025-09-20 21:06:24', '2025-09-20 21:06:28', NULL, NULL, NULL, NULL, '2025-09-20 21:06:27');
INSERT INTO `order` VALUES (1969388037218066434, 9700, 3, 1, 2, '2025-09-20 21:07:52', '2025-09-20 21:07:56', NULL, NULL, NULL, NULL, '2025-09-20 21:07:55');

-- ----------------------------
-- Table structure for order_detail
-- ----------------------------
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单详情id ',
  `order_id` bigint NOT NULL COMMENT '订单id',
  `item_id` bigint NOT NULL COMMENT 'sku商品id',
  `num` int NOT NULL COMMENT '购买数量',
  `name` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '商品标题',
  `spec` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '商品动态属性键值集',
  `price` int NOT NULL COMMENT '价格,单位：分',
  `image` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '商品图片',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `key_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 120 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '订单详情表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of order_detail
-- ----------------------------
INSERT INTO `order_detail` VALUES (1, 123865420, 100000003145, 2, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2021-07-28 19:05:21', '2021-07-28 19:05:21');
INSERT INTO `order_detail` VALUES (8, 1654779387523936258, 100002672274, 2, '三星 Galaxy S8+（SM-G9550）6GB+128GB 谜夜黑 移动联通电信4G手机 双卡双待', '{\"颜色\": \"红色\", \"版本\": \"6GB+128GB\"}', 55400, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t22954/298/30207467/96223/2f672221/5b233eabN82b4dedc.jpg!q70.jpg.webp', '2023-05-06 17:25:24', '2023-05-06 17:25:24');
INSERT INTO `order_detail` VALUES (9, 1654779387523936258, 100002672300, 1, '三星 Galaxy Note9（SM-N9600）6GB+128GB 寒霜蓝 移动联通电信4G游戏手机 双卡双待', '{\"颜色\": \"蓝色\", \"版本\": \"6GB+128GB\"}', 25000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t27082/302/324013085/140782/145fdd/5b8e3b98N4c3dcd05.jpg!q70.jpg.webp', '2023-05-06 17:25:24', '2023-05-06 17:25:24');
INSERT INTO `order_detail` VALUES (10, 1654782927348740097, 100002672274, 2, '三星 Galaxy S8+（SM-G9550）6GB+128GB 谜夜黑 移动联通电信4G手机 双卡双待', '{\"颜色\": \"红色\", \"版本\": \"6GB+128GB\"}', 55400, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t22954/298/30207467/96223/2f672221/5b233eabN82b4dedc.jpg!q70.jpg.webp', '2023-05-06 17:39:28', '2023-05-06 17:39:28');
INSERT INTO `order_detail` VALUES (11, 1654782927348740097, 100002672300, 1, '三星 Galaxy Note9（SM-N9600）6GB+128GB 寒霜蓝 移动联通电信4G游戏手机 双卡双待', '{\"颜色\": \"蓝色\", \"版本\": \"6GB+128GB\"}', 25000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t27082/302/324013085/140782/145fdd/5b8e3b98N4c3dcd05.jpg!q70.jpg.webp', '2023-05-06 17:39:28', '2023-05-06 17:39:28');
INSERT INTO `order_detail` VALUES (12, 1658434251768471554, 100002672272, 1, '荣耀V20胡歌同款手机全网通 标配版 6GB+128GB 魅丽红 游戏手机 移动联通电信4G全面屏手机 双卡双待', '{\"颜色\": \"红色\"}', 95000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/8112/20/10485/366920/5c2336deEab272fe3/12b58de5020ca1a1.jpg!q70.jpg.webp', '2023-05-16 19:28:32', '2023-05-16 19:28:32');
INSERT INTO `order_detail` VALUES (13, 1658434251768471554, 100002672300, 1, '三星 Galaxy Note9（SM-N9600）6GB+128GB 寒霜蓝 移动联通电信4G游戏手机 双卡双待', '{\"颜色\": \"蓝色\", \"版本\": \"6GB+128GB\"}', 25000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t27082/302/324013085/140782/145fdd/5b8e3b98N4c3dcd05.jpg!q70.jpg.webp', '2023-05-16 19:28:32', '2023-05-16 19:28:32');
INSERT INTO `order_detail` VALUES (14, 1658453559437434882, 100002672274, 1, '三星 Galaxy S8+（SM-G9550）6GB+128GB 谜夜黑 移动联通电信4G手机 双卡双待', '{\"颜色\": \"红色\", \"版本\": \"6GB+128GB\"}', 55400, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t22954/298/30207467/96223/2f672221/5b233eabN82b4dedc.jpg!q70.jpg.webp', '2023-05-16 20:45:15', '2023-05-16 20:45:15');
INSERT INTO `order_detail` VALUES (15, 1659160216593354754, 100001964366, 1, 'OPPO A7 全面屏拍照手机 4GB+64GB 清新粉 全网通 移动联通电信4G 双卡双待手机', '{\"颜色\": \"粉色\"}', 65400, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t25564/327/2615611632/135559/d3c69840/5bebd32eN3bf6f987.jpg!q70.jpg.webp', '2023-05-18 19:33:16', '2023-05-18 19:33:16');
INSERT INTO `order_detail` VALUES (16, 1659160216593354754, 100002624512, 1, '【千玺代言】华为新品 HUAWEI nova 4 极点全面屏手机 2000万超广角三摄 8GB+128GB 蜜语红 全网通双卡双待', '{\"颜色\": \"红色\"}', 90600, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/20085/14/1076/149604/5c0f8dd2Ebafd3bfd/0cb34a7826cbe1c3.jpg!q70.jpg.webp', '2023-05-18 19:33:16', '2023-05-18 19:33:16');
INSERT INTO `order_detail` VALUES (17, 1957637178322964482, 1713453, 3, '小鹿叮叮 超薄 成长裤 婴儿拉拉裤 男女学步通用尿不湿 大号L码84片【9-14kg】', '{}', 12800, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t22591/39/1930198982/188975/7385556d/5b6d33bdNa455e2b0.jpg!q70.jpg.webp', '2025-08-19 10:54:10', '2025-08-19 10:54:10');
INSERT INTO `order_detail` VALUES (18, 1957637178322964482, 2120808, 1, '姬龙雪 guy laroche女包 GL经典手提包女牛皮大容量单肩包女欧美时尚包包女包GS1210001-06杏色', '{}', 71800, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t30694/267/398774087/90954/6fc143cf/5bf25358N14dadbf7.jpg!q70.jpg.webp', '2025-08-19 10:54:10', '2025-08-19 10:54:10');
INSERT INTO `order_detail` VALUES (19, 1957637178322964482, 5706773, 2, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-08-19 10:54:10', '2025-08-19 10:54:10');
INSERT INTO `order_detail` VALUES (20, 1957637178322964482, 14741770661, 1, '康佳（KONKA） LED49UC3 49英寸超薄曲面36核4K HDR人工智能电视', '{}', 144600, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t16477/84/2032772431/400946/2887be91/5a912776N7e343ec3.jpg!q70.jpg.webp', '2025-08-19 10:54:10', '2025-08-19 10:54:10');
INSERT INTO `order_detail` VALUES (33, 1960234917862961154, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-08-26 14:56:39', '2025-08-26 14:56:39');
INSERT INTO `order_detail` VALUES (34, 1960235026961002497, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-08-26 14:57:04', '2025-08-26 14:57:04');
INSERT INTO `order_detail` VALUES (35, 1960238184713953282, 14741770661, 1, '康佳（KONKA） LED49UC3 49英寸超薄曲面36核4K HDR人工智能电视', '{}', 144600, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t16477/84/2032772431/400946/2887be91/5a912776N7e343ec3.jpg!q70.jpg.webp', '2025-08-26 15:09:37', '2025-08-26 15:09:37');
INSERT INTO `order_detail` VALUES (36, 1960239006768177154, 14741770661, 1, '康佳（KONKA） LED49UC3 49英寸超薄曲面36核4K HDR人工智能电视', '{}', 144600, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t16477/84/2032772431/400946/2887be91/5a912776N7e343ec3.jpg!q70.jpg.webp', '2025-08-26 15:12:53', '2025-08-26 15:12:53');
INSERT INTO `order_detail` VALUES (37, 1960240127096217601, 14741770661, 1, '康佳（KONKA） LED49UC3 49英寸超薄曲面36核4K HDR人工智能电视', '{}', 144600, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t16477/84/2032772431/400946/2887be91/5a912776N7e343ec3.jpg!q70.jpg.webp', '2025-08-26 15:17:20', '2025-08-26 15:17:20');
INSERT INTO `order_detail` VALUES (38, 1960240433280409602, 14741770661, 1, '康佳（KONKA） LED49UC3 49英寸超薄曲面36核4K HDR人工智能电视', '{}', 144600, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t16477/84/2032772431/400946/2887be91/5a912776N7e343ec3.jpg!q70.jpg.webp', '2025-08-26 15:18:33', '2025-08-26 15:18:33');
INSERT INTO `order_detail` VALUES (39, 1960245195199569922, 14741770661, 1, '康佳（KONKA） LED49UC3 49英寸超薄曲面36核4K HDR人工智能电视', '{}', 144600, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t16477/84/2032772431/400946/2887be91/5a912776N7e343ec3.jpg!q70.jpg.webp', '2025-08-26 15:37:28', '2025-08-26 15:37:28');
INSERT INTO `order_detail` VALUES (40, 1960246354786213889, 14741770661, 1, '康佳（KONKA） LED49UC3 49英寸超薄曲面36核4K HDR人工智能电视', '{}', 144600, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t16477/84/2032772431/400946/2887be91/5a912776N7e343ec3.jpg!q70.jpg.webp', '2025-08-26 15:42:05', '2025-08-26 15:42:05');
INSERT INTO `order_detail` VALUES (41, 1963081548585058306, 14741770661, 1, '康佳（KONKA） LED49UC3 49英寸超薄曲面36核4K HDR人工智能电视', '{}', 144600, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t16477/84/2032772431/400946/2887be91/5a912776N7e343ec3.jpg!q70.jpg.webp', '2025-09-03 11:28:09', '2025-09-03 11:28:09');
INSERT INTO `order_detail` VALUES (42, 1963081578909876225, 14741770661, 1, '康佳（KONKA） LED49UC3 49英寸超薄曲面36核4K HDR人工智能电视', '{}', 144600, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t16477/84/2032772431/400946/2887be91/5a912776N7e343ec3.jpg!q70.jpg.webp', '2025-09-03 11:28:15', '2025-09-03 11:28:15');
INSERT INTO `order_detail` VALUES (43, 1963081990362710018, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-03 11:29:53', '2025-09-03 11:29:53');
INSERT INTO `order_detail` VALUES (44, 1963082657953300482, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-03 11:32:32', '2025-09-03 11:32:32');
INSERT INTO `order_detail` VALUES (45, 1963083227896340481, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-03 11:34:48', '2025-09-03 11:34:48');
INSERT INTO `order_detail` VALUES (46, 1963083838729609218, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-03 11:37:14', '2025-09-03 11:37:14');
INSERT INTO `order_detail` VALUES (47, 1963085921901993986, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-03 11:45:30', '2025-09-03 11:45:30');
INSERT INTO `order_detail` VALUES (48, 1963089068863008770, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-03 11:58:01', '2025-09-03 11:58:01');
INSERT INTO `order_detail` VALUES (49, 1963096974022946817, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-03 12:29:26', '2025-09-03 12:29:26');
INSERT INTO `order_detail` VALUES (50, 1963102318568681473, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-03 12:50:40', '2025-09-03 12:50:40');
INSERT INTO `order_detail` VALUES (51, 1963104522797068289, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-03 12:59:25', '2025-09-03 12:59:25');
INSERT INTO `order_detail` VALUES (52, 1963177963390775297, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-03 17:51:15', '2025-09-03 17:51:15');
INSERT INTO `order_detail` VALUES (53, 1963177975889801217, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-03 17:51:18', '2025-09-03 17:51:18');
INSERT INTO `order_detail` VALUES (54, 1964328387292430338, 5706773, 2, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-06 22:02:38', '2025-09-06 22:02:38');
INSERT INTO `order_detail` VALUES (55, 1964328395592957953, 5706773, 2, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-06 22:02:39', '2025-09-06 22:02:39');
INSERT INTO `order_detail` VALUES (56, 1964341061799641089, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-06 22:53:00', '2025-09-06 22:53:00');
INSERT INTO `order_detail` VALUES (57, 1964341074000871425, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-06 22:53:02', '2025-09-06 22:53:02');
INSERT INTO `order_detail` VALUES (58, 1964345234243002369, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-06 23:09:35', '2025-09-06 23:09:35');
INSERT INTO `order_detail` VALUES (59, 1964345234243002369, 100000003145, 1, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-06 23:09:35', '2025-09-06 23:09:35');
INSERT INTO `order_detail` VALUES (60, 1964345246389706754, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-06 23:09:37', '2025-09-06 23:09:37');
INSERT INTO `order_detail` VALUES (61, 1964345246389706754, 100000003145, 1, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-06 23:09:37', '2025-09-06 23:09:37');
INSERT INTO `order_detail` VALUES (62, 1964352336650829826, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-06 23:37:48', '2025-09-06 23:37:48');
INSERT INTO `order_detail` VALUES (63, 1964352336650829826, 100000003145, 1, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-06 23:37:48', '2025-09-06 23:37:48');
INSERT INTO `order_detail` VALUES (64, 1964352351804850177, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-06 23:37:51', '2025-09-06 23:37:51');
INSERT INTO `order_detail` VALUES (65, 1964352351804850177, 100000003145, 1, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-06 23:37:51', '2025-09-06 23:37:51');
INSERT INTO `order_detail` VALUES (66, 1964354679576461313, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-06 23:47:06', '2025-09-06 23:47:06');
INSERT INTO `order_detail` VALUES (67, 1964354679576461313, 100000003145, 2, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-06 23:47:06', '2025-09-06 23:47:06');
INSERT INTO `order_detail` VALUES (68, 1964357116009938945, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-06 23:56:48', '2025-09-06 23:56:48');
INSERT INTO `order_detail` VALUES (69, 1964357116009938945, 100000003145, 2, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-06 23:56:48', '2025-09-06 23:56:48');
INSERT INTO `order_detail` VALUES (70, 1964357127762378753, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-06 23:56:49', '2025-09-06 23:56:49');
INSERT INTO `order_detail` VALUES (71, 1964357127762378753, 100000003145, 2, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-06 23:56:49', '2025-09-06 23:56:49');
INSERT INTO `order_detail` VALUES (72, 1964359936197033985, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-07 00:08:00', '2025-09-07 00:08:00');
INSERT INTO `order_detail` VALUES (73, 1964359936197033985, 100000003145, 2, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-07 00:08:00', '2025-09-07 00:08:00');
INSERT INTO `order_detail` VALUES (74, 1964363403359801345, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-07 00:21:47', '2025-09-07 00:21:47');
INSERT INTO `order_detail` VALUES (75, 1964363403359801345, 100000003145, 2, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-07 00:21:47', '2025-09-07 00:21:47');
INSERT INTO `order_detail` VALUES (76, 1964363420292206593, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-07 00:21:50', '2025-09-07 00:21:50');
INSERT INTO `order_detail` VALUES (77, 1964363420292206593, 100000003145, 2, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-07 00:21:50', '2025-09-07 00:21:50');
INSERT INTO `order_detail` VALUES (78, 1964365696205344770, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-07 00:30:53', '2025-09-07 00:30:53');
INSERT INTO `order_detail` VALUES (79, 1964365696205344770, 100000003145, 2, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-07 00:30:53', '2025-09-07 00:30:53');
INSERT INTO `order_detail` VALUES (80, 1964365706900819970, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-07 00:30:55', '2025-09-07 00:30:55');
INSERT INTO `order_detail` VALUES (81, 1964365706900819970, 100000003145, 2, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-07 00:30:55', '2025-09-07 00:30:55');
INSERT INTO `order_detail` VALUES (82, 1964366619740753921, 5706773, 1, '华为 HUAWEI Mate 10 Pro 全面屏徕卡双摄游戏手机 6GB+128GB 银钻灰 全网通移动联通电信4G手机 双卡双待', '{\"颜色\": \"银色\", \"选择内存\": \"128GB\"}', 544000, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t11986/295/1484411523/155164/77795126/5a01503cN19d7f1a0.jpg!q70.jpg.webp', '2025-09-07 00:34:33', '2025-09-07 00:34:33');
INSERT INTO `order_detail` VALUES (83, 1964366619740753921, 100000003145, 2, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-07 00:34:33', '2025-09-07 00:34:33');
INSERT INTO `order_detail` VALUES (84, 1964588340079091713, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-07 15:15:36', '2025-09-07 15:15:36');
INSERT INTO `order_detail` VALUES (85, 1964588340079091713, 100000003145, 1, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-07 15:15:36', '2025-09-07 15:15:36');
INSERT INTO `order_detail` VALUES (86, 1964588414729314305, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-07 15:15:53', '2025-09-07 15:15:53');
INSERT INTO `order_detail` VALUES (87, 1964588414729314305, 100000003145, 1, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-07 15:15:53', '2025-09-07 15:15:53');
INSERT INTO `order_detail` VALUES (88, 1964588546208161793, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-07 15:16:24', '2025-09-07 15:16:24');
INSERT INTO `order_detail` VALUES (89, 1964588546208161793, 100000003145, 1, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-07 15:16:24', '2025-09-07 15:16:24');
INSERT INTO `order_detail` VALUES (90, 1964605975734890497, 1713453, 1, '小鹿叮叮 超薄 成长裤 婴儿拉拉裤 男女学步通用尿不湿 大号L码84片【9-14kg】', '{}', 12800, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t22591/39/1930198982/188975/7385556d/5b6d33bdNa455e2b0.jpg!q70.jpg.webp', '2025-09-07 16:25:40', '2025-09-07 16:25:40');
INSERT INTO `order_detail` VALUES (91, 1964605985868328961, 1713453, 1, '小鹿叮叮 超薄 成长裤 婴儿拉拉裤 男女学步通用尿不湿 大号L码84片【9-14kg】', '{}', 12800, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t22591/39/1930198982/188975/7385556d/5b6d33bdNa455e2b0.jpg!q70.jpg.webp', '2025-09-07 16:25:42', '2025-09-07 16:25:42');
INSERT INTO `order_detail` VALUES (92, 1964969054221987842, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 16:28:26', '2025-09-08 16:28:26');
INSERT INTO `order_detail` VALUES (93, 1964969184882946050, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 16:28:55', '2025-09-08 16:28:55');
INSERT INTO `order_detail` VALUES (94, 1964969499212476417, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 16:30:10', '2025-09-08 16:30:10');
INSERT INTO `order_detail` VALUES (95, 1964985192813277185, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 17:32:32', '2025-09-08 17:32:32');
INSERT INTO `order_detail` VALUES (96, 1964985240158580738, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 17:32:43', '2025-09-08 17:32:43');
INSERT INTO `order_detail` VALUES (97, 1964989614704406529, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 17:50:06', '2025-09-08 17:50:06');
INSERT INTO `order_detail` VALUES (98, 1964990375546957825, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 17:53:08', '2025-09-08 17:53:08');
INSERT INTO `order_detail` VALUES (99, 1964990830951903234, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 17:54:56', '2025-09-08 17:54:56');
INSERT INTO `order_detail` VALUES (100, 1964991241603624961, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 17:56:34', '2025-09-08 17:56:34');
INSERT INTO `order_detail` VALUES (101, 1964991536563859458, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 17:57:44', '2025-09-08 17:57:44');
INSERT INTO `order_detail` VALUES (102, 1964992033186230274, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 17:59:43', '2025-09-08 17:59:43');
INSERT INTO `order_detail` VALUES (103, 1964992246433034242, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 18:00:34', '2025-09-08 18:00:34');
INSERT INTO `order_detail` VALUES (104, 1964993021137760257, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 18:03:38', '2025-09-08 18:03:38');
INSERT INTO `order_detail` VALUES (105, 1964994260168716290, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 18:08:34', '2025-09-08 18:08:34');
INSERT INTO `order_detail` VALUES (106, 1964994864391761922, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 18:10:58', '2025-09-08 18:10:58');
INSERT INTO `order_detail` VALUES (107, 1964995340470431746, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-08 18:12:51', '2025-09-08 18:12:51');
INSERT INTO `order_detail` VALUES (108, 1968228945598455809, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-17 16:22:10', '2025-09-17 16:22:10');
INSERT INTO `order_detail` VALUES (109, 1968228945598455809, 100002051630, 1, 'HMDIME 牛仔裤女2018秋冬新品 破洞chic潮显瘦宽松学生bf抓绒小脚哈伦裤女九分裤 DLSF1666 浅蓝色 30', '{\"颜色\": \"蓝色\", \"尺码\": \"30\"}', 53800, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t26962/248/1964399542/107647/516fb37e/5bf365b0N0b4ce8dd.jpg!q70.jpg.webp', '2025-09-17 16:22:10', '2025-09-17 16:22:10');
INSERT INTO `order_detail` VALUES (110, 1968229958292185089, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-17 16:26:04', '2025-09-17 16:26:04');
INSERT INTO `order_detail` VALUES (111, 1968229958292185089, 100002051630, 1, 'HMDIME 牛仔裤女2018秋冬新品 破洞chic潮显瘦宽松学生bf抓绒小脚哈伦裤女九分裤 DLSF1666 浅蓝色 30', '{\"颜色\": \"蓝色\", \"尺码\": \"30\"}', 53800, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t26962/248/1964399542/107647/516fb37e/5bf365b0N0b4ce8dd.jpg!q70.jpg.webp', '2025-09-17 16:26:04', '2025-09-17 16:26:04');
INSERT INTO `order_detail` VALUES (112, 1969376217128329218, 1127466, 2, '澳大利亚 进口牛奶 德运（Devondale） 全脂牛奶 1L*10 整箱装', '{}', 6700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t16621/175/688525604/194501/c26b153e/5aa1fc94Ndea68796.jpg!q70.jpg.webp', '2025-09-20 20:20:55', '2025-09-20 20:20:55');
INSERT INTO `order_detail` VALUES (113, 1969376255380381697, 1127466, 2, '澳大利亚 进口牛奶 德运（Devondale） 全脂牛奶 1L*10 整箱装', '{}', 6700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t16621/175/688525604/194501/c26b153e/5aa1fc94Ndea68796.jpg!q70.jpg.webp', '2025-09-20 20:21:03', '2025-09-20 20:21:03');
INSERT INTO `order_detail` VALUES (114, 1969387388266962946, 1533902, 3, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-20 21:05:17', '2025-09-20 21:05:17');
INSERT INTO `order_detail` VALUES (115, 1969387388266962946, 14741770661, 3, '康佳（KONKA） LED49UC3 49英寸超薄曲面36核4K HDR人工智能电视', '{}', 144600, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t16477/84/2032772431/400946/2887be91/5a912776N7e343ec3.jpg!q70.jpg.webp', '2025-09-20 21:05:17', '2025-09-20 21:05:17');
INSERT INTO `order_detail` VALUES (116, 1969387420424691713, 1533902, 3, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-20 21:05:25', '2025-09-20 21:05:25');
INSERT INTO `order_detail` VALUES (117, 1969387420424691713, 14741770661, 3, '康佳（KONKA） LED49UC3 49英寸超薄曲面36核4K HDR人工智能电视', '{}', 144600, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t16477/84/2032772431/400946/2887be91/5a912776N7e343ec3.jpg!q70.jpg.webp', '2025-09-20 21:05:25', '2025-09-20 21:05:25');
INSERT INTO `order_detail` VALUES (118, 1969387669885116417, 100000003145, 1, 'vivo X23 8GB+128GB 幻夜蓝 水滴屏全面屏 游戏手机 移动联通电信全网通4G手机', '{\"颜色\": \"红色\", \"版本\": \"8GB+128GB\"}', 95900, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t1/4612/28/6223/298257/5ba22d66Ef665222f/d97ed0b25cbe8c6e.jpg!q70.jpg.webp', '2025-09-20 21:06:24', '2025-09-20 21:06:24');
INSERT INTO `order_detail` VALUES (119, 1969388037218066434, 1533902, 1, '新西兰进口牛奶 纽仕兰 3.5g蛋白质全脂牛奶 250ml*24整箱装纯牛奶', '{}', 9700, 'https://m.360buyimg.com/mobilecms/s720x720_jfs/t3526/295/888755633/189982/16ea21b4/5816dce5N70820f42.jpg!q70.jpg.webp', '2025-09-20 21:07:52', '2025-09-20 21:07:52');

-- ----------------------------
-- Table structure for order_logistics
-- ----------------------------
DROP TABLE IF EXISTS `order_logistics`;
CREATE TABLE `order_logistics`  (
  `order_id` bigint NOT NULL COMMENT '订单id，与订单表一对一',
  `logistics_number` varchar(18) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '物流单号',
  `logistics_company` varchar(18) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '物流公司名称',
  `contact` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收件人',
  `mobile` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收件人手机号码',
  `province` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '省',
  `city` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '市',
  `town` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '区',
  `street` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '街道',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of order_logistics
-- ----------------------------
INSERT INTO `order_logistics` VALUES (123865420, '', '', '李四', '13838411438', '上海', '上海', '浦东新区', '航头镇', '2021-07-28 19:07:01', '2021-07-28 19:07:01');

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`  (
  `branch_id` bigint NOT NULL COMMENT 'branch transaction id',
  `xid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'global transaction id',
  `context` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'undo_log context,such as serialization',
  `rollback_info` longblob NOT NULL COMMENT 'rollback info',
  `log_status` int NOT NULL COMMENT '0:normal status,1:defense status',
  `log_created` datetime(6) NOT NULL COMMENT 'create datetime',
  `log_modified` datetime(6) NOT NULL COMMENT 'modify datetime',
  UNIQUE INDEX `ux_undo_log`(`xid` ASC, `branch_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AT transaction mode undo table' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of undo_log
-- ----------------------------
INSERT INTO `undo_log` VALUES (36723720918069260, '192.168.80.129:8099:36723720918069249', 'serializer=jackson&compressorType=NONE', 0x7B7D, 1, '2025-09-17 16:22:34.133500', '2025-09-17 16:22:34.133500');

SET FOREIGN_KEY_CHECKS = 1;
