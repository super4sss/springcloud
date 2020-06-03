create database ysd_overview character set utf8 collate utf8_general_ci;

use ysd_overview;

DROP TABLE IF EXISTS `pro_prop`;
CREATE TABLE `pro_prop` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL COMMENT '属性名',
  `text` varchar(255) NOT NULL COMMENT '属性值',
  `propSet` varchar(16) NOT NULL COMMENT '属性集',
  `project` varchar(32) NOT NULL COMMENT '项目编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目属性表';

DROP TABLE IF EXISTS `pro_picture`;
CREATE TABLE `pro_picture` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '图片名称',
  `path` varchar(255) NOT NULL COMMENT '图片路径',
  `type` tinyint(2) NOT NULL COMMENT '图片类别(1:现场航拍,2:效果图,3:项目照片)',
  `project` varchar(32) NOT NULL COMMENT '项目编码',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `customName` varchar(255) DEFAULT NULL COMMENT '事件名称',
  `uploadTime` datetime DEFAULT NULL COMMENT '时间',
  `site` varchar(255) DEFAULT NULL COMMENT '地点',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目图片表';

DROP TABLE IF EXISTS `pro_event`;
CREATE TABLE `pro_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL COMMENT '事件标题',
  `content` text NOT NULL COMMENT '事件内容',
  `creator` varchar(32) NOT NULL COMMENT '创建人工号',
  `creatorName` varchar(32) NOT NULL COMMENT '创建人名称',
  `createAt` datetime NOT NULL COMMENT '创建时间',
  `project` varchar(32) NOT NULL COMMENT '项目编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目事件表';

DROP TABLE IF EXISTS `pro_letter`;
CREATE TABLE `pro_letter` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`title` varchar(100) NOT NULL COMMENT '函件标题',
	`content` text NOT NULL COMMENT '函件内容',
	`creator` varchar(32) NOT NULL COMMENT '创建人',
	`creatorNum` varchar(32) NOT NULL COMMENT '创建人工号',
	`createAt` datetime NOT NULL COMMENT '创建时间',
	`project` varchar(32) NOT NULL COMMENT '项目编码',
	PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目函件表';

DROP TABLE IF EXISTS `pro_weekly_newspaper`;
CREATE TABLE `pro_weekly_newspaper`(
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`title` varchar(100) NOT NULL COMMENT '周报标题',
	`content` text NOT NULL COMMENT '周报内容',
	`creator` varchar(32) NOT NULL COMMENT '创建人',
	`creatorNum` varchar(32) NOT NULL COMMENT '创建人工号',
	`createAt` datetime NOT NULL COMMENT '创建时间',
	`project` varchar(32) NOT NULL COMMENT '项目编码',
	PRIMARY KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目周报表';

DROP TABLE IF EXISTS `pro_folder`;
CREATE TABLE `pro_folder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(32) NOT NULL COMMENT '文件标识',
  `name` varchar(32) NOT NULL COMMENT '文件名称',
  `entity` varchar(32) DEFAULT NULL COMMENT '实体名称',
  `entityId` varchar(32) DEFAULT NULL COMMENT '实体标识',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目文件夹表(关联文件云目录)';

DROP TABLE IF EXISTS `pro_vrshow`;
CREATE TABLE `pro_vrshow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL COMMENT '模型名称',
  `viewPath` varchar(255) DEFAULT NULL COMMENT '全景地址',
  `picPath` varchar(100) DEFAULT NULL COMMENT '图片地址',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `project` varchar(32) NOT NULL COMMENT '项目编码',
  `stage` varchar(32) DEFAULT NULL COMMENT '阶段编码',
  `creator` varchar(32) NOT NULL COMMENT '创建人工号',
  `creatorName` varchar(32) NOT NULL COMMENT '创建人名称',
  `createAt` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='VR展示表';

DROP TABLE IF EXISTS `pro_problem`;
CREATE TABLE `pro_problem` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(32) NOT NULL COMMENT '问题编号',
  `type` varchar(32) DEFAULT NULL COMMENT '问题类型',
  `category` varchar(32) DEFAULT NULL COMMENT '问题类别',
  `specialty` varchar(32) DEFAULT NULL COMMENT '专业',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `area` varchar(20) DEFAULT NULL COMMENT '发生区域',
  `build` varchar(20) DEFAULT NULL COMMENT '栋',
  `floor` varchar(20) DEFAULT NULL COMMENT '楼层',
  `position` varchar(50) DEFAULT NULL COMMENT '位置',
  `beginTime` date DEFAULT NULL COMMENT '发生时间',
  `endTime` date DEFAULT NULL COMMENT '解决时间',
  `contactMan` varchar(32) DEFAULT NULL COMMENT '责任人',
  `contactNum` varchar(32) DEFAULT NULL COMMENT '联系电话',
  `belongsGroup` varchar(50) DEFAULT NULL COMMENT '所属团组',
  `status` varchar(20) DEFAULT NULL COMMENT '状态',
  `project` varchar(32) NOT NULL COMMENT '项目编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目问题表';

DROP TABLE IF EXISTS `log_problem`;
CREATE TABLE `log_problem` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` tinyint(2) NOT NULL DEFAULT 0 COMMENT '操作类型(1:导入,2:导出)',
  `fileName` varchar(100) DEFAULT NULL COMMENT '文件名称',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `creator` varchar(32) NOT NULL COMMENT '操作人工号',
  `creatorName` varchar(32) NOT NULL COMMENT '操作人名称',
  `createAt` datetime NOT NULL COMMENT '操作时间',
  `project` varchar(32) NOT NULL COMMENT '项目编码',
  `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '状态(0:已发起,1:已完成)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='问题操作日志表';

DROP TABLE IF EXISTS `log_document`;
CREATE TABLE `log_document` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(36) NOT NULL COMMENT '文件编码',
  `name` varchar(100) NOT NULL COMMENT '文件名称',
  `path` varchar(255) DEFAULT NULL COMMENT '访问路径',
  `path` varchar(2000) DEFAULT NULL COMMENT '路径名称',
  `type` tinyint(2) NOT NULL DEFAULT 0 COMMENT '类型(1:图档,2:模型)',
  `creator` varchar(32) NOT NULL COMMENT '操作人工号',
  `creatorName` varchar(32) NOT NULL COMMENT '操作人名称',
  `createAt` datetime NOT NULL COMMENT '操作时间',
  `project` varchar(32) NOT NULL COMMENT '项目编码',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `stageName` varchar(32) DEFAULT NULL COMMENT '阶段名称',
  `stageId` varchar(32) DEFAULT NULL COMMENT '阶段ID',
  `organizationId` varchar(32) DEFAULT NULL COMMENT '组织ID',
  `organization` varchar(32) DEFAULT NULL COMMENT '组织角色',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='图档操作日志表';

DROP TABLE IF EXISTS `pro_organizer`;
CREATE TABLE `pro_organizer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parties` varchar(32) NOT NULL COMMENT '参与方',
  `unitName` varchar(100) NOT NULL COMMENT '单位名称',
  `unitCode` varchar(32) DEFAULT NULL COMMENT '单位编码',
  `contacts` varchar(32) DEFAULT NULL COMMENT '联系人',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `phone` varchar(32) DEFAULT NULL COMMENT '手机号码',
  `telephone` varchar(32) DEFAULT NULL COMMENT '电话',
  `fax` varchar(32) DEFAULT NULL COMMENT '传真',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序号',
  `project` varchar(32) NOT NULL COMMENT '项目编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='承办单位表';

DROP TABLE IF EXISTS `pro_menu`;
CREATE TABLE `pro_menu` (
  `id` varchar(32) NOT NULL COMMENT '菜单ID',
  `code` varchar(32) NOT NULL COMMENT '菜单编码',
  `title` varchar(32) NOT NULL COMMENT '菜单标题',
  `url` varchar(255) NOT NULL COMMENT '菜单地址',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` tinyint(2) NOT NULL DEFAULT 1 COMMENT '0:禁用,1:启用',
  `type` tinyint(2) NOT NULL DEFAULT 0 COMMENT '菜单类别(0:普通的,1:负责人的)',
  `link` tinyint(2) NOT NULL DEFAULT 0 COMMENT '链接类型(0:站内链接,1:站外链接)',
  `target` varchar(32) DEFAULT NULL COMMENT '重定向操作',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目菜单表';

DROP TABLE IF EXISTS `sys_app`;
CREATE TABLE `sys_app` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `channel` varchar(32) NOT NULL COMMENT '渠道',
  `appKey` varchar(64) NOT NULL UNIQUE COMMENT '授权KEY',
  `appSecret` varchar(64) NOT NULL COMMENT '授权密码',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `status` tinyint(2) NOT NULL COMMENT '0:禁用,1:启用',
  `docId` varchar(32) DEFAULT NULL COMMENT '图档ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='应用授权表';

DROP TABLE IF EXISTS `sys_app_token`;
CREATE TABLE `sys_app_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `authToken` varchar(32) NOT NULL UNIQUE COMMENT '授权验证',
  `appId` bigint(20) NOT NULL COMMENT '应用ID',
  `loginAt` datetime NOT NULL COMMENT '登录时间',
  `loginIp` varchar(20) NOT NULL COMMENT '登录IP',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='应用验证表';

DROP TABLE IF EXISTS `labor_worker`;
CREATE TABLE `labor_worker` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `workday` date NOT NULL COMMENT '工作日',
  `realNum` int(11) NOT NULL DEFAULT 0 COMMENT '实际人数',
  `planNum` int(11) NOT NULL DEFAULT 0 COMMENT '计划人数',
  `carft` varchar(16) NOT NULL COMMENT '工种',
  `project` varchar(32) NOT NULL COMMENT '项目编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='劳务工人表';

--增加索引
create index `idx_pro_prop_project` on `pro_prop` (`project`,`propSet`,`name`);
create index `idx_pro_picture_project` on `pro_picture` (`project`);
create index `idx_pro_event_project` on `pro_event` (`project`);
create index `idx_pro_folder_name` on `pro_folder` (`name`);
create index `idx_pro_folder_entity` on `pro_folder` (`entity`,`entityId`);
create index `idx_pro_vrshow_stage` on `pro_vrshow` (`project`,`stage`);
create index `idx_pro_problem_code` on `pro_problem` (`project`,`code`);
create index `idx_log_problem_creator` on `log_problem` (`project`,`creator`);
create index `idx_sys_app_token` on `sys_app_token` (`appId`);
create index `idx_labor_worker_day` on `labor_worker` (`project`,`workday`);
create index `idx_pro_letter_project` on `pro_letter` (`project`);
create index `idx_pro_newspaper_project` on `pro_weekly_newspaper`(`project`);
create index `idx_log_document_project` on `log_document` (`project`,`createAt`);
create index `idx_pro_organizer_project` on `pro_organizer` (`project`);

--初始化数据
insert into `pro_menu` (`id`, `code`, `title`, `url`, `sort`, `status`, `type`, `link`, `target`) values
('gooverview','项目.概况.进入','概况','/ysdOverview','1','1','0','0','_self'),
('golabor','项目.智慧工地.进入','智慧工地','http://139.159.226.224:9000/project/index.html?%93%E2%D7%D5%CD%CB%CE%DD%E9%E4%A9kcegikpkdkr%AE%D9%D8%E7','2','1','0','1','_blank'),
('goplan','项目.计划.进入','计划','/ysdScheduling/plan','3','1','0','0','_self'),
('goschedule','项目.进度.进入','进度','/ysdScheduling/time','4','1','0','0','_self'),
('gotask','项目.任务.进入','任务','/ysdScheduling/task','5','1','0','0','_self'),
('goweeklyreport','项目.周报.进入','周报','/Document/Document.aspx?isMajor=0','6','1','0','0','_self'),
('godocument','项目.图档.进入','图档','/Document/Document.aspx?isMajor=1','7','1','0','0','_self'),
('gomodel','项目.模型.进入','模型','/ysdBvShow/model','8','1','0','0','_self'),
('golibrary','项目.资料库.进入','资料库','/Document/Library.aspx','9','1','0','0','_self'),
('goquantity','项目.工程量.进入','工程量','','10','1','0','0','_self'),
('gomaterial','项目.物料追踪.进入','物料追踪','/Mate/Mate.aspx','11','1','0','0','_self'),
('gomate2','项目.构件跟踪.进入','构件跟踪','/Mate/Mate2.aspx','12','1','0','0','_self'),
('govr','项目.VR展示.进入','VR展示','/ysdOverview/vrshow','13','1','0','0','_self'),
('goMemberGroup','项目.组织角色.进入','组织角色','/GroupRole/GroupRole.aspx','14','1','1','0','_self'),
('gooperations','项目.运维.进入','运维','/ysdOperations','15','1','0','0','_blank');



DROP TABLE IF EXISTS `cmd_surve_vdo`;
CREATE TABLE `cmd_surve_vdo` (
	`id` int(11) NOT NULL auto_increment,
	`name` VARCHAR(32) NOT NULL COMMENT '名称',
	`sort` TINYINT(2) DEFAULT NULL COMMENT '排序',
	`vdo_path` VARCHAR(500) NOT NULL COMMENT '视频路径',
	`pic_path` VARCHAR(500) DEFAULT NULL COMMENT '图片路径',
	`type` VARCHAR(32) NOT NULL COMMENT '类型(venture:风投)',
	`project` VARCHAR(32) NOT NULL COMMENT '项目编号',
	`is_act` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
	`is_del` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	`create_at` datetime NOT NULL COMMENT '创建时间',
	`update_at` datetime DEFAULT NULL COMMENT '最后修改时间',
PRIMARY KEY(`id`)
)ENGINE=INNODB DEFAULT charset=utf8 COMMENT='指挥页视频监控表';

--DROP TABLE IF EXISTS `cmd_check_in_count`;
CREATE TABLE `cmd_check_in_count` (
	`id` int(11) NOT NULL auto_increment,
	`gmt_create` datetime NOT NULL COMMENT '创建时间',
	`gmt_modified` datetime DEFAULT NULL COMMENT '最后修改时间',
	`is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '1 表示删除，0 表示未删除',
	`creator` VARCHAR(32) NOT NULL COMMENT '创建人',
	`creator_num` VARCHAR(32) NOT NULL COMMENT '创建人编号',
	`project` VARCHAR(32) NOT NULL COMMENT '项目编号',
	`gmt_date` datetime NOT NULL COMMENT '日期',
	`check_in_num` int(5) NOT NULL DEFAULT 0 COMMENT '考勤人数',
	`scene_num` int(5) NOT NULL DEFAULT 0 COMMENT '在场人数',
	`type` VARCHAR(32) NOT NULL COMMENT '类型(venture:风投)',
PRIMARY KEY(`id`)
)ENGINE=INNODB DEFAULT charset=utf8 COMMENT='指挥页考勤统计表';

INSERT INTO cmd_check_in_count (`gmt_create`,`creator`,`creator_num`,`project`,`type`,`gmt_date`,`check_in_num`,`scene_num`)
VALUES
(NOW(),'郑希彬','678','310','venture','2019-12-01',212,298),
(NOW(),'郑希彬','678','310','venture','2019-12-02',231,322),
(NOW(),'郑希彬','678','310','venture','2019-12-03',241,331),
(NOW(),'郑希彬','678','310','venture','2019-12-04',223,328),
(NOW(),'郑希彬','678','310','venture','2019-12-05',208,339),
(NOW(),'郑希彬','678','310','venture','2019-12-06',274,337),
(NOW(),'郑希彬','678','310','venture','2019-12-07',287,345),
(NOW(),'郑希彬','678','310','venture','2019-12-08',263,340),
(NOW(),'郑希彬','678','310','venture','2019-12-09',253,342),
(NOW(),'郑希彬','678','310','venture','2019-12-10',249,342);


