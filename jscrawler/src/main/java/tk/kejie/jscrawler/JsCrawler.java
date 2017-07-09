package tk.kejie.jscrawler;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.evgenii.jsevaluator.JsEvaluator;
import com.evgenii.jsevaluator.interfaces.JsCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by ykj on 17/6/30.
 */
public class JsCrawler {

    private static JsCrawler singleton;

    private Context mContext;
    private JsEvaluator mJsEvaluator;
    private boolean jQueryEnabled = false;
    private String jQueryCode;
    private String requestModelCode;

    public static void initialize(Context context) {
        if (singleton == null) {
            synchronized (JsCrawler.class) {
                if (singleton == null) {
                    singleton = new JsCrawler(context);
                }
            }
        }
    }

    public static void release() {
        if(singleton != null) {
            synchronized (JsCrawler.class) {
                if (singleton != null) {
                    singleton.getJsEvaluator().destroy();
                    singleton = null;
                }
            }
        }
    }

    public static JsCrawler getInstance() {
        return singleton;
    }

    private JsCrawler(@NonNull Context context) {
        mContext = context;
        mJsEvaluator = new JsEvaluator(context);
        setRequestEngine(new JsoupEngine());
    }

    public JsEvaluator getJsEvaluator() {
        return mJsEvaluator;
    }

    public void setJQueryEnabled(boolean enabled) {
        jQueryEnabled = enabled;
    }

    public void setRequestEngine(RequestEngine requestEngine) {
        mJsEvaluator.getWebView().addJavascriptInterface(requestEngine, "RequestEngine");
    }

    protected String loadJquery() {
        if(jQueryCode != null) {
            return jQueryCode;
        }
        try {
            final AssetManager am = mContext.getAssets();
            final InputStream inputStream = am.open("jquery-3.2.1.min.js");
            jQueryCode = getFileString(inputStream);
            return jQueryCode;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String loadRequestModel() {
        if(requestModelCode != null) {
            return requestModelCode;
        }
        try {
            final AssetManager am = mContext.getAssets();
            final InputStream inputStream = am.open("request-model.js");
            requestModelCode =  getFileString(inputStream);
            return requestModelCode;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFileString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        return scanner.useDelimiter("\\A").next();
    }

    public void callFunction(String jsCode, JsCallback resultCallback, String name, Object... args) {
        jsCode = loadRequestModel() + ";" + jsCode;
        if(jQueryEnabled) {
            jsCode = loadJquery() + ";" + jsCode;
        }
        mJsEvaluator.callFunction(jsCode, resultCallback, name, args);
    }



}
