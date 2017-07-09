package tk.kejie.jscrawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ykj on 17/6/30.
 */
public class JsoupEngine extends RequestEngine {
    private static final String TAG = "JsoupEngine";

    protected Connection connection;

    @Override
    protected void processUrl(RequestModel model) {
        String url = model.getUrl();
        connection = Jsoup.connect(url).ignoreHttpErrors(true);
    }

    @Override
    protected void processMethod(RequestModel model) {
        String method = model.getMethod();
        if("POST".equals(method)) {
            connection.method(Connection.Method.POST);
        }else{
            connection.method(Connection.Method.GET);
        }
    }

    @Override
    protected void processHeader(RequestModel model) {
        Map<String, String> headers = model.getHeaders();
        if(headers != null && headers.get("Content-Type") == null) {
            connection.ignoreContentType(true);
        }
        connection.headers(model.getHeaders());
    }

    @Override
    protected void processData(RequestModel model) {
        if(model.getData() != null) {
            connection.data(model.getData());
        }
    }

    @Override
    protected void processCookie(RequestModel model) {
        if(model.getCookies() != null) {
            connection.cookies(model.getCookies());
        }
    }

    @Override
    protected void processTimeout(RequestModel model) {
        if(model.getTimeout() != null) {
            connection.timeout(model.getTimeout());
        }
    }

    @Override
    protected void processBody(RequestModel model) {
        if(model.getBody() != null) {
            connection.method(Connection.Method.POST);
            connection.header("Content-Type", "application/json");
            connection.requestBody(model.getBody());
        }
    }

    @Override
    public String execute() {
        Map<String, Object> resMap = new HashMap<>();
        try {
            Connection.Response response = connection.execute();
            resMap.put("code", response.statusCode());
            resMap.put("message", response.statusMessage());
            resMap.put("body", response.body());
        } catch (IOException e) {
            e.printStackTrace();
            resMap.put("code", "-1");
            resMap.put("message", "Request Exception");
            resMap.put("body", "");
        }
        return gson.toJson(resMap);
    }

}
