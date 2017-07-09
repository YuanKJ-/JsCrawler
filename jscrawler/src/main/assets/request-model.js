function Request(builder){
    this.url = builder.mUrl;
    this.method = builder.mMethod;
    this.headers = builder.mHeaders;
    this.cookies = builder.mCookies;
    this.data = builder.mData;
    this.body = builder.mBody;
    this.timeout = builder.mTimeout;
}

Request.prototype = {
    constructor:Request,
    url: null,
    method: null,
    headers: null,
    cookies: null,
    data: null,
    body: null,
    timeout: null
}

function RequestBuilder(){

}

RequestBuilder.prototype = {
    constructor:RequestBuilder,
    mUrl: null,
    mMethod: null,
    mHeaders: new Object(),
    mCookies: new Object(),
    mData: new Object(),
    mBody: null,
    mTimeout: null,
    url:function(url) {
        this.mUrl = url;
        return this;
    },
    method:function(method) {
        this.mMethod = method;
        return this;
    },
    addHeader:function(key, val) {
        this.mHeaders[key] = val;
        return this;
    },
    setHeaders:function(headers) {
        this.mHeaders = headers;
        return this;
    },
    addCookie:function(key, val) {
        this.mCookies[key] = val;
        return this;
    },
    setCookies:function(cookies) {
        this.mCookies = cookies;
        return this;
    },
    addData:function(key, val) {
        this.mData[key] = val;
        return this;
    },
    setData:function(data) {
        this.mData = data;
        return this;
    },
    body:function(body) {
        this.mBody = body;
        return this;
    },
    timeout:function(timeout) {
        this.mTimeout = timeout;
        return this;
    },
    build:function() {
        return JSON.stringify(new Request(this));
    }
}