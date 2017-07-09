package tk.kejie.dcrawler;

import tk.kejie.jscrawler.JsoupEngine;
import tk.kejie.jscrawler.RequestModel;

/**
 * Created by ykj on 17/7/8.
 */
public class MyJsoupEngine extends JsoupEngine {

    protected void processProxy(MyRequestModel model) {
        if (model.getProxy() != null) {
            String[] proxy = model.getProxy().split(":");
            if (proxy.length > 1) {
                // connection是jsoup请求的关键对象
                connection.proxy(proxy[0], Integer.parseInt(proxy[1]));
            }
        }
    }

    @Override
    protected void process(RequestModel model) {
        super.process(model);
        processProxy((MyRequestModel) model);
    }

    @Override
    protected RequestModel jsonToModel(String request) {
        return gson.fromJson(request, MyRequestModel.class);
    }
}
