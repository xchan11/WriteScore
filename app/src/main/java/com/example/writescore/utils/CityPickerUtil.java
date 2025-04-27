package com.example.writescore.utils;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import tuorong.com.healthy.model.ProvinceBean;
import tuorong.com.healthy.utils.dataDeal.GetJsonDataUtil;

public class CityPickerUtil {

    //  省
    private static List<ProvinceBean> options1Items = new ArrayList<ProvinceBean>();
    //  市
    private static ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    //  区
    private static ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();


    /**
     * 显示全国城市选择器
     * @param textView
     */
    public static void showCityPickerDialog(Context context, TextView textView){
        parseData(context);

        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, (options1, options2, options3, v) -> {
            //返回的分别是三个级别的选中位置
            String tx = options1Items.get(options1).name +" "+
                    options2Items.get(options1).get(options2) +" "+
                    options3Items.get(options1).get(options2).get(options3);
            textView.setText(tx);
        })
                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private static void parseData(Context context){
        String jsonStr = new GetJsonDataUtil().getJson(context, "province.json");//获取assets目录下的json文件数据
//     数据解析
        Gson gson =new Gson();
        java.lang.reflect.Type type =new TypeToken<List<ProvinceBean>>(){}.getType();
        List<ProvinceBean> shengList=gson.fromJson(jsonStr, type);
//     把解析后的数据组装成想要的list
        options1Items = shengList;
//     遍历省
        for(int i = 0; i <shengList.size() ; i++) {
//         存放城市
            ArrayList<String> cityList = new ArrayList<>();
//         存放区
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();
//         遍历市
            for(int c = 0; c <shengList.get(i).city.size() ; c++) {
//        拿到城市名称
                String cityName = shengList.get(i).city.get(c).name;
                cityList.add(cityName);

                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表
                if (shengList.get(i).city.get(c).area == null || shengList.get(i).city.get(c).area.size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(shengList.get(i).city.get(c).area);
                }
                province_AreaList.add(city_AreaList);
            }
            /**
             * 添加城市数据
             */
            options2Items.add(cityList);
            /**
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }

    }
}
