package com.giant.watsonapp.map;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.giant.watsonapp.R;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapActivity extends AppCompatActivity implements SensorEventListener {

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();

    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_container)
    RelativeLayout titleContainer;
    @BindView(R.id.bmapView)
    MapView bmapView;

    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    BaiduMap mBaiduMap;

    // UI相关
    RadioGroup.OnCheckedChangeListener radioButtonListener;
    Button requestLocButton;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private float direction;

    Context context;

    // 绘制覆盖物
    private List<Marker> markerList=new ArrayList<>();

    private InfoWindow mInfoWindow;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_location);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        context = this;
        titleTv.setText("地图");

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;//带方向的定位

        // 地图初始化
        mBaiduMap = bmapView.getMap();

        //绘制标记
        initOverlay();
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                Button button = new Button(getApplicationContext());
                button.setBackgroundResource(R.color.theme_color);
                InfoWindow.OnInfoWindowClickListener listener = null;
                for (int i=0;i<markerList.size();i++) {
                    if (marker == markerList.get(i)) {
                        button.setText(marker.getTitle());
                        button.setTextColor(Color.WHITE);
                        button.setAlpha(0.8f);
                        button.setWidth(400);

                        marker.setAlpha(0.8f);
                        LatLng ll = marker.getPosition();
                        mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -50, listener);
                        mBaiduMap.showInfoWindow(mInfoWindow);
                    }
                }
                return true;
            }
        });

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        // 标识定位方向
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker));
        //切到当前定位
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.overlook(0);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

    }

    /**
     * 初始化标记
     */
    public void initOverlay() {
        // add marker overlay
        MarkerOptions oo = new MarkerOptions()
                .position(new LatLng(18.25797,109.659829))
                .icon(bd)
                .title("亚龙湾热带天堂森林公园");
        oo.animateType(MarkerOptions.MarkerAnimateType.grow);
        markerList.add((Marker) (mBaiduMap.addOverlay(oo)));

        MarkerOptions oo2 = new MarkerOptions()
                .position(new LatLng(18.236155,109.654556))
                .icon(bd)
                .title("亚龙湾");
        oo2.animateType(MarkerOptions.MarkerAnimateType.grow);
        markerList.add((Marker) (mBaiduMap.addOverlay(oo2)));

        MarkerOptions oo3 = new MarkerOptions()
                .position(new LatLng(18.310497,109.21997))
                .icon(bd)
                .title("南山文化旅游区");
        oo3.animateType(MarkerOptions.MarkerAnimateType.grow);
        markerList.add((Marker) (mBaiduMap.addOverlay(oo3)));

        MarkerOptions oo4 = new MarkerOptions()
                .position(new LatLng(18.300306,109.357845))
                .icon(bd)
                .title("天涯海角");
        oo4.animateType(MarkerOptions.MarkerAnimateType.grow);
        markerList.add((Marker) (mBaiduMap.addOverlay(oo4)));

        MarkerOptions oo5 = new MarkerOptions()
                .position(new LatLng(18.276361,109.488119))
                .icon(bd)
                .title("椰梦长廊");
        oo5.animateType(MarkerOptions.MarkerAnimateType.grow);
        markerList.add((Marker) (mBaiduMap.addOverlay(oo5)));

        MarkerOptions oo6 = new MarkerOptions()
                .position(new LatLng(18.320088,109.770888))
                .icon(bd)
                .title("蜈支洲岛");
        oo6.animateType(MarkerOptions.MarkerAnimateType.grow);
        markerList.add((Marker) (mBaiduMap.addOverlay(oo6)));

        MarkerOptions oo7 = new MarkerOptions()
                .position(new LatLng(18.244147,109.515652))
                .icon(bd)
                .title("第一市场");
        oo7.animateType(MarkerOptions.MarkerAnimateType.grow);
        markerList.add((Marker) (mBaiduMap.addOverlay(oo7)));

        MarkerOptions oo8 = new MarkerOptions()
                .position(new LatLng(18.414564,109.735525))
                .icon(bd)
                .title("珠江南田温泉");
        oo8.animateType(MarkerOptions.MarkerAnimateType.grow);
        markerList.add((Marker) (mBaiduMap.addOverlay(oo8)));

        MarkerOptions oo9 = new MarkerOptions()
                .position(new LatLng(18.228685,109.530103))
                .icon(bd)
                .title("大东海");
        oo9.animateType(MarkerOptions.MarkerAnimateType.grow);
        markerList.add((Marker) (mBaiduMap.addOverlay(oo9)));

        MarkerOptions oo10 = new MarkerOptions()
                .position(new LatLng(18.298032,109.537426))
                .icon(bd)
                .title("三亚千古情景区");
        oo10.animateType(MarkerOptions.MarkerAnimateType.grow);
        markerList.add((Marker) (mBaiduMap.addOverlay(oo10)));
    }

    /**
     * 清除所有Overlay
     *
     * @param view
     */
    public void clearOverlay(View view) {
        mBaiduMap.clear();
        markerList.clear();
    }

    /**
     * 重新添加Overlay
     *
     * @param view
     */
    public void resetOverlay(View view) {
        clearOverlay(null);
        initOverlay();
    }

    @OnClick({R.id.back_iv, R.id.title_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.title_tv:
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || bmapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    protected void onPause() {
        bmapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        bmapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        bmapView.onDestroy();
        bmapView = null;
        super.onDestroy();
        // 回收 bitmap 资源
        bd.recycle();
    }
}
