// 参与方类别管理
GET  /party?limit=10&offset=20             参与方类别列表
POST /party/:className/:description        创建参与方类别(!)
GET  /party/:className                     查询参与方类别
PUT  /party/:id/:className/:description    更新参与方类别

// 参与方运营组管理
GET    /group/:party_class?limit=10&offset=20      参与方运营组列表
POST   /group/:party_class/:gid/:description       创建参与方运营组(!)
DELETE /group/:party_class/:gid                    删除参与方运营组
PUT    /group/id/:party_class/:gid/:description    更新参与方运营组

// 参与方实例管理
POST /inst/:party_class/:instance_id/party_name       创建参与方实例(!)
PUT  /inst/:id/:party/:instance_id/:party_name        更新参与方实例
GET  /inst/:party/:instance_id                        查询参与方实例

// 参与方用户管理(:class + :instance_id = userType)
POST /user/:party/:instance_id/:userId                 创建用户(!)
GET  /user/:party/:instance_id/:userId                 查询用户  -- 应该拿到: 1. 用户的基本信息, 2. 用户的任务
GET  /user/:party/:instance_id?limit=10&offset=20      用户列表  -- 拿到用户的列表信息
PUT  /user/:party/:instance_id:/:userId                更新用户  -- 更新用户的基本信息
