package com.example.liang.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by liang on 2017/12/8.
 */

public class WenJian extends Activity {
    private Button shangChuan;
    private String uploadUrl = "http://192.168.43.24:11403/ceshia/FuWuDuan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wenjian);
        quanXianShenQing();
        shangChuan = (Button) findViewById(R.id.shangchuan);
        shangChuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);

            }
        });
    }

    private void quanXianShenQing() {
        if (ContextCompat.checkSelfPermission(WenJian.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WenJian.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            chuanShuJu(getFileAbsolutePath(WenJian.this, data.getData()));
        }
    }

    private void chuanShuJu(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("xinxi",s);
                Log.i("xinxi",s.substring(s.lastIndexOf("/")+1));
                try {
                  NetTool.sendFile(uploadUrl, s, s.substring(s.lastIndexOf("/")+1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String getFileAbsolutePath(Activity context, Uri fileUri) {
        if (context == null || fileUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, fileUri)) {
            if (isExternalStorageDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(fileUri)) {
                String id = DocumentsContract.getDocumentId(fileUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
            if (isGooglePhotosUri(fileUri))
                return fileUri.getLastPathSegment();
            return getDataColumn(context, fileUri, null, null);
        } else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
            return fileUri.getPath();
        }
        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
