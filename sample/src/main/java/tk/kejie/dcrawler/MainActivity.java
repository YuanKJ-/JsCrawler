package tk.kejie.dcrawler;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.evgenii.jsevaluator.interfaces.JsCallback;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import tk.kejie.jscrawler.JsCrawler;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private BlogItemViewAdapter adapter;
    private JsCrawler jsCrawler;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        adapter = new BlogItemViewAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        gson = new Gson();
        JsCrawler.initialize(this);
        jsCrawler = JsCrawler.getInstance();
        jsCrawler.setJQueryEnabled(true);

        final String js = loadJs();
        jsCrawler.callFunction(js, new JsCallback() {

            @Override
            public void onResult(String result) {
                Log.d(TAG, "onResult: " + result);
                BlogModel[] models = gson.fromJson(result, BlogModel[].class);
                List<BlogModel> list = new ArrayList<BlogModel>();
                Collections.addAll(list, models);
                adapter.addAll(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Log.d(TAG, "onError: " + errorMessage);
            }
        }, "getBlogList");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JsCrawler.release();
    }

    public String loadJs() {
        try {
            final AssetManager am = getAssets();
            final InputStream inputStream = am.open("crawler.js");

            Scanner scanner = new Scanner(inputStream, "UTF-8");
            return scanner.useDelimiter("\\A").next();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
