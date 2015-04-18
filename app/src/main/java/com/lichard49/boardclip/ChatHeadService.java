package com.lichard49.boardclip;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.os.Handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ChatHeadService extends Service {
    private WindowManager windowManager;
    private ImageView chatHead;
    private WindowManager.LayoutParams params;
    private Handler checkActivityHandler;

    private Map<String, Integer> activityTime;

    private Handler autonomousHandler;

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override public void onCreate() {
        super.onCreate();
        Log.d("hi", "bye");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        autonomousHandler = new Handler();
        autonomousHandler.postDelayed(moveAutonomous, 10);

        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.marioicon);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(chatHead, params);

        chatHead.setOnTouchListener(moveChatHead);


        checkActivityHandler = new Handler();
        checkActivityHandler.postDelayed(checkActivity, 0);
        activityTime = new HashMap<String, Integer>();
    }

    enum Direction { UP, LEFT, RIGHT, DOWN };
    private Runnable moveAutonomous = new Runnable()
    {
        private Direction direction = Direction.RIGHT;
        @Override
        public void run()
        {
            if(params.x > windowManager.getDefaultDisplay().getWidth()-chatHead.getWidth()
                    && direction == Direction.RIGHT) direction = Direction.DOWN;
            else if(params.y > windowManager.getDefaultDisplay().getHeight()-chatHead.getHeight()
                    && direction == Direction.DOWN) direction = Direction.LEFT;
            else if(params.x < 0 && direction == Direction.LEFT) direction = Direction.UP;
            else if(params.y < 0 && direction == Direction.UP) direction = Direction.RIGHT;

            switch(direction)
            {
                case UP: params.y -= 10; break;
                case LEFT: params.x -= 10; break;
                case RIGHT: params.x += 10; break;
                case DOWN: params.y += 10; break;
            }

            windowManager.updateViewLayout(chatHead, params);
            //Log.d("hiii", params.x+"");
            autonomousHandler.postDelayed(this, 10);
        }
    };

    private View.OnTouchListener moveChatHead = new View.OnTouchListener() {
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;

        @Override public boolean onTouch(View v, MotionEvent event) {
            //ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            //List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

            //Log.i("hiii", "about to list " + appProcesses.size() + " apps");
            //for(ActivityManager.RunningAppProcessInfo a: appProcesses) {
//                if(a.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    //Log.i("hiii", "app: " + appProcesses.get(0).processName);
//                }
//            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_UP:
                    return true;
                case MotionEvent.ACTION_MOVE:
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(chatHead, params);
                    return true;
            }
            return false;
        }
    };

    private Runnable checkActivity = new Runnable()
    {
        @Override
        public void run()
        {
            String currentActivity = getForegroundActivityName();
            if (activityTime.containsKey(currentActivity)) {
                activityTime.put(currentActivity, activityTime.get(currentActivity) + 1);
            } else {
                activityTime.put(currentActivity, 1);
            }
            Log.d("chris", currentActivity + " " + activityTime.get(currentActivity));
            checkActivityHandler.postDelayed(this, 1000);
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }

    private String getForegroundActivityName() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        //Log.d("cw", appProcesses.get(0).getClass().getSimpleName());

        ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        try {
            CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(appProcesses.get(0).processName, PackageManager.GET_META_DATA));
            return c.toString();
        }catch(Exception e) {
            return null;
        }
    }
}
