package com.cikuu.pigai.activity.teacher;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.AboutAppActivity;
import com.cikuu.pigai.activity.BigUserPhotoActivity;
import com.cikuu.pigai.activity.LoginActivity;
import com.cikuu.pigai.activity.ResetPasswordByOldPasswordActivity;
import com.cikuu.pigai.activity.SearchSchoolActivity;
import com.cikuu.pigai.activity.SendFeedbackActivity;
import com.cikuu.pigai.activity.adapter.TeacherInformationAdapter;
import com.cikuu.pigai.activity.dialog.UpdateApplication;
import com.cikuu.pigai.activity.dialog.UpdateNoIngoreVersionApplication;
import com.cikuu.pigai.activity.utils.AppVisionTool;
import com.cikuu.pigai.activity.utils.ConstConfig;
import com.cikuu.pigai.activity.utils.ImageTools;
import com.cikuu.pigai.activity.utils.MobileTools;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
import com.cikuu.pigai.app.AppController;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.Teacher;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherHomeMeFragment extends Fragment implements VolleyRequest.UserPhotoModifyCallback, VolleyRequest.VersionCallback {

    private VolleyRequest mHttpRequest;
    private Teacher mTeacher;
    //about me variable
    private ImageView teacherPhoto;
    private TextView teacher_name;
    private TextView teacher_school;
    private TextView appVisionTextView;
    private TextView teacherQuit;
    private RelativeLayout teacherPhotoLine;
    private LinearLayout teacherbackline;
    private LinearLayout teacherInfoLine;
    private LinearLayout teacherAboutApp;
    private LinearLayout teacherupdateAppLine;
    private LinearLayout teacherresetpasswordLine;
    AppController.CacheImageLoader imageLoader;

    UpdateNoIngoreVersionApplication updateApp;
    int netWorkVersion = 1;
    int currentVersion = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_home_me, null);
        teacherPhoto = (ImageView) view.findViewById(R.id.userPhoto);
        teacher_name = (TextView) view.findViewById(R.id.teacher_name);
        teacher_school = (TextView) view.findViewById(R.id.teacher_school);
        teacherPhotoLine = (RelativeLayout) view.findViewById(R.id.user_info_photo_line);
        teacherInfoLine = (LinearLayout) view.findViewById(R.id.teacherInfoLine);
        teacherbackline = (LinearLayout) view.findViewById(R.id.teacherbackline);
        teacherresetpasswordLine = (LinearLayout) view.findViewById(R.id.teacherresetpasswordline);

        teacherAboutApp = (LinearLayout) view.findViewById(R.id.teacherAboutApp);
        teacherupdateAppLine = (LinearLayout) view.findViewById(R.id.teacherupdateApp);
        appVisionTextView = (TextView) view.findViewById(R.id.appVisionTextView);
        teacherQuit = (TextView) view.findViewById(R.id.login_out_textview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHttpRequest = new VolleyRequest();
        mHttpRequest.mUserPhotoModifyCallback = this;
        mHttpRequest.mVersion = this;
        mHttpRequest.getVersion();
        mTeacher = Teacher.GetInstance();

        try {
            if (getActivity() != null) {
                currentVersion = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
                appVisionTextView.setText(AppVisionTool.getVersion(getActivity()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        teacher_name.setText(mTeacher.mDescription.mName);
        teacher_school.setText(mTeacher.mDescription.mSchool);
        // load small head image
        imageLoader = AppController.getInstance().getCacheImageLoader();
        imageLoader.getMemoryCache().evictAll(); // remove all
        imageLoader.getDiskCache().clear(); // remove all
        imageLoader.get(mTeacher.mDescription.bHead, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean b) {
                if (response.getBitmap() != null) {
                    teacherPhoto.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("StudentHome_Me", "Image Load Error: " + volleyError.getMessage());
            }
        });
        //load big image
        teacherPhoto.setOnClickListener(listener);
        //modify the image
        teacherPhotoLine.setOnClickListener(listener);
        teacherInfoLine.setOnClickListener(listener);
        teacherbackline.setOnClickListener(listener);
        teacherresetpasswordLine.setOnClickListener(listener);
        teacherAboutApp.setOnClickListener(listener);
        teacherupdateAppLine.setOnClickListener(listener);
        teacherQuit.setOnClickListener(listener);
    }

    @Override
    public void Version(double version) {
        netWorkVersion = (int) (version * 10);
    }

    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.userPhoto) {
                Intent intent = new Intent(getActivity(), BigUserPhotoActivity.class);
                intent.putExtra("flag", 102);
                startActivityForResult(intent, 102);
            } else if (v.getId() == R.id.user_info_photo_line) {
                showPicturePicker(getActivity(), true);
            } else if (v.getId() == R.id.teacherInfoLine) {
                Intent teacherInfoIntent = new Intent(getActivity(), TeacherInformationActivity.class);
                startActivityForResult(teacherInfoIntent, 202);
            } else if (v.getId() == R.id.teacherbackline) {
                Intent teacherBackInfoIntent = new Intent(getActivity(), SendFeedbackActivity.class);
                startActivityForResult(teacherBackInfoIntent, 302);
            } else if (v.getId() == R.id.teacherresetpasswordline) {
                Intent resetPasswordIntent = new Intent(getActivity(), ResetPasswordByOldPasswordActivity.class);
                startActivity(resetPasswordIntent);
            } else if (v.getId() == R.id.teacherAboutApp) {
                Intent aboutIntent = new Intent(getActivity(), AboutAppActivity.class);
                startActivityForResult(aboutIntent, 402);
            } else if (v.getId() == R.id.teacherupdateApp) {

                if (netWorkVersion > currentVersion) {
                    updateApp = new UpdateNoIngoreVersionApplication(getActivity());
                    updateApp.Update();
                } else {
                    //todo   弹框
                    dialog();
                }
            } else if (v.getId() == R.id.login_out_textview) {
                SharedPreferenceUtil.setUserAutoLoginInSP(false, getActivity());
                Intent quitIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(quitIntent);
                getActivity().finish();
            }
        }
    };

    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("亲，您已是最新版本");

        builder.setTitle("提示");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;
    private static final int CROP = 2;
    private static final int CROP_PICTURE = 3;
    //Android   4.4  以后 图库的兼容性
    private static final int SELECT_PIC_KITKAT = 4;
    private String fileName = ConstConfig.PIGAI_CAMERA_IMAGE_FILE_NAME;
    private String filePath = ConstConfig.PIGAI_FILE_PATH;

    public void showPicturePicker(Context context, boolean isCrop) {
        final boolean crop = isCrop;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("图片来源");
        builder.setNegativeButton("取消", null);
        builder.setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
            int REQUEST_CODE;

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case TAKE_PICTURE:
                        Uri imageUri = null;
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (crop) {
                            REQUEST_CODE = CROP;
                            ImageTools.deletePhotoAtPathAndName(filePath, fileName);
                        } else {
                            REQUEST_CODE = TAKE_PICTURE;
                        }
                        imageUri = Uri.fromFile(new File(filePath, fileName));
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(openCameraIntent, REQUEST_CODE);
                        break;
                    case CHOOSE_PICTURE:
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        openAlbumIntent.setType("image/*");
                        if (crop) {
                            REQUEST_CODE = CROP;
                        } else {
                            REQUEST_CODE = CHOOSE_PICTURE;
                        }

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            startActivityForResult(openAlbumIntent, SELECT_PIC_KITKAT);
                        } else {
                            startActivityForResult(openAlbumIntent, REQUEST_CODE);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("TeacherHomeMeFragment"); //统计页面
        teacher_name.setText(mTeacher.mDescription.mName);
        teacher_school.setText(mTeacher.mDescription.mSchool);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("TeacherHomeMeFragment");
    }

    Bitmap photo = null;
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_OK = -1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //modify photo
                case CROP:
                    Uri uri = null;
                    if (data != null) {
                        uri = data.getData();
                        System.out.println("Data");
                        //----------------------------------------------------------------------------------------
                        if (uri == null)
                            break;
                        //----------------------------------------------------------------------------------------
                    } else {
                        System.out.println("File");
                        uri = Uri.fromFile(new File(filePath, fileName));
                    }
                    cropImage(uri, ConstConfig.IMAGE_RESOLUTION, ConstConfig.IMAGE_RESOLUTION, CROP_PICTURE);
                    break;
                case SELECT_PIC_KITKAT:
                    if (data != null) {
                        uri = data.getData();
                        if (uri == null)
                            break;
                        cropImage(uri, ConstConfig.IMAGE_RESOLUTION, ConstConfig.IMAGE_RESOLUTION, CROP_PICTURE);
                    } else {
                        break;
                    }
                    break;
                case CROP_PICTURE:
                    if (data == null)
                        break;
                    Uri photoUri = data.getData();
                    if (photoUri != null) {
                        photo = BitmapFactory.decodeFile(photoUri.getPath());
                    }
                    if (photoUri == null) {
                        Bundle extra = data.getExtras();
                        if (extra != null) {
                            photo = (Bitmap) extra.get("data");
                            if (photo != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                photo.compress(Bitmap.CompressFormat.JPEG, 85, stream);
                            } else {
                                break;
                            }
                            //----------------------------------------------------------------------
                        } else {
                            break;
                        }
                    }
                    ImageTools.deletePhotoAtPathAndName(filePath, fileName);
                    String fileSaveName = ConstConfig.PIGAI_CROPPED_IMAGE_FILE_NAME;
                    ImageTools.savePhotoToSDCard(photo, filePath, fileSaveName);
                    File file = new File(filePath + "/" + fileSaveName);
                    //TODO 上传图片的逻辑操作
                    int uid = mTeacher.mDescription.mUid;
                    mHttpRequest.UserPhotoModify(uid, file);
                    break;
                default:
                    break;
            }
        }

        if (resultCode == RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }

    //截取图片
    public void cropImage(Uri uri, int outputX, int outputY, int requestCode) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String url = getPath(getActivity(), uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        } else {
            intent.setDataAndType(uri, "image/*");
        }
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, requestCode);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @Override
    public void UserPhotoModify(String sImage, String bImage) {
        mTeacher.mDescription.sHead = sImage + "?";
        mTeacher.mDescription.bHead = bImage + "?";
        imageLoader.getMemoryCache().evictAll(); // remove all
        imageLoader.getDiskCache().clear(); // remove all
        imageLoader.get(mTeacher.mDescription.sHead, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean b) {
                if (response.getBitmap() != null) {
                    // load image into imageview
                    teacherPhoto.setImageBitmap(response.getBitmap());
                    teacherPhoto.invalidate();
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("StudentHome_Me", "Image Load Error: " + volleyError.getMessage());
            }
        });
        Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
        // 重新记录studentinfo sp
        mTeacher.SetDescriptionAndWriteToSP(getActivity(), mTeacher.mDescription);
    }

    public void ErrorNetwork() {
        Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
    }


}
