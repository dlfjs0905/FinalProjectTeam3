package com.example.swufinalproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 1;

    private int mMinPrice, mValue; //최소 참여 금액 값 확인을 위한 변수


    //public int index; //주문 접수 마감


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final EditText et = new EditText(this);

        //mJoinAdapter = new JoinAdapter(getContext(), mJoinList);

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
            //주문 완료
            //작성자
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

                if (TextUtils.equals(mBoardBean.full, "full")) {
                    Toast.makeText(DetailActivity.this, "모집이 완료된 글입니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DetailActivity.this);

                    dialogBuilder.setTitle("참여하기");
                    dialogBuilder.setMessage("\n얼마를 주문하겠습니까?(참여 후에는 수정이 불가능하니, 신중하게 입력해주세요.)");

                final EditText et = new EditText(DetailActivity.this);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);

                dialogBuilder.setView(et);

                dialogBuilder.setPositiveButton("참여", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        DatabaseReference dbRef = mFirebaseDatabase.getReference();
                        String id = dbRef.push().getKey();

                        String iwanttojoin = mFirebaseAuth.getCurrentUser().getEmail();
                        mBoardBean.getUserList().add(iwanttojoin);
                        dbRef.child(mBoardBean.id).setValue(mBoardBean);

                        String value = et.getText().toString();         //입력창 생성
                        String value_n = et.getText().toString();

                        //최소 참여 금액 확인을 위해 인트로 변경
                        mMinPrice = Integer.parseInt(mBoardBean.minjoinprice);
                        mValue = Integer.parseInt(value);

                            if (value_n.matches("")) {
                                Toast.makeText(getBaseContext(), "값을 입력하세요", Toast.LENGTH_SHORT).show();
                            }
                            if (mValue < mMinPrice){ //입력 값이 최소 참여 금액에 부합하는지 확인
                                Toast.makeText(getBaseContext(), "최소 참여 금액 이상 주문해주세요.", Toast.LENGTH_SHORT).show();
                            }
                            else {

                                dbIntprice = Integer.parseInt(mBoardBean.totalprice);    //DB에 Strng으로 저장된 가격을 int형으로 바꿔서 dbintprice에 저장
                                curIntprice = Integer.parseInt(value);                  //사용자가 입력한 금액도 int로 바궈준다.

                                dbIntprice = dbIntprice + curIntprice;                  //두 int의 값을 더해서 최종 합 price를 정한다.


                                mBoardBean.getUserPrice().add(value);
                                dbRef.child(mBoardBean.id).setValue(mBoardBean);
                                mBoardBean.myprice = String.valueOf(value);
                                dbStringprice = String.valueOf(dbIntprice);            //다시 db에 넣기 위해 string으로 변환
                                mBoardBean.totalprice = dbStringprice;                      //db price값을 수정한다.
                                mFirebaseDatabase.getReference().child(mBoardBean.id).setValue(mBoardBean);         //firebase에 최종 올려준다.

                                //이제 카카오톡 오픈채팅 비밀번호 알려주는 다이얼로그 하나 밑에 띄운다.

                                if (!(TextUtils.equals(mBoardBean.kakaopwd, ""))) {          //값이 있으면
                                    AlertDialog.Builder dialogBuilder2 = new AlertDialog.Builder(DetailActivity.this);
                                    dialogBuilder2.setTitle("카카오톡 오픈 채팅 비밀번호");
                                    String alertpwd = mBoardBean.kakaopwd;
                                    dialogBuilder2.setMessage("비밀번호 : " + alertpwd + "\n\n * 비밀번호는 한 번만 알려드립니다.");


                                dialogBuilder2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Toast.makeText(DetailActivity.this, "참여가 완료되었습니다.", Toast.LENGTH_SHORT).show();

                                        //여기에 이제 글쓴이에게 알림창

                                    }
                                });
                                dialogBuilder2.show();
                            }
                            onResume();
                        }

                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                dialogBuilder.show();
            }}
        });

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogBuilder3 = new AlertDialog.Builder(DetailActivity.this);
                dialogBuilder3.setTitle("모집을 마감하시겠습니까?");
                dialogBuilder3.setMessage("더 이상 다른 사람이 참여할 수 없습니다.");
                dialogBuilder3.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mBoardBean.full = "full";
                        mFirebaseDatabase.getReference().child(mBoardBean.id).setValue(mBoardBean);         //firebase에 최종 올려준다.
                        Toast.makeText(DetailActivity.this, "모집이 정상적으로 마감되었습니다.", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                finish();
                            }
                        }, 800);//딜레이를 준 후 시작
                    }
                });
                dialogBuilder3.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mBoardBean.full = "unfull";
                        mFirebaseDatabase.getReference().child(mBoardBean.id).setValue(mBoardBean);         //firebase에 최종 올려준다.
                    }
                });
                dialogBuilder3.show();
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
        TextView minJoinPrice = findViewById(R.id.minJoinPrice);
        ImageView imgBar = findViewById(R.id.imgBar);
        TextView writeFoodSpinner = findViewById(R.id.writeFoodSpinner);
        TextView number = findViewById(R.id.number);
        TextView joinid = findViewById(R.id.joinid);
        TextView joinPrice = findViewById(R.id.joinPrice);

        writeFoodDetail.setText(mBoardBean.food);
        startTimeDetail.setText(mBoardBean.starttime);
        endTimeDetail.setText(mBoardBean.endtime);
        locationDetail.setText(mBoardBean.choice_location);
        memoDetail.setText(mBoardBean.memo);
        number.setText("글쓴이");
        joinid.setText(mBoardBean.writerId);
        minJoinPrice.setText(mBoardBean.minjoinprice + "원");
        lowestPrice.setText("목표 금액 : " + mBoardBean.price + "원");
        totalPrice.setText("현재 금액 : " + mBoardBean.totalprice + "원");
        writeFoodSpinner.setText(mBoardBean.choice_foodWrite);

        String[] mStringArray = new String[mBoardBean.getUserList().size()];
        mStringArray = mBoardBean.getUserList().toArray(mStringArray);
        String[] mStringArray2 = new String[mBoardBean.getUserPrice().size()];
        mStringArray2 = mBoardBean.getUserPrice().toArray(mStringArray2);

        joinPrice.setText(mStringArray2[0] + "원");

        if (TextUtils.equals(mBoardBean.writerId, mFirebaseAuth.getCurrentUser().getEmail())) {
            for (int i = 1; i < mStringArray.length; i++) {
                joinid.append("\n" + mStringArray[i]);
            }
        } else {
            String a;

            for (int j = 1; j < mStringArray.length; j++) {
                a = "*";
                for (int i = 1; i < mStringArray[j].length()-3; i++) {
                    a = a+"*";
                }
                joinid.append("\n" + mStringArray[j].charAt(0) + mStringArray[j].charAt(1) + mStringArray[j].charAt(2) + a);
            }
        }
        for (int i = 1; i < mStringArray.length; i++) {
            number.append("\n" + i);
        }
        for (int i = 1; i < mStringArray2.length; i++) {
            joinPrice.append("\n" + mStringArray2[i] + "원");

        }

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