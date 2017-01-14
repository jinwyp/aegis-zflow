-- 文件资源管理
create table asset(
  id BIGINT not null auto_increment,        -- 非业务主键
  asset_id varchar(36) not null,            -- 资源id
  file_type varchar(128) not null,          -- 文件类型  0: 未知,  1: pdf, 2: image
  busi_type varchar(30) not null DEFAULT 0, -- 业务类别
  username varchar(128) not null,           -- 上传用户
  description varchar(512),                 -- 可以为空
  url varchar(256) not null,                -- 文件位置信息, 可能为aliyun, filesystem  etc
  origin_name varchar(256) not null,        -- 文件原始名字
  ts_c timestamp default current_timestamp,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE UNIQUE INDEX asset_index ON asset(asset_id);