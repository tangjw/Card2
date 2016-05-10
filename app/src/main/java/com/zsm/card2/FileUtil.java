package com.zsm.card2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {

    
    /**
     * 将Bitmap 图片保存到本地路径，并返回路径
     * @param c
     * @param mType 资源类型，参照  MultimediaContentType 枚举，根据此类型，保存时可自动归类
     * @param fileName 文件名称
     * @param bitmap 图片
     * @return
     */
	public static String saveFile(Context c, String fileName, Bitmap bitmap) {
		return saveFile(c, "", fileName, bitmap);
	}
	
	public static String saveFile(Context c, String filePath, String fileName, Bitmap bitmap) {
		byte[] bytes = bitmapToBytes(bitmap);
		return saveFile(c, filePath, fileName, bytes);
	}
	
	public static byte[] bitmapToBytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}
	
	public static String saveFile(Context c, String filePath, String fileName, byte[] bytes) {
		String fileFullName = "";
		FileOutputStream fos = null;
//		String dateFolder = new SimpleDateFormat("yyyyMMdd", Locale.CHINA)
//				.format(new Date());
		try {
			String suffix = "";
			if (filePath == null || filePath.trim().length() == 0) {
				filePath = Environment.getExternalStorageDirectory() + "/yixue/temp/";
			}
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			File fullFile = new File(filePath, fileName + suffix);
			fileFullName = fullFile.getPath();
			fos = new FileOutputStream(new File(filePath, fileName + suffix));
			fos.write(bytes);
		} catch (Exception e) {
			fileFullName = "";
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					fileFullName = "";
				}
			}
		}
		return fileFullName;
	}
	public static String getFileType(String fileName) {
		if (fileName != null // 非空
				&& fileName.contains(".")// 是否有点
				&& fileName.lastIndexOf(".") != fileName.length() - 1) {// 点是不是在文件名的最后出现
			return fileName.substring(fileName.lastIndexOf(".") +1,
					fileName.length());
		} else {
			return ""; // 这里一定要写空字符串，写null会造成空指针
		}
	}
	public static void getListDataAll(List<Map<String, Object>> list,
			String path) {

		// 获取文件夹目录数组
		File pfile = new File(path);
		File[] files = null;
		if (pfile.exists()) {
			files = pfile.listFiles();
		}

		if (files != null && files.length > 0) {

			for (File file : files) {

				if (file.isDirectory() && file.canRead()) {
					// 遇到文件夹
					getListDataAll(list, file.getAbsolutePath()
						);
				} else if (file.isFile()) {// 遇到文件
					System.out.println("mmmmmmmmmm");
					if(getFileType(file.getName()).equals("doc")||getFileType(file.getName()).equals("docs")){
						Map<String, Object> item = new HashMap<String, Object>();
						
						item.put("name", file.getName());
						item.put("path", file.getPath());
						// 2. 扩展名-->rid
//						item.put("icon", getDrawableIcon(end));
						list.add(item);// 添加元素
					}

				}

			}
		}
	}
	public static String getSDCardPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
		.getExternalStorageState());
		} 

}
