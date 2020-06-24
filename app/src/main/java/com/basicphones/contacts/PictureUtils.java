package com.basicphones.contacts;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;





public class PictureUtils {

    public static Bitmap getScaledBitmap(String path, Activity activity) {
    Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);
        return getScaledBitmap(path, size.x/2, size.y/2);
    }

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        // Figure out how much to scale down by
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        // Read in and create final bitmap

        Bitmap src_bitmap = BitmapFactory.decodeFile(path, options);

        return getSquareBitmap(src_bitmap);
    }
    public static Bitmap getSquareBitmap(Bitmap srcBmp) {
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        return dstBmp;
    }

    public static Bitmap createSquaredBitmap(Bitmap srcBmp) {
        int dim = Math.max(srcBmp.getWidth(), srcBmp.getHeight());
        Bitmap dstBmp = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(dstBmp);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(srcBmp, (dim - srcBmp.getWidth()) / 2, (dim - srcBmp.getHeight()) / 2, null);

        return dstBmp;
    }

    public static Bitmap decodeUri(Uri selectedImage, Context context, int destWidth, int destHeight) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        // Figure out how much to scale down by
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;


        // Decode with inSampleSize
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, options);
    }

    public static Bitmap getCircularBitmap(String filePath, float diameterDP, int imageViewWidth , int imageViewHeight ) {

        Bitmap bitmap = getScaledBitmap(filePath, (int) (imageViewWidth*0.7), (int) (imageViewHeight*0.7));

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float diameterPixels = diameterDP * (metrics.densityDpi / 160f);

        Bitmap output = Bitmap.createBitmap((int) diameterPixels, (int) diameterPixels, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final Rect rect = new Rect(0, 0, (int) diameterPixels, (int) diameterPixels);
        RectF rectF = new RectF(rect);


        final int color = 0xff424242;
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);

        canvas.drawOval(rectF, paint);

        float left = (diameterPixels-bitmap.getWidth())/2;
        float top = (diameterPixels-bitmap.getHeight())/2;

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, left, top, paint);
        bitmap.recycle();

        return output;
    }

    private static List<Integer> materialColors = Arrays.asList(
            0xffe57373,
            0xfff06292,
            0xffba68c8,
            0xff9575cd,
            0xff7986cb,
            0xff64b5f6,
            0xff4fc3f7,
            0xff4dd0e1,
            0xff4db6ac,
            0xff81c784,
            0xffaed581,
            0xffff8a65,
            0xffd4e157,
            0xffffd54f,
            0xffffb74d,
            0xffa1887f,
            0xff90a4ae
    );


    public static Bitmap generateCircleBitmap(Context context, int circleColor, float diameterDP, String text){
        final int textColor = 0xffffffff;

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float diameterPixels = diameterDP * (metrics.densityDpi / 160f);
        float radiusPixels = diameterPixels/2;

        // Create the bitmap
        Bitmap output = Bitmap.createBitmap((int) diameterPixels, (int) diameterPixels,
                Bitmap.Config.ARGB_8888);

        // Create the canvas to draw on
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);

        // Draw the circle
        final Paint paintC = new Paint();
        paintC.setAntiAlias(true);
        paintC.setColor(circleColor);
        canvas.drawCircle(radiusPixels, radiusPixels, radiusPixels, paintC);

        // Draw the text
        if (text != null && text.length() > 0) {
            final Paint paintT = new Paint();
            paintT.setColor(textColor);
            paintT.setAntiAlias(true);
            paintT.setTextSize(radiusPixels * 2);
            Typeface typeFace = Typeface.defaultFromStyle(Typeface.BOLD);
            paintT.setTypeface(typeFace);
            final Rect textBounds = new Rect();
            paintT.getTextBounds(text, 0, text.length(), textBounds);
            canvas.drawText(text, radiusPixels - textBounds.exactCenterX(), radiusPixels - textBounds.exactCenterY(), paintT);
        }

        return output;
    }


    public static int getMaterialColor(Object key) {
        return materialColors.get(Math.abs(key.hashCode()) % materialColors.size());
    }

    public static void moveFile(String inputPath, String inputFile, String outputPath, String outputFile) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            boolean isOutDirExists=true;
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                isOutDirExists=dir.mkdirs();
            }

            if(isOutDirExists) {
                in = new FileInputStream(inputPath + inputFile);
                out = new FileOutputStream(outputPath + outputFile);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;

                // write the output file
                out.flush();
                out.close();
                out = null;

                // delete the original file
                if(!new File(inputPath + inputFile).delete())
                    Log.e("allonsy.contacts", inputPath + inputFile + " not deleted after moving");

            }
            else
                Log.e("allonsy.contacts", outputPath + " does not exist, move failed");
        }
        catch (Exception e) {
            Log.e("allonsy.contacts", e.getMessage());
        }


    }

    public static void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            if(!new File(inputPath + inputFile).delete())
                Log.e("allonsy.contacts", inputPath + inputFile + " not deleted, delete failed");
        }
        catch (Exception e) {
            Log.e("allonsy.contacts", e.getMessage());
        }
    }

    public static void copyFile(String inputPath, String inputFile, String outputPath,  String outputFile) {

        InputStream in = null;
        OutputStream out = null;
        try {

            boolean isOutDirExists=true;
            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                isOutDirExists=dir.mkdirs();
            }

            if(isOutDirExists) {
                in = new FileInputStream(inputPath + inputFile);
                out = new FileOutputStream(outputPath + outputFile);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;

                // write the output file (You have now copied the file)
                out.flush();
                out.close();
                out = null;
            }
            else
                Log.e("allonsy.contacts", outputPath + " does not exist, copy failed");
        }
        catch (Exception e) {
            Log.e("allonsy.contacts", e.getMessage());
        }

    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap, int dst_width, int dst_height) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        Bitmap _bmp = Bitmap.createScaledBitmap(output, (int)(dst_width * 0.7), (int)(dst_height * 0.7), false);
        return _bmp;
//        return output;
    }

}
