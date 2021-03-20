package com.leimans.lib.okhttp.intercept;

import android.content.Context;

import org.chromium.net.CronetEngine;

import java.net.URLConnection;

/**
 * @Author ：FengLi
 * @Date : 2021-03-12
 * @Description : Cronet使用工具
 */
public class CronetHelper {
    public static final int DEFAULT_CHUNKED_LEN = 4096;
    public static String TAG = "CronetHttp3";
    private static CronetHelper instance;
    private CronetEngine cronetEngine;
    private volatile boolean enable;
    private CronetRequestFilter mRequestFilter;
    private IPrintProtocol mPrintProtocol;

    private CronetHelper() {
    }

    public static CronetHelper getInstance() {
        if (instance == null) {
            synchronized (CronetHelper.class) {
                if (instance == null) {
                    instance = new CronetHelper();
                }
            }
        }
        return instance;
    }

    public CronetHelper init(Context context) {
        if (cronetEngine == null) {
            CronetEngine.Builder builder = new CronetEngine.Builder(context);
            builder.enableQuic(true);
            builder.enableHttp2(true);
            //builder.addQuicHint(getHost(url), DEFAULT_PORT, DEFAULT_ALTERNATEPORT);
            cronetEngine = builder.build();
            enable = true;
        }
        return this;
    }

    public CronetEngine getCronetEngine() {
        return cronetEngine;
    }

    public boolean isEnable() {
        return enable && cronetEngine != null;
    }

    public CronetRequestFilter getRequestFilter() {
        return mRequestFilter;
    }

    /**
     * 项目中可能使用了一个Okttpclient实例，部分请求使用了cronet，如果需要使用则添加过滤即可，不设置默认全部使用cronent
     * @param requestFilter
     * @return
     */
    public CronetHelper setRequestFilter(CronetRequestFilter requestFilter) {
        this.mRequestFilter = requestFilter;
        return this;
    }

    public IPrintProtocol getPrintProtocol() {
        return mPrintProtocol;
    }

    /**
     * 设置打印协议，可以查看协议
     * @param printProtocol
     * @return
     */
    public CronetHelper setPrintProtocol(IPrintProtocol printProtocol) {
        this.mPrintProtocol = printProtocol;
        return this;
    }

    public interface IPrintProtocol {
        void printProtocol(URLConnection urlConnection);
    }
}
