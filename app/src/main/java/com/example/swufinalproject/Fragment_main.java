package com.example.swufinalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Fragment_main extends Fragment {

    ArrayAdapter<CharSequence> spin1, spin2;
    public String choice_food ="";
    public String choice_loc ="";
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private ListView mListView;
    private List<BoardBean> mBoardList = new ArrayList<>();
    private BoardAdapter mBoardAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        final Spinner spin1 = view.findViewById(R.id.spinner);
        final Spinner spin2 = view.findViewById(R.id.spinner2);
        ImageButton btn_refresh = view.findViewById(R.id.btn_refresh);

        this.spin1 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_food, android.R.layout.simple_spinner_dropdown_item);

        this.spin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin1.setAdapter(this.spin1);
        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                choice_food = Fragment_main.this.spin1.getItem(i).toString();

                Fragment_main.this.spin2 = ArrayAdapter.createFromResource(getContext(), R.array.spinner_location, android.R.layout.simple_spinner_dropdown_item);
                Fragment_main.this.spin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin2.setAdapter(Fragment_main.this.spin2);
                spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        choice_loc = Fragment_main.this.spin2.getItem(i).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), choice_food + ", " + choice_loc + "(으)로 검색합니다.", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //데이터 취득
                        mFirebaseDB.getReference().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //데이터를 받아와서 List에 저장.
                                mBoardList.clear();

                                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    BoardBean bean = snapshot.getValue(BoardBean.class);
                                    if (TextUtils.equals(choice_food, "상관없음") && TextUtils.equals(choice_loc, "상관없음") && TextUtils.equals(bean.full, "unfull")) {
                                        mBoardList.add(0, bean);
                                    }
                                    else if (TextUtils.equals(choice_food, "상관없음") && TextUtils.equals(bean.choice_location, choice_loc) && TextUtils.equals(bean.full, "unfull")) {
                                        mBoardList.add(0, bean);
                                    }
                                    else if (TextUtils.equals(bean.choice_foodWrite, choice_food) && TextUtils.equals(choice_loc, "상관없음") && TextUtils.equals(bean.full, "unfull")) {
                                        mBoardList.add(0, bean);
                                    } else if (TextUtils.equals(bean.choice_foodWrite, choice_food) && TextUtils.equals(bean.choice_location, "상관없음")&& TextUtils.equals(bean.full, "unfull")) {
                                        mBoardList.add(0, bean);

                                    } else if (TextUtils.equals(bean.choice_foodWrite, choice_food) && TextUtils.equals(bean.choice_location, choice_loc)&& TextUtils.equals(bean.full, "unfull")) {
                                        mBoardList.add(0, bean);

                                    }
                                }

                                //바뀐 데이터로 Refresh 한다.
                                if(mBoardAdapter != null) {
                                    mBoardAdapter.setBoardList(mBoardList);
                                    mBoardAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }, 500);//딜레이를 준 후 시작
            }
        });

        view.findViewById(R.id.btnWrite).setOnClickListener(mClicks);

        mListView = view.findViewById(R.id.mainLstBoard);

        //ViewGroup layout = (ViewGroup)view.findViewById(R.id.)

        //최초 데이터 셋팅
        mBoardAdapter = new BoardAdapter(getContext(), mBoardList);
        mListView.setAdapter(mBoardAdapter);



        return view;
    }

    private View.OnClickListener mClicks = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnWrite:
                    Intent i = new Intent(getContext(), WriteActivity.class);
                    startActivity(i);
                    break;
            }
        }
    };

}
