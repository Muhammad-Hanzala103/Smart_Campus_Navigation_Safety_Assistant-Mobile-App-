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

    private static final int DEFAULT_MAX_DIMENSION = 1024; // pixels
    private static final int DEFAULT_COMPRESSION_QUALITY = 70; // 0-100
    private static final long SIZE_LIMIT_BYTES = 1 * 1024 * 1024; // 1MB

    /**
     * Compresses an image from a given Uri to a file under 1MB.
     * It first scales the image down so its longest side is `DEFAULT_MAX_DIMENSION`,
     * then adjusts JPEG quality until the file size is under the limit.
     */
    public static File compressImage(Context context, Uri imageUri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        if (inputStream == null) {
            throw new IOException("Unable to open input stream for URI: " + imageUri);
        }

        // 1. Decode bounds to calculate the scaling factor (inSampleSize)
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();

        options.inSampleSize = calculateInSampleSize(options, DEFAULT_MAX_DIMENSION, DEFAULT_MAX_DIMENSION);

        // 2. Decode the bitmap with the calculated scaling factor
        options.inJustDecodeBounds = false;
        InputStream scaledInputStream = context.getContentResolver().openInputStream(imageUri);
        Bitmap scaledBitmap = BitmapFactory.decodeStream(scaledInputStream, null, options);
        if (scaledInputStream != null) {
            scaledInputStream.close();
        }
        if (scaledBitmap == null) {
            throw new IOException("Failed to decode bitmap. The URI may be invalid or the image corrupt.");
        }

        // 3. Create a temporary file to save the compressed image
        File tempFile = File.createTempFile("cnsms_upload_", ".jpg", context.getCacheDir());

        // 4. Compress the bitmap to the file, reducing quality until it's under the size limit
        int currentQuality = DEFAULT_COMPRESSION_QUALITY;
        FileOutputStream fos = new FileOutputStream(tempFile);
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, currentQuality, fos);
        fos.close();

        while (tempFile.length() > SIZE_LIMIT_BYTES && currentQuality > 10) {
            currentQuality -= 10; // Decrease quality by 10
            FileOutputStream newFos = new FileOutputStream(tempFile);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, currentQuality, newFos);
            newFos.close();
        }

        // 5. Clean up the bitmap from memory
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
