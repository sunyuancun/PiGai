package com.cikuu.pigai.activity.student;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.cikuu.pigai.activity.SendFeedbackActivity;

import com.cikuu.pigai.activity.dialog.UpdateNoIngoreVersionApplication;
import com.cikuu.pigai.activity.uiutils.CircleImage;
import com.cikuu.pigai.activity.utils.AppVisionTool;
import com.cikuu.pigai.activity.utils.BitmapScaleUtils;
import com.cikuu.pigai.activity.utils.ConstConfig;
import com.cikuu.pigai.activity.utils.ImageTools;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
import com.cikuu.pigai.app.AppController;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class StudentHomeMeFragment extends Fragment implements VolleyRequest.UserPhotoModifyCallback, VolleyRequest.VersionCallback, VolleyRequest.ResetPasswordByOldPasswordCallback {

    Context mStudentHomeActivityContext;
    private ImageView studentPhoto;
    private TextView student_name;
    private TextView student_school;
    private TextView appVisionTextView;
    private TextView studentQuit;
    private RelativeLayout studentPhotoLine;
    private LinearLayout studentInfoLine;
    private LinearLayout studentbackline;
    private LinearLayout studentAboutApp;
    private LinearLayout studentupdateAppLine;
    private LinearLayout studentresetpasswordLine;
    private VolleyRequest mHttpRequest;
    private Student mStudent;
    private AppController.CacheImageLoader imageLoader;

    UpdateNoIngoreVersionApplication updateApp;
    int netWorkVersion = 1;
    int currentVersion = 1;
    String currentVisionStr = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_home_me, null);
        studentPhoto = (ImageView) view.findViewById(R.id.userPhoto);
        student_name = (TextView) view.findViewById(R.id.student_name);
        student_school = (TextView) view.findViewById(R.id.student_school);
        studentPhotoLine = (RelativeLayout) view.findViewById(R.id.user_info_photo_line);
        studentInfoLine = (LinearLayout) view.findViewById(R.id.studentInfoLine);
        studentbackline = (LinearLayout) view.findViewById(R.id.studentbackline);
        studentresetpasswordLine = (LinearLayout) view.findViewById(R.id.studentresetpasswordline);

        studentAboutApp = (LinearLayout) view.findViewById(R.id.studentAboutApp);
        studentupdateAppLine = (LinearLayout) view.findViewById(R.id.studentupdateApp);
        appVisionTextView = (TextView) view.findViewById(R.id.appVisionTextView);
        studentQuit = (TextView) view.findViewById(R.id.login_out_textview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mStudentHomeActivityContext = getActivity();
        mHttpRequest = new VolleyRequest();
        mHttpRequest.mUserPhotoModifyCallback = this;
        mHttpRequest.mVersion = this;
        mHttpRequest.mResetPasswordByOldPasswordCallback = this;
        mHttpRequest.getVersion();
        mStudent = Student.GetInstance();
        imageLoader = AppController.getInstance().getCacheImageLoader();
        try {
            if (mStudentHomeActivityContext != null) {
                currentVisionStr = AppVisionTool.getVersion(mStudentHomeActivityContext);
                currentVersion = mStudentHomeActivityContext.getPackageManager().getPackageInfo(mStudentHomeActivityContext.getPackageName(), 0).versionCode;
                appVisionTextView.setText(currentVisionStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        student_name.setText(mStudent.mStudentDescription.mName);
        student_school.setText(mStudent.mStudentDescription.mSchool);
        // 加载小头像
        imageLoader.getMemoryCache().evictAll(); // remove all
        imageLoader.getDiskCache().clear(); // remove all
        imageLoader.get(mStudent.mStudentDescription.bHead, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean b) {
                if (response.getBitmap() != null) {
                    // load image into imageview
//                    studentPhoto.setImageBitmap(response.getBitmap());
                    studentPhoto.setImageBitmap(new CircleImage(mStudentHomeActivityContext)
                            .transform(response.getBitmap()));
                    studentPhoto.invalidate();
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("StudentHome_Me", "Image Load Error: " + volleyError.getMessage());
            }
        });

        //加载大图片
        studentPhoto.setOnClickListener(listener);
        //修改设置图片
        studentPhotoLine.setOnClickListener(listener);
        studentInfoLine.setOnClickListener(listener);
        studentbackline.setOnClickListener(listener);
        studentresetpasswordLine.setOnClickListener(listener);

        studentupdateAppLine.setOnClickListener(listener);
        studentAboutApp.setOnClickListener(listener);
        studentQuit.setOnClickListener(listener);

    }

    public void ResetPasswordByOldPassword(int success, String errorMsg) {

    }

    @Override
    public void Version(double version) {
        netWorkVersion = (int) (version * 10);
    }

    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.userPhoto) {
                Intent photoIntent = new Intent(mStudentHomeActivityContext, BigUserPhotoActivity.class);
                photoIntent.putExtra("flag", 101);
                startActivityForResult(photoIntent, 101);
            } else if (v.getId() == R.id.user_info_photo_line) {
                //截图后显示
                showPicturePicker(mStudentHomeActivityContext, true);
            } else if (v.getId() == R.id.studentInfoLine) {
                Intent studentInfoIntent = new Intent(mStudentHomeActivityContext, StudentInformationActivity.class);
                startActivityForResult(studentInfoIntent, 201);
            } else if (v.getId() == R.id.studentbackline) {
                //todo
                Intent backInfoIntent = new Intent(mStudentHomeActivityContext, SendFeedbackActivity.class);
                startActivityForResult(backInfoIntent, 301);

            } else if (v.getId() == R.id.studentresetpasswordline) {
                Intent resetPasswordIntent = new Intent(mStudentHomeActivityContext, ResetPasswordByOldPasswordActivity.class);
                startActivity(resetPasswordIntent);
            } else if (v.getId() == R.id.studentAboutApp) {
                Intent aboutIntent = new Intent(mStudentHomeActivityContext, AboutAppActivity.class);
                startActivityForResult(aboutIntent, 401);
            } else if (v.getId() == R.id.studentupdateApp) {

                if (netWorkVersion > currentVersion) {
                    updateApp = new UpdateNoIngoreVersionApplication(mStudentHomeActivityContext);
                    updateApp.Update();
                } else {
                    //todo   弹框
                    dialog();
                }
            } else if (v.getId() == R.id.login_out_textview) {
                SharedPreferenceUtil.setUserAutoLoginInSP(false, getActivity());
                Intent quitIntent = new Intent(mStudentHomeActivityContext, LoginActivity.class);
                startActivity(quitIntent);
                getActivity().finish();
            }
        }
    };

    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mStudentHomeActivityContext);
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


    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("StudentHomeMeFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("StudentHomeMeFragment"); //统计页面
        student_name.setText(mStudent.mStudentDescription.mName);
        student_school.setText(mStudent.mStudentDescription.mSchool);
    }

    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;
    private static final int CROP = 2;
    private static final int CROP_PICTURE = 3;
    //Android   4.4  以后
    private static final int SELECT_PIC_KITKAT = 4;
    private String fileName = ConstConfig.PIGAI_CAMERA_IMAGE_FILE_NAME;
    private String filePath = ConstConfig.PIGAI_FILE_PATH;

    // pic take/crop
    public void showPicturePicker(Context context, boolean isCrop) {
        final boolean crop = isCrop;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("图片来源");
        builder.setNegativeButton("取消", null);
        builder.setItems(new String[]{"拍照", "图库"}, new DialogInterface.OnClickListener() {
            //类型码
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

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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

    //截取图片
    public void cropImage(Uri uri, int outputX, int outputY, int requestCode) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String url = getPath(mStudentHomeActivityContext, uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        } else {
            intent.setDataAndType(uri, "image/*");
        }
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
//        intent.putExtra("outputFormat", "JPEG");
//        intent.putExtra("noFaceDetection", true);
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

    Bitmap photo = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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
                    photo = BitmapScaleUtils.decodeSampledBitmapFromResource(photoUri.getPath(), 120, 120);
//                    photo = BitmapFactory.decodeFile(photoUri.getPath());
                } else {
                    Bundle extra = data.getExtras();
                    if (extra != null) {
                        photo = (Bitmap) extra.get("data");
                        if (photo != null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 85, stream);
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }

                ImageTools.deletePhotoAtPathAndName(filePath, fileName);

                String fileSaveName = ConstConfig.PIGAI_CROPPED_IMAGE_FILE_NAME;
                ImageTools.savePhotoToSDCard(photo, filePath, fileSaveName);
                File file = new File(filePath + "/" + fileSaveName);
                Log.e("---------------->", "" + file.length());
                // 上传图片的逻辑操作
                int uid = mStudent.mStudentDescription.mUid;
                mHttpRequest.UserPhotoModify(uid, file);
                break;

            default:
                break;

        }
    }

    @Override
    public void UserPhotoModify(String sImage, String bImage) {
        mStudent.mStudentDescription.sHead = sImage + "?";
        mStudent.mStudentDescription.bHead = bImage + "?";

        imageLoader.getMemoryCache().evictAll(); // remove all
        imageLoader.getDiskCache().clear(); // remove all
        imageLoader.get(mStudent.mStudentDescription.bHead, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean b) {
                if (response.getBitmap() != null) {
                    // load image into imageview
                    studentPhoto.setImageBitmap(new CircleImage(mStudentHomeActivityContext)
                            .transform(response.getBitmap()));
                    studentPhoto.invalidate();
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("StudentHome_Me", "Image Load Error: " + volleyError.getMessage());
            }
        });

        Toast.makeText(mStudentHomeActivityContext, "修改成功", Toast.LENGTH_SHORT).show();
        // 重新记录studentinfo sp
        mStudent.SetDescriptionAndWriteToSP(mStudentHomeActivityContext, mStudent.mStudentDescription);
    }

    @Override
    public void ErrorNetwork() {
        Toast store = Toast.makeText(mStudentHomeActivityContext, "网络或服务器错误！", Toast.LENGTH_LONG);
        store.show();
    }
}
