package com.hodur.common.extension;

import com.hodur.common.extension.noAdaptiveMethod.NoAdaptiveMethodExt;
import com.hodur.common.extension.noAdaptiveMethod.impl.ExtImpl1;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;


/**
 * @author Hodur
 * @className ExtensionLoader_Test.java
 * @description
 * @date 2021年11月21日 15:43
 */
public class ExtensionLoader_Test {
    @Test
    public void test_getExtensionLoader_Null() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(),
                    CoreMatchers.containsString("Extension type == null"));
        }
    }
    @Test
    public void test_getExtensionLoader_NotInterface() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(ExtensionLoader_Test.class);
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(),
                    CoreMatchers.containsString("Extension type(class com.alibaba.dubbo.common.extensionloader.ExtensionLoaderTest) is not interface"));
        }
    }
    @Test
    public void test_getExtension() {
        try {
            String res = ExtensionLoader.getExtensionLoader(NoAdaptiveMethodExt.class).getExtension("impl1").echo(null, "impl1");
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_getDefaultExtension() throws Exception {
        NoAdaptiveMethodExt ext = ExtensionLoader.getExtensionLoader(NoAdaptiveMethodExt.class).getDefaultExtension();
        assertThat(ext, instanceOf(ExtImpl1.class));
        String name = ExtensionLoader.getExtensionLoader(NoAdaptiveMethodExt.class).getDefaultExtensionName();
        assertEquals("impl1", name);
    }
}
