#Timo中的术语

下面是一些在Timo的代码或者文档中可能会用到的术语：

- 逻辑数据库：Timo用户眼中的一个表的集合
- 逻辑数据表：Timo用户眼中的一个表
- 物理数据库：MySQL上的一个数据库，由IP、端口、数据库名唯一标识
- 物理数据表：MySQL数据库中的一个表
- MySQL实例：MySQL服务，由IP、端口唯一标识
- 配置库：存放Timo配置信息的物理数据库
- 数据拆分：将存放在同一个数据库中的数据分散存储到多个数据库中
- 水平拆分：将同一个表的数据分散存储到多个数据库中，这些数据库中的表结构完全相同
- 垂直拆分：将一个库中的多个表分散到多个数据库中，这些数据库中的表结构不完全相同
- 数据节点：Timo中的数据拆分单位，表示数据分散存储中的一个部分，也称数据分片
- 数据源：数据节点数据的物理存储位置，在Timo中通常由一个物理数据库组成
- 分片表：逻辑表存储于多个数据节点中，所有这些数据节点的数据共同组成一个完整的表数据
- 全局表：逻辑表在多个数据节点中的数据都是 一样的，是一种冗余
- 单库：指操作仅涉及单个数据节点
- 跨库：指操作涉及多个数据节点
- 路由：数据内容到数据节点的一个映射
- 拆分字段：Timo将根据该字段的值来做路由
- 拆分函数：具体的值到数据节点的一个映射
- 拆分规则：拆分字段和拆分函数之间的一个关联关系，分片表根据它来进行数据的路由
- 服务端口：Timo提供数据服务的端口，通常为8066
- 管理端口：Timo提供管理、监控服务的端口，通常为9066
- 前端连接：前端用户针对Timo的连接
- 后端连接：Timo针对后端MySQL的连接
- 心跳检测：Timo通过定时执行SQL的方式确认后端数据源的可用性
- 宕机切换：Timo发现后端数据源不可用时，将数据节点切换到其他可用的数据源上
- HINT：绕过Timo解析器执行SQL的方法，对前端应用不透明
