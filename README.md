# JsCrawler for dynamic update crawler script on android app 

[![](https://jitpack.io/v/YuanKJ-/JsCrawler.svg)](https://jitpack.io/#YuanKJ-/JsCrawler)

## How to setup

### Step 1. Add the JitPack repository to your build file

```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

### Step 2. Add the dependency

```gradle
	dependencies {
	        compile 'com.github.YuanKJ-:JsCrawler:1.0.0'
	}
```

## Usage


### Initialize JsCrawler in your Application:

```java
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JsCrawler.initialize(this);
        // get JsCrawler instance
        JsCrawler jsCrawler = JsCrawler.getInstance();
        // set JQuery enabled
        jsCrawler.setJQueryEnabled(true);
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        JsCrawler.release();
    }
}
```

---

### Load script and call function `getBlogList`  

Note: make sure to call callFunction method in UI thread. 

```java
public class MainActivity extends Activity {
    private JsCrawler jsCrawler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get JsCrawler instance
        jsCrawler = JsCrawler.getInstance();

        final String js = loadJs();
        jsCrawler.callFunction(js, new JsCallback() {
            @Override
            public void onResult(String result) {
                Log.d(TAG, "onResult: " + result);
                // use json to communicate between js and java
                Gson gson = new Gson();
                MyModel model = gson.fromJson(result, MyModel.class);
                // do something
            }

            @Override
            public void onError(String errorMessage) {
                Log.d(TAG, "onError: " + errorMessage);
            }
        }, "getBlogList");

    }


    public String loadJs() {
        String path = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Download/crawler.js";
        try {
            File file = new File(path);
            InputStream inputStream = new FileInputStream(file);

            Scanner scanner = new Scanner(inputStream, "UTF-8");
            return scanner.useDelimiter("\\A").next();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
```

---

### Call js function with parameters: 

```java
jsCrawler.callFunction("function myFunction(a, b, c, a) { return 'result'; }", 
	new JsCallback() {
	
	    @Override
	    public void onResult(String result) {
	        // handle result
	    }
	
	    @Override
	    public void onError(String errorMessage) {
	        // handle error
	    }
	}, "myFunction", "parameter 1", "parameter 2", 912, 101.3);
```

---

### JavaScript sample

You can be very easy to make the http request in JavaScript and use JQuery to parse the body.  

```js
function getBlogList() {
    // define url
    var url = "http://droidyue.com";

    // create request with RequestBuilder
    var request = new RequestBuilder()
        .url(url).method("GET")
        .timeout(10000).build();

    // get response with RequestEngine.executeByRequest()
    var response = RequestEngine.executeByRequest(request);
    
    // the response is a string of json:
    // {"code":"200", "message":"OK", "body":"content"}
    // {"code":"404", "message":"NOT FOUND", "body":"content"}
    // {"code":"-1", "message":"Request Exception", "body":""}
    // eval json to js object
    response = eval("("+response+")");
    
    // process exception code
    if(response.code != 200) {
        return "response error";
    }

    // get response body and process with JQuery
    var body = response.body;
    var articleEles = $(body).find(".blog-index article");
    var articleList = new Array();

    $.each(articleEles, function(index, element){
        var article = new Object();
        element = $(element);
        var entry = element.find(".entry-title a").first();
        article.title = entry.text();
        article.url = url + entry.attr("href");
        article.describe = element.find(".entry-content").text().trim();
        articleList.push(article);
    });

    // parse array to json and return
    return JSON.stringify(articleList);
}
```

---

### RequestBuilder API  

You can set `Method` `Header` `Cookie` `Form-Data` `body` `timeout` in request.  

```js
// create builder, support chains call
var builder = new RequestBuilder();

// set url
builder.url("http://api.kejie.tk");

// set method, only support POST or GET
builder.method("POST");
builder.method("GET");

// addHeader or setHeaders
builder.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N)")
	.addHeader("Referer", "http://api.kejie.tk");
	
var headers = {
    "User-Agent": "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N)",
    "Referer": "http://api.kejie.tk"
}
builder.setHeaders(headers);

// addCookie or setCookies
builder.addCookie("uid", "1170120F8E53899BC88B236FA6A731FC");

var cookies = {
    "uid": "1170120F8E53899BC88B236FA6A731FC",
    "type": "1"
}
builder.setCookies(cookies);

// addData or setData
// data will encode as a query append to the url if method GET
// data will encode as form-data append to body if method POST
builder.addData("wd", "testData");

var data = {
    "wd": "testData",
    "qid": "59"
}
builder.setData(data);

// set body with string
// Note: setData will fail if set the body
// Content-type will automatically change to application/json
builder.body('{"username":"kejie","pwd":"d8j3kduui461p"}');

// set timeout, milliseconds
builder.timeout(10000);

// build request object
var request = builder.build();
```

---

### Execute request  

Get http response with `RequestEngine.executeByRequest(request)`. The response is a string with json, it contains `http code` `message` and `body`. Http code will return -1 if a Request Exception has occurred.  

```js
var response = RequestEngine.executeByRequest(request);

// {"code":"200", "message":"OK", "body":"content"}
// {"code":"404", "message":"NOT FOUND", "body":"content"}
// {"code":"-1", "message":"Request Exception", "body":""}
response = eval("("+response+")");

// use console.log can print a string to android log
console.log(response.code);
console.log(response.message);
console.log(response.body);
```

---

### Change default request engine

JsCrawler has two request engine, Jsoup and OkHttp. Default use Jsoup, you can change to OkHttp.

```java
jsCrawler.setRequestEngine(new OkHttpEngine());
```

---

## Extended Request Engine

#### Extend `JsoupEngine` `OkHttpEngine` to support specific http settings or extend `RequestEngine` to create a new Engine.  

An example of adding proxy for JsoupEngine.  

- 1. Extend RequestModel，add variable proxy.

```java
public class MyRequestModel extends RequestModel {
    private String proxy;

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }
}
```

- 2. Extend JsoupEngine，overwrite process()，add processProxy()，overwrite jsonToModel()，convert json to MyRequestModel。

```java
public class MyJsoupEngine extends JsoupEngine {

    protected void processProxy(MyRequestModel model) {
        if (model.getProxy() != null) {
            String[] proxy = model.getProxy().split(":");
            if (proxy.length > 1) {
                // connection is an object of jsoup
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
```

- 3. In script file, extend RequestBuilder add proxy function and overwrite build function.

```js
RequestBuilder.prototype.proxy = function(host){
    this.mProxy = host;
    return this;
}

RequestBuilder.prototype.build = function() {
    var request = new Request(this);
    request.proxy = this.mProxy;
    return JSON.stringify(request);
}

function getBlogList() {
	// your js code ...
	var request = new RequestBuilder()
        .url(url).method("GET").proxy("127.0.0.1:8088")
        .timeout(10000).build();
   var response = RequestEngine.executeByRequest(request);
   // your js code ...
}
```

- 4. JsCrawler setRequestEngine

```java
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JsCrawler.initialize(this);
        // 获取JsCrawler实例
        JsCrawler jsCrawler = JsCrawler.getInstance();
        // 设置是否开启使用JQuery
        jsCrawler.setJQueryEnabled(true);
        // 修改JsCrawler请求引擎
        jsCrawler.setRequestEngine(new MyJsoupEngine());
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        JsCrawler.release();
    }
}
```

### For additional information see [sample](https://github.com/YuanKJ-/JsCrawler/tree/master/sample) module.

## License

	MIT License
	
	Copyright (c) 2017 kejie
	
	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	
	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.
	
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.