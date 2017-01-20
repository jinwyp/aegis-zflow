-- 流程实例
create table flow_instance(
  id BIGINT not null auto_increment,
  flow_id varchar(64) not null,
  flow_type varchar(16) not null,     -- cang   ying
  guid varchar(64) not null,  -- 用户id  ?????
  data  varchar(8192),         -- 流程上下文
  state varchar(1024),
  finished TINYINT not NULL ,    -- 0：未完成 1：已完成
  ts_c timestamp default current_timestamp,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- flow_id 唯一索引
CREATE UNIQUE INDEX flowId_index ON flow_instance(flow_id);

-- 流程任务
create table flow_task(
  id BIGINT not null auto_increment,
  flow_id    varchar(64)    not null,
  flow_type  varchar(16)    not null,
  task_id    varchar(128)   not null,
  task_name  varchar(64)    not null,
  task_submit varchar(1024) not null,   -- 用户提交数据
  guid VARCHAR(64) not null,
  ts_c timestamp default current_timestamp,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- flow_id和task_id唯一索引
CREATE UNIQUE INDEX flowId_taskid_index ON flow_task(flow_id,task_id);

-- 用户流程设计
create table design(
  id BIGINT not null auto_increment,
  name varchar(64) not null,
  json text(65532),
  meta text(65532) not null,
  ts_c timestamp default current_timestamp,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 流程部署
create table deploy(
  id BIGINT not null auto_increment,
  flow_type varchar(64) not null,
  jar blob(104857600) not null,   -- 100M
  enable bool not null,           -- 激活
  ts_c timestamp default current_timestamp,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE UNIQUE INDEX flow_type_index ON deploy(flow_type);