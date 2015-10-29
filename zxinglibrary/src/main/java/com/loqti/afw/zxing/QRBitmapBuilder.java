package com.loqti.afw.zxing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

/**
 * for QR Bitmap build
 * Created by "sinlov" on 2015/10/28.
 */
public class QRBitmapBuilder {

    private static final String CHARACTER_UTF_8 = "utf-8";
    private static final int PIXELS_SET_DEFAULT = 0xff000000;
    private static final int WIDTH_HEIGHT_DEFAULT = 800;
    private static final Bitmap.Config BITMAP_CONF_DEFAULT = Bitmap.Config.ARGB_4444;
    private static final float INNER_BITMAP_SIZE_DEFAULT = 0.5f;

    /**
     * create QR bitmap by content
     * <BR> use default set {@link QRBitmapBuilder#WIDTH_HEIGHT_DEFAULT}
     * <BR> use default set {@link QRBitmapBuilder#BITMAP_CONF_DEFAULT}
     * @param content you words for QR map information
     * @return {@link Bitmap}
     * @throws WriterException
     */
    public static Bitmap createQRBitMap(String content) throws WriterException{
        return createQRBitMap(content, WIDTH_HEIGHT_DEFAULT, WIDTH_HEIGHT_DEFAULT);
    }

    /**
     * create QR bitmap by content
     * <BR> use default set {@link QRBitmapBuilder#BITMAP_CONF_DEFAULT}
     * @param content you words for QR map information
     * @param width bitmap width
     * @param height bitmap height
     * @return {@link Bitmap}
     * @throws WriterException
     */
    public static Bitmap createQRBitMap(String content, int width, int height) throws WriterException{
        return createQRBitMap(content, width, height, BITMAP_CONF_DEFAULT);
    }

    /**
     * create QR bitmap by content
     * @param content you words for QR map information
     * @param width bitmap width
     * @param height bitmap height
     * @param config you can use like {@link Bitmap.Config#ARGB_4444} or others,
     *               do not use {@link Bitmap.Config#RGB_565}
     * @return {@link Bitmap}
     * @throws WriterException
     */
    public static Bitmap createQRBitMap(String content, int width, int height, Bitmap.Config config) throws WriterException {
        BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height);
        int w = matrix.getWidth();
        int h = matrix.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * w + x] = PIXELS_SET_DEFAULT;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    /**
     * create QR Bitmap with inner image if inner bitmap create error , it will return null
     * <BR> use default set {@link QRBitmapBuilder#WIDTH_HEIGHT_DEFAULT}
     * <BR> use default set {@link QRBitmapBuilder#INNER_BITMAP_SIZE_DEFAULT}
     * <BR> use default set {@link QRBitmapBuilder#BITMAP_CONF_DEFAULT}
     * @param content information
     * @param innerBitmap inner image width and height
     * @return {@link Bitmap}
     * @throws WriterException
     */
    public static Bitmap createQRBitMapWithImage(String content, Bitmap innerBitmap
    ) throws WriterException {
        return createQRBitMapWithImage(content, WIDTH_HEIGHT_DEFAULT, innerBitmap);
    }


    /**
     * create QR Bitmap with inner image if inner bitmap create error , it will return null
     * <BR> use default set {@link QRBitmapBuilder#INNER_BITMAP_SIZE_DEFAULT}
     * <BR> use default set {@link QRBitmapBuilder#BITMAP_CONF_DEFAULT}
     * @param content information
     * @param widthAndHeight inner image width and height
     * @param innerBitmap inner image width and height
     * @return {@link Bitmap}
     * @throws WriterException
     */
    public static Bitmap createQRBitMapWithImage(String content, int widthAndHeight, Bitmap innerBitmap
                                                 ) throws WriterException {
        return createQRBitMapWithImage(content, widthAndHeight, innerBitmap, INNER_BITMAP_SIZE_DEFAULT);
    }

    /**
     * create QR Bitmap with inner image if inner bitmap create error , it will return null
     * <BR> use default set {@link QRBitmapBuilder#BITMAP_CONF_DEFAULT}
     * @param content information
     * @param widthAndHeight inner image width and height
     * @param innerBitmap inner image width and height
     * @param size inner bitmap hundred percent sometimes use 0.2f
     * @return {@link Bitmap}
     * @throws WriterException
     */
    public static Bitmap createQRBitMapWithImage(String content, int widthAndHeight, Bitmap innerBitmap,
                                                 float size) throws WriterException {
        return createQRBitMapWithImage(content, widthAndHeight, innerBitmap, size, BITMAP_CONF_DEFAULT);
    }

    /**
     * create QR Bitmap with inner image if inner bitmap create error , it will return null
     * @param content information
     * @param widthAndHeight inner image width and height
     * @param innerBitmap inner image width and height
     * @param size inner bitmap hundred percent sometimes use 0.2f
     * @param config you can use like {@link Bitmap.Config#ARGB_4444} or others,
     *               do not use {@link Bitmap.Config#RGB_565}
     * @return {@link Bitmap}
     * @throws WriterException
     */
    public static Bitmap createQRBitMapWithImage(String content, int widthAndHeight, Bitmap innerBitmap,
                                                 float size, Bitmap.Config config) throws WriterException {
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARACTER_UTF_8);
        BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = PIXELS_SET_DEFAULT;
                }
            }
        }
        Bitmap downBitmap = Bitmap.createBitmap(width, height, config);
        downBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        Bitmap logoBmp = scaling(innerBitmap, size);
        Bitmap bitmap = Bitmap.createBitmap(downBitmap.getWidth(), downBitmap.getHeight(), downBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(downBitmap, 0, 0, null);
        if (logoBmp != null) {
            canvas.drawBitmap(logoBmp, downBitmap.getWidth() / 2 - logoBmp.getWidth() / 2,
                    downBitmap.getHeight() / 2 - logoBmp.getHeight() / 2, null);
            logoBmp.recycle();
            downBitmap.recycle();
            return bitmap;
        } else {
            new Throwable("innerBitmap create is error").printStackTrace();
            return null;
        }
    }

    /**
     * scaling by size
     * @param bitmap your bitmap object
     * @param size scaling size
     * @return {@link Bitmap}
     */
    public static Bitmap scaling(Bitmap bitmap, float size) {
        if (null == bitmap) {
            new Throwable("Bitmap is null").printStackTrace();
            return null;
        } else {
            Matrix matrix = new Matrix();
            matrix.postScale(size, size);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
        }
    }

    private QRBitmapBuilder() {
    }
}
