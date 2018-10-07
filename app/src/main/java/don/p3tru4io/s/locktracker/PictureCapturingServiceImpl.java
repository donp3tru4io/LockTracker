package don.p3tru4io.s.locktracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;
import java.util.UUID;



    public class PictureCapturingServiceImpl extends APictureCapturingService {

        private static final String TAG = don.p3tru4io.s.locktracker.PictureCapturingServiceImpl.class.getSimpleName();

        private CameraDevice cameraDevice;
        private ImageReader imageReader;
        /***
         * camera ids queue.
         */
        private Queue<String> cameraIds;

        private String currentCameraId;
        private boolean cameraClosed;
        private String date;
        /**
         * stores a sorted map of (pictureUrlOnDisk, PictureData).
         */
        private TreeMap<String, byte[]> picturesTaken;

        /***
         * private constructor, meant to force the use of {@link #getInstance}  method
         */
        private PictureCapturingServiceImpl(final Context context) {
            super(context);
        }

        /**
         * //@param activity the activity used to get the app's context and the display manager
         * @return a new instance
         */
        public static APictureCapturingService getInstance(final Context context) {
            return new don.p3tru4io.s.locktracker.PictureCapturingServiceImpl(context);
        }

        /**
         * Starts pictures capturing treatment.
         *
         *
         */
        @Override
        public void startCapturing(String date) {

            this.picturesTaken = new TreeMap<>();
            this.cameraIds = new LinkedList<>();
            this.date = date;
            try {
                final String[] cameraIds = manager.getCameraIdList();
                if (cameraIds.length > 0) {
                    this.cameraIds.addAll(Arrays.asList(cameraIds));
                    while(!this.cameraIds.isEmpty()) {
                        this.currentCameraId = this.cameraIds.poll();
                    }
                    openCamera();
                } else {
                    //No camera detected!

                }
            } catch (final CameraAccessException e) {
                Log.e(TAG, "Exception occurred while accessing the list of cameras", e);
            }
        }

        private void openCamera() {
            Log.d(TAG, "opening camera " + currentCameraId);
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    manager.openCamera(currentCameraId, stateCallback, null);
                }
            } catch (final CameraAccessException e) {
                Log.e(TAG, " exception occurred while opening camera " + currentCameraId, e);
            }
        }

        private final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                           @NonNull TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
                if (picturesTaken.lastEntry() != null) {

                    Log.i(TAG, "done taking picture from camera " + cameraDevice.getId());
                }
                closeCamera();
            }
        };


        private final ImageReader.OnImageAvailableListener onImageAvailableListener = (ImageReader imReader) -> {
            final Image image = imReader.acquireLatestImage();
            final ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            final byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            saveImageToDisk(bytes);
            image.close();
        };

        private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                cameraClosed = false;
                Log.d(TAG, "camera " + camera.getId() + " opened");
                cameraDevice = camera;
                Log.i(TAG, "Taking picture from camera " + camera.getId());
                //Take the picture after some delay. It may resolve getting a black dark photos.
                new Handler().postDelayed(() -> {
                    try {
                        takePicture();

                    } catch (final IllegalStateException e) {
                        Log.e(TAG, " exception illegal state from " + currentCameraId, e);
                        return;
                    } catch (final CameraAccessException e) {
                        Log.e(TAG, " exception occurred while taking picture from " + currentCameraId, e);
                        return;
                    }
                }, 500);
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                Log.d(TAG, " camera " + camera.getId() + " disconnected");
                if (cameraDevice != null && !cameraClosed) {
                    cameraClosed = true;
                    cameraDevice.close();
                }
            }

            @Override
            public void onClosed(@NonNull CameraDevice camera) {
                cameraClosed = true;
                Log.d(TAG, "camera " + camera.getId() + " closed");
                //once the current camera has been closed, start taking another picture
                /*if (!cameraIds.isEmpty()) {
                    takeAnotherPicture();
                } else {
                    capturingListener.onDoneCapturingAllPhotos(picturesTaken);
                }*/

            }


            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                Log.e(TAG, "camera in error, int code " + error);
                if (cameraDevice != null && !cameraClosed) {
                    cameraDevice.close();
                }
            }
        };


        private void takePicture() throws CameraAccessException, IllegalStateException{
            if (null == cameraDevice) {
                Log.e(TAG, "cameraDevice is null");
                return;
            }
            final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            StreamConfigurationMap streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (streamConfigurationMap != null) {
                jpegSizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
            }
            final boolean jpegSizesNotEmpty = jpegSizes != null && 0 < jpegSizes.length;
            int width = jpegSizesNotEmpty ? jpegSizes[0].getWidth() : 640;
            int height = jpegSizesNotEmpty ? jpegSizes[0].getHeight() : 480;
            final ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            final List<Surface> outputSurfaces = new ArrayList<>();
            outputSurfaces.add(reader.getSurface());
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation());
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);
            reader.setOnImageAvailableListener(onImageAvailableListener, null);
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            try {
                                session.capture(captureBuilder.build(), captureListener, null);
                            } catch (final CameraAccessException e) {
                                Log.e(TAG, " exception occurred while accessing " + currentCameraId, e);
                            }catch (final IllegalStateException e)
                            {
                                Log.e(TAG, " exception illegal state from " + currentCameraId, e);
                                return;
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        }
                    }
                    , null);
        }


        private void saveImageToDisk(final byte[] bytes) {
            final String cameraId = this.cameraDevice == null ? UUID.randomUUID().toString() : this.cameraDevice.getId();

            File dir = new File(Environment.getExternalStorageDirectory()+"/LockTracker");
            if(!dir.exists())
            {
                dir.mkdir();
            }

            final File file = new File(Environment.getExternalStorageDirectory() + "/LockTracker/" + date + "_pic.jpg");
            try (final OutputStream output = new FileOutputStream(file)) {
                output.write(bytes);
                this.picturesTaken.put(file.getPath(), bytes);
            } catch (final IOException e) {
                Log.e(TAG, "Exception occurred while saving picture to external storage ", e);
            }
        }

        private void takeAnotherPicture() {
            this.currentCameraId = this.cameraIds.poll();
            openCamera();
        }

        private void closeCamera() {
            Log.d(TAG, "closing camera " + cameraDevice.getId());
            if (null != cameraDevice && !cameraClosed) {
                cameraDevice.close();
                cameraDevice = null;
            }
            if (null != imageReader) {
                imageReader.close();
                imageReader = null;
            }
        }


    }
