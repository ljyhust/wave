# ribbon组件说明

## 负载均衡配置

ribbon-starter启动时 `RibbonClientConfiguration` 注入 `ServerList<Server>` 用于注入相关配置；
nacos-discovery启动时 `NacosRibbonClientConfiguration` 注入 `ServerList<Server>`替换以上注入；
RibbonClientConfiguration 注入Bean `ILoadBalancer`
在具体 `RestTemplate`中调用时，则会调用 ILoadBalancer.chooseSever方法 -> `ServerList.getList` -> nacosConfig http从注册中心获取列表
ILoadBalancer.rule从 severList 中选择server 负载

```java
@Bean
@ConditionalOnMissingBean
public ServerList<Server> ribbonServerList(IClientConfig config) {
    if (this.propertiesFactory.isSet(ServerList.class, this.name)) {
        return (ServerList)this.propertiesFactory.get(ServerList.class, config, this.name);
    } else {
        ConfigurationBasedServerList serverList = new ConfigurationBasedServerList();
        serverList.initWithNiwsConfig(config);
        return serverList;
    }
}
```
