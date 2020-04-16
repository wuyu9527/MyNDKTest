package com.whx.myndk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.whx.apmtools.APMManager;
import com.whx.giflib.GifHandler;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    TextView sample_text;
    EditText etMD5Context;
    EditText etContext;
    Notification mNotification;
    private int progress;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
        imageView = findViewById(R.id.ivGif);
        findViewById(R.id.ib).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setUpNotification();

                if (NotificationUtil.isNotifyEnabled(MainActivity.this)) {
                    Log.i("whx", "通知通过");
//                     createNotification();
                    showMessage(MainActivity.this, MainActivity.class, getString(R.string.app_name), "正文", 1234);
                } else {
                    Log.i("whx", "无通知");
                    startNo();
                }
                load();
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity();
            }
        });
        sample_text.setText("MD5(内容)=");
        //必须root后才能展示
//        APMManager.getInstance(this).showAPM();
    }

    private void startNo() {
        Intent localIntent = new Intent();
        //直接跳转到应用通知设置的代码：
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0及以上
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上到8.0以下
            localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            localIntent.putExtra("app_package", getPackageName());
            localIntent.putExtra("app_uid", getApplicationInfo().uid);
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {//4.4
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.setData(Uri.parse("package:" + getPackageName()));
        } else {
            //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
            }
        }
        startActivity(localIntent);
    }

    private void createNotification() {
        mNotification = new NotificationCompat.Builder(this, "123")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(getString(R.string.app_name) + "下载中:" + progress + "%")
                .setWhen(System.currentTimeMillis())
                .setProgress(100, progress, false)
                .build();
        mNotificationManager.notify(1, mNotification);
    }

    /**
     * 创建通知
     */
    @SuppressWarnings("deprecation")
    private synchronized void setUpNotification() {

        CharSequence tickerText = "开始下载";
        long when = System.currentTimeMillis();
        //mNotification = new Notification(icon, tickerText, when);
        // 放置在"正在运行"栏目中


        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.update_version_notification);
        contentView.setTextViewText(R.id.name, getString(R.string.app_name) + "正在下载...");

        // 指定个性化视图

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotification = new NotificationCompat.Builder(this, "123")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(bmp)
                    .setTicker(tickerText)
                    .setContentText(getString(R.string.app_name) + "下载中:" + progress + "%")
                    .setWhen(when)
                    .setCustomContentView(contentView)
                    .setProgress(100, progress, false)
                    .build();
        } else {

            mNotification = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(bmp)
                    .setTicker(tickerText)
                    .setContentText(getString(R.string.app_name) + "下载中:" + progress + "%")
                    .setWhen(when)
                    .setContent(contentView)
                    .setProgress(100, progress, false)
                    .build();
        }


        mNotification.flags |= Notification.FLAG_AUTO_CANCEL; // FL
        //mNotification.flags = Notification.FLAG_ONGOING_EVENT;//持续
        mNotificationManager.notify(0, mNotification);
        mNotification.bigContentView = contentView;
        mNotification.contentView = contentView;

    }

    private void startActivity() {
        try {
            Uri uri = Uri.parse("qjj://auction/openGoodsInfo?param1=测试&param2=实验");
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(uri);
            PackageManager packageManager = getPackageManager();
            ComponentName componentName = intent.resolveActivity(packageManager);
            if (componentName != null) {
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTopStatusBar(Activity activity, View view) {
        int statusBarHeight = 0;
        int resourceId = activity.getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = activity.getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
        Log.i("whx", "statusBarHeight:" + statusBarHeight);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(0, statusBarHeight, 0, 0);
    }


    /**
     * 消息通知栏
     *
     * @param context 上下文
     * @param cl      需要跳转的Activity
     * @param tittle  通知栏标题
     * @param content 通知栏内容
     * @param i       通知的标识符
     */
    public static void showMessage(Context context, Class cl, String tittle, String content, int i) {
        Intent intent = new Intent(context, cl);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        //频道的ID。每个包必须是唯一的
        String id = context.getPackageName();
        //渠道名字
        String name = context.getString(R.string.app_name);//频道的用户可见名称
        //创建一个通知管理器
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(context, id)
                    .setChannelId(id)
                    .setContentTitle(tittle)//设置通知标题
                    .setContentText(content)//设置通知内容
                    .setSmallIcon(R.mipmap.ic_launcher)//设置小图标
                    .setLargeIcon(BitmapFactory.decodeResource
                            (context.getResources(), R.mipmap.ic_launcher))//设置大图标
                    .setContentIntent(pendingIntent)//打开消息跳转到这儿
                    .setAutoCancel(false)// 将AutoCancel设为true后，当你点击通知栏的notification后，它会自动被取消消失
                    .setOngoing(true)//将Ongoing设为true 那么notification将不能滑动删除
                    // 从Android4.1开始，可以通过以下方法，设置notification的优先级，优先级越高的，通知排的越靠前，优先级低的，不会在手机最顶部的状态栏显示图标
                    //.setPriority(NotificationCompat.PRIORITY_MAX)

                    // Notification.DEFAULT_ALL：铃声、闪光、震动均系统默认。
                    // Notification.DEFAULT_SOUND：系统默认铃声。
                    // Notification.DEFAULT_VIBRATE：系统默认震动。
                    // Notification.DEFAULT_LIGHTS：系统默认闪光。
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .build();
        } else {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle(tittle)
                            .setContentText(content)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource
                                    (context.getResources(), R.mipmap.ic_launcher))//设置大图标
                            .setContentIntent(pendingIntent)//打开消息跳转到这儿
                            .setAutoCancel(false)
                            .setOngoing(true);
            //.setPriority(NotificationCompat.PRIORITY_MAX);
            notification = notificationBuilder.build();
        }
        notificationManager.notify(i, notification);
    }

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
            "Pictures/gif.gif";
    private Bitmap bitmap;
    private int maxLength;
    private int currentLength;
    private GifHandler gifHandler;
    private ImageView imageView;

    private void load() {
        Log.i("whx", path);
        gifHandler = new GifHandler(path);
        int width = gifHandler.getWidth();
        int height = gifHandler.getHeight();
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        maxLength = gifHandler.getLength();

        long delayTime = gifHandler.renderFrame(bitmap, currentLength);
        imageView.setImageBitmap(bitmap);
        if (handler != null) {
            handler.sendEmptyMessageDelayed(1, delayTime);
        }
        /*Glide.with(this).asGif().load(path).into(imageView);*/
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            currentLength++;
            if (currentLength >= maxLength) {
                currentLength = 0;
            }
            long delayTime = gifHandler.renderFrame(bitmap, currentLength);
            imageView.setImageBitmap(bitmap);
            handler.sendEmptyMessageDelayed(0, delayTime);
            return false;
        }
    });

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native String stringFromMD5(String input);
}
