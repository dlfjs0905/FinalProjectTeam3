package com.example.swufinalproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.zip.Inflater;

public class WriteActivity extends AppCompatActivity {

    public static final String STORAGE_DB_URL = "gs://swufinalprojectteam3-e4b9e.appspot.com";

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URL);
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    public String key;

    //google map
    private SupportMapFragment mMapFragment;
    private LocationManager mLocationManager;
    private LatLng mCurPosLatLng;    //현재위치 저장 위도,경도 변수

    private int mBtnClickIndex = 0; //어떤 버튼의 index 가 클릭됐는지를 저장

    String choice_foodWrite = "";
    String choice_location = "";
    String choice_startTime = "";
    String choice_endTime = "";
    private int intPrice;

    TextView txtTime, txtLocation;
    private BoardBean mBoardBean;

    EditText edtFoodWrite;
    EditText edtPrice, writerPrice;
    EditText edtMemo;
    String txtFoodWrite, txtPrice, txtMemo, txtwriterPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);


        final Spinner spinnerFoodWrite = findViewById(R.id.spinnerFoodWrite);
        final Spinner spinnerLocation = findViewById(R.id.spinnerLocation);
        final Spinner spinnerStartTime = (Spinner) findViewById(R.id.spinnerStartTime);
        final Spinner spinnerEndTime = (Spinner) findViewById(R.id.spinnerEndTime);


        edtFoodWrite = findViewById(R.id.edtFoodWrite);
        writerPrice = findViewById(R.id.writerPrice);
        edtPrice = findViewById(R.id.edtPrice);
        edtMemo = findViewById(R.id.edtMemo);

        //음식 선택 스피너
        ArrayAdapter<CharSequence> adapterFoodWrite = ArrayAdapter.createFromResource(this, R.array.spinner_food, android.R.layout.simple_spinner_dropdown_item);
        adapterFoodWrite.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFoodWrite.setAdapter(adapterFoodWrite);
        spinnerFoodWrite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                choice_foodWrite = spinnerFoodWrite.getSelectedItem().toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {            }
        });


        //장소선택 스피너

        //Spinner spinner = (Spinner) findViewById(R.id.spinnerLocation);
        // 선택창에 표시되는 타이틀의 문자열을 설정
        //spinner.setPrompt("선택하세요");

        ArrayAdapter<CharSequence> adapterLocation = ArrayAdapter.createFromResource(this, R.array.spinner_location, android.R.layout.simple_spinner_dropdown_item);
        adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(adapterLocation);
        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                choice_location = spinnerLocation.getSelectedItem().toString();
                //구글맵이 로딩이 완료되면 아래의 이벤트가 발생한다.
                mMapFragment.getMapAsync(mapReadyCallback);


            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {           }
        });


        //start 시간 스피너
        ArrayAdapter<CharSequence> adapterStartTime = ArrayAdapter.createFromResource(this, R.array.spinner_time1, android.R.layout.simple_spinner_dropdown_item);
        adapterStartTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStartTime.setAdapter(adapterStartTime);
        spinnerStartTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                choice_startTime = spinnerStartTime.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {            }
        });

        //end 시간 스피너
        ArrayAdapter<CharSequence> adapterEndTime = ArrayAdapter.createFromResource(this, R.array.spinner_time2, android.R.layout.simple_spinner_dropdown_item);
        adapterEndTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEndTime.setAdapter(adapterEndTime);
        spinnerEndTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                choice_endTime = spinnerEndTime.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {            }
        });

        //google map
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //구글맵이 로딩이 완료되면 아래의 이벤트가 발생한다.
        mMapFragment.getMapAsync(mapReadyCallback);

        //GSP 가 켜져 있는지 확인한다.
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //GSP 설정하는 Setting 화면으로 이동한다.
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(i);
        }

        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }

        //GPS 위치를 0.1초마다 10m 간격범위안에서 이동하면 위치를 listener 로 보내주도록 등록한다.
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, locationListener);
        //WIFI 위치를 0.1초마다 10m 간격범위안에서 이동하면 위치를 listener 로 보내주도록 등록한다.
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 10, locationListener);

        //intPrice = Integer.parseInt(txtwriterPrice) + Integer.parseInt(txtPrice);

        //글쓰기 완료 버튼 누르면 DB 저장
        ImageButton btnComplete = (ImageButton) findViewById(R.id.btnComplete);
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference dbRef = mFirebaseDatabase.getReference();
                String id = dbRef.push().getKey();

                txtFoodWrite = edtFoodWrite.getText().toString();
                txtPrice = edtPrice.getText().toString();
                txtwriterPrice = writerPrice.getText().toString();
                txtMemo = edtMemo.getText().toString();


                BoardBean boardBean = new BoardBean();
                boardBean.id = id;
                boardBean.food = txtFoodWrite;
                boardBean.price = txtPrice;
                boardBean.myprice = txtwriterPrice;
                boardBean.totalprice = txtwriterPrice;
                boardBean.starttime = choice_startTime;
                boardBean.endtime = choice_endTime;
                boardBean.choice_foodWrite = choice_foodWrite;
                boardBean.choice_location = choice_location;
                boardBean.memo = txtMemo;

                //내가 쓴 글도 참여내역에 뜨도록
                key = "true";
                boardBean.key = key;


                dbRef.child(boardBean.id).setValue(boardBean);

                Toast.makeText(WriteActivity.this, "작성되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //위치 변경시 위도, 경도 정보 update 수신
            mCurPosLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            //Toast.makeText(getBaseContext(), "현재 위치가 갱신 되었습니다. " + mCurPosLatLng.latitude + ", " + mCurPosLatLng.longitude, Toast.LENGTH_SHORT).show();
            //구글맵을 현재 위치로 이동시킨다.
            mMapFragment.getMapAsync(mapReadyCallback);

            //현재 위치를 한번만 호출하기 위해 리스너 해지
            mLocationManager.removeUpdates(locationListener);
        }
        @Override        public void onStatusChanged(String s, int i, Bundle bundle) {        }
        @Override        public void onProviderEnabled(String s) {        }
        @Override        public void onProviderDisabled(String s) {        }
    };

    //구글맵 로딩완료후 이벤트
    private OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(final GoogleMap googleMap) {

            if (
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                return;
            }
            //현재버튼 추가
            googleMap.setMyLocationEnabled(true);
            //줌인 줌아웃 버튼 추가
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            //나침반 추가
            googleMap.getUiSettings().setCompassEnabled(true);

            MarkerOptions markerOptions = new MarkerOptions();

            googleMap.clear();

            if(TextUtils.equals(choice_location, "샬롬하우스")){
                mCurPosLatLng = new LatLng(37.628959, 127.088874);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
            }else if(TextUtils.equals(choice_location, "제2과학관")){
                mCurPosLatLng = new LatLng(37.629255, 127.090492);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
            }else if(TextUtils.equals(choice_location, "제1과학관")){
                mCurPosLatLng = new LatLng(37.628971, 127.089621);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
            }else if(TextUtils.equals(choice_location, "50주년기념관")){
                mCurPosLatLng = new LatLng(37.626119, 127.093053);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
            }else if(TextUtils.equals(choice_location, "인문사회관")){
                mCurPosLatLng = new LatLng(37.628008, 127.092541);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
            }else if(TextUtils.equals(choice_location, "학생누리관")){
                mCurPosLatLng = new LatLng(37.628662, 127.090524);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
            }else if(TextUtils.equals(choice_location, "국제생활관")){
                mCurPosLatLng = new LatLng(37.628101, 127.088646);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
            }else{
                mCurPosLatLng = new LatLng(37.625714, 127.093692);
            }
            markerOptions.position(mCurPosLatLng);
            googleMap.addMarker(markerOptions).showInfoWindow();
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

            //맵을 클릭했을 때 이벤트를 등록한다.
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("클릭한 장소 ");
                    markerOptions.snippet("위도:" + latLng.latitude + ", 경도: " + latLng.longitude);
                    googleMap.addMarker(markerOptions).showInfoWindow();
                }
            });

            //snippet 클릭시 마커삭제
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    marker.remove();
                }
            });

        }
    };


}
