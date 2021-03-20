package com.leimans.lib.okhttp.intercept;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import okhttp3.Call;

/**
 * @Author ：FengLi
 * @Date : 2021-03-19
 * @Description : 使用代理，方便取消网络请求
 */
public class OutputStreamProxy extends OutputStream {
    private OutputStream mRealOutputStream;
    private Call mCall;
    private HttpURLConnection mConnection;

    public OutputStreamProxy(OutputStream outputStream, Call mCall, HttpURLConnection mConnection) {
        this.mRealOutputStream = outputStream;
        this.mCall = mCall;
        this.mConnection = mConnection;
    }

    @Override
    public void write(int b) throws IOException {
//        Log.d(CronetHelper.TAG, "OutputStreamProxy write: " + b);
        if (mCall.isCanceled()) {
            Log.d(CronetHelper.TAG, "OutputStreamProxy write Canceled!!!!!!!!!!!!");
            mConnection.disconnect();
            close();
            throw new IOException("Canceled");
        }
        mRealOutputStream.write(b);
    }
}
