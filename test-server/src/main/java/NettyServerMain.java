import com.hodur.HelloService;
import com.hodur.annotation.RpcScan;
import com.hodur.URL;
import com.hodur.remoting.transport.netty.server.NettyRpcServer;
import com.hodur.serviceimpl.HelloServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Hodur
 * @className NettyServerMain.java
 * @description
 * @date 2021年04月15日 22:08
 */
@RpcScan(basePackage = {"com.hodur"})
public class NettyServerMain {
    public static void main(String[] args) {
        // Register service via annotation
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        // Register service manually
        HelloService helloService2 = new HelloServiceImpl();
        URL url = URL.builder()
                .group("test1").version("version1").build();
        nettyRpcServer.registerService(helloService2, url);
        nettyRpcServer.start();
    }
}
