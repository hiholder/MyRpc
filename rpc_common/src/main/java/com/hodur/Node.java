package com.hodur;

public interface Node {
    /**
     * @describe 获取url
     * @author Hodur
     * @date 2021/12/3 10:25
     * @return com.hodur.URL
     */
    URL getUrl();
    /**
     * @describe 可用
     * @author Hodur
     * @date 2021/12/3 10:25
     * @return boolean
     */
    boolean isAvailable();
    /**
     * @describe 销毁
     * @author Hodur
     * @date 2021/12/3 10:25
     */
    void destroy();

}
