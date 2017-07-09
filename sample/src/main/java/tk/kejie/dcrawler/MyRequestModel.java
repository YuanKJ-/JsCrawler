package tk.kejie.dcrawler;

import tk.kejie.jscrawler.RequestModel;

/**
 * Created by ykj on 17/7/8.
 */
public class MyRequestModel extends RequestModel {
    private String proxy;

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }
}
