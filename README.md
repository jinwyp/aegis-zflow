# aegis-zflow
akka-persistence-based engine

# 上传大文件(100M以内) 需修改数据库配置
以mysql5.6为例
1.进入mysql安装目录
2.my.cnf 添加配置：
     max_allowed_packet = 150M
     innodb_log_file_size = 1G 
