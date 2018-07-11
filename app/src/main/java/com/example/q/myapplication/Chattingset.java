package com.example.q.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.*;
import android.os.*;
import android.util.Log;
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
    private String mRealtime = "";
    private long hereherenow = 0;

    public TextView tv;
    public EditText nickText;
    public EditText msgText;
    public ScrollView sv;

    public String nickName;
    public Button refreshbtn;

    private String id = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_layout);

        Button refreshbtn = (Button) findViewById(R.id.refreshbtn);
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkTask getContact = new NetworkTask("api/getallmessages", "get", null, null);
                getContact.execute();
                try{
                    Toast.makeText(Chattingset.this.getApplicationContext(), "It works well", Toast.LENGTH_LONG).show();
                    String s = getContact.get();
                    JSONArray messageList = new JSONArray(s);
                    for (int i = 0 ; i < messageList.length() ; i ++){
                        JSONObject jsonObject = (JSONObject) messageList.get(i);
                        String eachname = jsonObject.getString("name");
                        String eachmessage = jsonObject.getString("text");
                        String eachtime = jsonObject.getString("time");
                        String eachrealtime = jsonObject.getString("realtime");

                        long eachrealtimelong = Long.parseLong(eachrealtime);

                        if (hereherenow < eachrealtimelong){
                            if (eachname.equals(userName)){
                                eachname = "                                                " + eachname;
                                eachmessage = "                                                "+ eachmessage;
                                eachtime= "                                                " + eachtime;
                            }
                            String asdfasdf = eachname + "\n" + eachmessage + "\n" + eachtime;
                            logger(asdfasdf);
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(Chattingset.this.getApplicationContext(), "Toast JSONError", Toast.LENGTH_LONG).show();
                }

                hereherenow = System.currentTimeMillis();
                sv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });


       Button camera = (Button) findViewById(R.id.cameraButton2);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Screenshot!", Toast.LENGTH_LONG).show();
                View rootView = getWindow().getDecorView();
                File screenShot =  ScreenShot(rootView);
                if(screenShot!=null){
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
                }
            }
            public File ScreenShot(View view) {
                view.setDrawingCacheEnabled(true); //화면에 뿌릴때 캐시를 사용하게 한다

                Bitmap screenBitmap = view.getDrawingCache(); //캐시를 비트맵으로 변환

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");

                String formatDate = sdfNow.format(date);
                String filename = formatDate+".png"; //저장될 파일명
                File file = new File(Environment.getExternalStorageDirectory()+"/Pictures" , filename); //Pictures폴더 screenshot.png 파일

                Log.d("Pictures", Environment.getExternalStorageDirectory().toString()); //저장되는위치를 확인하기위한 코드
                //로그캣으로 "Pictures"를 태그로걸면 저장되는 위치를 출력해준다. 06-22 17:23:57.772 31578-31578/com.ezen.naver2 D/Pictures: /storage/emulated/0  -출력값

                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(file);
                    screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os); //비트맵을 PNG파일로 변환
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                view.setDrawingCacheEnabled(false);
                return file;
            }

        });


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
                    Toast.makeText(getApplicationContext(), userName +"으로 접속했습니다 !", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.closeBtn: // 종료 버튼
                nickText.setText("");
                finish();
                break;

            case R.id.sendBtn: // 메세지 보내기 버튼
                if (userName != "") {
                    String msgString = msgText.getText().toString();
                    if (msgString != null && !"".equals(msgString)) {
                        JSONArray jsonarray = new JSONArray();
                        long now = System.currentTimeMillis();
                        String stnow = String.valueOf(now);
                        Date date = new Date(now);
                        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        String formatDate = sdfNow.format(date);
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.accumulate("id", id);
                            jsonObject.accumulate("name", userName);
                            jsonObject.accumulate("text", msgString);
                            jsonObject.accumulate("time", formatDate);
                            jsonObject.accumulate("realtime", stnow);
                            jsonarray.put(jsonObject);
                        } catch (Exception e) {
                        }
                        NetworkTask addnewcontacts = new NetworkTask("api/sendmessage", "post", null, jsonarray);
                        addnewcontacts.execute();
                        msgText.setText("");
                    }
                } else {
                    logger("접속을 먼저 해주세요.");
                }
                break;
        }
    }

    private void logger(String MSG) {
        tv.append(MSG + "\n\n");
        // 텍스트뷰에 메세지를 더해줍니다.
        sv.fullScroll(ScrollView.FOCUS_DOWN); // 스크롤뷰의 스크롤을 내려줍니다.
    }

}
