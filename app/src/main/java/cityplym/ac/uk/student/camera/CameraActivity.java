package cityplym.ac.uk.student.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import cityplym.ac.uk.student.R;

/**
 * Created by Eddy on 23/02/2017.
 */

public class CameraActivity extends Activity implements Camera.PreviewCallback{

    private Camera mCamera;
    private CameraPreview mPreview;
    private BarcodeDetector bc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camprev);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        Camera.Parameters cPrams = mCamera.getParameters();
        for(int i = 0; i < cPrams.getSupportedPreviewFpsRange().size(); i++){
            String res = "";
            for(int j : cPrams.getSupportedPreviewFpsRange().get(i)){
                res += j + ",";
            }
            res =res.substring(0, res.length() - 1);
            Log.d("CAM", res);
        }
        cPrams.setPreviewFpsRange(15000,15000);
        cPrams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setParameters(cPrams);
        mCamera.setPreviewCallbackWithBuffer(this);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera, this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);


        findViewById(R.id.button_capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Press to take picture
            }
        });

        this.bc = new BarcodeDetector.Builder(getApplicationContext()).setBarcodeFormats(Barcode.QR_CODE | Barcode.DATA_MATRIX).build();
        if(!this.bc.isOperational()){
            Toast.makeText(this, "Unable to start barcode detector", Toast.LENGTH_SHORT).show();
            finish(false, "");
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(0); // attempt to get a Camera instance
        } catch (Exception e){
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Parameters cp = camera.getParameters();
        Camera.Size s = cp.getPreviewSize();
        Bitmap bitmap = Bitmap.createBitmap(s.width, s.height, Bitmap.Config.ARGB_8888);
        Allocation bmData = renderScriptNV21ToRGBA888(this.getApplicationContext(),s.width, s.height,data);
        bmData.copyTo(bitmap);
        if(bitmap != null)scanBarcodes(bitmap);
    }

    public void scanBarcodes(Bitmap img){
        Frame frame = new Frame.Builder().setBitmap(img).build();
        SparseArray<Barcode> barcodes = this.bc.detect(frame);

        Log.d("CAM", ""+barcodes.size());

        if(barcodes.size() > 0){
            Barcode bb = barcodes.valueAt(0);
            finish(true, bb.rawValue);
        }
    }

    public void finish(boolean good, String val) {

        if(good){
            Intent i = new Intent();
            i.putExtra("val", val);
            setResult(RESULT_OK, i);
        }else{
            setResult(RESULT_CANCELED);
        }

        super.finish();
    }

    public Allocation renderScriptNV21ToRGBA888(Context context, int width, int height, byte[] nv21) {
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(nv21.length);
        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        in.copyFrom(nv21);

        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        return out;
    }
}
