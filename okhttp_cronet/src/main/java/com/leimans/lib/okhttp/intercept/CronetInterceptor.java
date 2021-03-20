package com.leimans.lib.okhttp.intercept;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * @Author ：FengLi
 * @Date : 2021-03-08
 * @Description : cronet协议拦截器，如果使用cronet协议
 */
public class CronetInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!CronetHelper.getInstance().isEnable()) {
            return chain.proceed(chain.request());
        }

        Request req = chain.request();
        URL url = req.url().url();
//        if(!TextUtils.equals("dev-file-im.raymannet.com", url.getHost())){
//            return chain.proceed(chain.request());
//        }

        if (CronetHelper.getInstance().getRequestFilter() != null
                && !CronetHelper.getInstance().getRequestFilter().filter(req)) {
            return chain.proceed(chain.request());
        }

        // covert okhttp request to cornet request
        HttpURLConnection connection = (HttpURLConnection) CronetHelper.getInstance().getCronetEngine().openConnection(url);
//        connection.
        connection.setChunkedStreamingMode(CronetHelper.DEFAULT_CHUNKED_LEN);
        // add headers
        Set<String> headerlist = req.headers().names();

        for (String headerName : headerlist) {
            connection.addRequestProperty(headerName, req.headers().get(headerName));
        }

        // todo pass cookie
        // method
        connection.setRequestMethod(req.method());

        // add body
        if (req.body() != null) {
            RequestBody requestBody = req.body();
            if (requestBody.contentType() != null) {
                connection.setRequestProperty("Content-Type", requestBody.contentType().toString());
            }

            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os = new OutputStreamProxy(os, chain.call(), connection);
            BufferedSink sink = Okio.buffer(Okio.sink(os));
            requestBody.writeTo(sink);
            sink.flush();
            os.close();
        }


        int statusCode = connection.getResponseCode();

//        printProtocol(connection);
        if(CronetHelper.getInstance().getPrintProtocol() != null){
            CronetHelper.getInstance().getPrintProtocol().printProtocol(connection);
        }

        // handling http redirect
        if (statusCode >= 300 && statusCode <= 310) {
            return chain.proceed(req);
        }

        Response.Builder respBuilder = new Response.Builder();
        respBuilder
                .request(req)
                .protocol(Protocol.QUIC)
                .code(statusCode)
                .message(connection.getResponseMessage() == null ? connection.getResponseMessage() : "");

        Map<String, List<String>> respHeaders = connection.getHeaderFields();


        for (Map.Entry<String, List<String>> stringListEntry : respHeaders.entrySet()) {
            for (String valueString : stringListEntry.getValue()) {
                if (stringListEntry.getKey() != null) {
                    respBuilder.addHeader(stringListEntry.getKey(), valueString);
                }
            }
        }

        InputStream inputStream = null;
        if (statusCode >= 200 && statusCode <= 399) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
        }

        inputStream = new InputStreamProxy(inputStream, chain.call(), connection);
        BufferedSource bodySource = Okio.buffer(Okio.source(inputStream));


        List<String> contentTypeList = respHeaders.get("Content-Type");
        List<String> contentLengthList = respHeaders.get("Content-Length");
        String contentTypeString = "";
        long contentLength = 0;
        if (contentTypeList != null && contentTypeList.size() > 0) {
            contentTypeString = contentTypeList.get(contentTypeList.size() - 1);
        }

        if (contentLengthList != null && contentLengthList.size() > 0) {
            contentLength = Long.parseLong(contentLengthList.get(contentLengthList.size() - 1));
        }

        RealResponseBody realResponseBody = new RealResponseBody(contentTypeString, contentLength, bodySource);

        respBuilder.body(realResponseBody);
        Response response = respBuilder.build();

        return response;
    }

//    /**
//     * 打印协议
//     * @param connection
//     */
//    private void printProtocol(CronetHttpURLConnection connection){
//        try {
//            Class<CronetHttpURLConnection> classz = (Class<CronetHttpURLConnection>) connection.getClass();
//            Field field = classz.getDeclaredField("mResponseInfo");
//            field.setAccessible(true);
//            UrlResponseInfo urlResponseInfo = (UrlResponseInfo) field.get(connection);
//            String protocol = urlResponseInfo.getNegotiatedProtocol();
//            Log.d(CronetHelper.TAG, "协议: " + protocol);
//
//        } catch (Exception e) {
//            Log.d(CronetHelper.TAG, "协议: " + e);
//        }
//
//    }
}
