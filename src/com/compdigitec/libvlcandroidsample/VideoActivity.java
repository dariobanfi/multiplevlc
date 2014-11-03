package com.compdigitec.libvlcandroidsample;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;

public class VideoActivity extends Activity  {
    public final static String TAG = "LibVLCAndroidSample/VideoActivity";

    public final static String LOCATION = "com.compdigitec.libvlcandroidsample.VideoActivity.location";



    
    private VideoView mVideoView;
    private VideoView mVideoView1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);

        String video = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";	
        String video1 = "/sdcard/a.mp4";

        SurfaceView v = (SurfaceView) findViewById(R.id.surface);
        SurfaceView v1 = (SurfaceView) findViewById(R.id.surface1);
        
        mVideoView = new VideoView(v, video, this);
        mVideoView1 = new VideoView(v1, video1, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.createPlayer();
        mVideoView1.createPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.releasePlayer();
        mVideoView1.releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.releasePlayer();
        mVideoView1.releasePlayer();
    }

}
