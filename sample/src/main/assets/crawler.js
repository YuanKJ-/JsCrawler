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
    // 定义抓取url
    var url = "http://droidyue.com/";
    // 通过RequestBuilder构造请求
    var request = new RequestBuilder()
        .url(url).method("GET")
        .timeout(10000).build();
    // 调用RequestEngine.executeByRequest()传入构造好的request对象
    var response = RequestEngine.executeByRequest(request);
    // 得到response对象的json字符串,格式如下:
    // {"code":"200", "message":"OK", "body":"请求获取的内容"}
    // {"code":"404", "message":"NOT FOUND", "body":"请求获取的内容"}
    // {"code":"-1", "message":"Request Exception", "body":""}
    // 通过eval函数, 转成js对象
    response = eval("("+response+")");
    // 处理异常的请求返回码
    if(response.code != 200) {
        return "response error";
    }
    // 得到正确内容后, 获取相应的body并通过JQuery对内容进行处理
    var body = response.body;
    var articleEles = $(body).find(".blog-index article");
    var articleList = new Array();
    // 处理元素数组
    $.each(articleEles, function(index, element){
        var article = new Object();
        element = $(element);
        var entry = element.find(".entry-title a").first();
        article.title = entry.text().replace(/[ ]/g,"").replace(/[\r\n]/g,"");
        article.url = url.substring(0, url.length - 1) + entry.attr("href");
        article.describe = element.find(".entry-content").text().trim().replace(/[\r\n]+/g,"\n");
        articleList.push(article);
    });
    // 把js数组对象转成json字符串返回
    return JSON.stringify(articleList);
}
