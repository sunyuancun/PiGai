package com.cikuu.pigai.httprequest;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MultipartRequest extends Request<String> {

    private MultipartEntity entity = new MultipartEntity();
    private static String mKEY;

    private final Response.Listener<String> mListener;
    private final File mFilePart;
//    private final String mStringPart;

    public MultipartRequest(String KEY, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, File file) {
        super(Method.POST, url, errorListener);
        mKEY = KEY;
        mListener = listener;
        mFilePart = file;
        buildMultipartEntity();
    }

    private void buildMultipartEntity() {
        entity.addPart(mKEY, new FileBody(mFilePart));
//        try
//        {
//            entity.addPart(STRING_PART_NAME, new StringBody(mStringPart));
//        }
//        catch (UnsupportedEncodingException e)
//        {
//            VolleyLog.e("UnsupportedEncodingException");
//        }
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
        return Response.success(jsonString, getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}