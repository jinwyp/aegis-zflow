--
流程部分
--

1. 创建流程
POST  /flow?guid=:guid&flowType=:flowType

2. 查询流程          guiid, flowType, status为可选参数, 若page没指定, 
GET   /flow?
guid=:guid&
flowType=:flowType&
status=:status&
page=:page&
pageSize=:pageSite

3. 查询指定流程
GET /flow/:flowId

4. 流程劫持(hijack)
PUT /flow/:flowId?trigger=true
{
}

-- 
任务部分
--



 

6. 手动触发自动恩物
POST /auto?flowId=:flowId&taskName=:taskName

--
流程设计
--

1. 用户列出所有流程设计
GET /design/graph

2. 用户加载流程设计
GET /design/graph:id  --> JSON

3. 保存流程设计
POST /design/graph:id  + JSON

4. 下载模板项目
GET /design/download/:id

--
流程部署
--
1. 部署流程
POST /deploy/:flowType  + 文件上传


