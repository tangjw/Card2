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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    
    private static final int REQUESTCODE_TAKE = 1; // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2; // 图片裁切标记
    
    private Button start;
    private ImageView img;
    private String file;
    
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
    
    
    Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/temp.jpg"));
    
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
                final Bitmap bitmap = decodeUriAsBitmap(imageUri);
//                System.out.println(bitmap.getByteCount());
                file = FileUtil.saveFile(this, "hahaha.jpg", bitmap);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadImg1(bitmap);
                    }
                }).start();
                
                
                img.setImageBitmap(bitmap);
            }
            
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void openCamera() {
        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
    
    private String path = "http://bcr2.intsig.net/BCRService/BCR_VCF2?user=tangjw@zonsim.com&pass=CQNTPGAP7FX3NXGF&lang=524287";
    String end = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    String newName = "image.jpg";
    
    private void uploadImg1(Bitmap bitmap) {
        
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //允许Input Output, 不使用Cache
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            
            //设置请求方法
            connection.setRequestMethod("POST");
            
            //设置RequestProperty
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            
            //设置DataOutputStream
            DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " + "name=\"file1\";filename=\"" + newName + "\"" + end);
            ds.writeBytes(end);
            
            //取得文件的FileInputStream
            FileInputStream fStream = new FileInputStream(file);
            //设置每次写入1024bytes
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            //从文件读取数据至缓冲区
            while ((length = fStream.read(buffer)) != -1) {
                //将资料写入DataOutputStream中
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            //close streams
            fStream.close();
            ds.flush();
            //取得Response内容 
            InputStream is = connection.getInputStream();
            int ch;
            File file = new File(Environment.getExternalStorageDirectory() + "/card.vcf");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            while ((ch = is.read()) != -1) {
                fileOutputStream.write(ch);
            }

            fileOutputStream.close();
            ds.close();
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
}

