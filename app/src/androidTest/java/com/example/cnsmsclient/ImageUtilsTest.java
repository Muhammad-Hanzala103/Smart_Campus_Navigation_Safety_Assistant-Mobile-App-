package com.example.cnsmsclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.cnsmsclient.util.ImageUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ImageUtilsTest {

    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void compressImage_reducesImageSize() throws IOException {
        // Create a dummy large bitmap file
        File originalFile = createDummyBitmapFile(2048, 2048);
        long originalSize = originalFile.length();

        // Compress the file
        File compressedFile = ImageUtils.compressImage(context, Uri.fromFile(originalFile));

        assertNotNull(compressedFile);
        long compressedSize = compressedFile.length();

        // Assert that the compressed file is smaller than the original
        assertTrue("Compressed file size (" + compressedSize + ") should be smaller than original size (" + originalSize + ")", compressedSize < originalSize);

        // Clean up
        originalFile.delete();
        compressedFile.delete();
    }

    private File createDummyBitmapFile(int width, int height) throws IOException {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        File file = new File(context.getCacheDir(), "test_image.png");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
        return file;
    }
}
