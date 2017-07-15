OIUE
=======

#Open Intelligent Unitive Efficient
##开放 智能 统一 高效
题外话：

	不知道你有没有修改开源项目的经历，好比在一锅乱炖里去捞所有的白菜，很多时候，需要把一锅都翻遍才能找出所有的白菜叶，我们希望倡导一种方式，完全遵循解耦的架构，从根本去解决这种一锅乱炖的局面。
	
	OIUE 开源的目的是推进轻量化、标准化、架构解耦、模块化以及去框架化，框架立足将模块切分最小粒度，确保每个模块均可单独替换、并最小限度模块依赖，开发者无需培训即可快速入手，亦不用关心底层架构、分布式服务调用及服务治理等相关繁琐复杂的实现。1.0版本框架采用了OSGI的底层容器，部分遵循OSGI标准，但模块Activator请严格遵循本项目，后续将推出多个版本的启动容器，以满足不同场景下业务需求，同时也欢迎有兴趣的朋友共同加入与探讨。
	
	作为程序开发人员，每天都在重复同样的工作，浪费了大量时间，严重影响了开发热情，简单而又重复的工作占用了我们太多的时间。程序语言由低级逐渐走向高级，出现了许多控件及框架，Java中spring等是很不错的框架，其应用广泛程度难有出其右者，但极其庞大的体系结构让人望而却步，笨重而又耦合的模块体系是在让人无法恭维，强制的调用及结构已经改变了语言原本的色彩，其本质并未从根本上解决程序开发中大量的重复工作，我们希望有一个工具或者一套设计，能够按照我们所习惯的操作模式，最大程度的复用、更高程度的解耦，延续一些开发偏好，以及更大的灵活性，不约定俗成的满足我们的开发需求，开放、智能、统一、高效，本软件框架构想由此应运而生。
	
	许多人会好奇为什么选择OSGI作运行容器，“OSGI架构师的天堂”这句话并不足以概括初衷，最重要的是，OSGI优秀的模块化结构，要求我们更加严谨，同时也让我们更加重视对封装、模块化的理解，模块应该尽可能减少对外界的依赖除非逼不得已，我见过太多原本应该最小实现却引用一大堆附加无用的功能的工具包，这里不一一列举，无力吐槽、仁者见仁智者见智吧，筑建一个开放、严谨、高效的开发群体，是本架构的初衷之一。
		
	
	从未见过哪个男人跟女生吵架能吵赢的，不是气急败坏地动起了手，就是沉默以对。这仅仅是男人的问题吗？达尔文说：“以前也有吵赢的，后来他们都找不到女朋友，于是灭绝了。”
	
	我们不希望出现类似与上面的故事，从前有一群很刻苦的人，由于没有时间恋爱，后来灭绝了！

开源库名称|描述
---|---
OIUE主工程（服务启动容器felix）|https://github.com/OIUE/OIUE
OIUE基于OSGI打包配置|https://github.com/OIUE/OIUE-CONFIGURATOR
OIUE核心服务（容器隔离及核心服务定义）|https://github.com/OIUE/OIUE-CORE
OIUE核心服务实现|https://github.com/OIUE/OIUE-BASE
OIUE基于jdbc事物封装|https://github.com/OIUE/OIUE-ODPS
OIUE开放服务|https://github.com/OIUE/OIUE-SERVICES

服务名称|纯接口|bundle
---|---|---
Action服务定义|Y|org.oiue.service.action.api-1.0.0.jar
Action调度服务实现|N|org.oiue.service.action.base-1.0.0.jar
ActionFilter认证服务|N|org.oiue.service.action.filter.auth-1.0.0.jar
免登陆调试服务|N|org.oiue.service.action.filter.auth.debug-1.0.0.jar
Action服务HTTP方式访问实现OLD|N|org.oiue.service.action.http.action-1.0.0.jar
HTTP图片验证码服务|N|org.oiue.service.action.http.imageCode-1.0.0.jar
Action服务HTTp方式访问实现NEW|N|org.oiue.service.action.http.services-1.0.0.jar
HTTP文件上传服务|N|org.oiue.service.action.http.upload-1.0.0.jar
Action服务TCP方式JSON访问实现|N|org.oiue.service.action.tcp.action-1.0.0.jar
Action服务TCP方式流访问实现|N|org.oiue.service.action.tcp.bytes-1.0.0.jar
认证服务定义|Y|org.oiue.service.auth-1.0.0.jar
认证调度服务实现|N|org.oiue.service.auth.impl-1.0.0.jar
本地认证服务实现(直接查jdbc库)|N|org.oiue.service.auth.local-1.0.0.jar
自定义缓存定义|Y|org.oiue.service.buffer-1.0.0.jar
自定义缓存实现|N|org.oiue.service.buffer.impl-1.0.0.jar
自定义缓存同步服务|N|org.oiue.service.buffer.synchronization.db-1.0.0.jar
流数据编解码处理服务定义|Y|org.oiue.service.bytes.api-1.0.0.jar
流数据编解码调度处理服务实现|N|org.oiue.service.bytes.base-1.0.0.jar
数据流编解码|N|org.oiue.service.bytes.bytes-1.0.0.jar
整形数据编解码|N|org.oiue.service.bytes.int16-1.0.0.jar
字符串编解码|N|org.oiue.service.bytes.string-1.0.0.jar
缓存服务定义|Y|org.oiue.service.cache-1.0.0.jar
自定义缓存实现|N|org.oiue.service.cache.buffer-1.0.0.jar
缓存调度实现|N|org.oiue.service.cache.impl-1.0.0.jar
混存redis封装|N|org.oiue.service.cache.jedis-1.0.0.jar
混存脚本操作|N|org.oiue.service.cache.script-1.0.0.jar
树状结构缓存定义|Y|org.oiue.service.cache.tree-1.0.0.jar
树状缓存脚本操作|N|org.oiue.service.cache.tree.script-1.0.0.jar
树状缓存zookeeper封装|N|org.oiue.service.cache.tree.zookeeper.curator-1.0.0.jar
缓存调试服务|N|org.oiue.service.debug.cache-1.0.0.jar
Http客户端调试服务|N|org.oiue.service.debug.httpclient-1.0.0.jar
资源操作调试服务|N|org.oiue.service.debug.res-1.0.0.jar
树状缓存调试服务|N|org.oiue.service.debug.treecache-1.0.0.jar
数据接入总线服务定义|Y|org.oiue.service.driver.api-1.0.0.jar
数据接入总线调度实现|N|org.oiue.service.driver.base-1.0.0.jar
数据总线过滤实现|N|org.oiue.service.driver.filter.impl-1.0.0.jar
数据总线订阅实现|N|org.oiue.service.driver.listener.impl-1.0.0.jar
数据总线存储实现|N|org.oiue.service.driver.listener.storage-1.0.0.jar
事件执行服务定义|Y|org.oiue.service.event.execute-1.0.0.jar
事件执行服务实现|N|org.oiue.service.event.execute.impl-1.0.0.jar
获取系统时间事件定义|Y|org.oiue.service.event.system.time-1.0.0.jar
获取系统时间事件实现|N|org.oiue.service.event.system.time.impl-1.0.0.jar
文件上传服务定义|Y|org.oiue.service.file.upload-1.0.0.jar
文件上传服务实现|N|org.oiue.service.file.upload.impl-1.0.0.jar
HTTP客户端服务定义|Y|org.oiue.service.http.client-1.0.0.jar
apacheHttp客户端封装|N|org.oiue.service.http.client.apache-1.0.0.jar
日志服务定义|Y|org.oiue.service.log-1.0.0.jar
日志Log4j封装|N|org.oiue.service.log4j-1.0.0.jar
消息服务定义|Y|org.oiue.service.message-1.0.0.jar
消息服务实现|N|org.oiue.service.message.impl-1.0.0.jar
JDBC事务基础服务|N|org.oiue.service.odp.base-1.0.0.jar
H2持久层底层公共方法定义|N|org.oiue.service.odp.dmo.h2-1.0.0.jar
Mysql持久层底层公共方法定义|N|org.oiue.service.odp.dmo.mysql-1.0.0.jar
Neo4j持久层底层公共方法定义|N|org.oiue.service.odp.dmo.neo4j-1.0.0.jar
Postgresql持久层底层公共方法定义|N|org.oiue.service.odp.dmo.postgresql-1.0.0.jar
数据操作事件定义|Y|org.oiue.service.odp.event.api-1.0.0.jar
数据操作事件Mysql实现|N|org.oiue.service.odp.event.dmo.mysql-1.0.0.jar
数据操作事件Mysql查询实现|N|org.oiue.service.odp.event.dmo.mysql.q-1.0.0.jar
数据操作事件Mysql单条查询实现|N|org.oiue.service.odp.event.dmo.mysql.select-1.0.0.jar
数据操作事件Mysql多条查询实现|N|org.oiue.service.odp.event.dmo.mysql.selects-1.0.0.jar
数据操作事件Mysql查询事件转换实现|N|org.oiue.service.odp.event.dmo.mysql.t-1.0.0.jar
数据操作事件Neo4j实现|N|org.oiue.service.odp.event.dmo.neo4j-1.0.0.jar
数据操作事件Neo4j插入实现|N|org.oiue.service.odp.event.dmo.neo4j.insert-1.0.0.jar
数据操作事件Postgresql实现|N|org.oiue.service.odp.event.dmo.postgresql-1.0.0.jar
数据操作事件Postgresql查询实现|N|org.oiue.service.odp.event.dmo.postgresql.q-1.0.0.jar
数据操作事件Postgresql查询实现|N|org.oiue.service.odp.event.dmo.postgresql.query-1.0.0.jar
数据操作事件Postgresql查询实现|N|org.oiue.service.odp.event.dmo.postgresql.selects-1.0.0.jar
事件sql处理定义|Y|org.oiue.service.odp.event.sql.structure-1.0.0.jar
事件sql处理实现|N|org.oiue.service.odp.event.sql.structure.impl-1.0.0.jar
资源操作服务定义|Y|org.oiue.service.odp.res.api-1.0.0.jar
资源操作服务业务实现|N|org.oiue.service.odp.res.base-1.0.0.jar
资源操作持久层定义|Y|org.oiue.service.odp.res.dmo-1.0.0.jar
H2资源操作持久层实现|N|org.oiue.service.odp.res.dmo.h2-1.0.0.jar
Mysql资源操作持久层实现|N|org.oiue.service.odp.res.dmo.mysql-1.0.0.jar
Neo4j资源操作持久层实现|N|org.oiue.service.odp.res.dmo.neo4j-1.0.0.jar
sql处理定义|Y|org.oiue.service.odp.structure-1.0.0.jar
sql处理实现|N|org.oiue.service.odp.structure.impl-1.0.0.jar
查询sql处理实现|N|org.oiue.service.odp.structure.selectsql-1.0.0.jar
在线维护服务定义|Y|org.oiue.service.online-1.0.0.jar
在线维护服务实现|N|org.oiue.service.online.impl-1.0.0.jar
启动容器隔离封装服务（基于OSGI）|N|org.oiue.service.osgi.rpc-1.0.0.jar
权限校验服务定义|Y|org.oiue.service.permission-1.0.0.jar
权限校验调度服务实现|N|org.oiue.service.permission.impl-1.0.0.jar
访问鉴权及转换服务|N|org.oiue.service.permission.verify-1.0.0.jar
JDBC连接池定义|Y|org.oiue.service.sql-1.0.0.jar
Apache的JDBC连接池封装|N|org.oiue.service.sql.apache-1.0.0.jar
系统分析服务定义|Y|org.oiue.service.system.analyzer-1.0.0.jar
系统分析服务实现|N|org.oiue.service.system.analyzer.impl-1.0.0.jar
定时任务调度服务定义|Y|org.oiue.service.task-1.0.0.jar
定时任务调度|N|org.oiue.service.task.quartz-1.0.0.jar
TCP/UDP服务定义|Y|org.oiue.service.tcp-1.0.0.jar
Mina封装|N|org.oiue.service.tcp.mina-1.0.0.jar
模板定义|Y|org.oiue.service.template.api-1.0.0.jar
模板管理服务实现|N|org.oiue.service.template.base-1.0.0.jar
beetl封装|N|org.oiue.service.template.beetl-1.0.0.jar
velocity封装|N|org.oiue.service.template.velocity-1.0.0.jar
线程池定义|Y|org.oiue.service.threadpool-1.0.0.jar
线程池实现|N|org.oiue.service.threadpool.impl-1.0.0.jar
工具包|N|org.oiue.tools-1.0.0.jar





一个典型的Activator

```
package org.oiue.service.action.http.action;

import java.util.Dictionary;

import org.oiue.service.action.api.ActionService;
import org.oiue.service.log.LogService;
import org.oiue.service.log.Logger;
import org.oiue.service.osgi.FrameActivator;
import org.oiue.service.osgi.MulitServiceTrackerCustomizer;
import org.osgi.service.http.HttpService;

public class Activator extends FrameActivator {

    @Override
    public void start() throws Exception {
        this.start(new MulitServiceTrackerCustomizer() {
            private String url = getProperty("org.oiue.service.action.http.root") + "/action";
            private HttpService httpService;
            private PostServlet posServlet;

            @Override
            public void removedService() {
                httpService.unregister(url);
            }

            @Override
            public void addingService() {
                httpService = getService(HttpService.class);
                LogService logService = getService(LogService.class);
                ActionService actionService = getService(ActionService.class);

                posServlet = new PostServlet(actionService, logService);
                Logger log = logService.getLogger(this.getClass());
                if (log.isInfoEnabled()) {
                	log.info("绑定url：" + url);
				}
                try {
                    httpService.registerServlet(url, posServlet, null, null);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

            @Override
            public void updated(Dictionary<String, ?> props) {
                posServlet.updated(props);
            }
        }, HttpService.class, ActionService.class, LogService.class);
    }

    @Override
    public void stop() throws Exception {}
}
```
[more](http://www.oiue.org)

