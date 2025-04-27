package com.example.writescore.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.example.writescore.api.ApiService;
import com.example.writescore.api.RetrofitRequest;
import com.example.writescore.utils.cookie_tool.AppCookieJar;
import com.example.writescore.utils.cookie_tool.cache.SetCookieCache;
import com.example.writescore.utils.cookie_tool.persistence.SharedPrefsCookiePersistor;
import com.example.writescore.utils.file.SharePreferenceUtil;
import com.google.android.datatransport.backend.cct.BuildConfig;
import com.tencent.bugly.crashreport.CrashReport;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import tuorong.com.healthy.api.interceptor.LogPrinter;
import tuorong.com.healthy.utils.LoginInfoManager;


public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    public static AppCookieJar cookieJar;
    @Deprecated //别用了！
    public static FragmentActivity currentAty;
    public static Context appContext;
    public static ApiService apiService;//没必要每个ViewModel都持有
    public static Long initialTime = 0L;
    public static String deviceId = "";
    public static String lastIRoute = "";

    /**
     * 0最大程度阉割
     * */
    public static int degree = 0;//功能隐藏级别
    public static Context currentAty(){
        if(currentAty != null)
            return currentAty;
        if(appContext != null)
            return appContext;
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        SharePreferenceUtil.init(this, "healthy");
        LoginInfoManager.Companion.init(appContext);
        AutoApplySystemBarAnnotation.init(this);
        initBugLy();
        initHttp();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initialTime = System.currentTimeMillis();
    }

    private void initBugLy() {
        HandlerThread bugHdTd = new HandlerThread("init_bugly");
        bugHdTd.start();
        Looper looper = bugHdTd.getLooper();
        Handler handler = new Handler(looper);
        handler.post(() -> {
                    try {
                        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(appContext);
                        strategy.setAppVersion(BuildConfig.VERSION_NAME);
                        strategy.setDeviceModel("Produce: " + Build.BRAND + " ; Android: " + Build.VERSION.RELEASE);

                        // 设置自定义策略和标签
                        if (BuildConfig.DEBUG) {
                            strategy.setAppChannel("debug"); // 设置渠道为 debug
                            strategy.setAppPackageName(appContext.getPackageName() + ".debug");
                            strategy.setAppReportDelay(20000); // 设置 Bugly 的延迟上报时间
                        } else {
                            strategy.setAppChannel("release"); // 设置渠道为 release
                            strategy.setAppPackageName(appContext.getPackageName());
                        }

                        // 初始化 Bugly
                        CrashReport.initCrashReport(appContext, "b9efadcfb8", BuildConfig.DEBUG, strategy);
                        Log.d(TAG, "Bugly initialized successfully");
                    } catch (Exception e) {
                        Log.e(TAG, "Error initializing Bugly", e);
                    }finally {
                        handler.removeCallbacksAndMessages(null);
                        bugHdTd.quit();
                    }
                }
        );

    }

    private void initHttp() {
        cookieJar = new AppCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(appContext));
        synchronized (RetrofitRequest.class) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(LogPrinter.INSTANCE::printNetLog).setLevel(HttpLoggingInterceptor.Level.BODY);

            // 创建信任管理器，以允许不受信任的证书
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // 创建SSL上下文，绕过证书验证
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new SecureRandom());
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
                clientBuilder
                        .cookieJar(cookieJar)
                        .addInterceptor(interceptor)
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                        .hostnameVerifier((hostname, session) -> {
                                    Log.d("请求网络ip", hostname);
                                    return true;
                                }
                        ); // 全部主机名都通过验证

                OkHttpClient okHttpClient = clientBuilder.build();
                RetrofitRequest.initClient(okHttpClient);
                apiService = RetrofitRequest.getRetrofit().create(ApiService.class);
//                OkHttpUtils.initClient(okHttpClient); //这句代码能让OkHttpUtils和retrofit共用一套信任机制
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//    private void writeLogToFile(String message) {
//        try {
//            File logFile = new File(getExternalFilesDir(null), "network_log.txt"); // 存储文件的路径
//            if (!logFile.exists()) {
//                logFile.createNewFile();
//            }
//            FileWriter writer = new FileWriter(logFile, true); // 追加模式
//            writer.append(message).append("\n");
//            writer.flush();
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.fontScale = 1.0f; // 设置字体缩放比例为1，即默认大小
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}
