package com.cikuu.pigai.activity.dialog;

import android.content.Context;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.utils.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2016-05-19
 * Time: 15:42
 * Protect: PiGai_v1.6(version1.6) _bug_fix
 */
public class SpeechInputDialog {

    public static final String TAG = "SpeechRecognizer";
    private RecognizerDialog mRecognizerDialog;
    private Toast mToast;
    private Context mContext;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();


    public SpeechInputDialog(Context context) {
        try {
            mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
            mContext = context;
            mRecognizerDialog = new RecognizerDialog(context, mInitListener);
            // 设置语音参数
            setRecognizerDaialogParam();
            //设置监听器接收数据
            mRecognizerDialog.setListener(mRecognizerDialogListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showRecognizerDaialog() {
        try {
            mRecognizerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRecognizerDaialogParam() {
        // 清空参数
        mRecognizerDialog.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎(云端)
        mRecognizerDialog.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mRecognizerDialog.setParameter(SpeechConstant.RESULT_TYPE, "json");

//        String lag = mSharedPreferences.getString("iat_language_preference",
//                "mandarin");
        String lag = "en_us";
        if (lag.equals("en_us")) {
            // 设置语言
            mRecognizerDialog.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            mRecognizerDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mRecognizerDialog.setParameter(SpeechConstant.ACCENT, lag);
        }
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
//        mRecognizerDialog.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));
        mRecognizerDialog.setParameter(SpeechConstant.VAD_BOS, "3000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
//        mRecognizerDialog.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));
        mRecognizerDialog.setParameter(SpeechConstant.VAD_EOS, "1000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
//        mRecognizerDialog.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
        mRecognizerDialog.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mRecognizerDialog.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mRecognizerDialog.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/pigai.wav");
    }


    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            printResult(recognizerResult, isLast);
        }

        @Override
        public void onError(SpeechError speechError) {
            if (speechError.getErrorCode() == 20006)
                showTip("请设置中心添加录音权限");
        }
    };

    private void printResult(RecognizerResult results, Boolean isLast) {
        try {
            String text = JsonParser.parseIatResult(results.getResultString());
            String sn = null;
            // 读取json结果中的sn字段
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");

            mIatResults.put(sn, text);
            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }
            // isLast 用于防止重复输入文字
            if (isLast) {
                mGetSpeechTextDataCallBack.GetSpeechTextData(resultBuffer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    public GetSpeechTextDataCallBack mGetSpeechTextDataCallBack;

    public interface GetSpeechTextDataCallBack {
        void GetSpeechTextData(String string);
    }


}
