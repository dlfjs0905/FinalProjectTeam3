package com.example.swufinalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.net.URL;
import java.util.List;

public class BoardAdapter extends BaseAdapter {
    private Context mContext;
    private List<BoardBean> mBoardList;
    private int mPrice;
    private int mWriterPrice;

    public BoardAdapter(Context context, List<BoardBean> boardList) {
        mContext = context;
        mBoardList = boardList;
    }

    @Override
    public int getCount() {
        return mBoardList.size();
    }

    @Override
    public Object getItem(int i) {
        return mBoardList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setBoardList(List<BoardBean> boardList){
        mBoardList = boardList;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view =  inflater.inflate(R.layout.view_board_item, null);

        TextView txtLocation = view.findViewById(R.id.txtLocation);
        TextView txtStartTime = view.findViewById(R.id.txtStartTime);
        TextView txtEndTime = view.findViewById(R.id.txtEndTime);
        TextView txtPrice = view.findViewById(R.id.txtPrice);
        ImageView imgFood = view.findViewById(R.id.imgFood);
        ImageView imgBar = view.findViewById(R.id.imgBar);

        final BoardBean boardBean = mBoardList.get(i);

        if("한식".equals(boardBean.choice_foodWrite))
            imgFood.setImageResource(R.drawable.korea);
        else if("중식".equals(boardBean.choice_foodWrite))
            imgFood.setImageResource(R.drawable.china);
        else if("일식".equals(boardBean.choice_foodWrite))
            imgFood.setImageResource(R.drawable.japan);
        else if("분식".equals(boardBean.choice_foodWrite))
            imgFood.setImageResource(R.drawable.bunsik);
        else if("패스트푸드".equals(boardBean.choice_foodWrite))
            imgFood.setImageResource(R.drawable.america);


        txtLocation.setText(boardBean.choice_location);
        txtStartTime.setText(boardBean.starttime);
        txtEndTime.setText(boardBean.endtime);
        txtPrice.setText(boardBean.price + "원");

        mWriterPrice = (Integer.parseInt(boardBean.totalprice)); //현재 참여 금액 int로 변경
        mPrice = (Integer.parseInt(boardBean.price)/5); //최소 금액 int로 변경


        //바 이미지 변경
        if (mWriterPrice < mPrice){
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

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("writeFoodDetail", boardBean);
                intent.putExtra("startTimeDetail", boardBean);
                intent.putExtra("endTimeDetail", boardBean);
                intent.putExtra("locationDetail", boardBean);
                intent.putExtra("memoDetail", boardBean);
                intent.putExtra("lowestPrice", boardBean);
                intent.putExtra("totalPrice", boardBean);

                mContext.startActivity(intent);
            }
        });

        return view;
    }
}