package com.androidstudy.pushchat;

import android.app.NotificationManager;
import android.os.AsyncTask;
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

    HashMap<String, String> mTargetList = new HashMap<String, String>();
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
        //mTargetList.put("스노야", "APA91bFf7n2S-O7W1lpTaSoQNqSCwoXinevIsc7NVdJUDpoMa4FQ8bbzW76wG8yIH8V_cddnCxCkkiG9Aev46vG5owea3Bys0jgx8KoTKQZ4S6fm0DAGVpSlR4Zv_sOdz7-k2Xa62KRVdlp0_SFf7pE0t1mPpScurQ");
        mTargetList.put("멜트", "APA91bH0zkR6P0PtKboTE3xOOM5Ssib3tDFVrzHhBMM_d4iyWs_y9EsUurznqnipxKdbWGLjdgV2s4bK71ORl8_1bjqPmAon8AOMDGLugFYxK8BNEb759qdFuInmwupw7UQDLtErK4L57yPEYToKxvXjw006DS7O7A");
        mTargetList.put("Gus", "APA91bE8DeJQJ0FC-5ROqsDkv4x1ZP39cjBiGc5oQyI0EiNdHVpBF3azWKam7drW0P2V2Xk5bJL3kTXTWa0zaPBaVDiZfR8e4_FjprspAg3KJAE4QA4-tYInn5wvjgZamcI31fEwCw2aW0Njw5m3S2_3KFHC90FgWg");
        mTargetList.put("아이린", "APA91bFz0GSPUcBdQNTCfT4H83H8jGvWsboUA9RNb1MyObRnhNGI6lfZ8r_J9bgQgf15ha-zxfPSn-tCPhClHxkep9KWDAHmpmXDxhoPDywWln_VZHvQzfaR8JMf5GlMIs8v5ngTEW1CmyRI9mLR4AWGcB9OT3AJGA");
        mTargetList.put("taiwan", "APA91bH1U8N8XfihoVG2Kq1GeQAjUkoFasQhAToeAsnWuMXrqIt46fEg4jRQGs3Z3AKfPXobpA8xlxuPKstX_QbTcRafl8RSUaePuXAAuTIIKLgI3SMll2ctPu1j-JzJ0n3OgWvpfuvO");
        mTargetList.put("Foxy", "APA91bFJDJLGyocR4dskb5m7XK1l5XjAddHo3HCA3DEiszdUIRI54h4cW7iY4OK4TPpY7KNRpc35ukhjNTA9N6IPLUJJe_Lr7DVAYEV2jm7Gpfh73x2Sx_RXti31TGhsgxpXtuToMAdb0JBPCCg4356NasEw94jDgQ");
//        mTargetList.put("에이든", "APA91bFDqjJ-N1EtEehf841I1Ha0nxkdi_iWnqr8sLx_h9X9wldhmCTj32WWRflb8Jkt_iJ_T2MHU6T6xcSZIVIJdaltlSdmlBB6wSmAM-PCgD0z2_vdG59VUYn6g1-Gqsc2r5jkDoW7Yx-6wqANzhaByiqtPKlO-A");
//        mTargetList.put("권터", "APA91bGYEDi_ZPEC6T2lGPxUAls90ASSAqjDF2zB9lPYNmSKQMzh3gJssuyz04GxK1DQT7RNVSs5T3nbJme_VaTkXST0DI6y_baUtN4PMLcRHh8dj7dIU0vIZvMwUk5q1_6BB4u32F17PSlJSNhMLylhfOdqja0nNg");
//        mTargetList.put("클로이", "APA91bGM9KAMb2aKJYUmbxp9_w-oRk7NKiWJR8FG7li4ScZq09CO8wscLpUDqkHI8TozLB0f3Jf1-5s-s2v9t9m6dDzR3myZtt0ugZ_I926vOxV2FaQwyUYZY7D-7ya5s23EklsnN6bJNyzDQaC0P2iUF_pEnqLl1Q");
//        mTargetList.put("Angel", "APA91bGl0tL0cjk8PP2kA4EgiVssYPVAqRRwZFJ32U5spS55yUkP7FXlRv9lxtMtKuSj0cNriv14cnz48NuPP7yOgoRVsfmERgEyt3t1CNTO7rstG5KbBUoMPxPicx_02wvnFyfaOoYfaQk0r2orq6uggvH5VUV03w");
//        mTargetList.put("emmily", "APA91bHj2GrBg8aOD3p4Q_zrh6-1L7kPamB5Nmo2s7ynFKzS2oZXiZlrQCWjmznEgQs0rDrN4-e7NIeejIHEmSTU6fkY44vpO-GFlagtq2gRRpPLxqNrGcIStOsYvy2Soiw3kq9ZYSB0SUejLgtNqnq672IX7gSIKw");
//        mTargetList.put("홍(Hong)", "APA91bFiV1SG40xrYA54by0E0zuX2sgJdxJ5MU1mDXFS-Q2-WkGgfkMClk54DC0d2PoQ4vktZqcSE42zeeRTe36GpPO-FprmxA83KdbMIPl5mvvWT3hgEA4_9WWYRQlemqEK0OSijuL19F72x3jl7GBF1dj0oloh1g");
//        mTargetList.put("Park", "APA91bHlyqeJdHWMnWcNzdpSk03KuaTNLhNFYcKiLm0pVWh4Iu8SqauU_fNNr04niIUtcrJUSZ8L0gysuQe9g2ncUBqx8AuWIxLthI7sIu3_OEhWWLSmwUEDF_37nFwroKcXi9Dx6qWE7YoR0wQQC7qqgUjACY6otQ");
//        mTargetList.put("Geun", "APA91bHvTXBPJ3AUthtpgyRa9r8SNst8-PDxcov0r19vMtkVAXseJXdAqzlHt95_2iZtGO4TsCpdsJ5VAbZL2AfBd3xkKKt2aY3GYT-Tl4bklwW27Q1OmBD3YVrWHjwSs1ACEFUqgn408EbWsjm-5z5hJIVGA0HKnw");

        for (String name : mTargetList.keySet())
            mTargetRegIdList.add(mTargetList.get(name));
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String msg = "";
                try {
                    regid = gcm.register(SENDER_ID);
                    Log.i(TAG, "[GCM Registration Success] " + regid);
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
//            final ArrayList<String> nameList = new ArrayList<String>();
//            for (String name : mTargetList.keySet())
//                nameList.add(name);
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Select Target");
//            builder.setItems(nameList.toArray(new String[0]), new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    String targetName = nameList.get(which);
//                    Button btnSelect = (Button)findViewById(R.id.btnSelect);
//                    btnSelect.setText(targetName);
//                    mTargetRegId = mTargetList.get(targetName);
//                }
//            });
//            builder.show();
        }
        else if (view.getId() == R.id.btnSend) {
//            Log.i(TAG, "Push button clicked!");
//            if (mTargetRegId == null) {
//                Toast toast = Toast.makeText(this, "대상을 지정해 주세요.", Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//                return;
//            }
            EditText edtContent = (EditText)findViewById(R.id.edtContent);
            sendPush(edtContent.getText().toString());
            edtContent.setText("");

            MyApp.mApiService.talk_create(new TalkModel(), new Callback<TalkModel>() {
                @Override
                public void success(TalkModel mealModels, Response response) {
                }

                @Override
                public void failure(RetrofitError error) {
                }
            });
        }
    }

    public static String API_KEY = "AIzaSyDKSIu5JIw_E27pAarcQDnSe2QAM4A8J48";

    public void sendPush(final String content) {
        final TalkModel talk = new TalkModel();
        talk.author = MY_NICK;
        talk.created = new Date();
        talk.content = content;
        talk.my_talk = true;
        MyApp.dbHelper.addTalk(talk);
        addTalk(talk);
        scrollDown();

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Sender sender = new Sender(API_KEY);
                Message message = new Message.Builder()
                        .addData("author", talk.author)
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
        if (talk.author != null) {
            TextView lblAuthor = (TextView)item.findViewById(R.id.lblAuthor);
            lblAuthor.setText(talk.author);
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
