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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    
    
    //The Uri to store the big bitmap
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
    
    /**
     * 上传图片到名片全能王
     *
     * @param bitmap
     */
    private void uploadImg(Bitmap bitmap) {
        byte[] bytes = bitmapToBytes(bitmap);
        /*HttpUtils httpUtils = new HttpUtils();
    
        String url = "http://bcr2.intsig.net/BCRService/BCR_VCF2?user=tangjw@zonsim.com&pass=CQNTPGAP7FX3NXGF&lang=524287";
        RequestParams params = new RequestParams();
        params.addBodyParameter("upfile", new File(file));
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                System.out.println(result);
            }
    
            @Override
            public void onFailure(HttpException e, String s) {
        
            }
        });*/
        String path = "http://bcr2.intsig.net/BCRService/BCR_VCF2?user=tangjw@zonsim.com&pass=CQNTPGAP7FX3NXGF&lang=524287";
        try {
            URL url = new URL(path);
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.setRequestMethod("POST");
            //上传图片的一些参数设置  
            uc.setRequestProperty("Accept", "image/gif,   image/x-xbitmap,   image/jpeg,   image/pjpeg,   application/vnd.ms-excel,   application/vnd.ms-powerpoint,   application/msword,   application/x-shockwave-flash,   application/x-quickviewplus,   */*");
            uc.setRequestProperty("Accept-Language", "zh-cn");
            uc.setRequestProperty("Content-type", "multipart/form-data;   boundary=---------------------------7d318fd100112");
            uc.setRequestProperty("Accept-Encoding", "gzip,   deflate");
            uc.setRequestProperty("User-Agent", "Mozilla/4.0   (compatible;   MSIE   6.0;   Windows   NT   5.1)");
            uc.setRequestProperty("Connection", "Keep-Alive");
            uc.setDoOutput(true);
            uc.setUseCaches(false);
            //读取文件流  
            int size = (int) file.length();
            byte[] data = new byte[size];
            FileInputStream fis = new FileInputStream(file);
            
            fis.read(data, 0, size);
            OutputStream os = uc.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(os);
            dataOutputStream.write(data);

//            os.write(data);
            dataOutputStream.close();
            
            os.flush();
            os.close();
            fis.close();
            
            int code = uc.getResponseCode();
            String result = "";
            
            if (200 == code) {
                InputStream is = uc.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                while (bufferedReader.readLine() != null) {
                    result = result + bufferedReader.readLine().trim();
                }
            }
            System.out.println(result);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    }
    
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
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
    
    private String path = "http://bcr2.intsig.net/BCRService/BCR_VCF2?user=tangjw@zonsim.com&pass=CQNTPGAP7FX3NXGF&lang=524287";
    String end = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    String newName = "image.jpg";
    
    private void uploadImg1(Bitmap bitmap) {
        
        byte[] bytes = bitmapToBytes(bitmap);
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
//            StringBuffer b = new StringBuffer();
            File file = new File(Environment.getExternalStorageDirectory() + "/card.vcf");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            while ((ch = is.read()) != -1) {
//                b.append((char) ch);
                fileOutputStream.write(ch);
            }
            //将Response显示于Dialog
//            System.out.println("返回:" + b.toString().trim());
            
            //关闭DataOutputStream
            fileOutputStream.close();
            ds.close();
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
}

