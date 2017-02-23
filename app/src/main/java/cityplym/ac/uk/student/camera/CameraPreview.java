package cityplym.ac.uk.student.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Eddy on 23/02/2017.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{


    private SurfaceHolder holder;
    private Camera cam;
    private CameraActivity ca;

    public CameraPreview(Context c, Camera cam, CameraActivity ca){
        super(c);
        this.cam = cam;
        this.ca = ca;
        holder = this.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            cam.setPreviewDisplay(this.holder);
            cam.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(this.holder.getSurface() == null){
            return;
        }

        try{
            this.cam.stopPreview();
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            this.cam.setPreviewDisplay(this.holder);
            this.cam.setPreviewCallback(this.ca);
            this.cam.startPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
