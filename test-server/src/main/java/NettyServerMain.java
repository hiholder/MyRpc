import com.hodur.HelloService;
import com.hodur.common.URL;
import com.hodur.remote.annotation.RpcScan;
import com.hodur.remote.remoting.transport.netty.server.NettyRpcServer;
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
        URL url = new URL("test1", "version1");
        nettyRpcServer.registerService(helloService2, url);
        nettyRpcServer.start();
    }
}
