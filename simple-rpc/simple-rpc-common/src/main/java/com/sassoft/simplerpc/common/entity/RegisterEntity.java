package com.sassoft.simplerpc.common.entity;

/**
 * Created by shjk_000 on 2019/3/9.
 */
public class RegisterEntity {
    private String url;
    private String methodName;
    private String className;
    private String[] params;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
}
