package com.androidstudy.pushchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidstudy.pushchat.network.DeviceListResponse;
import com.androidstudy.pushchat.network.DeviceModel;
import com.androidstudy.pushchat.network.TalkModel;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity {

    static final String TAG = "GCM_Demo";

    public static MainActivity mThis = null;
    public Handler mHandler;

    String SENDER_ID = "171794693509";
    String MY_NICK = "스노야";

    LinearLayout layoutChatList;
    ScrollView scrollChatList;

    GoogleCloudMessaging gcm;
    String regid;

    ArrayList<DeviceModel> mDeviceList = new ArrayList<DeviceModel>();
    ArrayList<String> mTargetRegIdList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_main);

        layoutChatList = (LinearLayout)findViewById(R.id.layoutChatList);
        scrollChatList = (ScrollView)findViewById(R.id.scrollChatList);

        gcm = GoogleCloudMessaging.getInstance(this);
        registerInBackground();

        initalizeTargetList();

        mHandler = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                Log.d(TAG, "handleMessage");
                if (msg.obj != null) {
                    addTalk((TalkModel) msg.obj);
                    scrollDown();
                }
            }
        };

        ArrayList<TalkModel> talkList = MyApp.dbHelper.getLalkLog();
        for (TalkModel talk : talkList)
            addTalk(talk);
        scrollDown();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mThis = this;
        MyApp.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mThis = null;
        MyApp.activityPaused();
    }

    private void initalizeTargetList()
    {
        MyApp.mApiService.device_list(new Callback<DeviceListResponse>() {
            public void success(DeviceListResponse deviceListResponse, Response response) {
                Toast.makeText(getApplicationContext(), "device/list success\n" + deviceListResponse.results.toString(), Toast.LENGTH_SHORT).show();
                for (DeviceModel device : deviceListResponse.results) {
                    mDeviceList.add(device);
                }

                for (DeviceModel device : mDeviceList)
                    mTargetRegIdList.add(device.push_token);
            }

            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "device/list failed\n" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String msg = "";
                try {
                    regid = gcm.register(SENDER_ID);
                    Log.i(TAG, "[GCM Registration Success] " + regid);

                    DeviceModel device = new DeviceModel();
                    device.device_id = Build.SERIAL;
                    device.user_name = MY_NICK;
                    device.push_token = regid;
                    MyApp.mApiService.device_create(device, new Callback<DeviceModel>() {
                        public void success(DeviceModel deviceModel, Response response) {
                            Toast.makeText(getApplicationContext(), "device/create success", Toast.LENGTH_SHORT).show();
                        }

                        public void failure(RetrofitError error) {
                            Toast.makeText(getApplicationContext(), "device/create failed\n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch (IOException e) {
                    Log.i(TAG, "[GCM Registration Failed] " + e.getMessage());
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String s) {
            }
        }.execute(null, null, null);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btnSelect) {
            String[] nameList = new String[mDeviceList.size()];
            for (int i=0; i<mDeviceList.size(); i++)
                nameList[i] = mDeviceList.get(i).user_name;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("친구 목록");
            builder.setItems(nameList, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
//                    String targetName = nameList.get(which);
//                    Button btnSelect = (Button)findViewById(R.id.btnSelect);
//                    btnSelect.setText(targetName);
//                    mTargetRegId = mDeviceList.get(targetName);
                }
            });
            builder.show();
        }
        else if (view.getId() == R.id.btnSend) {
            EditText edtContent = (EditText)findViewById(R.id.edtContent);

            TalkModel talk = new TalkModel();
            talk.author_name = MY_NICK;
            talk.created = new Date();
            talk.author_name = MY_NICK;
            talk.content = edtContent.getText().toString();
            talk.my_talk = true;

            sendPush(talk);
            edtContent.setText("");

            MyApp.mApiService.talk_create(talk, new Callback<TalkModel>() {
                @Override
                public void success(TalkModel mealModels, Response response) {
                    Toast.makeText(getApplicationContext(), "talks/create success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getApplicationContext(), "talks/create failed\n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static String API_KEY = "AIzaSyDKSIu5JIw_E27pAarcQDnSe2QAM4A8J48";

    public void sendPush(final TalkModel talk) {
        MyApp.dbHelper.addTalk(talk);
        addTalk(talk);
        scrollDown();

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Sender sender = new Sender(API_KEY);
                Message message = new Message.Builder()
                        .addData("author", talk.author_name)
                        .addData("created", CalUtil.dateToString(talk.created))
                        .addData("content", talk.content)
                        .build();
                try {
                    sender.send(message, mTargetRegIdList, 0);
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        }.execute();
    }

    private void addTalk(TalkModel talk) {
        View item = View.inflate(this, R.layout.item_chat, null);

        LinearLayout layoutChat = (LinearLayout)item.findViewById(R.id.layoutChat);
        if (talk.my_talk) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)layoutChat.getLayoutParams();
            lp.gravity = Gravity.RIGHT;
            lp.leftMargin = DisplayUtil.PixelFromDP(50);
            lp.rightMargin = 0;
            layoutChat.setLayoutParams(lp);
            layoutChat.setBackgroundResource(R.drawable.shape_talk_my);
        }
        else {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)layoutChat.getLayoutParams();
            lp.gravity = Gravity.LEFT;
            lp.leftMargin = 0;
            lp.rightMargin = DisplayUtil.PixelFromDP(50);
            layoutChat.setLayoutParams(lp);
            layoutChat.setBackgroundResource(R.drawable.shape_talk_others);
        }

        // 작성자
        if (talk.author_name != null) {
            TextView lblAuthor = (TextView)item.findViewById(R.id.lblAuthor);
            lblAuthor.setText(talk.author_name);
        }

        // 날짜 및 시간
        if (talk.created != null) {
            TextView lblDatetime = (TextView)item.findViewById(R.id.lblDatetime);
            lblDatetime.setText(CalUtil.dateToString(talk.created));
        }

        // 메시지
        if (talk.content != null) {
            TextView lblMessage = (TextView)item.findViewById(R.id.lblMessage);
            lblMessage.setText(talk.content);
        }

        layoutChatList.addView(item);
    }

    private void scrollDown() {
        scrollChatList.post(new Runnable() {
            public void run() {
                scrollChatList.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_delete_all_talk) {
            MyApp.dbHelper.clearTalkLog();
            layoutChatList.removeAllViews();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
