package com.easylibs.http;

/**
 * Created by sachin.gupta on 03-11-2016.
 */
public interface EasyHttpExecutor {

    <T> void executeAsync(EasyHttpRequest<T> pRequest);

    <T> EasyHttpResponse<T> executeSync(EasyHttpRequest<T> pRequest);
}
