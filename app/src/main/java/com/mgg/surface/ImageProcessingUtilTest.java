package com.mgg.surface;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.Surface;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

@SuppressLint("RestrictedApi")
public class ImageProcessingUtilTest {
    private static final int WIDTH = 8;
    private static final int HEIGHT = 4;

    private static final int PADDING_BYTES = 16;

    public void writeJpegToSurface_returnsTheSameImage(Surface surface) {
        // Arrange: create a JPEG image with solid color.
        byte[] inputBytes = createJpegBytesWithSolidColor(Color.RED);
        // Act: acquire image and get the bytes.
        ImageProcessingUtil.writeJpegBytesToSurface(surface, inputBytes);
    }


    /**
     * Returns JPEG bytes of a image with the given color.
     */
    private byte[] createJpegBytesWithSolidColor(int color) {
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        // Draw a solid color
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        // Encode to JPEG and return.
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }


    public void canCopyBetweenBitmapAndByteBufferWithDifferentStrides() {
        // Create bitmap with a solid color
        Bitmap bitmap1 = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        bitmap1.eraseColor(Color.YELLOW);

        int bufferStride = bitmap1.getRowBytes() + PADDING_BYTES;

        // Same size bitmap with a different color
        Bitmap bitmap2 = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        bitmap2.eraseColor(Color.BLUE);

        ByteBuffer bytebuffer = ByteBuffer.allocateDirect(bufferStride * bitmap1.getHeight());

        // Copy bitmap1 into bytebuffer
        ImageProcessingUtil.copyBitmapToByteBuffer(bitmap1, bytebuffer, bufferStride);

        // Copy bytebuffer into bitmap2
        ImageProcessingUtil.copyByteBufferToBitmap(bitmap2, bytebuffer, bufferStride);
    }
}
