package com.example.coolweather;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Wiggins on 2017/6/17.
 */

public class ChooseAreaFragment extends Fragment{
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
     TextView titleText;
     Button backBtn;
     ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<>();
    //省市县列表
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    //选中的省份/城市
    private Province selectedProvince;
    private City selectedCity;
    //当前选中的级别
    private int currentLevel;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_area,container,false);
        titleText=view.findViewById(R.id.title_text);
        backBtn=view.findViewById(R.id.back_button);
        listView=view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(i);
                    queryCity();
                }else if (currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(i);
                    queryCounty();
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel==LEVEL_COUNTY){
                    queryCity();
                }else if (currentLevel==LEVEL_CITY){
                    queryProvince();
                }
            }
        });
        queryProvince();
    }
    //查询所有的省，优先从数据库查询，如果没有就在去服务器上查询
    private void queryProvince(){
        titleText.setText("中国");
        backBtn.setVisibility(View.GONE);
        provinceList= DataSupport.findAll(Province.class);
        if (provinceList.size()>0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else {
            String address="http://guolin.tech/aqi/china";
            queryForServer(address,"province");
        }
    }
    private void queryCity(){
        titleText.setText(selectedProvince.getProvinceName());
        backBtn.setVisibility(View.VISIBLE);
        cityList=DataSupport.findAll(City.class);
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else {
            int provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/aqi/china"+provinceCode;
            queryForServer(address,"city");
        }
    }
    private void queryCounty(){
        titleText.setText(selectedCity.getCityName());
        backBtn.setVisibility(View.VISIBLE);
        countyList=DataSupport.findAll(County.class);
        if (countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else {
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/aqi/china"+provinceCode+cityCode;
            queryForServer(address,"county");
        }
    }
    //根据传入的地址和类型从服务器上查询省市县数据
    private void queryForServer(String address, final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result=false;
                if ("province".equals(type)){
                    result= Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result=Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvince();
                            }else if ("city".equals(type)){
                                queryCity();
                            }else if ("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //显示进度对话框
    private void showProgressDialog() {
        if (progressDialog==null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }
    //关闭进度对话框
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

}
