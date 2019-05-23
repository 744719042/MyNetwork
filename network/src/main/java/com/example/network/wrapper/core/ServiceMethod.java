package com.example.network.wrapper.core;

import com.example.network.FormRequestBody;
import com.example.network.Headers;
import com.example.network.HttpProtocol;
import com.example.network.HttpUrl;
import com.example.network.Request;
import com.example.network.RequestMethod;
import com.example.network.wrapper.annotation.Field;
import com.example.network.wrapper.annotation.FormUrlRequest;
import com.example.network.wrapper.annotation.GET;
import com.example.network.wrapper.annotation.HEADER;
import com.example.network.wrapper.annotation.HTTPS;
import com.example.network.wrapper.annotation.POST;
import com.example.network.wrapper.annotation.Query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ServiceMethod {
    private Annotation[] methodAnnotation;
    private Annotation[][] paramAnnotation;
    private HttpProtocol protocol = HttpProtocol.HTTP;
    private String host;
    private String path;
    private RequestMethod method;

    private String query;
    private FormRequestBody body;

    public ServiceMethod(Method method, String host) {
        methodAnnotation = method.getDeclaredAnnotations();
        paramAnnotation = method.getParameterAnnotations();
        parseMethodAnnotations();
        this.host = host;
    }

    private void parseMethodAnnotations() {
        for (Annotation annotation : methodAnnotation) {
            if (annotation instanceof GET) {
                method = RequestMethod.GET;
                path = ((GET) annotation).path();
            } else if (annotation instanceof POST) {
                method = RequestMethod.POST;
                path = ((POST) annotation).path();
            } else if (annotation instanceof HEADER) {
                method = RequestMethod.HEADER;
                path = ((HEADER) annotation).path();
            } else if (annotation instanceof FormUrlRequest) {
                body = new FormRequestBody();
            } else if (annotation instanceof HTTPS) {
                protocol = HttpProtocol.HTTPS;
            }
        }
    }

    private void parseParamAnnotations(Object[] args) {
        if (body != null) {
            body.clear();
            for (int i = 0; i < paramAnnotation.length; i++) {
                Annotation annotation =  paramAnnotation[i][0];
                if (annotation instanceof Field) {
                    body.addFormParam(((Field) annotation).name(), String.valueOf(args[i]));
                }
            }
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < paramAnnotation.length; i++) {
                Annotation annotation =  paramAnnotation[i][0];
                if (annotation instanceof Query) {
                    builder.append(((Query) annotation).name()).append("=").append(String.valueOf(args[i])).append("&");
                }
            }
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
                query = builder.toString();
            }
        }
    }

    public Request getRequest(Object[] args) {
        parseParamAnnotations(args);
        HttpUrl httpUrl = new HttpUrl.Builder().schema(protocol).host(host).path(path).query(query).build();
        Request.Builder builder = new Request.Builder();
        builder.url(httpUrl);
        switch (method) {
            case POST:
                return builder.post(body);
            case HEADER:
                return builder.head();
            case GET:
                default:
                return builder.get();
        }
    }
}
