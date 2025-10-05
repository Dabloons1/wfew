package com.myfloatingmodule;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;

public class FloatingWindowService extends Service {
    
    private WindowManager windowManager;
    private View floatingView;
    private boolean isFloatingWindowVisible = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, createNotification());
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingWindow();
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                "floating_window_channel",
                "Floating Window Service",
                NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    
    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        return new NotificationCompat.Builder(this, "floating_window_channel")
            .setContentTitle("Floating Window Service")
            .setContentText("Floating window is active")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build();
    }
    
    private void showFloatingWindow() {
        if (isFloatingWindowVisible) return;
        
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        // Create floating view
        floatingView = createFloatingView();
        
        // Set window parameters
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O 
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 100;
        params.y = 100;
        
        try {
            windowManager.addView(floatingView, params);
            isFloatingWindowVisible = true;
        } catch (Exception e) {
            // Handle permission or other errors
            e.printStackTrace();
        }
    }
    
    private View createFloatingView() {
        // Create a simple floating window with buttons
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(0x80000000); // Semi-transparent black
        layout.setPadding(20, 20, 20, 20);
        
        // Title
        TextView title = new TextView(this);
        title.setText("My Floating Module");
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(16);
        layout.addView(title);
        
        // Buttons
        Button button1 = new Button(this);
        button1.setText("Button 1");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your functionality here
            }
        });
        layout.addView(button1);
        
        Button button2 = new Button(this);
        button2.setText("Button 2");
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your functionality here
            }
        });
        layout.addView(button2);
        
        Button closeButton = new Button(this);
        closeButton.setText("Close");
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFloatingWindow();
                stopSelf();
            }
        });
        layout.addView(closeButton);
        
        return layout;
    }
    
    private void hideFloatingWindow() {
        if (isFloatingWindowVisible && windowManager != null && floatingView != null) {
            try {
                windowManager.removeView(floatingView);
                isFloatingWindowVisible = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        hideFloatingWindow();
    }
}
