package com.compdigitec.libvlcandroidsample;

import java.lang.ref.WeakReference;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaList;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

public class VideoView {


    private String mFilePath;

    // display surface
    private SurfaceView mSurface;
    private SurfaceHolder holder;

    // media player
    private LibVLC libvlc;
    private int mVideoWidth;
    private int mVideoHeight;
    private final static int VideoSizeChanged = -1;
    
    private Activity mContext;
    
    
    /*************
     * Activity
     *************/
	IVideoPlayer mVideoPlayer = new IVideoPlayer(){


	    @Override
	    public void setSurfaceSize(int width, int height, int visible_width,
	            int visible_height, int sar_num, int sar_den) {
	        Message msg = Message.obtain(mHandler, VideoSizeChanged, width, height);
	        msg.sendToTarget();
	    }

		@Override
		public void eventHardwareAccelerationError() {
			// TODO Auto-generated method stub
			
		}
		
	};
	
    /*************
     * Surface
     *************/
	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
	    }

	    public void surfaceChanged(SurfaceHolder surfaceholder, int format,
	            int width, int height) {
	        if (libvlc != null)
	            libvlc.attachSurface(holder.getSurface(), mVideoPlayer);
	    }

	    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
	    }
	};

	
    /*************
     * Events
     *************/

    private Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<VideoView> mOwner;

        public MyHandler(VideoView owner) {
            mOwner = new WeakReference<VideoView>(owner);
        }

        @Override
        public void handleMessage(Message msg) {
        	VideoView player = mOwner.get();

            // SamplePlayer events
            if (msg.what == VideoSizeChanged) {
                player.setSize(msg.arg1, msg.arg2);
                return;
            }

            // Libvlc events
            Bundle b = msg.getData();
            switch (b.getInt("event")) {
            case EventHandler.MediaPlayerEndReached:
                player.releasePlayer();
                break;
            case EventHandler.MediaPlayerPlaying:
            case EventHandler.MediaPlayerPaused:
            case EventHandler.MediaPlayerStopped:
            default:
                break;
            }
        }
    }
    
    public VideoView(SurfaceView view, String url, Activity ac){
    	mContext  =ac;
    	mFilePath = url;
        mSurface = view;
        holder = mSurface.getHolder();
        holder.addCallback(mSHCallback);
    }
    
    public void createPlayer() {
    	String media = mFilePath;
        releasePlayer();
        try {

            // Create a new media player
            libvlc = new LibVLC();
            libvlc.setHardwareAcceleration(LibVLC.HW_ACCELERATION_DECODING);
            libvlc.setSubtitlesEncoding("");
            libvlc.setAout(LibVLC.AOUT_OPENSLES);
            libvlc.setTimeStretching(true);
            libvlc.setChroma("RV32");
            libvlc.setVerboseMode(true);
            libvlc.restart(mContext);
            EventHandler.getInstance().addHandler(mHandler);
            holder.setFormat(PixelFormat.RGBX_8888);
            holder.setKeepScreenOn(true);
            MediaList list = libvlc.getMediaList();
            list.clear();
            list.add(new Media(libvlc, LibVLC.PathToURI(media)), false);
            libvlc.playIndex(0);
        } catch (Exception e) {
        }
    }

    public void releasePlayer() {
        if (libvlc == null)
            return;
        EventHandler.getInstance().removeHandler(mHandler);
        libvlc.stop();
        libvlc.detachSurface();
        holder = null;
        libvlc.closeAout();
        libvlc.destroy();
        libvlc = null;

        mVideoWidth = 0;
        mVideoHeight = 0;
    }


    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        // get screen size
        int w = mContext.getWindow().getDecorView().getWidth();
        int h = mContext.getWindow().getDecorView().getHeight();

        // getWindow().getDecorView() doesn't always take orientation into
        // account, we have to correct the values
        boolean isPortrait = mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        // force surface buffer size
        holder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        LayoutParams lp = mSurface.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }
    /*************
     * Events
     *************/


}
