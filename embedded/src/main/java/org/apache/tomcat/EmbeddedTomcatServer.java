package org.apache.tomcat;


import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.servlet.DemoServlet;

/**
 * 嵌入式tomcat
 *
 * 优点:灵活部署、任意指定位置、通过复杂的条件判断。
 *
 * 前景：
 *  * 未来的Spring Boot（你如果要做微服务的话）以后肯定会是一个标配了，
 *  * 使用Spring Boot要运行始终要使用嵌入式容器，Spring Boot内嵌容器支持Tomcat、Jetty、Undertow三种。但是默认集成的容器是Tomcat
 *
 * Springboot引入Tomcat：
 * <dependency>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-starter-tomcat</artifactId>
 *             <scope>provided</scope>
 * </dependency>
 *
 * maven插件：
 * <dependency>
 *  <groupId>org.apache.tomcat.maven</groupId>
 *  <artifactId>tomcat7-maven-plugin</artifactId>
 *  <version>2.2</version>
 * </dependency>
 *  插件运行 选择pom.xml文件，击右键——>选择 Run As——> Maven build 在Goals框加加入以下命令: tomcat7:run 命令意思：启动嵌入式tomcat ，并运行当前项目
 * 插件启动原理：
 * Tomcat7RunnerCli 是引导类，Tomcat7RunnerCli 主要依靠 Tomcat7Runner，最终调用Tomcat类（该类由tomcat留给外部实现嵌入式而提供的外部API）
 *
 * Tomcat7Runner
 * run()方法
 * *在提取目录下创建tomcat的各个目录，conf，logs，webapps，work，temp
 * *如果server.xml存在，
 * *使用server.xml启动，则创建Catalina作为container，设置相关属性，调用container的start()方法
 * server.xml不存在，
 * *则new Tomcat()，addWebapp方法，设置StandardContext对象的相关属性
 * *设置tomcat的相关属性，host的相关属性，HttpProtocol，httpPort，设置connector，对webapps目录下的项目add webapps
 * *tomcat.start();
 * *增加tomcat关闭的钩子方法

 *
 * Tomcat类分析:
 * 1.位置:org.apache.catalina.startup.Tomcat
 * 2.该类是public的。
 * 3.该类有Server、Service、Engine、Connector、Host等属性。
 * 4.该类有init()、start()、stop()、destroy()等方法。
 *
 *
 * 手写嵌入式步骤：
 *  1.新建一个Tomcat对象
 *  2.设置Tomcat的端口号
 *  3.设置Context目录
 *  4.添加Servlet容器
 *  5.调用Tomcat对象Start()
 *  6.强制Tomcat等待
 *
 */
public class EmbeddedTomcatServer {

    public static void main(String[] args) throws LifecycleException {
//把目录的绝对的路径获取到
        String classpath = System.getProperty("user.dir");
        System.out.println(classpath);
        //D:\workspace-tomcat\tomcat-maven
        //我们new一个Tomcat
        Tomcat tomcat = new Tomcat();

        //插件是6或者6以前的
        //Embbedded


        //设置Tomcat的端口
        //tomcat.setPort(9090);
        Connector connector = tomcat.getConnector();
        connector.setPort(9091);
        //设置Host
        Host host = tomcat.getHost();
        //我们会根据xml配置文件来
        host.setName("localhost");
        host.setAppBase("webapps");
        //前面的那个步骤只是把Tomcat起起来了，但是没啥东西
        //要把class加载进来,把启动的工程加入进来了
        Context context =tomcat.addContext(host, "/", classpath);

        if(context instanceof StandardContext){
            StandardContext standardContext = (StandardContext)context;
            standardContext.setDefaultContextXml("/Users/mac/PROJECT/WORKSPACE_GITHUB/tomcatNotes/catalina-home/conf/web.xml");
            //我们要把Servlet设置进去
            Wrapper wrapper =  tomcat.addServlet("/", "DemoServlet", new DemoServlet());
            wrapper.addMapping("/king");
        }
        //Tomcat跑起来
        tomcat.start();
        //强制Tomcat server等待，避免main线程执行结束后关闭
        tomcat.getServer().await();
    }
}
