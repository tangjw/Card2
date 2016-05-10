package com.zsm.card2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    
    private static final String IMAGE_FILE_NAME = "temp.jpg";
    private static final int REQUESTCODE_TAKE = 1; // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2; // 图片裁切标记
    
    private Button start;
    private ImageView img;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        img = (ImageView) findViewById(R.id.iv_img);
        start = (Button) findViewById(R.id.btn_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
    }
    
    
    Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/123.jpg"));//The Uri to store the big bitmap
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUESTCODE_TAKE == requestCode && resultCode == Activity.RESULT_OK) {
            cropImageUri(imageUri);
        }
        if (REQUESTCODE_CUTTING == requestCode) {
            if (data != null) {
                
            }
        }
        
        if (5 == requestCode) {
            if (imageUri != null) {
                Bitmap bitmap = decodeUriAsBitmap(imageUri);
//                System.out.println(bitmap.getByteCount());
//                FileUtil.saveFile(this, "hahaha.jpg", bitmap);
    
                uploadImg(bitmap);
                
                img.setImageBitmap(bitmap);
            }
            
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * 上传图片到名片全能王
     * @param bitmap
     */
    private void uploadImg(Bitmap bitmap) {
        
        
        
        
    }
    
    private void openCamera() {
        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 下面这句指定调用相机拍照后的照片存储的路径
//        File file = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(takeIntent, REQUESTCODE_TAKE);
    }
    
    private void cropImageUri(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        /*intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 1);*/
        /*intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);*/
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, 5);
    }
    
    
    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
    
    
}

