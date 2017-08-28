package com.giant.watsonapp.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.giant.watsonapp.models.Hotel;
import com.giant.watsonapp.models.HotelDao;
import com.giant.watsonapp.models.MyMarker;
import com.giant.watsonapp.models.Restaurant;
import com.giant.watsonapp.models.RestaurantDao;
import com.giant.watsonapp.models.Scenery;
import com.giant.watsonapp.models.SceneryDao;
import com.giant.watsonapp.utils.T;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.R.attr.button;
import static android.R.attr.mode;
import static com.baidu.location.d.a.i;
import static com.giant.watsonapp.models.SceneryDao.queryAll;

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
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    BaiduMap mBaiduMap;
    MapStatus ms;

    // UI相关
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;

    Context context;

    //对应图标，0-景点|1-美食|2-酒店
    private int[] imgs = {
            R.mipmap.sanya01, R.mipmap.sanya02, R.mipmap.sanya03,
    };

    //跳转的传入对象
    MyMarker myMarker;
    List<MyMarker> sumList = new ArrayList<>();//汇总list

    // 绘制覆盖物
    private List<MyMarker> sceneryList = new ArrayList<>();
    private List<MyMarker> foodList = new ArrayList<>();
    private List<MyMarker> hotelList = new ArrayList<>();

    private InfoWindow mInfoWindow;


    //0-景点|1-美食|2-酒店
    public static final int TYPE_SCENERY = 0;
    public static final int TYPE_FOOD = 1;
    public static final int TYPE_HOTEL = 2;

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

        //标记点击事件
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                //xml转视图
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.pop_map_info, null);
                TextView textView = (TextView) view.findViewById(R.id.title_tv);
                ImageView imageView = (ImageView) view.findViewById(R.id.img_iv);

                InfoWindow.OnInfoWindowClickListener listener = null;

                for (MyMarker model : sumList) {
                    if (marker.getTitle().equals(model.getName())) {
                        //设置标题
                        textView.setText(model.getName());
                        //设置相应图标
                        imageView.setImageResource(imgs[model.getType()]);

                        listener = () -> {
                            T.showShort(context, "开始导航...");

                            String fromName = "我的位置";
                            LatLng fromLoc = new LatLng(mCurrentLat, mCurrentLon);

                            String toName = marker.getTitle();
                            LatLng toLoc = marker.getPosition();

                            startNavi(fromName, fromLoc, toName, toLoc);
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

    }

    /**
     * 启动百度地图导航(Native)
     */
    public void startNavi(String fromName, LatLng fromLoc, String toName, LatLng toLoc) {

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
            T.showShort(context, "您尚未安装百度地图app或app版本过低");
//            showDialog();
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

    @OnClick({R.id.back_iv, R.id.title_tv, R.id.showMyLoc_iv, R.id.showTargetLoc_iv
            , R.id.showSceneryLoc_iv, R.id.showFoodLoc_iv, R.id.showHotelLoc_iv
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.showMyLoc_iv:
                switchView(mCurrentLat, mCurrentLon, 17);
                break;
            case R.id.showTargetLoc_iv:
                showTarget();
                break;
            case R.id.showSceneryLoc_iv:
                showScenery();
                break;
            case R.id.showFoodLoc_iv:
                showFood();
                break;
            case R.id.showHotelLoc_iv:
                showHotel();
                break;
            case R.id.title_tv:
                break;
        }
    }

    /**
     * 显示传入的地点
     */
    private void showTarget() {
        mBaiduMap.clear();
        if (myMarker == null) {
            //如果未获取到传入参数
            myMarker = (MyMarker) getIntent().getSerializableExtra("model");
        }

        MarkerOptions oo = new MarkerOptions()
                .title(myMarker.getName())
                .position(new LatLng(Double.parseDouble(myMarker.getLat()), Double.parseDouble(myMarker.getLon())))
                .icon(getBd(myMarker.getType()))
                .alpha(0.8f)
                .animateType(MarkerOptions.MarkerAnimateType.grow);
        mBaiduMap.addOverlay(oo);
        sumList.add(myMarker);

        //切换视图
        switchView(Double.parseDouble(myMarker.getLat()), Double.parseDouble(myMarker.getLon()), 11);

        //显示弹框，xml转视图
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_map_info, null);
        TextView textView = (TextView) view.findViewById(R.id.title_tv);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_iv);

        InfoWindow.OnInfoWindowClickListener listener = null;

        //设置标题
        textView.setText(myMarker.getName());
        //设置相应图标
        imageView.setImageResource(imgs[myMarker.getType()]);

        LatLng toLoc = new LatLng(Double.parseDouble(myMarker.getLat()), Double.parseDouble(myMarker.getLon()));
        listener = () -> {
            T.showShort(context, "开始导航...");

            String fromName = "我的位置";
            LatLng fromLoc = new LatLng(mCurrentLat, mCurrentLon);

            String toName = myMarker.getName();
            startNavi(fromName, fromLoc, toName, toLoc);
        };

        mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(view), toLoc, -50, listener);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    /**
     * 显示景点
     */
    private void showScenery() {
        //清空图标
        mBaiduMap.clear();

        //若已请求过，直接显示
        if (sceneryList != null && sceneryList.size() > 0) {
            addMarkerToMap(sceneryList);
            switchView(Double.parseDouble(sceneryList.get(0).getLat())
                    , Double.parseDouble(sceneryList.get(0).getLon()), 11);
            return;
        }

        SceneryDao.queryAll(new SceneryDao.DbCallBack() {
            @Override
            public void onSuccess(List<Scenery> datas) {
                //转mymarker
                for (Scenery model : datas) {
                    MyMarker marker = new MyMarker();
                    marker.setName(model.getTitle());
                    marker.setType(0);
                    marker.setImg(model.getImg());
                    marker.setLat(model.getLat());
                    marker.setLon(model.getLon());
                    sceneryList.add(marker);
                }
                addMarkerToMap(sceneryList);
                switchView(Double.parseDouble(sceneryList.get(0).getLat())
                        , Double.parseDouble(sceneryList.get(0).getLon()), 11);
            }

            @Override
            public void onFailed(Exception e) {
                T.showShort(context, "获取数据失败");
            }
        });
    }

    /**
     * 显示美食
     */
    private void showFood() {
        //清空图标
        mBaiduMap.clear();

        //若已请求过，直接显示
        if (foodList != null && foodList.size() > 0) {
            addMarkerToMap(foodList);
            switchView(Double.parseDouble(foodList.get(0).getLat())
                    , Double.parseDouble(foodList.get(0).getLon()), 11);
            return;
        }

        RestaurantDao.queryAll(new RestaurantDao.DbCallBack() {
            @Override
            public void onSuccess(List<Restaurant> datas) {
                //转mymarker
                for (Restaurant model : datas) {
                    MyMarker marker = new MyMarker();
                    marker.setName(model.getName());
                    marker.setType(0);
                    marker.setImg(model.getImgs());
                    marker.setLat(model.getLat());
                    marker.setLon(model.getLon());
                    foodList.add(marker);
                }
                addMarkerToMap(foodList);
                switchView(Double.parseDouble(foodList.get(0).getLat())
                        , Double.parseDouble(foodList.get(0).getLon()), 11);
            }

            @Override
            public void onFailed(Exception e) {
                T.showShort(context, "获取数据失败");
            }
        });
    }

    /**
     * 显示酒店
     */
    private void showHotel() {
        //清空图标
        mBaiduMap.clear();

        //若已请求过，直接显示
        if (hotelList != null && hotelList.size() > 0) {
            addMarkerToMap(hotelList);
            switchView(Double.parseDouble(hotelList.get(0).getLat())
                    , Double.parseDouble(hotelList.get(0).getLon()), 11);
            return;
        }

        HotelDao.queryAll(new HotelDao.DbCallBack() {
            @Override
            public void onSuccess(List<Hotel> datas) {
                //转mymarker
                for (Hotel model : datas) {
                    MyMarker marker = new MyMarker();
                    marker.setName(model.getName());
                    marker.setType(0);
                    marker.setImg(model.getImgs());
                    marker.setLat(model.getLat());
                    marker.setLon(model.getLon());
                    hotelList.add(marker);
                }
                addMarkerToMap(hotelList);
                switchView(Double.parseDouble(hotelList.get(0).getLat())
                        , Double.parseDouble(hotelList.get(0).getLon()), 11);
            }

            @Override
            public void onFailed(Exception e) {
                T.showShort(context, "获取数据失败");
            }
        });
    }

    /**
     * 添加lsit标注
     *
     * @param list
     */
    private void addMarkerToMap(List<MyMarker> list) {
        if (list == null || list.size() <= 0) return;
        for (MyMarker model : list) {
            MarkerOptions oo = new MarkerOptions()
                    .title(model.getName())
                    .position(new LatLng(Double.parseDouble(model.getLat()), Double.parseDouble(model.getLon())))
                    .icon(getBd(model.getType()))
                    .alpha(0.8f)
                    .animateType(MarkerOptions.MarkerAnimateType.grow);
            mBaiduMap.addOverlay(oo);
            sumList.add(model);
        }
    }

    /**
     * 切换到某个视图
     *
     * @param lat
     * @param lon
     * @param zoom
     */
    private void switchView(double lat, double lon, int zoom) {
        ms = new MapStatus.Builder()
                .target(new LatLng(lat, lon))
                .overlook(0)
                .zoom(zoom)
                .build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }

    /**
     * 根据地点类型获取图标,0-景点|1-美食|2-酒店
     *
     * @param type
     * @return
     */
    private BitmapDescriptor getBd(int type) {
        BitmapDescriptor bd = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_loc_spot);
        switch (type) {
            case TYPE_SCENERY:
                bd = BitmapDescriptorFactory
                        .fromResource(R.mipmap.icon_loc_spot);
                break;
            case TYPE_FOOD:
                bd = BitmapDescriptorFactory
                        .fromResource(R.mipmap.icon_loc_spot);
                break;
            case TYPE_HOTEL:
                bd = BitmapDescriptorFactory
                        .fromResource(R.mipmap.icon_loc_spot);
                break;
        }
        return bd;
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
        //显示传入位置，需延时，否则定位自动回调
        new Handler().postDelayed(() -> showTarget(), 2000);
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
//        bd.recycle();
    }

    /**
     * 根据传入标注信息并启动自身
     */
    public static void startMyself(Context context, MyMarker myMarker) {
        if (myMarker == null) {
            MyMarker marker = new MyMarker();
            marker.setType(TYPE_SCENERY);
            marker.setImg("https://gss0.bdstatic.com/94o3dSag_xI4khGkpoWK1HF6hhy/baike/crop%3D89%2C0%2C710%2C469%3Bc0%3Dbaike92%2C5%2C5%2C92%2C30/sign=69cc9ade00fa513d45e5369e00556cd7/42166d224f4a20a4be6033c598529822720ed0b2.jpg");
            marker.setName("三亚市");
            marker.setLat("18.244147");
            marker.setLon("109.515652");
            startMyself(context, marker);
            return;
        }

        Intent intent = new Intent();
        intent.setClass(context, MapActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("model", myMarker);
        intent.putExtras(mBundle);
        context.startActivity(intent);
    }
}
