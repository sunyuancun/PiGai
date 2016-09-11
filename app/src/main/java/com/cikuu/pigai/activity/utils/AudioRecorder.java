package com.cikuu.pigai.activity.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.cikuu.pigai.activity.teacher.StudentArticleInTeacherActivity;
import com.cikuu.pigai.businesslogic.Teacher;
import com.cikuu.pigai.httprequest.VolleyRequest;

import java.io.File;
import java.io.IOException;

public class AudioRecorder {

    public interface AudioStateCallback {
        void AudioStoppedPlaying();

        void AudioStoppedRecording(File file);
    }

    private static final String LOG_TAG = "AudioRecord";

    private static String mFileName = null;
    private static String mPlayingUrl = "";

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private static AudioRecorder instance = null;

    public AudioStateCallback mCallback;
    private ProgressDialog mProgressDialog;

    private boolean mCancel = false;


    public String getAudioFilePath() {
        return mFileName;
    }

    private AudioRecorder(AudioStateCallback callback) {
        mFileName = ConstConfig.PIGAI_FILE_PATH;
        mFileName += "/" + ConstConfig.PIGAI_AUDIO_FILE_NAME;


        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new
                                                MediaPlayer.OnCompletionListener() {
                                                    @Override
                                                    public void onCompletion(MediaPlayer arg0) {
                                                        mCallback.AudioStoppedPlaying();
                                                    }
                                                });
        mPlayer.setOnPreparedListener(new
                                              MediaPlayer.OnPreparedListener() {
                                                  @Override
                                                  public void onPrepared(MediaPlayer mp) {
                                                      if (mCancel) {
                                                          mPlayer.stop();
                                                          mCancel = false;
                                                      } else {
                                                          mPlayer.start();
                                                          DismissProgressDialog();
                                                      }
                                                  }
                                              });

        mPlayer.setOnErrorListener(new
                                           MediaPlayer.OnErrorListener() {
                                               @Override
                                               public boolean onError(MediaPlayer mp, int what, int extra) {
//                                                   stopPlaying();
                                                   mCallback.AudioStoppedPlaying();
                                                   DismissProgressDialog();
                                                   return true;
                                               }
                                           });

        mCallback = callback;

    }

    public void CreateAndShowProgressDialog() {
        mProgressDialog = null;
        mProgressDialog = new ProgressDialog((Activity) (mCallback));
        mProgressDialog.setTitle("正在同步网络数据");
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if (mPlayer != null) {
                    mCancel = true;
                    if (mPlayer.isPlaying())
                        mPlayer.stop();
                    mCallback.AudioStoppedPlaying();
                }
            }
        });

        mProgressDialog.show();
    }

    public void DismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }


    public static AudioRecorder GetInstance(AudioStateCallback callback) {
        if (instance == null) {
            instance = new AudioRecorder(callback);
        }

        return instance;
    }

    public void onRecord(boolean start) {
        try {
            if (start) {
                startRecording();
            } else {
                stopRecording();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPlay(boolean start, String url) {
        if (start) {
            startPlaying(url);
        } else {
            stopPlaying();
        }
    }

    public boolean isPlaying() {
        if (mPlayer == null)
            return false;
        if (mPlayer.isPlaying())
            return true;
        else
            return false;
    }

    private void startPlaying(String url) {
        if (url == null || url.equals(""))
            return;

        try {
            mPlayingUrl = url;
            mPlayer.reset();
            mPlayer.setDataSource(mPlayingUrl);
            mPlayer.prepareAsync();
            if (mPlayingUrl.contains("http")) {
                CreateAndShowProgressDialog();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        try {
            if (mPlayer != null) {
                mPlayer.stop();
                DismissProgressDialog();
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setOutputFile(mFileName);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        mRecorder.start();
    }

    public void stopRecording() {
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;

                File file = new File(mFileName);
                mCallback.AudioStoppedRecording(file);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "stopRecording() failed");
        }
    }

}
