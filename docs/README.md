# wave项目后台开发

## wave-user 用户系统设计
- [x] 账号分库  

- [x] 用户信息分库

- [x] 好友关注：关注与被关注

## wave-blog 帖子中心设计
- [ ] 帖子分库

> 基因分库法，采用uid分库，取uid % n ^ 2 作为分库索引号，tid = 60 bit + n bit 形成tid，
>查询时，通过tid查时，则需要 tid % n ^ 2 取出后n bit位，定位到库，然后再根据tid查到数据

- [ ] 帖子信息

- [ ] 首页热帖

- [ ] 好友帖子

- [ ] 关注好友的帖子

- [ ] ES同步

## wave-url 短链接系统设计
帖子链接设计

统计功能

## wave-search 搜索组件设计
数据同步：增量 MQ / cannal

帖子搜索

用户搜索


## wave-trx 分布式事务设计

## wave-admin 运营层设计
大量数据查看：同步spark

## wave-gate 网关

## wave-zk zk一致性使用

## wave-demo 技术应用demo

## api设计
### 如何识别用户
wave-gate网关层实现用户认证，转发请求时header加入user等身份识别

### API安全

