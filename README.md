## RPC通信基础框架



---

### 1. 基础实现

- **传输 & 协议**

    采用 TCP 协议为通信基础，基于 Netty 自定义数据包格式，心跳机制维持 TCP 单一长链接

- **注册中心**

    缺省基于 ZooKeeper 实现服务注册和服务发现，可扩展

- **序列化**

    默认基于 ProtoStuff 实现序列化机制，可扩展
    
- **负载均衡**

    默认 Random 访问，可扩展
    
    ...

---

### 2. 结构说明

- signal-base 
    
    封装了上述提到的 RPC 各**基础组件和扩展点**；同时将需要发布的上层接口放在 common.service 包下
        
- signal-front 

    基于 Netty 实现简易版独立的 HttpServer；同时引入 base 包提供作为 RPC Client 端的基础向下游发起调用
    ```
    /** 通过注解和继承 指定path和输入输出类型，扩展接口无需关注通信细节 */
    @HandlerTag(path = "/front") 
    public class TransHandler extends Handler<RequestInfo, ResponseInfo> {
        @Override
        public ResponseInfo handle(RequestInfo requestInfo) {
            ...
            return respInstance;
        }
    }
    ```
- signal-core 、 signal-route 、 signal-data

    引入 base 包、配置 config-rpc.properties 后通过 @RpcServiceTag 发布注册服务，使用 RpcClient.create(XService.class) 远程调用服务，同时可以在 Client 端自行扩展负载均衡策略
    

---

### 3. 使用示例

- Step-1： 没有扩展注册中心情况下，服务通信默认需要提前安装启动ZooKeeper；

- Step-2： 确认配置信息，如果ZK正常启动缺省值即可用；

config-rpc.properties
```
registration.address=host:port          # 注册中心 host:port
server.address=port                     # 当前服务占用的端口
server.basePackage=com.skr.xxx          # 递归扫描配置包下的服务提供者并注册服务
...
```

- Step-3： 启动 DataMain、RouteMain、CoreMain、FrontMain

- Step-4： 向前置（front）节点发送Http请求

```
curl  -X POST --data '{"traceId":"traceId","businessId":"businessId","requestKey":"requestKey"}' http://127.0.0.1:9001/front
```
Response：
```
{"answer":"[core][route][data]requestKey[data][route][core]"}
```

--- 

<br>
<div align=center><img src="https://github.com/BBLLMYD/netty-stroll/blob/master/other/img.png?raw=true" width="677" alt="应用模型示例" ></div>
<br>




