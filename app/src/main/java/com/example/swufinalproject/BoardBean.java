package com.example.swufinalproject;

import android.widget.EditText;

import java.io.Serializable;

public class BoardBean implements Serializable {

    public String id;
    public String food;
    public String starttime;
    public String endtime;
    public String price;        //배달 위한 최저 금액
    public String myprice;      //내가 참여하는 금액
    public String totalprice;   //총 모인 금액
    public String choice_foodWrite;
    public String choice_location;
    public String memo;
    public String key;

}