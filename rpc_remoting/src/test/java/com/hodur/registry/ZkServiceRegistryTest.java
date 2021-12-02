package com.hodur.registry;

import com.alibaba.nacos.client.naming.utils.NetUtils;
import com.hodur.URL;
import com.hodur.registry.api.RegistryService;
import com.hodur.registry.zk.ZkServiceDiscovery;
import com.hodur.registry.zk.ZkServiceRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * @author Hodur
 * @className ZkServiceRegistryTest.java
 * @description
 * @date 2021年04月15日 20:43
 */
public class ZkServiceRegistryTest {
    private URL registryUrl;
    private String service = "com.hodur.rpc.test.injvmServie";
    private URL serviceUrl = URL.valueOf("zookeeper://zookeeper/" + service + "?notify=false&methods=test1,test2");
    private ZkServiceRegistry zkServiceRegistry;
    @Before
    public void setUp() throws Exception {

        this.registryUrl = URL.valueOf("zookeeper://localhost:" + 2181);
        zkServiceRegistry = new ZkServiceRegistry(registryUrl);
    }
    @Test
    void should_register_service_successful_and_lookup_service_by_service_name() {
        this.registryUrl = URL.valueOf("zookeeper://localhost:" + 2181);
        zkServiceRegistry = new ZkServiceRegistry(registryUrl);
        InetSocketAddress givenInetSocketAddress = new InetSocketAddress("127.0.0.1", 9333);

        Set<URL> registered;
        for (int i = 0; i < 2; i++) {
            zkServiceRegistry.register(serviceUrl);
            registered =zkServiceRegistry.getRegistered();
            assertThat(registered.contains(serviceUrl), is(true));
        }

        /*ServiceDiscovery zkServiceDiscovery = new ZkServiceDiscovery();
        InetSocketAddress acquiredInetSocketAddress = zkServiceDiscovery.lookupService("com.hodur.registry.zk.ZkServiceRegistry");
        assertEquals(givenInetSocketAddress.toString(), acquiredInetSocketAddress.toString());*/
    }



    @Test
    public void testLookup() {
        Set<URL> registered;
        this.registryUrl = URL.valueOf("zookeeper://localhost:" + 2181);
        zkServiceRegistry = new ZkServiceRegistry(registryUrl);
        List<URL> lookup = zkServiceRegistry.lookup(serviceUrl);
        assertThat(lookup.size(), is(0));

        zkServiceRegistry.register(serviceUrl);
        lookup = zkServiceRegistry.lookup(serviceUrl);
        assertThat(lookup.size(), is(1));
    }
}
