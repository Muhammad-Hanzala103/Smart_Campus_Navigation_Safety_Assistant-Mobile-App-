package com.example.cnsmsclient.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    private static final int MAX_DIMENSION = 1024;
    private static final int COMPRESSION_QUALITY = 80;

    public static File compressImage(Context context, Uri imageUri, long sizeLimitBytes) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        if (inputStream == null) return null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();

        options.inSampleSize = calculateInSampleSize(options, MAX_DIMENSION, MAX_DIMENSION);

        options.inJustDecodeBounds = false;
        inputStream = context.getContentResolver().openInputStream(imageUri);
        Bitmap scaledBitmap = BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();

        if (scaledBitmap == null) return null;

        File tempFile = File.createTempFile("upload_", ".jpg", context.getCacheDir());
        int currentQuality = COMPRESSION_QUALITY;

        FileOutputStream fos = new FileOutputStream(tempFile);
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, currentQuality, fos);
        fos.close();

        // Further reduce quality if file is still too large
        while (tempFile.length() > sizeLimitBytes && currentQuality > 10) {
            currentQuality -= 10;
            fos = new FileOutputStream(tempFile);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, currentQuality, fos);
            fos.close();
        }

        scaledBitmap.recycle();
        return tempFile;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
