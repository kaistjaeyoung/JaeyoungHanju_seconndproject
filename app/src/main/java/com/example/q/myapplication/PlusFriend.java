package com.example.q.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.example.q.myapplication.MainActivity.mDbOpenHelper;

public class PlusFriend extends AppCompatActivity {
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plus_friend);
        context = this;
        ListView listview = findViewById(R.id.nonFavListView);

        Button btn = (Button) findViewById(R.id.favAddButton);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context, "An item of the ListView is clicked.", Toast.LENGTH_LONG).show();
                ListItemClickAdapter.ViewHolder holder = (ListItemClickAdapter.ViewHolder) view.getTag();
                if(holder.clickedOddTimes){
                    holder.heart.setImageResource(R.drawable.heartbreak);
                    holder.clickedOddTimes = false;
                }
                else{
                    holder.heart.setImageResource(R.drawable.heart);
                    holder.clickedOddTimes = true;
                    String name = (String) holder.name.getText();
                    String phoneNum = (String) holder.phoneNum.getText();
                }

                String phoneNum = (String) holder.phoneNum.getText();
                Cursor cursor = mDbOpenHelper.mDB.rawQuery("SELECT * FROM people", null);
                while (cursor.moveToNext()) {
                    int ID = cursor.getInt(0);
                    String NAME = cursor.getString(1);
                    String PHONE =  cursor.getString(2);
                    Log.d("database phoneNum",  PHONE);
                    Log.d("ViewHolder phoneNum",  phoneNum);
                    int FAVOR = cursor.getInt(3);
                    if(Objects.equals(PHONE, phoneNum)){
                        Log.d( "", "DETECTED!!!");
                        mDbOpenHelper.updateColumn(ID,NAME,PHONE,1-FAVOR);
                    }
                }

            }
        });


        ListUpdate(listview);
    }

    public void ListUpdate(ListView l1){

        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> phoneNumList = new ArrayList<>();

        if ( mDbOpenHelper.mDB != null) {
            //CONTACTS.execSQL("CREATE TABLE IF NOT EXISTS people (Name TEXT, Phonenumber TEXT, Favor INTEGER)");
            // 쿼리문으로 데이터 불러옴
            Cursor cursor = mDbOpenHelper.mDB.rawQuery("SELECT * FROM people", null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                String phoneNum = cursor.getString(2);
                int favor = cursor.getInt(3);
                if(favor == 0){
                    nameList.add(name);
                    phoneNumList.add(phoneNum);
                }
            }
        }
        ListItemClickAdapter adapter = new ListItemClickAdapter(this,R.layout.plus_friend_list_item, nameList, phoneNumList);
        l1.setAdapter(adapter);
    }
}
