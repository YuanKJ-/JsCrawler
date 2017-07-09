package tk.kejie.jscrawler;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;

/**
 * Created by ykj on 17/6/30.
 */
public abstract class RequestEngine {
    private static final String TAG = "RequestEngine";

    protected Gson gson = new Gson();

    protected abstract void processUrl(RequestModel model);

    protected abstract void processMethod(RequestModel model);

    protected abstract void processHeader(RequestModel model);

    protected abstract void processData(RequestModel model);

    protected abstract void processCookie(RequestModel model);

    protected abstract void processTimeout(RequestModel model);

    protected abstract void processBody(RequestModel model);

    protected abstract String execute();

    protected void process(RequestModel model) {
        processUrl(model);
        processMethod(model);
        processHeader(model);
        processData(model);
        processCookie(model);
        processTimeout(model);
        processBody(model);
    }

    protected RequestModel jsonToModel(String request) {
        return gson.fromJson(request, RequestModel.class);
    }

    @JavascriptInterface
    public String executeByRequest(String request) {
        Log.d(TAG, "executeByRequest: " + request);
        RequestModel model = jsonToModel(request);
        process(model);
        return execute();
    }
}
