package com.ape.stsmonths;

import java.util.Vector;

public class HttpRespons {
    int code;
    int connectTimeout;
    String content;
    Vector<String> contentCollection;
    String contentEncoding;
    String contentType;
    int defaultPort;
    String file;
    String host;
    String message;
    String method;
    String path;
    int port;
    String protocol;
    String query;
    int readTimeout;
    String ref;
    String urlString;
    String userInfo;

    public String getContent() {
        return this.content;
    }

    public String getContentType() {
        return this.contentType;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public Vector<String> getContentCollection() {
        return this.contentCollection;
    }

    public String getContentEncoding() {
        return this.contentEncoding;
    }

    public String getMethod() {
        return this.method;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public String getUrlString() {
        return this.urlString;
    }

    public int getDefaultPort() {
        return this.defaultPort;
    }

    public String getFile() {
        return this.file;
    }

    public String getHost() {
        return this.host;
    }

    public String getPath() {
        return this.path;
    }

    public int getPort() {
        return this.port;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public String getQuery() {
        return this.query;
    }

    public String getRef() {
        return this.ref;
    }

    public String getUserInfo() {
        return this.userInfo;
    }
}
