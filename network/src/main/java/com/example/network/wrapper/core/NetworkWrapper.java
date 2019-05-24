package com.example.network.wrapper.core;

import com.example.network.Callback;
import com.example.network.HttpClient;
import com.example.network.MyNetException;
import com.example.network.Request;
import com.example.network.Response;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkWrapper {
    private Map<Method, ServiceMethod> cache = new ConcurrentHashMap<>();
    private String baseHost;
    private HttpClient httpClient;
    private Gson gson;

    public NetworkWrapper(String baseHost, HttpClient httpClient) {
        this.baseHost = baseHost;
        this.httpClient = httpClient;
        this.gson = new Gson();
    }

    public <T> T create(Class<T> tClass) {
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ tClass }, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }

                ServiceMethod serviceMethod = cache.get(method);
                if (serviceMethod == null) {
                    serviceMethod = new ServiceMethod(method, baseHost);
                    cache.put(method, serviceMethod);
                }
                Request request = serviceMethod.getRequest(args);
                if (method.getGenericReturnType() == Request.class) {
                    return request;
                }
                return (T) parseResponse(method, httpClient.execute(request));
            }
        });
    }

    private Object parseResponse(Method method, Response response) {
        if (response == null) {
            return null;
        }

        try {
            if (method.getGenericReturnType() == method.getReturnType() || method.getReturnType() == List.class) {
                String body = response.getResponseBody().string();
                return gson.fromJson(body, method.getGenericReturnType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T> void enqueue(Request request, final DataCallback<T> callback) {
        httpClient.enqueue(request, new Callback() {
            @Override
            public void onSuccess(Response response) {
                ParameterizedType parameterizedType = (ParameterizedType) callback.getClass().getGenericSuperclass();
                Type type = parameterizedType.getActualTypeArguments()[0];
                String body = null;
                try {
                    body = response.getResponseBody().string();
                    T data = gson.fromJson(body, type);
                    if (callback != null) {
                        callback.onSuccess(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onFailure(-1, new MyNetException("", e));
                }
            }

            @Override
            public void onFailure(int code, MyNetException e) {
                if (callback != null) {
                    callback.onFailure(code, e);
                }
            }
        });
    }
}
