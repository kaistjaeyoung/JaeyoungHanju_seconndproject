package com.example.q.myapplication;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class facebooklogin extends AppCompatActivity {
    TextView mtextStatus;
    LoginButton login_button;
    Button mloginbtn, mlogoutbtn;
    CallbackManager callbackManager;
    AccessToken mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.facebooklogin_layout);
        initializeControls();

        Button chatbtn = (Button) findViewById(R.id.guest);
        chatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Chattingset.class);
                startActivityForResult(intent, 30);
            }
        });

    }




    private void initializeControls(){
        callbackManager = CallbackManager.Factory.create();
        mtextStatus = (TextView)findViewById(R.id.textStatus);

        login_button = (LoginButton)findViewById(R.id.login_button);
        login_button.setReadPermissions("email");

        mloginbtn = (Button)findViewById(R.id.login_2);
        mloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
                if (loggedIn == true) {
                    login_button.performClick();
                } else {
                    Toast.makeText(getApplicationContext(), "넌 이미 login 되어있다", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Chattingset.class);
                    startActivityForResult(intent, 30);
                }
            }
        });
        mlogoutbtn = (Button)findViewById(R.id.logout);
        mlogoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnectFromFacebook();
            }
        });


        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "Log in 성공", Toast.LENGTH_SHORT).show();
                mAccessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(mAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override

                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String email = object.getString("name");
                            Toast.makeText(getApplicationContext(), email, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Chattingset.class);
                            startActivityForResult(intent, 30);

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "에러뜸 ㅋ", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "email, name");//데이터를 전부 받아오지 않고 email만 받아온다. "email,name,age" 의 형식으로 받아올수 있음.
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Log in 취소", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Log in 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            Toast.makeText(getApplicationContext(), "넌 이미 Log Out 되어있다.", Toast.LENGTH_SHORT).show();
            return; // already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
                Toast.makeText(getApplicationContext(), "로그아웃 성공 !", Toast.LENGTH_SHORT).show();
            }
        }).executeAsync();
    }
}