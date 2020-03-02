package com.whx.myndk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    TextView sample_text;
    EditText etMD5Context;
    EditText etContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        sample_text = findViewById(R.id.sample_text);
        etMD5Context = findViewById(R.id.etMD5Context);
        etContext = findViewById(R.id.etContext);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sample_text.setText("MD5(" + etContext.getText().toString() + ")=");
                etMD5Context.setText(stringFromMD5(etContext.getText().toString().trim()));
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sample_text.setText("MD5(内容)=");
                etMD5Context.setText("");
            }
        });
        sample_text.setText("MD5(内容)=");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native String stringFromMD5(String input);
}
