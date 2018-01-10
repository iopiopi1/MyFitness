package cardam2.cardam2;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by yaros on 1/10/2018.
 */

public class ImageHelper {
    public static File bitmapToJpeg(String filename, Bitmap bitmap){
        File file = null;
        try {
            file = new File(filename);
            FileOutputStream fOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);
            fOutputStream.flush();
            fOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return file;
    }

    public static Bitmap checkOrientation(Bitmap image, String imagePath){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        }
        catch(IOException e){
            return image;
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(image, 270);
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(image, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(image, 180);
            default:
                return image;
        }

    }
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
    }
}
