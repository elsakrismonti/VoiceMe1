package com.example.voiceme.view.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voiceme.Firebase;
import com.example.voiceme.R;
import com.example.voiceme.adapter.MessageAdapter;
import com.example.voiceme.model.ChatModel;
import com.example.voiceme.presenter.MessagePresenter;

import java.util.List;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MessageActivity extends AppCompatActivity implements MessagePresenter.Presenter {

    String currentUserId;
    Toolbar chatToolBar;
    TextView tVProgress;
    String recipientId;

    private MessageAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView micImage;
    private ImageButton btnRecord;
    private PulsatorLayout pulsatorLayout;
    private Animation animScale;
    private MessagePresenter messagePresenter;
    private static final int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initialize();
        settingActionBar();
        setRecycleView();

    }

    private void initialize() {
        tVProgress = findViewById(R.id.tVProgress);
        recyclerView = findViewById(R.id.recycle_view_message);
        messagePresenter = new MessagePresenter(this);
        pulsatorLayout = findViewById(R.id.pulsator);
        micImage = findViewById(R.id.imgV_mic);
        btnRecord = findViewById(R.id.btn_record);
        animScale = AnimationUtils.loadAnimation(this,R.anim.scale);
        chatToolBar = findViewById(R.id.chat_toolbar);
    }

    private void settingActionBar() {
        setSupportActionBar(chatToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        currentUserId = Firebase.currentUser().getUid();
    }

    private void setRecycleView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MessageAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    public void sendMessageBtn(View view) {
        micImage.startAnimation(animScale);
        btnRecord.startAnimation(animScale);
        messagePresenter.start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messagePresenter.destroy();
    }

    @Override
    public void stopPulsatorLayout() {
        pulsatorLayout.stop();
    }

    @Override
    public void startPulsatorLayout() {
        pulsatorLayout.start();
    }

    public int getPermission(String permission) {
        return ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permission);
    }

    @Override
    public boolean checkPermission() {
        int permissionWrite = getPermission(WRITE_EXTERNAL_STORAGE);
        int permissionRecord = getPermission(RECORD_AUDIO);
        return permissionWrite == PackageManager.PERMISSION_GRANTED &&
                permissionRecord == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void setTVProgressText(String text) {
        tVProgress.setText(text);
    }

    @Override
    public void updateList(List<ChatModel> chatModels) {
        adapter.setDataMessage(chatModels);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setName(String username) {
        getSupportActionBar().setTitle(username);
    }


}