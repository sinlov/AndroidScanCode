/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jule.sinlov.scancode.utils.zxing.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.jule.sinlov.scancode.utils.zxing.ZXingConf;

import java.util.regex.Pattern;

public final class CameraConfigurationManager {

    private static final String TAG = CameraConfigurationManager.class.getSimpleName();

    private static final int TEN_DESIRED_ZOOM = 27;
    private static final int DESIRED_SHARPNESS = 30;

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    private final Context context;
    private Point screenResolution;
    private Point cameraResolution;
    private int previewFormat;
    private String previewFormatString;

    CameraConfigurationManager(Context context) {
        this.context = context;
    }

    /**
     * Reads, one time, values from the camera that are needed by the app.
     */
    void initFromCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        previewFormat = parameters.getPreviewFormat();
        previewFormatString = parameters.get("preview-format");
        if (ZXingConf.DEBUG) {
            Log.d(TAG, "Default preview format: " + previewFormat + '/' + previewFormatString);
        }
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        screenResolution = new Point(display.getWidth(), display.getHeight());
        if (ZXingConf.DEBUG) {
            Log.d(TAG, "Screen resolution: " + screenResolution);
        }
        cameraResolution = getCameraResolution(parameters, screenResolution);
        if (ZXingConf.DEBUG) {
            Log.d(TAG, "Camera resolution: " + screenResolution);
        }
    }

    /**
     * Sets the camera up to take preview images which are used for both preview and decoding.
     * We detect the preview format here so that buildLuminanceSource() can build an appropriate
     * LuminanceSource subclass. In the future we may want to force YUV420SP as it's the smallest,
     * and the planar Y can be used for barcode scanning without a copy in some cases.
     */
    void setDesiredCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        if (ZXingConf.DEBUG) {
            Log.d(TAG, "Setting preview size: " + cameraResolution);
        }
        rotationCamera(camera, parameters);
    }

    /**
     * modify here for camera
     * @param camera
     * @param parameters
     */
    private void rotationCamera(Camera camera, Camera.Parameters parameters) {
        parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
        setFlash(parameters);
        setZoom(parameters);
        camera.setDisplayOrientation(90);
        camera.setParameters(parameters);
    }

    Point getCameraResolution() {
        return cameraResolution;
    }

    Point getScreenResolution() {
        return screenResolution;
    }

    int getPreviewFormat() {
        return previewFormat;
    }

    String getPreviewFormatString() {
        return previewFormatString;
    }

    private static Point getCameraResolution(Camera.Parameters parameters, Point screenResolution) {

        String previewSizeValueString = parameters.get("preview-size-values");
        previewSizeValueString = sawOnPhone(parameters, previewSizeValueString);

        Point cameraResolution = null;
        if (previewSizeValueString != null) {
            if (ZXingConf.DEBUG) {
                Log.d(TAG, "preview-size-values parameter: " + previewSizeValueString);
            }
            cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
        }
        cameraResolution = cameraResolutionPoint(screenResolution, cameraResolution);
        return cameraResolution;
    }

    /**
     * Ensure that the camera resolution is a multiple of 8, as the screen may not be.
     *
     * @param screenResolution screen resolution
     * @param cameraResolution camera resolution
     * @return Point
     */
    @NonNull
    private static Point cameraResolutionPoint(Point screenResolution, Point cameraResolution) {
        if (cameraResolution == null) {
            cameraResolution = new Point(
                    (screenResolution.x >> 3) << 3,
                    (screenResolution.y >> 3) << 3);
        }
        return cameraResolution;
    }

    private static String sawOnPhone(Camera.Parameters parameters, String previewSizeValueString) {
        if (previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }
        return previewSizeValueString;
    }

    private static Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenResolution) {
        int bestX = 0;
        int bestY = 0;
        int diff = Integer.MAX_VALUE;
        for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {

            previewSize = previewSize.trim();
            int dimPosition = previewSize.indexOf('x');
            if (dimPosition < 0) {
                if (ZXingConf.DEBUG) {
                    Log.w(TAG, "Bad preview-size: " + previewSize);
                }
                continue;
            }

            int newX;
            int newY;
            try {
                newX = Integer.parseInt(previewSize.substring(0, dimPosition));
                newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
            } catch (NumberFormatException nfe) {
                Log.w(TAG, "Bad preview-size: " + previewSize);
                continue;
            }

            int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
            if (newDiff == 0) {
                bestX = newX;
                bestY = newY;
                break;
            } else if (newDiff < diff) {
                bestX = newX;
                bestY = newY;
                diff = newDiff;
            }
        }

        if (bestX > 0 && bestY > 0) {
            return new Point(bestX, bestY);
        }
        return null;
    }

    private static int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
        int tenBestValue = 0;
        for (String stringValue : COMMA_PATTERN.split(stringValues)) {
            stringValue = stringValue.trim();
            double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException nfe) {
                return tenDesiredZoom;
            }
            int tenValue = (int) (10.0 * value);
            if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
                tenBestValue = tenValue;
            }
        }
        return tenBestValue;
    }

    private void setFlash(Camera.Parameters parameters) {
        // FIXME: This is a hack to turn the flash off on the Samsung Galaxy.
        // And this is a hack-hack to work around a different value on the Behold II
        // Restrict Behold II check to Cupcake, per Samsung's advice
        //if (Build.MODEL.contains("Behold II") &&
        //    CameraManager.SDK_INT == Build.VERSION_CODES.CUPCAKE) {
        if (Build.MODEL.contains("Behold II") && CameraManager.SDK_INT == 3) { // 3 = Cupcake
            parameters.set("flash-value", 1);
        } else {
            parameters.set("flash-value", 2);
        }
        // This is the standard setting to turn the flash off that all devices should honor.
        parameters.set("flash-mode", "off");
    }

    private void setZoom(Camera.Parameters parameters) {
        String zoomSupportedString = parameters.get("zoom-supported");
        if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) {
            return;
        }
        int tenDesiredZoom = TEN_DESIRED_ZOOM;
        String maxZoomString = parameters.get("max-zoom");
        tenDesiredZoom = checkZoomMax(tenDesiredZoom, maxZoomString);
        String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
        tenDesiredZoom = checkZoomTakingPictureMax(tenDesiredZoom, takingPictureZoomMaxString);
        String motZoomValuesString = parameters.get("mot-zoom-values");
        tenDesiredZoom = checkZoomMotValues(tenDesiredZoom, motZoomValuesString);
        String motZoomStepString = parameters.get("mot-zoom-step");
        tenDesiredZoom = checkZoomMotStep(tenDesiredZoom, motZoomStepString);
        setZoomUserPullBack(parameters, tenDesiredZoom, maxZoomString, motZoomValuesString);
        setZoomParameter(parameters, tenDesiredZoom, takingPictureZoomMaxString);
    }

    /**
     * Most devices, like the Hero, appear to expose this zoom parameter.
     * <br> It takes on values like "27" which appears to mean 2.7x zoom
     * @param parameters
     * @param tenDesiredZoom
     * @param takingPictureZoomMaxString
     */
    private void setZoomParameter(Camera.Parameters parameters, int tenDesiredZoom, String takingPictureZoomMaxString) {
        if (takingPictureZoomMaxString != null) {
            parameters.set("taking-picture-zoom", tenDesiredZoom);
        }
    }

    /**
     * Set zoom. This helps encourage the user to pull back.
     * <br>Some devices like the Behold have a zoom parameter.
     * @param parameters parameters
     * @param tenDesiredZoom ten Desired Zoom
     * @param maxZoomString max zoom string
     * @param motZoomValuesString mot zoom values string
     */
    private void setZoomUserPullBack(Camera.Parameters parameters, int tenDesiredZoom, String maxZoomString, String motZoomValuesString) {
        if (maxZoomString != null || motZoomValuesString != null) {
            parameters.set("zoom", String.valueOf(tenDesiredZoom / 10.0));
        }
    }

    private int checkZoomMotStep(int tenDesiredZoom, String motZoomStepString) {
        if (motZoomStepString != null) {
            try {
                double motZoomStep = Double.parseDouble(motZoomStepString.trim());
                int tenZoomStep = (int) (10.0 * motZoomStep);
                if (tenZoomStep > 1) {
                    tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
                }
            } catch (NumberFormatException nfe) {
                // continue
            }
        }
        return tenDesiredZoom;
    }

    private int checkZoomMotValues(int tenDesiredZoom, String motZoomValuesString) {
        if (motZoomValuesString != null) {
            tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
        }
        return tenDesiredZoom;
    }

    private int checkZoomTakingPictureMax(int tenDesiredZoom, String takingPictureZoomMaxString) {
        if (takingPictureZoomMaxString != null) {
            try {
                int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
                if (ZXingConf.DEBUG) {
                    Log.w(TAG, "Bad taking-picture-zoom-max: " + takingPictureZoomMaxString);
                }
            }
        }
        return tenDesiredZoom;
    }

    private int checkZoomMax(int tenDesiredZoom, String maxZoomString) {
        if (maxZoomString != null) {
            try {
                int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
                if (ZXingConf.DEBUG) {
                    Log.w(TAG, "Bad max-zoom: " + maxZoomString);
                }
            }
        }
        return tenDesiredZoom;
    }

    public static int getDesiredSharpness() {
        return DESIRED_SHARPNESS;
    }
}
