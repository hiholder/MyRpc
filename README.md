# MyRpc
一个基于JavaGuide哥的rpc框架的升级版rpc
* 仿照Dubbo完善了服务注册和服务发现，对外接口暴露在com.hodur.registry.api中。具体实现在zookeeper类中。
* 完成了URL（总线）类的基础功能
* 完成了nacos注册中心的实现
* 完善了SPI注解，实现加载默认类
* 使用netty4实现网络通信
* 使用kryo实现序列化
* 实现了负载均衡

# 待完成事项
- [ ] 服务订阅，取消订阅
- [ ] 过滤器的实现
- [ ] 监控器的实现

