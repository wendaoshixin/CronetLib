## 集成指南

1. 在**build.gradle**中添加
  
  ```
   allprojects {
      repositories {
          maven {url 'http://10.1.9.109:8081/repository/android-maven-group/'}
      }
  }
  ```
  
2. 在项目的**build.gradle**
  

```
implementation 'com.leimans.lib:cronet2okhttp:1.0.2@aar'
//使用im项目的cronet库
implementation 'com.leimans.lib:imcronet:1.0.2@aar'
//IM项目的imcronet和谷歌浏览器的cronet-fallback可以自由切换
//替换成google自带的库，
//implementation "org.chromium.net:cronet-fallback:76.3809.111"
```

## 使用

```java
//初始化
CronetHelper.getInstance().init(this.getApplicationContext());
//设置拦截器，建议拦截起放到自定义拦截的末尾，比如放日志拦截器之后
mOkhttpClentWithQUIC = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .cache(null)
                .addInterceptor(new CronetInterceptor())
                .build();
```

## 参考

- 参考[网易新闻QUIC敏捷实践：响应速度提升45%，请求错误率降低50%-InfoQ](https://www.infoq.cn/article/fyhfrafwtwgpw4q1orf1)
- [Chromium内核原理之cronet独立化 - 简书](https://www.jianshu.com/p/79a959b038fd)