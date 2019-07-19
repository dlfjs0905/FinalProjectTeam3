package com.example.swufinalproject;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class DetailActivity extends AppCompatActivity {

    public static final String STORAGE_DB_URL = "gs://swufinalprojectteam3-e4b9e.appspot.com";
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URL);
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private BoardBean mBoardBean;
    private int mPrice;
    private int mWriterPrice;

    //google map
    private SupportMapFragment mMapFragment;
    private LocationManager mLocationManager;
    private LatLng mCurPosLatLng;

    private int dbIntprice, curIntprice;
    private String dbStringprice, key;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final EditText et = new EditText(this);

        mBoardBean = (BoardBean)getIntent().getSerializableExtra("writeFoodDetail");
        mBoardBean = (BoardBean)getIntent().getSerializableExtra("startTimeDetail");
        mBoardBean = (BoardBean)getIntent().getSerializableExtra("endTimeDetail");
        mBoardBean = (BoardBean)getIntent().getSerializableExtra("locationDetail");
        mBoardBean = (BoardBean)getIntent().getSerializableExtra("memoDetail");
        mBoardBean = (BoardBean)getIntent().getSerializableExtra("lowestPrice");
        mBoardBean = (BoardBean)getIntent().getSerializableExtra("totalPrice");

        Button btnJoin = findViewById(R.id.btnJoin);
        ImageButton btnCommit = findViewById(R.id.btnCommit);
        ImageButton btnkakaolink =findViewById(R.id.btnkakaolink);

        if(TextUtils.equals(mBoardBean.writerId, mFirebaseAuth.getCurrentUser().getEmail())){
            btnJoin.setVisibility(View.INVISIBLE);
            btnCommit.setVisibility(View.VISIBLE);
        }else{
            btnJoin.setVisibility(View.VISIBLE);
            btnCommit.setVisibility(View.INVISIBLE);
        }

        onResume();

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

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DetailActivity.this);

                dialogBuilder.setTitle("참여하기");
                dialogBuilder.setMessage("\n얼마를 주문하겠습니까?");

                final EditText et = new EditText(DetailActivity.this);
                dialogBuilder.setView(et);


                dialogBuilder.setPositiveButton("참여", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        DatabaseReference dbRef = mFirebaseDatabase.getReference();
                        String id = dbRef.push().getKey();

                        key = "true";
                        mBoardBean.key = key;

                        dbRef.child(mBoardBean.id).setValue(mBoardBean);

                        String value = et.getText().toString();         //입력창 생성
                        dbIntprice = Integer.parseInt(mBoardBean.totalprice);    //DB에 Strng으로 저장된 가격을 int형으로 바꿔서 dbintprice에 저장
                        curIntprice = Integer.parseInt(value);                  //사용자가 입력한 금액도 int로 바궈준다.

                        dbIntprice=dbIntprice+curIntprice;                  //두 int의 값을 더해서 최종 합 price를 정한다.


                        mBoardBean.myprice = String.valueOf(value);
                        dbStringprice = String.valueOf(dbIntprice);            //다시 db에 넣기 위해 string으로 변환
                        mBoardBean.totalprice = dbStringprice;                      //db price값을 수정한다.
                        mFirebaseDatabase.getReference().child(mBoardBean.id).setValue(mBoardBean);         //firebase에 최종 올려준다.

                        //이제 카카오톡 오픈채팅 비밀번호 알려주는 다이얼로그 하나 밑에 띄운다.

                        AlertDialog.Builder dialogBuilder2 = new AlertDialog.Builder(DetailActivity.this);
                        dialogBuilder2.setTitle("카카오톡 오픈 채팅 비밀번호");
                        String alertpwd = mBoardBean.kakaopwd;
                        dialogBuilder2.setMessage("비밀번호 : " + alertpwd + "\n\n * 비밀번호는 한 번만 알려드립니다.");


                        dialogBuilder2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });
                        dialogBuilder2.show();

                        onResume();

                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {  }
                });
                dialogBuilder.show();
            }
        });

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Toast.makeText(DetailActivity.this, "완료된 주문입니다.", Toast.LENGTH_LONG).show();
            }
        });

        btnkakaolink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBoardBean.kakaolink));
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        TextView  lowestPrice= findViewById(R.id.lowestPrice);
        TextView totalPrice = findViewById(R.id.totalPrice);
        TextView writeFoodDetail = findViewById(R.id.writeFoodDetail);
        TextView startTimeDetail = findViewById(R.id.startTimeDetail);
        TextView endTimeDetail = findViewById(R.id.endTimeDetail);
        TextView locationDetail = findViewById(R.id.locationDetail);
        TextView memoDetail = findViewById(R.id.memoDetail);
        ImageView imgBar = findViewById(R.id.imgBar);
        TextView writeFoodSpinner = findViewById(R.id.writeFoodSpinner);

        writeFoodDetail.setText(mBoardBean.food);
        startTimeDetail.setText(mBoardBean.starttime);
        endTimeDetail.setText(mBoardBean.endtime);
        locationDetail.setText(mBoardBean.choice_location);
        memoDetail.setText(mBoardBean.memo);
        lowestPrice.setText("목표 금액 : " + mBoardBean.price + "원");
        totalPrice.setText("현재 금액 : " + mBoardBean.totalprice + "원");
        writeFoodSpinner.setText(mBoardBean.choice_foodWrite);

        mWriterPrice = (Integer.parseInt(mBoardBean.totalprice)); //현재 참여 금액 int로 변경
        mPrice = (Integer.parseInt(mBoardBean.price)/5); //최소 금액 int로 변경


        //바 이미지 변경
        if ((mPrice <= mWriterPrice) && (mWriterPrice < (mPrice * 2))){
            imgBar.setImageResource(R.drawable.bar_20per);
        } else if (((mPrice * 2) <= mWriterPrice) && (mWriterPrice < (mPrice * 3))){
            imgBar.setImageResource(R.drawable.bar_40per);
        } else if (((mPrice * 3) <= mWriterPrice) && (mWriterPrice < (mPrice * 4))){ //
            imgBar.setImageResource(R.drawable.bar_60per);
        } else if (((mPrice* 4) <= mWriterPrice) && (mWriterPrice < (mPrice * 5))){
            imgBar.setImageResource(R.drawable.bar_80per);
        } else if (mWriterPrice >= (mPrice * 5)){
            imgBar.setImageResource(R.drawable.bar_100per);
        }

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

            //줌인 줌아웃 버튼 추가
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            //나침반 추가
            googleMap.getUiSettings().setCompassEnabled(true);

            if(TextUtils.equals(mBoardBean.choice_location, "샬롬하우스")){
                mCurPosLatLng = new LatLng(37.628959, 127.088874);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
                mMapFragment.getMapAsync(mapReadyCallback);
            }else if(TextUtils.equals(mBoardBean.choice_location, "제2과학관")){
                mCurPosLatLng = new LatLng(37.629255, 127.090492);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
                mMapFragment.getMapAsync(mapReadyCallback);
            }else if(TextUtils.equals(mBoardBean.choice_location, "제1과학관")){
                mCurPosLatLng = new LatLng(37.628971, 127.089621);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
                mMapFragment.getMapAsync(mapReadyCallback);
            }else if(TextUtils.equals(mBoardBean.choice_location, "50주년기념관")){
                mCurPosLatLng = new LatLng(37.626119, 127.093053);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
                mMapFragment.getMapAsync(mapReadyCallback);
            }else if(TextUtils.equals(mBoardBean.choice_location, "인문사회관")){
                mCurPosLatLng = new LatLng(37.628008, 127.092541);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
                mMapFragment.getMapAsync(mapReadyCallback);
            }else if(TextUtils.equals(mBoardBean.choice_location, "학생누리관")){
                mCurPosLatLng = new LatLng(37.628662, 127.090524);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
                mMapFragment.getMapAsync(mapReadyCallback);
            }else if(TextUtils.equals(mBoardBean.choice_location, "국제생활관")){
                mCurPosLatLng = new LatLng(37.628101, 127.088646);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
                mMapFragment.getMapAsync(mapReadyCallback);
            }else{
                mCurPosLatLng = new LatLng(37.625714, 127.093692);
            }

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(mCurPosLatLng);
            googleMap.addMarker(markerOptions).showInfoWindow();

            googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        }
    };

}