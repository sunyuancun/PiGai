package com.cikuu.pigai.activity.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.widget.Toast;

import com.cikuu.pigai.httprequest.VolleyRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateApplication implements VolleyRequest.VersionCallback {

    private Context mActivity;
    private ProgressDialog mProgressDialog;
    private String mDownloadUrl = "http://client.pigai.org/APK/PiGai.apk";
    private String fileLoaction;// = "/sdcard/Download/PiGai.apk";

    private int netWorkVersion = 1;
    private VolleyRequest mHttpRequest;

    public UpdateApplication(Context activity) {
        mActivity = activity;
        fileLoaction = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileLoaction += "/PiGai.apk";
    }

    public void Update() {
        mHttpRequest = new VolleyRequest();
        mHttpRequest.mVersion = this;
        mHttpRequest.getVersion();
    }

    public void Version(double version) {
        //convert version 1.1 to 11
        netWorkVersion = (int) (version * 10);
        try {
            int currentVersion = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionCode;

            boolean needThisVersion = getUserChoiceInSP();
            int versionInSP = getNetworkVersionInSP();

            if ((netWorkVersion == versionInSP) && (!needThisVersion)) {
                return;
            }

            if (netWorkVersion > currentVersion) {
                updateDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                    .setTitle("程序升级")
                    .setMessage("有新版本" + netWorkVersion / 10.0 + "，请下载升级！" + getUpdateInfo(netWorkVersion))
                    .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // instantiate it within the onCreate method
                            mProgressDialog = new ProgressDialog(mActivity);
                            mProgressDialog.setMessage("升级程序...");
                            mProgressDialog.setIndeterminate(true);
                            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            mProgressDialog.setCancelable(true);
                            mProgressDialog.setCanceledOnTouchOutside(false);

                            final DownloadTask downloadTask = new DownloadTask(mActivity);
                            downloadTask.execute(mDownloadUrl);

                            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    downloadTask.cancel(true);
                                }
                            });
                        }
                    })
                    .setNegativeButton("忽略版本", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setUserChoiceInSP(false);
                            setNetworkVersionInSP(netWorkVersion);
                        }
                    })
                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUpdateInfo(int netWorkVersion) {
        String updateInfo = "";

        switch (netWorkVersion) {
            case 13:
                updateInfo = updateInfo +
                        "\r\n" + "1.学生端修复了自测作文保存草稿的Bug" +
                        "\r\n" + "2.学生端添加学生按老师名搜索作文功能" +
                        "\r\n" + "3.教师端新增老师删除自己布置的作文功能";
                break;
            default:
                break;
        }
        return updateInfo;
    }

    private static final String SHAREDPREFERENCES_NAME = "update_app";

    private void setNetworkVersionInSP(int version) {
        SharedPreferences preferences = mActivity.getSharedPreferences(
                SHAREDPREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("version", version);
        editor.apply();
    }

    private int getNetworkVersionInSP() {
        SharedPreferences preferences = mActivity.getSharedPreferences(
                SHAREDPREFERENCES_NAME, Activity.MODE_PRIVATE);
        return preferences.getInt("version", 10);
    }

    private void setUserChoiceInSP(boolean yesOrNo) {
        SharedPreferences preferences = mActivity.getSharedPreferences(
                SHAREDPREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("user_choice", yesOrNo);
        editor.apply();
    }

    private boolean getUserChoiceInSP() {
        SharedPreferences preferences = mActivity.getSharedPreferences(
                SHAREDPREFERENCES_NAME, Activity.MODE_PRIVATE);
        return preferences.getBoolean("user_choice", true);
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(fileLoaction);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "下载错误: " + result, Toast.LENGTH_LONG).show();
            else {
                Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(fileLoaction)),
                        "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

}
