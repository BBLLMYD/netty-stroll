RPC通信基础框架

---

- **注册中心**

    缺省基于ZooKeeper实现服务注册和服务发现，可扩展

- **序列化**

    默认基于ProtoStuff实现序列化机制，可扩展
    
- **负载均衡**

    默认Random访问，可扩展
    
- **传输 & 协议**

    采用TCP协议为通信基础，基于Netty自定义数据包格式，心跳机制维持TCP单一长链接

...

---


<div align=center><img src="https://github.com/BBLLMYD/netty-stroll/blob/master/other/img.jpg?raw=true" width="556" alt="应用模型示例" ></div>
<div align=center>应用模型示例</div>
<br>

PS：服务通信需要提前启动ZooKeeper，并配置在classpath下config-rpc.properties文件，默认配置了本地2181端口

```
registration.address=host:port          # 注册中心 host:port
server.address=port                     # 当前服务占用的端口
server.basePackage=com.skr.xxx          # 递归扫描配置包下的服务提供者并注册服务
...
```
向前置发送Http请求：
```
curl  -X POST --data '{"traceId":"traceId","businessId":"businessId","requestKey":"requestKey"}' http://127.0.0.1:9001/front
```
Response：
```
{"answer":"[core][route][data]requestKey[data][route][core]"}
```


