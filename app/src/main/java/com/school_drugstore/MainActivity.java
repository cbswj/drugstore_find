package com.school_drugstore;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.LogManager;

public class MainActivity extends AppCompatActivity   {

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

    ArrayList<drugstore_MyItem> items;

    titleThread titleTR;

    drugstore_MyAdapter mMyAdapter;

    TMapTapi tmaptapi; // T맵

    TextView danger;


    private ListView mListView;
    TextView locatin;

    String x;
    String y;

    ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 권한 허용을 묻지 않으면 보안상 꺼지는구나..
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        MainActivity activity= this;
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        tmaptapi = new TMapTapi(getApplicationContext());
        tmaptapi.setSKTMapAuthentication ("f0e3f19c-fd86-469f-96a0-cccb845845c7");

        locatin = findViewById(R.id.location);
        danger = findViewById(R.id.danger);

        GpsTracker gpsTracker;
        gpsTracker = new GpsTracker(MainActivity.this);
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        x=String.valueOf(latitude);
        y=String.valueOf(longitude);
        start();
        String address = getCurrentAddress(latitude, longitude);
        locatin.setText(address);
        if (items.size()==0){
            danger.setText("현재 열려있는 약국 정보가 없습니다.");
            danger.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }

        danger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmaptapi.invokeNavigate("T타워", 126.984098f, 37.566385f, 0, true);
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



        mMyAdapter = new drugstore_MyAdapter();

        mMyAdapter.tmap_setOnItemClickListener(new drugstore_MyAdapter.tmapClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                drugstore_MyItem model = items.get(pos);
                tmaptapi.invokeRoute(model.getTitle(), Float.valueOf(model.getX()), Float.valueOf(model.getY()));
            }
        });

        items = new ArrayList<>();
        for (int i=0; i<titlevec.size(); i++) {
            items.add(new drugstore_MyItem(titlevec.get(i), addressvec.get(i), telvec.get(i), longitudevec.get(i),latitudevec.get(i),R.drawable.list_tmap2));
        }

        mMyAdapter.addAll(items);
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(mMyAdapter);
        dialog.cancel();
    }

    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }
}



