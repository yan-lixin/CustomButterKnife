package com.butterKnife.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.butterknife.annotation.BindView;
import com.butterknife.annotation.OnClick;
import com.butterknife.library.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv)
    TextView mTextView;
    @BindView(R.id.btn)
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn)
    public void onClick(View view) {
        Toast.makeText(this, "点击", Toast.LENGTH_SHORT).show();
    }
}
