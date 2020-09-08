package com.school_drugstore;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;
import java.util.Vector;

public class Tmap extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback  {

    //title부분을 저장하기 위한 객체 선언
    Vector<String> titlevec = new Vector<String>();
    //주소 저장하기 위한 객체 선언
    Vector<String> addressvec = new Vector<String>();
    //전화번호 저장하기 위한 객체 선언
    Vector<String> telvec = new Vector<String>();
    //latitudedvec를 저장하기 위한 객체 선언
    Vector<String> latitudevec = new Vector<String>();
    //longitudevec를 저장하기 위한 객체 선언
    Vector<String> longitudevec = new Vector<String>();

    titleThread titleTR;

    String x;
    String y;

    TMapTapi tmaptapi; // T맵 API

    TMapView tMapView;// 티맵
    TMapGpsManager tmapgps =null;

    ProgressDialog dialog;


    @Override
    public void onLocationChange(Location location) {
        tMapView.setLocationPoint(location.getLongitude(),location.getLatitude());
        dialog.cancel();
    }

    private TMapData tMapData;
    ArrayList<TMapPoint> alTMapPoint= new ArrayList();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tmap);

        // 권한 허용을 묻지 않으면 보안상 꺼지는구나..
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
        }

        View view = getWindow().getDecorView();  // 액티비티의 view 뷰 정보 가져오기

        if (Build.VERSION.SDK_INT >= 21) {
            //21 버전보다 낮으면 검은색 바탕
            getWindow().setStatusBarColor(Color.BLACK);

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정

                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);  // 밝은 상태바 요청

                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));

            }

        }

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("데이터 확인중");
        dialog.show();// 프로그레스바 시작

        tmaptapi = new TMapTapi(getApplicationContext());
        tmaptapi.setSKTMapAuthentication ("f0e3f19c-fd86-469f-96a0-cccb845845c7");

        LinearLayout li = (LinearLayout) findViewById(R.id.tmap);

        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("f0e3f19c-fd86-469f-96a0-cccb845845c7");
        li.addView(tMapView);

        tMapData = new TMapData();
        tmapgps = new TMapGpsManager(Tmap.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);


        tmapgps.OpenGps();


        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);


        GpsTracker gpsTracker;
        gpsTracker = new GpsTracker(Tmap.this);
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        x=String.valueOf(latitude);
        y=String.valueOf(longitude);
        start();


        tMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                tmaptapi.invokeRoute(tMapMarkerItem.getName(), (float)tMapMarkerItem.longitude,(float)tMapMarkerItem.latitude);
            }
        });


    }


    public void start(){
        titleTR = new titleThread(x,y);
        titleTR.start();
        while(true){
            try {
                Thread.sleep(1000); //1.0초마다 실행
                if(titleTR.flag == true){

                    titlevec = titleTR.titlevec;
                    addressvec = titleTR.addressvec;
                    telvec = titleTR.telvec;
                    latitudevec = titleTR.latitudevec;
                    longitudevec = titleTR.longitudevec;
                    break; //반복문 종료
                }
            } catch (Exception e) {
            }
        }



        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gps);
        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),R.drawable.tmap2);


        for(int i=0; i<latitudevec.size();i++) {
            alTMapPoint.add(new TMapPoint(Double.valueOf(latitudevec.get(i)), Double.valueOf(longitudevec.get(i))));
        }



        for(int i=0; i<alTMapPoint.size(); i++){
            TMapMarkerItem markerItem1 = new TMapMarkerItem();

            // 마커 아이콘 지정
            markerItem1.setName(titlevec.get(i));
            markerItem1.setIcon(bitmap);
            // 마커의 좌표 지정
            markerItem1.setTMapPoint(alTMapPoint.get(i));
            markerItem1.setCalloutRightButtonImage(bitmap1);

            markerItem1.setCalloutTitle(titlevec.get(i));
            markerItem1.setAutoCalloutVisible(true);
            markerItem1.setCanShowCallout(true);
            //지도에 마커 추가
            tMapView.addMarkerItem("markerItem"+i, markerItem1);
        }

    }
}
