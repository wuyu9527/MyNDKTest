package com.whx.myndk;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.whx.giflib.GifPlayer;

import java.io.File;

public class GifActivity extends AppCompatActivity {

    private ImageView mIvShow;
    private GifPlayer mGifPlayer;

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
            "Pictures/gif.gif";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 666);
        }
        mIvShow = findViewById(R.id.iv_show);
        mGifPlayer = new GifPlayer();
        mGifPlayer.setOnGifListener(new GifPlayer.OnGifListener() {
            @Override
            public void start() {
                Log.i("DMUI", "gif start");
            }

            @Override
            public void draw(final Bitmap bitmap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIvShow.setImageBitmap(bitmap);
                    }
                });
            }

            @Override
            public void end() {
                Log.i("DMUI", "gif end");
            }
        });
        findViewById(R.id.btn_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mGifPlayer.assetPlay(false, GifActivity.this, "demo.gif");
                // 外部目录play

                File file = new File(path);
                if (file.exists()) {
                    mGifPlayer.storagePlay(false,file.getPath());
                } else {
                    Toast.makeText(GifActivity.this, "file not exists!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.btn_two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGifPlayer.pause();
            }
        });
        findViewById(R.id.btn_three).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGifPlayer.resume();
            }
        });
        findViewById(R.id.btn_four).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGifPlayer.stop();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGifPlayer.resume();
    }

    @Override
    protected void onPause() {
        mGifPlayer.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mGifPlayer.destroy();
        super.onDestroy();
    }


}
