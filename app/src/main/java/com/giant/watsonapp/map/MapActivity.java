package com.giant.watsonapp.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.bumptech.glide.Glide;
import com.giant.watsonapp.R;
import com.giant.watsonapp.utils.T;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.R.attr.button;

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
    MapStatus ms;

    // UI相关
    RadioGroup.OnCheckedChangeListener radioButtonListener;
    Button requestLocButton;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private float direction;

    Context context;

    // 绘制覆盖物
    private double mSanYaLat = 18.236155;//三亚坐标
    private double mSanYaLon = 109.654556;//三亚坐标
    private int[] imgs = {
            R.mipmap.sanya01,R.mipmap.sanya02,R.mipmap.sanya03,R.mipmap.sanya04,
            R.mipmap.sanya05,R.mipmap.sanya06,R.mipmap.sanya07,R.mipmap.sanya08,
            R.mipmap.sanya09,R.mipmap.sanya10
    };

    private List<Marker> markerList=new ArrayList<>();

    private InfoWindow mInfoWindow;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_loc_spot);

    // 坐标类型
    private CoordType mCoordType;

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
                //xml转视图
                LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view=inflater.inflate(R.layout.pop_map_info, null);
                TextView textView = (TextView)view.findViewById(R.id.title_tv);
                ImageView imageView = (ImageView)view.findViewById(R.id.img_iv);

                InfoWindow.OnInfoWindowClickListener listener = null;
                for (int i=0;i<markerList.size();i++) {
                    if (marker == markerList.get(i)) {
                        textView.setText(marker.getTitle());
                        imageView.setImageResource(imgs[i]);
                        listener = new InfoWindow.OnInfoWindowClickListener() {
                            public void onInfoWindowClick() {
                                T.showShort(context,"开始导航...");

                                String fromName = "我的位置";
                                LatLng fromLoc = new LatLng(mCurrentLat, mCurrentLon);

                                String toName = marker.getTitle();
                                LatLng toLoc = marker.getPosition();

                                startNavi(fromName,fromLoc,toName,toLoc);
                            }
                        };

                        LatLng ll = marker.getPosition();
                        mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(view), ll, -50, listener);
                        mBaiduMap.showInfoWindow(mInfoWindow);
                        break;
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

        mCoordType = SDKInitializer.getCoordType();//获取全局设置的坐标类型
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

        //半透明
        for(Marker marker:markerList){
            marker.setAlpha(0.8f);
        }
    }

    /**
     * 启动百度地图导航(Native)
     */
    public void startNavi(String fromName,LatLng fromLoc,String toName,LatLng toLoc) {

        // 构建 导航参数
        NaviParaOption para = new NaviParaOption()
                .startName(fromName)
                .startPoint(fromLoc)
                .endName(toName)
                .endPoint(toLoc);

        try {
            BaiduMapNavigation.openBaiduMapNavi(para, this);
        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            showDialog();
        }

    }

    /**
     * 提示未安装百度地图app或app版本过低
     */
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenClientUtil.getLatestBaiduMapApp(context);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    @OnClick({R.id.back_iv, R.id.title_tv,R.id.showMyLoc_iv,R.id.showSpotLoc_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.showMyLoc_iv:
                ms  = new MapStatus.Builder()
                        .target(new LatLng(mCurrentLat, mCurrentLon))
                        .overlook(0)
                        .zoom(17)
                        .build();
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
                break;
            case R.id.showSpotLoc_iv:
                ms  = new MapStatus.Builder()
                    .target(new LatLng(mSanYaLat, mSanYaLon))
                    .overlook(0)
                    .zoom(11)
                    .build();
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
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
