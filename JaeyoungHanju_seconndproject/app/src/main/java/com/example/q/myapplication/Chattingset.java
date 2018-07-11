package com.example.q.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import android.support.v7.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class Chattingset extends AppCompatActivity {
    /** Called when the activity is first created. */
    public Socket cSocket = null;
    private String server = "52.231.70.47";  // 서버 ip주소
    private int port = 8082;                           // 포트번호

    public PrintWriter streamOut = null;
    public BufferedReader streamIn = null;

    private String userName = "";

    public TextView tv;
    public EditText nickText;
    public EditText msgText;
    public ScrollView sv;

    public String nickName;

    private String id = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_layout);

        sv = (ScrollView)findViewById(R.id.scrollView1);
        tv = (TextView)findViewById(R.id.text01);
        nickText = (EditText)findViewById(R.id.connText);
        msgText = (EditText)findViewById(R.id.chatText);
        logger("채팅을 시작합니다.");

    }

    public void connBtnClick(View v) {
        switch (v.getId()) {
            case R.id.connBtn: // 접속버튼
                if (cSocket == null) {
                    nickName = nickText.getText().toString();
                    userName = nickName;
                    logger("접속중입니다...");


                }
                break;
            case R.id.closeBtn: // 종료 버튼
                 if (cSocket != null) {
                    userName = "";
                     finish();
                }
                break;
            case R.id.sendBtn: // 메세지 보내기 버튼
                if (userName != "") {
                    String msgString = msgText.getText().toString();
                    if (msgString != null && !"".equals(msgString)) {
                        JSONArray jsonarray = new JSONArray();
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        String formatDate = sdfNow.format(date);

                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.accumulate("id", id);
                            jsonObject.accumulate("name", userName);
                            jsonObject.accumulate("text", msgString);
                            jsonObject.accumulate("time", formatDate);
                            jsonarray.put(jsonObject);
                        } catch (Exception e) {
                        }

                        logger(msgString);
                        NetworkTask addnewcontacts = new NetworkTask("api/sendmessage", "post", null, jsonarray);
                        addnewcontacts.execute();

                    }
                } else {
                    logger("접속을 먼저 해주세요.");
                }
                break;
        }
    }

    private void logger(String MSG) {
        tv.append(MSG + "\n");     // 텍스트뷰에 메세지를 더해줍니다.
        sv.fullScroll(ScrollView.FOCUS_DOWN); // 스크롤뷰의 스크롤을 내려줍니다.
    }


}
