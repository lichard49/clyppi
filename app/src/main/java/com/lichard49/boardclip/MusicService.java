package com.lichard49.boardclip;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.lichard49.boardclip.R;

import java.util.HashMap;
import java.util.Random;

public class MusicService extends Service {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundsMap;
    int SOUND1=1;
    int SOUND2=2;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundsMap = new HashMap<Integer, Integer>();
        soundsMap.put(SOUND1, soundPool.load(this, R.raw.airhorn, 1));
        //soundsMap.put(SOUND2, soundPool.load(this, R.raw.touchdown, 1));
    }
    public void playSound(int sound, float fSpeed) {
        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;


        soundPool.play(soundsMap.get(sound), volume, volume, 1, 0, fSpeed);
    }


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        MusicService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** method for clients */
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }

    public void soundPlay(int index){

        playSound(index, 1.0f);
        Log.d("SOUND1", "hi1");

    }
}