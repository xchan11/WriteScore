package com.example.writescore.utils.file;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import tuorong.com.healthy.utils.ui.ToastUtil;


//功能待测试
public class FileUtil {
    static public void initData() {
        String filePath = "/sdcard/FileUtil/";
        String fileName = "log.txt";

        writeTxtToFile("txt content", filePath, fileName);
    }

    //    private String scanQRCodeFromImageView(ImageView imageView) {
//        // 获取 ImageView 中的二维码图片（Bitmap）
//        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//
//        try {
//            // 创建一个 MultiFormatReader 实例
//            MultiFormatReader multiFormatReader = new MultiFormatReader();
//
//            // 将 Bitmap 转换为 BinaryBitmap，用于扫描
//            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
//                    new BitmapLuminanceSource(bitmap)
//            ));
//
//            // 调用 decode 方法扫描二维码
//            Result result = multiFormatReader.decode(binaryBitmap);
//
//            // 获取扫描结果的字符串内容
//            String decodedText = result.getText();
//
//            // 返回扫描结果
//            return decodedText;
//        } catch (Exception e) {
//            // 处理扫描失败的情况
//            e.printStackTrace();
//            return null;
//        }
//    }
    public static String scanQRCode2(Bitmap bitmap) {
        try {
            Reader reader = new MultiFormatReader();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            BinaryBitmap binaryBitmap = new BinaryBitmap(
                    new HybridBinarizer(new RGBLuminanceSource(width, height, pixels))
            );
            Result result = reader.decode(binaryBitmap);
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String scanQRCode(Bitmap bitmap) {
        try {
            Reader reader = new MultiFormatReader();
            int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            BinaryBitmap binaryBitmap = new BinaryBitmap(
                    new HybridBinarizer(
                            new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray)
                    )
            );
            Result result = reader.decode(binaryBitmap);
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 将字符串写入到文本文件中
    static public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    // 生成文件
    static public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    /**
     * 保存二维码图片到本地
     */
    static public boolean saveCode(Bitmap bitmap, Context context) {
        SimpleDateFormat df1 = new SimpleDateFormat("mmss");
        String time = df1.format(new Date());
        String path = context.getFilesDir().getAbsolutePath();
        FileUtil.makeFilePath(Environment.getExternalStorageDirectory() + "/family", time);
        File file = new File(Environment.getExternalStorageDirectory(), "捐款二维码" + time + ".JPEG");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            Bitmap bitMap = bitmap;
            //((BitmapDrawable) ivQRCode.getDrawable()).getBitmap();//通过强制转化weiBitmapDrable然后获取Bitmap
            // ToastUtil.ToastLong(appContext,s);
            boolean saved = bitMap.compress(Bitmap.CompressFormat.JPEG,
                    100, fileOutputStream);//然后按照指定的图片格式转换，并以stream方式保存文件
            fileOutputStream.close();
            if (saved) {
                ToastUtil.ToastMsg(context, "保存成功!");
                sendBroadcastToScanFile(file, context); //广播扫描文件
                return true;
            } else {
                ToastUtil.ToastMsg(context, "保存失败!请重试");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.ToastMsg(context, "保存失败!请重试" + e.getMessage());
            return false;
        }
    }

    // 发起广播让系统扫描该file
    public static void sendBroadcastToScanFile(File file, Context context) {
        Intent intentScan = new Intent();
        intentScan.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intentScan.setData(Uri.fromFile(file));
        context.sendBroadcast(intentScan);
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 50, baos);
        byte[] bytes = baos.toByteArray();
//        String myBase64 = Base64.encodeToString(bytes,Base64.DEFAULT);
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static Bitmap getCoverOfVideo(String videoUrl) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoUrl, new java.util.HashMap<>());
            return retriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static File getFileFromUri(Context context, Uri uri) {
        if (context == null || uri == null) return null;

        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        File tempFile = null;

        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            String fileName = getFileName(context, uri);
            tempFile = new File(context.getCacheDir(), fileName);

            if (inputStream != null) {
                outputStream = new FileOutputStream(tempFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return tempFile;
    }

    public static String getFileName(Context context, Uri uri) {
        if (context == null || uri == null) return null;

        String fileName = null;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName != null ? fileName : uri.getLastPathSegment() != null ? uri.getLastPathSegment() : "temp_file";
    }
}
