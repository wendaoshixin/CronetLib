package com.leimans.lib.cronetlib;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.leimans.lib.okhtpp.intercept.CronetHelper;
import com.leimans.lib.okhtpp.intercept.CronetInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSource;

/**
 * @Author ：FengLi
 * @Date : 2021-03-12
 * @Description :
 */

public class MainActivity extends AppCompatActivity {
    OkHttpClient mOkhttpClentWithQUIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOkhttpClentWithQUIC = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .cache(null)
                .addInterceptor(new CronetInterceptor())
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
        CronetHelper.getInstance().init(this.getApplicationContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions(){
        this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100000);
    }

    /**
     * get请求测试按钮点击
     * @param view
     */
    public void okhttpQUICTest(View view) {
        String URL = "https://dev-file-im.raymannet.com/storage/v1/testhttp3";
   
        request(mOkhttpClentWithQUIC, URL);
    }

    /**
     * quic网络请求，get请求
     * @param okHttpClient
     * @param url
     */
    public void request(OkHttpClient okHttpClient, String url)
    {
        long time = System.currentTimeMillis();

        Request req = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(req).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                BufferedSource source = response.body().source();
                String reponseStr = response.body().string();
                Log.d(CronetHelper.TAG, "onResponse: " + reponseStr);
            }
        });
    }

    /**
     * 文件上传点击
     * @param view
     */
    public void okhttpQUICUpload(View view) {
        String URL = "https://dev-file-im.raymannet.com/storage/v1/testhttp3file";
        upload(mOkhttpClentWithQUIC, URL);
//        uploadHttp2(mOkhttpClentWithQUIC, URL);
    }

    int progress = -1;

    /**
     * quic网络上传测试
     * @param okHttpClient
     * @param url
     */
    private void upload(OkHttpClient okHttpClient, String url){

//        NowTalk_1.0.5.20210301174331\ (1).apk
//        NowTalk_1.0.5.20210301174331.apk
//        NowTalk_1.0.5.20210301190054.apk
//        NowTalk_1.0.6.20210305190158.apk
//        debugTest1.0.5.20210129153905.apk
//        debugTest1.0.5.20210202205532.apk
//        release1.0.5.20210204201440.apk
//        yunTest1.0.3.20201216111921_20210111_153726.apk
//        yunTest1.0.4.20210107204825_20210111_151105.apk
//        yunTest1.0.4.20210107204825_20210302_110301.apk


//       String filePath = "/sdcard/Download/Browser/yunTest1.0.4.20210107204825_20210302_110301.apk";
        String filePath = "/sdcard/Download/updateApp.apk";

        String fileName = "IMG_20200713_103536.jpg";
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(MediaType.parse("multipart/form-data"), new File(filePath)))
                .build();



        ExMultipartBody exMultipartBody = new ExMultipartBody(multipartBody, new UploadProgressListener() {
            @Override
            public void onProgress(long total, long current) {

                int curProgress = (int)(current*100/total);
                if(curProgress != progress){
                    Log.d(CronetHelper.TAG, "Progress: " +curProgress);

                    progress = curProgress;
                }

                if(curProgress<2){
                    Log.d(CronetHelper.TAG, "total: " +total+",current: " +current);
                }else if(total == current){
                    Log.d(CronetHelper.TAG, "total: " +total+",current: " +current);
                }



            }
        });

        Request request = new Request.Builder()
                .url(url)
                .post(exMultipartBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(CronetHelper.TAG, "IOException: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                BufferedSource source = response.body().source();
                String reponseStr = response.body().string();
                Log.d(CronetHelper.TAG, "onResponse: " + reponseStr);
            }
        });
    }


    /**
     * 测试原始OKhttp3网络请求
     * @param view
     */
    public void okhttpQUICTest2(View view) {
        String url = "http://wwww.baidu.com";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(CronetHelper.TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(CronetHelper.TAG, "onResponse: " + response.body().string());
            }
        });
    }

    /**
     * 使用okhttp上传
     * @param okHttpClient
     * @param url
     */
    private void uploadHttp2(OkHttpClient okHttpClient, String url){
        okHttpClient = new OkHttpClient();
        String filePath = "/sdcard/Download/updateApp.apk";

        String fileName = "IMG_20200713_103536.jpg";
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(MediaType.parse("multipart/form-data"), new File(filePath)))
                .build();



        ExMultipartBody exMultipartBody = new ExMultipartBody(multipartBody, new UploadProgressListener() {
            @Override
            public void onProgress(long total, long current) {

                int curProgress = (int)(current*100/total);
                if(curProgress != progress){
                    Log.d(CronetHelper.TAG, "Progress: " +curProgress);

                    progress = curProgress;
                }

                if(curProgress<2){
                    Log.d(CronetHelper.TAG, "total: " +total+",current: " +current);
                }else if(total == current){
                    Log.d(CronetHelper.TAG, "total: " +total+",current: " +current);
                }



            }
        });

        Request request = new Request.Builder()
                .url(url)
                .post(exMultipartBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(CronetHelper.TAG, "IOException: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                BufferedSource source = response.body().source();
                String reponseStr = response.body().string();
                Log.d(CronetHelper.TAG, "onResponse: " + reponseStr);
//                File downloadedFile = new File(MainActivity2.this.getExternalCacheDir(), "b.jpg");
//                BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
//                sink.writeAll(source);
//                sink.close();
//                source.close();
//                long cost = System.currentTimeMillis() - time;
//                Log.i(TAG, "download complete $img cost = "+cost);

            }
        });
    }
}

