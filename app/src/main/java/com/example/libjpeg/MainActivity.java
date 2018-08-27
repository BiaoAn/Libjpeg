package com.example.libjpeg;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mCombineCompress, mCompressBtn,mOriginalBtn,mAfterCompressBtn;
    private ImageView mImage;

    /*
    压缩保存路径:原来的demo是将压缩图片也放在mImageRootDir
    但是会出现文件权限的问题，在imagerar.c中进行图片压缩的时候会打开失败
    ACTION_OPEN_DOCUMENT_TREE，举个栗子，在网上下载一个第三方的文件管理应用
    在安装时，会提示用户申请sd卡某个具体目录的访问权限
     */
    private File fileDir;

    /** 图片存放根目录*/
    private final String mImageRootDir = Environment
            .getExternalStorageDirectory().getPath() + "/DCIM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestReadExternalPermission();
        fileDir  = getBaseContext().getExternalFilesDir("");

        mCompressBtn = (Button) findViewById(R.id.compress_btn);
        mCombineCompress = (Button) findViewById(R.id.size_quality_libjpeg_compress_btn);
        mOriginalBtn = (Button) findViewById(R.id.original_btn);
        mImage = (ImageView) findViewById(R.id.image);

        mCompressBtn.setOnClickListener(this);
        mCombineCompress.setOnClickListener(this);
        mOriginalBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.compress_btn://直接jni libjpeg压缩

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        final File afterCompressImgFile = new File(fileDir
                                + "/test.jpg");

                        String tempCompressImgPath = mImageRootDir+File.separator+"temp.jpg";//事先准备好的sd卡目录下的图片

                        //直接使用jni libjpeg压缩
                        Bitmap bitmap = BitmapFactory.decodeFile(tempCompressImgPath);
                        String codeString = ImageUtils.compressBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), 40, afterCompressImgFile.getAbsolutePath().getBytes(), true);
                        Log.e("code", "code "+codeString);

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mImage.setImageBitmap(BitmapFactory
                                        .decodeFile(afterCompressImgFile.getPath()));
                                showImaMessPopwindow();
                            }
                        });
                    }
                }).start();

                break;
            case R.id.size_quality_libjpeg_compress_btn://尺寸 质量 libjpeg结合压缩

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File afterCompressImgFile = new File(fileDir
                                + "/test.jpg");
                        //先尺寸质量压缩后在用jni libjpeg压缩   (先保证SD卡目录下/jpeg_picture/temp.jpg存在)
                        String tempCompressImgPath = mImageRootDir+File.separator+"temp.jpg";
                        Log.d("System.out----",tempCompressImgPath);
                        ImageUtils.compressBitmap(tempCompressImgPath, afterCompressImgFile.getPath());

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mImage.setImageBitmap(BitmapFactory
                                        .decodeFile(afterCompressImgFile.getPath()));
                                showImaMessPopwindow();
                            }
                        });
                    }
                }).start();
                break;

            case R.id.original_btn://原图
                String tempCompressImgPath = mImageRootDir+File.separator+"temp.jpg";
                mImage.setImageBitmap(BitmapFactory
                        .decodeFile(tempCompressImgPath));
                break;

            default:
                break;
        }
    }

    private void showImaMessPopwindow() {
        PopupWindow popupWindow = new PopupWindow();
        View popView = getLayoutInflater().inflate(R.layout.popview,null);
        TextView textView = popView.findViewById(R.id.pop_tv);
        textView.setText("原有图片的大小为："+FilesSizeUtils.getFileOrFilesSize("/storage/emulated/0/DCIM/temp.jpg")+"   "+
                "压缩后的图片大小："+FilesSizeUtils.getFileOrFilesSize(fileDir
                + "/test.jpg"));
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(popView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(mCombineCompress);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @SuppressLint("NewApi")
    private void requestReadExternalPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "READ permission IS NOT granted...");

            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        } else {
//            Log.d(TAG, "READ permission is granted...");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    // request successfully, handle you transactions
                }
                return;
            }
            default:
                break;
        }
    }
}
