package com.leimans.lib.okhttp.intercept;

import okhttp3.Request;

/**
 * @Author ：FengLi
 * @Date : 2021-03-20
 * @Description :  过滤需要使用cronet等请求
 * 使用场景： 项目中可能使用了一个Okttpclient实例，部分请求使用了cronet，如果需要使用则添加过滤即可，不设置默认全部使用cronent
 */
public interface CronetRequestFilter {

    boolean filter(Request request);
}
