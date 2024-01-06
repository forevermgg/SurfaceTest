package com.mgg.surface;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import java.nio.ByteBuffer;

public final class ImageProcessingUtil {

    private static final String TAG = "ImageProcessingUtil";
    private static final int sImageCount = 0;

    static {
        // System.loadLibrary("image_processing_util_jni");
    }

    private ImageProcessingUtil() {
    }

    /**
     * Copies information from a given Bitmap to the address of the ByteBuffer
     *
     * @param bitmap       source bitmap
     * @param byteBuffer   destination ByteBuffer
     * @param bufferStride the stride of the ByteBuffer
     */
    public static void copyBitmapToByteBuffer(@NonNull Bitmap bitmap,
                                              @NonNull ByteBuffer byteBuffer, int bufferStride) {
        int bitmapStride = bitmap.getRowBytes();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        nativeCopyBetweenByteBufferAndBitmap(bitmap, byteBuffer, bitmapStride, bufferStride, width,
                height, false);
    }

    /**
     * Copies information from a ByteBuffer to the address of the Bitmap
     *
     * @param bitmap       destination Bitmap
     * @param byteBuffer   source ByteBuffer
     * @param bufferStride the stride of the ByteBuffer
     */
    public static void copyByteBufferToBitmap(@NonNull Bitmap bitmap,
                                              @NonNull ByteBuffer byteBuffer, int bufferStride) {
        int bitmapStride = bitmap.getRowBytes();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        nativeCopyBetweenByteBufferAndBitmap(bitmap, byteBuffer, bufferStride, bitmapStride, width,
                height, true);
    }

    /**
     * Writes a JPEG bytes data as an Image into the Surface. Returns true if it succeeds and false
     * otherwise.
     */
    public static boolean writeJpegBytesToSurface(
            @NonNull Surface surface,
            @NonNull byte[] jpegBytes) {

        if (nativeWriteJpegToSurface(jpegBytes, surface) != 0) {
            Log.e(TAG, "Failed to enqueue JPEG image.");
            return false;
        }
        return true;
    }

    private static boolean isSupportedRotationDegrees(
            @IntRange(from = 0, to = 359) int rotationDegrees) {
        return rotationDegrees == 0
                || rotationDegrees == 90
                || rotationDegrees == 180
                || rotationDegrees == 270;
    }

    private static native int nativeCopyBetweenByteBufferAndBitmap(Bitmap bitmap,
                                                                   ByteBuffer byteBuffer,
                                                                   int sourceStride, int destinationStride, int width, int height,
                                                                   boolean isCopyBufferToBitmap);

    private static native int nativeWriteJpegToSurface(@NonNull byte[] jpegArray,
                                                       @NonNull Surface surface);


    enum Result {
        UNKNOWN,
        SUCCESS,
        ERROR_CONVERSION,  // Native conversion error.
    }
}
