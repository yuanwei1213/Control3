package com.gengy.control;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.gengy.control.Base.BaseActivity;

public class MapActivity extends BaseActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;




    @Override
    public int intiLayout() {
        return R.layout.activity_map;
    }

    @Override
    public void initView() {

        mMapView=findViewById(R.id.map_view);
        mBaiduMap = mMapView.getMap();
        //显示卫星图层
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        initsetCenter();
    }

    @Override
    public void initData() {

    }

    private void initsetCenter() {

        double lat=Double.parseDouble(extras.getString("lat"));
        double lon=Double.parseDouble(extras.getString("lon"));

        //设定中心点坐标
        LatLng cenpt = new LatLng(lat,lon);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(15)
                .build();

        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(cenpt);
        View view= LayoutInflater.from(this).inflate(R.layout.layout_map_marker,null);
        TextView tv=view.findViewById(R.id.camera_name);
        tv.setText(extras.getString("con"));
        OverlayOptions option = new MarkerOptions()
                .position(cenpt)
                .icon(getBitmap(view));

        mBaiduMap.addOverlay(option);
    }

    public  BitmapDescriptor getBitmap(View addViewContent){
        addViewContent.setDrawingCacheEnabled(true);
        addViewContent.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0,
                addViewContent.getMeasuredWidth(),
                addViewContent.getMeasuredHeight());
        addViewContent.buildDrawingCache();

        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        BitmapDescriptor bitmap =  BitmapDescriptorFactory.fromBitmap( addViewContent.getDrawingCache());

        return bitmap;
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时必须调用mMapView. onResume ()
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时必须调用mMapView. onPause ()
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时必须调用mMapView.onDestroy()
        mMapView.onDestroy();
    }
}
