package don.p3tru4io.s.locktracker;

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.util.SparseIntArray;
import android.view.Surface;

/**
 * Abstract Picture Taking Service.
 *
 * @author hzitoun (zitoun.hamed@gmail.com)
 */
public abstract class APictureCapturingService {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    final Context context;
    final CameraManager manager;

    /***
     * constructor.
     *
     * //@param activity the activity used to get display manager and the application context
     */
    APictureCapturingService(final Context context) {
        this.context = context;
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    /***
     * @return  orientation
     */
    int getOrientation() {
        return Surface.ROTATION_0;
    }


    /**
     * starts pictures capturing process.
     *
     *
     */
    public abstract void startCapturing(String date);
}