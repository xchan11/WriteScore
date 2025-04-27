package com.example.writescore.utils;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import tuorong.com.healthy.utils.ui.ToastUtil;

/**
 * 用于获取当前城市+区县
 * Create BY ZiWei 2021-10-29
 * 用法如下:
 *  LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
 *  LocationUtils locationUtils = new LocationUtils(MainActivity.this,locationManager,name);
 *  使用前确保GPS已打开
 *  需传入一个String数组（地址）到构造器中,第一个元素将会被赋值。
 * */
public abstract class LocationUtils {
    int n = 1 ;
    Boolean getable=false;
    String jingdu,weidu;
    public static String locationname ;

    public LocationUtils(Context context, LocationManager locationManager) {
        initLocation(context,locationManager);
        Log.d("LAMTEST1","localmanager为空:"+(locationManager==null)+";locationname="+locationname);
    }

    public void initLocation(Context context, LocationManager locationManager){
        Log.d("LAMTEST2","locamanager为空:"+(locationManager==null));
        //  LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("LAM!","return");
            return;
        }
       // Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location location = getLocation(locationManager);
        if(location!=null){
            Log.d("LAMTEST2", "location为空:" + (location == null));
            Log.d("经纬度jingweidu", location.getLatitude() + "," + location.getLongitude());
            jingdu = location.getLongitude() + "";
            weidu = location.getLatitude() + "";
            if(jingdu!=null&&weidu!=null)
           onGetTitude(location.getLongitude(),location.getLatitude());
            new Thread(new Runnable() {
                @SuppressLint("LongLogTag")
                @Override
                public void run() {
                    initAddress(weidu, jingdu + "");
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else
             ToastUtil.ToastMsg(context,"定位失败!请重试");
    }


    ;
    /*此方法可以增加获取有效location的几率*/
    @SuppressLint("MissingPermission")
    public Location getLocation(LocationManager locationManager){
        Location result = null;
        if (locationManager != null) {
            result = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (result != null) {
                return result;
            } else {
                result = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                return result;
            }
        }
        return result;
    }
    /*根据经纬度获取城市名,用的是百度地图api。ak值有可能某天会失效,需另想办法*/
    public void initAddress(final String wei, final String jing) {
        String urltest = "http://api.map.baidu.com/geocoder?output=json&location=23.5,113.379763&ak=esNPFDwwsXWtsQfw4NMNmur1";
        final String url = "http://api.map.baidu.com/geocoder?output=json&location="+wei+","+jing+"&ak=esNPFDwwsXWtsQfw4NMNmur1";
        OkHttpUtils.get()
                .url(url)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d("LAM", "定位失败" + (n++) + "次, 错误信息:" + e.getMessage());
                e.printStackTrace();
                if (n < 10 || locationname == null)
                    initAddress(weidu,jingdu);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    Log.d("第"+n+"次RESPONSE",response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject addressComponen = new JSONObject((new JSONObject(jsonObject.getString("result"))).
                            getString("addressComponent"));
                    String city = addressComponen.getString("city");
                    String district = addressComponen.getString("district");
                    if (city!=null&&city.length()>0)
                    {
                        Log.d("onResponse", city + district);
                     //   Toast.makeText(MainActivity.this, "定位成功" + response,Toast.LENGTH_LONG).show();
                        final String llocationname = city+district;
                        onGetLocationName(llocationname);
                        Log.d(llocationname+"Lam","Lam"+llocationname);
                    }
                    else if(n<50) {
                        /*递归：失败则再试*/
                        Log.d("第"+(n++)+"次失败",url+"");
                        initAddress(weidu,jingdu);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("failedresponse", "json获取失败");
                    if (n < 50 || locationname==null)
                        initAddress(weidu,jingdu);
                }
            }
        });
    }
    /**
     * 在成功获取城市区县的String之后要做的事情*/
    public abstract void onGetLocationName(String locationname);
    protected abstract void onGetTitude(double longtitude,double latitude);

}
