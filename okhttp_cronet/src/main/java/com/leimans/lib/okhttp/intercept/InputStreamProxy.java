package com.leimans.lib.okhttp.intercept;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import okhttp3.Call;

/**
 * @Author ：FengLi
 * @Date : 2021-03-19
 * @Description : 使用代理，方便取消网络请求
 */
public class InputStreamProxy extends InputStream {
    private InputStream mRealInputStream;
    private Call mCall;
    private HttpURLConnection mConnection;

    public InputStreamProxy(InputStream inputStream, Call mCall, HttpURLConnection mConnection) {
        this.mRealInputStream = inputStream;
        this.mCall = mCall;
        this.mConnection = mConnection;
    }


    @Override
    public int read() throws IOException {
        if (mCall.isCanceled()) {
            Log.d(CronetHelper.TAG, "InputStreamProxy read Canceled!!!!!!!!!!!!");
            mConnection.disconnect();
            close();
            throw new IOException("Canceled");
        }
        return mRealInputStream.read();
    }
}
