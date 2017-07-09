package tk.kejie.jscrawler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by ykj on 17/7/2.
 */
public class OkHttpEngine extends RequestEngine {
    private static final String TAG = "OkHttpEngine";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    protected int mTimeout = 10000;
    protected OkHttpClient okHttpClient = new OkHttpClient();
    protected Request request;
    protected Request.Builder requestBuilder;

    public OkHttpEngine() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(mTimeout, TimeUnit.MILLISECONDS)
                .build();
    }


    @Override
    protected void processUrl(RequestModel model) {
        requestBuilder = new Request.Builder();
        requestBuilder.url(model.getUrl());
    }

    @Override
    protected void processMethod(RequestModel model) {
        String method = model.getMethod();
        if("POST".equals(method)) {
            requestBuilder.post(new FormBody.Builder().build());
        }else{
            requestBuilder.get();
        }
    }

    @Override
    protected void processHeader(RequestModel model) {
        Map<String, String> headers = model.getHeaders();
        if(headers != null) {
            requestBuilder.headers(Headers.of(headers));
        }
    }

    @Override
    protected void processData(RequestModel model) {
        Map<String, String> data = model.getData();
        if("POST".equals(model.getMethod())) {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> keyVal : data.entrySet()) {
                builder.add(keyVal.getKey(), keyVal.getValue());
            }
            RequestBody formBody = builder.build();
            requestBuilder.post(formBody);
        }else {
            HttpUrl httpUrl = HttpUrl.parse(model.getUrl());
            HttpUrl.Builder builder = httpUrl.newBuilder();
            for (Map.Entry<String, String> keyVal : data.entrySet()) {
                builder.addQueryParameter(keyVal.getKey(), keyVal.getValue());
            }
            requestBuilder.url(builder.build());
        }
    }

    @Override
    protected void processCookie(RequestModel model) {
        Map<String, String> cookies = model.getCookies();
        if(cookies != null) {
            requestBuilder.header("Cookie", getRequestCookieString(cookies));
        }
    }

    @Override
    protected void processTimeout(RequestModel model) {
        //如果有设置timeout且timeout被改变,则新建okHttpClient
        Integer timeout = model.getTimeout();
        if(timeout != null && timeout != mTimeout) {
            mTimeout = timeout;
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(mTimeout, TimeUnit.MILLISECONDS)
                    .build();
        }
    }

    @Override
    protected void processBody(RequestModel model) {
        String body = model.getBody();
        if(body != null) {
            requestBuilder.post(RequestBody.create(JSON, body));
        }
    }

    @Override
    protected String execute() {
        Map<String, Object> resMap = new HashMap<>();
        try {
            Response response = okHttpClient.newCall(requestBuilder.build()).execute();
            resMap.put("code", response.code());
            resMap.put("message", response.message());
            String tmpBody = "";
            ResponseBody body = response.body();
            if(body != null) {
                tmpBody = body.string();
            }
            resMap.put("body", tmpBody);
        } catch (IOException e) {
            e.printStackTrace();
            resMap.put("code", "-1");
            resMap.put("message", "Request Exception");
            resMap.put("body", "");
        }
        return gson.toJson(resMap);
    }

    private static String getRequestCookieString(Map<String, String> cookies) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            if (!first)
                sb.append("; ");
            else
                first = false;
            sb.append(cookie.getKey()).append('=').append(cookie.getValue());
        }
        return sb.toString();
    }
}
