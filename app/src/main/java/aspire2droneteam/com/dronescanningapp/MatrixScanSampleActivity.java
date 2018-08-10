package aspire2droneteam.com.dronescanningapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.scandit.barcodepicker.BarcodePicker;
import com.scandit.barcodepicker.OnScanListener;
import com.scandit.barcodepicker.ProcessFrameListener;
import com.scandit.barcodepicker.ScanOverlay;
import com.scandit.barcodepicker.ScanSession;
import com.scandit.barcodepicker.ScanSettings;
import com.scandit.barcodepicker.ScanditLicense;
import com.scandit.recognition.Barcode;
import com.scandit.recognition.TrackedBarcode;

import java.util.ArrayList;

public class MatrixScanSampleActivity
        extends Activity implements OnScanListener, ProcessFrameListener {

    // Enter your Scandit SDK License key here.
    public static final String sScanditSdkAppKey = "AYSrmTtcJyhqE5r5JQ9pZI5EHqjiC0LJGHaaMTVff/kGQtLjwVFW1GFYPYnLAiwYhwv98Ox0UAZJYWWNy0EyQf50H6U4Qq/0AgMt2ltE/qKdbifBwUvoA8Yvc/flRu+YD0Do/JtHCsEhdxw8TkC39O9N2yuGRXN1A3o7e6J7geIuSz2DUEazaDko69HZcVuk8FRHWqlZZuPfc25NvUmBTgdFCuFiS5abAkj1qxEj8x3SXS4B33IbdltB+4BLbT4hAS7fIsV3IOGKe0aR913l05lcjqSmXblzl394RSFjhniGaqyDimunUHEjn9Y+ao4ltUAX5Ch4KkfHQcvpzG/hSeV2g1TtYuZQhWPdzh9ZSBP9S82vd0wZQsBRcUSrWDC7hXYf16Ra6JLGfhv9OQed9hxHW/rBQVhHz0rpzxdaFEVtQMiyUmpYhrVdwZ/CIs/4ghaElmpFUb9KajPkORE8l/Aqz4aHdD/OZUM1S/AG/jOcBbUk4k4Oto8Wc01GUUN/O+ozfkGBP5syYl13BYbndCz+V7p6WhfxeMD34lIH1sJU8DAyJ66FjNvvhnSo7IbDT2AHFC4SJaS1POU61zzeTM4ljFxIFU/1g0aA2fpfkG0Ba0TL0yyHn4GZZnG+kg2qoT3hQ4iVcm6ZYbzfgzVYpTL+05hKHFoUzDnFIgD6OapIHzb88s6xRzoryXoR+dpEfmdQ255XqqXfsx4ILlk/pnu2pdzjqFoFlflmA0w910rvxU7v1Y6RbqCqXMp0GLsbccmeUBQZ0w6St+SvM0TJQMwG+oR0tcBBvxkuQpXTmyTcu/2O+e9pWabWQ6W6jOdmc4kVa+7wLUu18gyfkTU8nDAUpxD0h2QbfVSb1j5MScuw1Qoz6mL8Nncdw476o1zmdEj+FH8vI1UBmXrO4BCAd7i/oJRJ/PbsM4qfy2Sqew+BeeZQ2rodoE9PMCrlZobWrSOdliHdnoHnTPm60K2lGIlkFCkv6rBgxwxDSF5gjshMU980FwHVsheVbhm2ixXvb/Sc7CwQ2VK3CZuL+LlnKpDbaoFTrO9RmlUPsx2OkzQsXGFWiTJS4Nz7dNNhRDsLzVmtCRK3KM3h5sKjalGSYk4HCTQM4SZ2upsqD0/vAg2TZjTPJaTGyhb6Y845zUWF1b8=";

    private final int CAMERA_PERMISSION_REQUEST = 0;

    // The main object for recognizing a displaying barcodes.
    private BarcodePicker mBarcodePicker;
    private boolean mDeniedCameraAccess = false;
	private boolean mPaused = true;

    // Data structure responsible for storing barcodes, recognized by the matrix scan
    // during a single scanning session.
    private ArrayList<Barcode> mRecognizedBarcodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScanditLicense.setAppKey(sScanditSdkAppKey);
        // Initialize and start the bar code recognition.
        initializeAndStartBarcodeScanning();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // When the activity is in the background immediately stop the 
        // scanning to save resources and free the camera.
        mBarcodePicker.stopScanning();
        mPaused = true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void grantCameraPermissionsThenStartScanning() {
        if (this.checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (!mDeniedCameraAccess) {
                // it's pretty clear for why the camera is required. We don't need to give a
                // detailed reason.
                this.requestPermissions(new String[]{Manifest.permission.CAMERA},
                                        CAMERA_PERMISSION_REQUEST);
            }
        } else {
            // we already have the permission
            mBarcodePicker.startScanning();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mDeniedCameraAccess = false;
                if (!mPaused) {
                    mBarcodePicker.startScanning();
                }
            } else {
                mDeniedCameraAccess = true;
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPaused = false;
        // handle permissions for Marshmallow and onwards...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            grantCameraPermissionsThenStartScanning();
        } else {
            // Once the activity is in the foreground again, restart scanning.
            mBarcodePicker.startScanning();
        }
    }

    /**
     * Initializes and starts the MatrixScan
     */
    public void initializeAndStartBarcodeScanning() {
        // Switch to full screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // The scanning behavior of the barcode picker is configured through scan
        // settings. We start with empty scan settings and enable a generous set
        // of 1D symbologies. MatrixScan is currently only supported for 1D
        // symbologies, enabling 2D symbologies will result in unexpected results.
        // In your own apps, only enable the symbologies you actually need.
        ScanSettings settings = ScanSettings.create();
        int[] symbologiesToEnable = new int[] {
                Barcode.SYMBOLOGY_EAN13,
                Barcode.SYMBOLOGY_EAN8,
                Barcode.SYMBOLOGY_UPCA,
                Barcode.SYMBOLOGY_CODE39,
                Barcode.SYMBOLOGY_CODE128,
                Barcode.SYMBOLOGY_INTERLEAVED_2_OF_5,
                Barcode.SYMBOLOGY_UPCE
        };
        for (int sym : symbologiesToEnable) {
            settings.setSymbologyEnabled(sym, true);
        }


        settings.setMatrixScanEnabled(true);
        settings.setMaxNumberOfCodesPerFrame(10 );

        // Prefer the back-facing camera, if there is any.
        settings.setCameraFacingPreference(ScanSettings.CAMERA_FACING_BACK);

        // Some Android 2.3+ devices do not support rotated camera feeds. On these devices, the
        // barcode picker emulates portrait mode by rotating the scan UI.
        boolean emulatePortraitMode = !BarcodePicker.canRunPortraitPicker();
        if (emulatePortraitMode) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        BarcodePicker picker = new BarcodePicker(this, settings);

        // Set the GUI style to MatrixScan to see a visualization of the tracked barcodes. If you
        // would like to visualize it yourself, set it to ScanOverlay.GUI_STYLE_NONE and update your
        // visualization in the didProcess() callback.
        picker.getOverlayView().setGuiStyle(ScanOverlay.GUI_STYLE_MATRIX_SCAN);

        // When using MatrixScan vibrating is often not desired.
        picker.getOverlayView().setVibrateEnabled(false);

        mBarcodePicker = picker;

        prepareUI();

        // Register listener, in order to be notified about relevant events
        // (e.g. a successfully scanned bar code).
        mBarcodePicker.setOnScanListener(this);

        // Register a process frame listener to be able to reject tracked codes.
        mBarcodePicker.setProcessFrameListener(this);
    }

    private void prepareUI() {
        setContentView(R.layout.activity_main); // find main layout
        RelativeLayout pickerContainer = findViewById(R.id.picker);
        pickerContainer.addView(mBarcodePicker.getRootView());

        Button doneButton = findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> listOfBarcodes = new ArrayList<>();
                for (Barcode b : mRecognizedBarcodes) {
                    listOfBarcodes.add(b.getData());
                }
                String json = new Gson().toJson(listOfBarcodes);
                System.out.println(json);

                mRecognizedBarcodes.clear(); // Clear ArrayLis
            }
        });
    }

    @Override
    public void didScan(ScanSession session) {
        // This callback acts the same as when not tracking and can be used for the events such as
        // when a code is newly recognized. Rejecting tracked codes has to be done in didProcess().
    }

    @Override
    public void didProcess(byte[] imageBuffer, int width, int height, ScanSession session) {
        for (TrackedBarcode code : session.getTrackedCodes().values()) {
            if (code.getSymbology() == Barcode.SYMBOLOGY_EAN8) {
                session.rejectTrackedCode(code);
            }
        }

        for (Barcode newBarcode : session.getNewlyRecognizedCodes()) {
            if (!isAlreadyRecognized(newBarcode)) {
                mRecognizedBarcodes.add(newBarcode);
            }
        }

        System.out.println("It's alive!!!!");
    }

    private boolean isAlreadyRecognized(Barcode barcode) {
        boolean recognized = false;
        for (Barcode recognizedBarcode : mRecognizedBarcodes) {
            if (recognizedBarcode.getSymbology() == barcode.getSymbology()
                    && recognizedBarcode.getData().equals(barcode.getData())) {
                recognized = true;
                break;
            }
        }
        return recognized;
    }

    @Override
    public void onBackPressed() {
        mBarcodePicker.stopScanning();
        finish();
    }
}


