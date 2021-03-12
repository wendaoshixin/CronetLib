package com.leimans.lib.okhtpp.intercept;

import android.content.Context;

import org.chromium.net.CronetEngine;

/**
 * @Author ：FengLi
 * @Date : 2021-03-12
 * @Description : Cronet使用工具
 */
public class CronetHelper {
    public static String TAG = "TestHttp3";
    private static CronetHelper instance;
    private CronetEngine cronetEngine;
    private volatile boolean enable;

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

    public void init(Context context) {
        if(cronetEngine == null){
            CronetEngine.Builder builder = new CronetEngine.Builder(context);
            builder.enableQuic(true);
            builder.enableHttp2(true);
            //builder.addQuicHint(getHost(url), DEFAULT_PORT, DEFAULT_ALTERNATEPORT);
            cronetEngine = builder.build();
            enable = true;
        }
    }

    public CronetEngine getCronetEngine() {
        return cronetEngine;
    }

    public boolean isEnable() {
        return enable && cronetEngine != null;
    }
}
