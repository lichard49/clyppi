package com.lichard49.boardclip;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatHeadService extends Service {
    private WindowManager windowManager;
    private ImageView chatHead;
    private TextView textBubble;
    private WindowManager.LayoutParams chatParams;
    private WindowManager.LayoutParams textParams;
    private Handler checkActivityHandler;

    private Map<String, Integer> activityTime;
    private Map<String, Integer> firstActivatedActivityTime;
    public static String[] badPrograms = new String[] {"Messenger","Facebook","Tinder", "Hangouts"};

    private Handler autonomousHandler;
    private AnimationDrawable bombOmbAnimation;

    MusicService mService;
    private boolean mBound;

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
        chatHead.setBackgroundResource(R.drawable.bombomb180);
        bombOmbAnimation = (AnimationDrawable) chatHead.getBackground();
        chatHead.post(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable frameAnimation =
                        (AnimationDrawable) chatHead.getBackground();
                frameAnimation.start();
            }
        });


        textBubble = new TextView(this);
        textBubble.setMaxEms(10);
        textBubble.setPadding(10, 10, 10, 10);
        textBubble.setBackgroundColor(Color.LTGRAY);
        textBubble.setTextColor(Color.BLACK);
        textBubble.setVisibility(View.VISIBLE);
        //textBubble.setText("It's Mario! This is a really long sentence.");

        chatParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        textParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        chatParams.gravity = Gravity.TOP | Gravity.LEFT;
        chatParams.x = 0;
        chatParams.y = 100;
        textParams.gravity = Gravity.TOP | Gravity.LEFT;
        textParams.x = 0;
        textParams.y = 0;

        windowManager.addView(chatHead, chatParams);
        windowManager.addView(textBubble, textParams);

        chatHead.setOnTouchListener(moveChatHead);


        checkActivityHandler = new Handler();
        checkActivityHandler.postDelayed(checkActivity, 0);
        activityTime = new HashMap<String, Integer>();
        firstActivatedActivityTime = new HashMap<String, Integer>();

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {


        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }


        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private boolean autonomousMode = true;
    enum Direction { UP, LEFT, RIGHT, DOWN };
    private Runnable moveAutonomous = new Runnable()
    {
        private Direction direction = Direction.RIGHT;
        @Override
        public void run()
        {
            if(autonomousMode) {
                if (chatParams.x > windowManager.getDefaultDisplay().getWidth() - chatHead.getWidth()
                        && direction == Direction.RIGHT) direction = Direction.DOWN;
                else if (chatParams.y > windowManager.getDefaultDisplay().getHeight() - chatHead.getHeight()
                        && direction == Direction.DOWN) direction = Direction.LEFT;
                else if (chatParams.x < 0 && direction == Direction.LEFT) direction = Direction.UP;
                else if (chatParams.y < 0 && direction == Direction.UP) direction = Direction.RIGHT;

                switch (direction) {
                    case UP:
                        chatParams.y -= 10;
                        textParams.y -= 10;
                        chatHead.setBackgroundResource(R.drawable.bombomb90);
                        bombOmbAnimation = (AnimationDrawable) chatHead.getBackground();
                        chatHead.post(new Runnable() {
                            @Override
                            public void run() {
                                AnimationDrawable frameAnimation =
                                        (AnimationDrawable) chatHead.getBackground();
                                frameAnimation.start();
                            }
                        });
                        break;
                    case LEFT:
                        chatParams.x -= 10;
                        textParams.x -=10;
                        chatHead.setBackgroundResource(R.drawable.bombomb0);
                        bombOmbAnimation = (AnimationDrawable) chatHead.getBackground();
                        chatHead.post(new Runnable() {
                            @Override
                            public void run() {
                                AnimationDrawable frameAnimation =
                                        (AnimationDrawable) chatHead.getBackground();
                                frameAnimation.start();
                            }
                        });
                        break;
                    case RIGHT:
                        chatParams.x += 10;
                        textParams.x += 10;
                        chatHead.setBackgroundResource(R.drawable.bombomb180);
                        bombOmbAnimation = (AnimationDrawable) chatHead.getBackground();
                        chatHead.post(new Runnable() {
                            @Override
                            public void run() {
                                AnimationDrawable frameAnimation =
                                        (AnimationDrawable) chatHead.getBackground();
                                frameAnimation.start();
                            }
                        });
                        break;
                    case DOWN:
                        chatParams.y += 10;
                        textParams.y += 10;
                        chatHead.setBackgroundResource(R.drawable.bombomb270);
                        bombOmbAnimation = (AnimationDrawable) chatHead.getBackground();
                        chatHead.post(new Runnable() {
                            @Override
                            public void run() {
                                AnimationDrawable frameAnimation =
                                        (AnimationDrawable) chatHead.getBackground();
                                frameAnimation.start();
                            }
                        });
                        break;
                }

                windowManager.updateViewLayout(chatHead, chatParams);
                windowManager.updateViewLayout(textBubble, textParams);
            }
            autonomousHandler.postDelayed(this, 10);
        }
    };

    private View.OnTouchListener moveChatHead = new View.OnTouchListener() {
        private int initialChatX;
        private int initialChatY;
        private int initialTextX;
        private int initialTextY;

        private float initialTouchX;
        private float initialTouchY;

        @Override public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialChatX = chatParams.x;
                    initialChatY = chatParams.y;
                    initialTextX = chatParams.x;
                    initialTextY = chatParams.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    autonomousMode = false;
                    return true;
                case MotionEvent.ACTION_UP:
                    autonomousMode = true;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    chatParams.x = initialChatX + (int) (event.getRawX() - initialTouchX);
                    chatParams.y = initialChatY + (int) (event.getRawY() - initialTouchY);
                    textParams.x = initialTextX + (int) (event.getRawX() - initialTouchX);
                    textParams.y = initialTextY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(chatHead, chatParams);
                    windowManager.updateViewLayout(textBubble, textParams);
                    return true;
            }
            return false;
        }
    };

    private Runnable checkActivity = new Runnable()
    {
        //bombOmbAnimation.start();
        String previousActivity = null;
        int textShow = 0;
        @Override
        public void run()
        {
            String currentActivity = getForegroundActivityName();

            if (previousActivity == null) {
                firstActivatedActivityTime.put(currentActivity, 0);
                activityTime.put(currentActivity, 1);
            } else if (previousActivity.equals(currentActivity)) {
                if (activityTime.containsKey(currentActivity)) {
                    activityTime.put(currentActivity, activityTime.get(currentActivity)+1);
                } else {
                    activityTime.put(currentActivity, 1);
                }

                if (activityTime.containsKey(currentActivity) && firstActivatedActivityTime.containsKey(currentActivity)) {
                    Integer timeDiff = activityTime.get(currentActivity) - firstActivatedActivityTime.get(currentActivity);
                    if (Arrays.asList(badPrograms).contains(currentActivity) && activityTime.containsKey(currentActivity)) {
                        if (timeDiff == 10) {
                            Log.d("cw", "GREAT YOU'VE BEEN ON " + currentActivity + " FOR " + timeDiff + " SECONDS");
                            textBubble.setVisibility(View.VISIBLE);
                            textBubble.setText("GREAT YOU'VE BEEN ON " + currentActivity + " FOR " + timeDiff + " SECONDS");
                            textShow = activityTime.get(currentActivity);
                        } else if (timeDiff >= 30 && timeDiff % 5 == 0) {
                            textBubble.setVisibility(View.VISIBLE);
                            textBubble.setText("CLOSE THE DAMN APP.");
                            textShow = activityTime.get(currentActivity);
                            if (mBound) {
                                mService.playSound(1, 1.0f);
                            }
                        }
                    }
                }

            } else {
                if (activityTime.containsKey(currentActivity)) {
                    firstActivatedActivityTime.put(currentActivity, activityTime.get(currentActivity));
                    activityTime.put(currentActivity, activityTime.get(currentActivity)+1);
                } else {
                    firstActivatedActivityTime.put(currentActivity, 0);
                    activityTime.put(currentActivity, 1);
                }
                if (Arrays.asList(badPrograms).contains(currentActivity) && activityTime.containsKey(currentActivity)) {
                    Integer timeOnActivity = activityTime.get(currentActivity);
                    if (timeOnActivity <= 5) {
                        textBubble.setVisibility(View.VISIBLE);
                        textBubble.setText("I'M DISAPPOINTED IN YOU");
                        textShow = activityTime.get(currentActivity);
                    }else if (timeOnActivity > 5) {
                        Log.d("cw", "YOU'VED WASTED " + timeOnActivity + " SECONDS ON" + currentActivity + " ALREADY");
                        textBubble.setVisibility(View.VISIBLE);
                        textBubble.setText("YOU'VED WASTED " + timeOnActivity + " SECONDS ON" + currentActivity + " ALREADY");
                        textShow = activityTime.get(currentActivity);
                    }
                } else if (Arrays.asList(badPrograms).contains(previousActivity) && !Arrays.asList(badPrograms).contains(currentActivity)) {
                    textBubble.setVisibility(View.VISIBLE);
                    textBubble.setText("THANKS FOR CLOSING THE DAMN APP");
                    textShow = activityTime.get(currentActivity);
                }
            }
            previousActivity = currentActivity;
            //Log.d("chris", currentActivity + " " + activityTime.get(currentActivity));
            checkActivityHandler.postDelayed(this, 1000);

            if (activityTime.containsKey(currentActivity)){
                if (activityTime.get(currentActivity) - textShow == 5) {
                    textBubble.setVisibility(View.GONE);
                }
            }

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
        PackageManager pm = this.getPackageManager();
        try {
            CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(appProcesses.get(0).processName, PackageManager.GET_META_DATA));
            return c.toString();
        }catch(Exception e) {
            return null;
        }
    }
}
