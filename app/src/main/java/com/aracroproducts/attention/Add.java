package com.aracroproducts.attention;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class Add extends AppCompatActivity {

    private DecoratedBarcodeView barcodeView;
    private Vibrator v;
    private String lastText;
    private boolean cameraActive = true;
    private long lastSnackBar = 0;

    private static final int CAMERA_CALLBACK_CODE = 10;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null) {
                return;
            }
            if (result.getText().equals(lastText)) {
                if ((System.currentTimeMillis() - lastSnackBar) > 5000) {
                    View layout = findViewById(R.id.add_constraint);
                    Snackbar snackbar = Snackbar.make(layout, R.string.scan_new, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    lastSnackBar = System.currentTimeMillis();
                }
                return;
            }

            lastText = result.getText();
            TextView enter_id = findViewById(R.id.manual_code);
            enter_id.setText(lastText);

            pause();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        QRCodeWriter writer = new QRCodeWriter();
        String id = getSharedPreferences(MainActivity.USER_INFO, Context.MODE_PRIVATE).getString("id", null);
        String name = getSharedPreferences(MainActivity.USER_INFO, Context.MODE_PRIVATE).getString("name", null);
        String user = name + " " + id;
        try {
            BitMatrix bitMatrix = writer.encode(user, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ((ImageView) findViewById(R.id.QR)).setImageBitmap(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        ((TextView) findViewById(R.id.user_id_text)).setText(user);

        if (hasCameraPermission()) {
            startScan();
        }

    }

    private void startScan() {
        barcodeView = findViewById(R.id.zxing_barcode_scanner);

        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

        barcodeView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                resume();
            }

        });
    }

    public void finishActivity(View view) {
        TextView textView = findViewById(R.id.manual_code);
        String complete = textView.getText().toString();
        if (complete.length() == 0) {
            textView.setError(getString(R.string.no_id));
            resume();
            return;
        }

        int separatorIndex = complete.lastIndexOf(' ') + 1;
        if (separatorIndex == 0) {
            textView.setError(getString(R.string.invalid_id));
            resume();
            return;
        }

        SharedPreferences preferences = getSharedPreferences(MainActivity.FRIENDS, Context.MODE_PRIVATE);
        HashSet<String> friendIds = new HashSet<>(preferences.getStringSet("ids", new HashSet<String>()));
        HashSet<String> friendNames = new HashSet<>(preferences.getStringSet("names", new HashSet<>()));

        String id = complete.substring(separatorIndex);
        String name = complete.substring(0, separatorIndex);
        friendIds.add(id);
        friendNames.add(name);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("names", friendNames);
        editor.putStringSet("ids", friendIds);
        editor.apply();

        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_CALLBACK_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasCameraPermission() && barcodeView != null) {
            barcodeView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (barcodeView != null) {
            barcodeView.pause();
        }
    }

    public void pause() {
        if (cameraActive) {
            barcodeView.pause();
            cameraActive = false;
        }
    }

    public void resume() {
        if (!cameraActive) {
            barcodeView.resume();
            cameraActive = true;
        }
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private boolean hasCameraPermission() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) //app already has camera permission. Great!
                return true;
            else if (ActivityCompat.shouldShowRequestPermissionRationale(this, // display dialog explaining why the app is requesting camera permission
                    Manifest.permission.CAMERA)) {

                AlertDialog alert = new AlertDialog.Builder(this).create();
                alert.setTitle(getString(R.string.permissions_needed));
                alert.setMessage(getString(R.string.permission_details));

                alert.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.allow), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        ActivityCompat.requestPermissions(Add.this, new String[]{Manifest.permission.CAMERA}, CAMERA_CALLBACK_CODE);
                        //Works as long as there is only one required permission. More complicated code refactoring may be necessary in the future
                    }
                });

                alert.setButton(DialogInterface.BUTTON_NEGATIVE, this.getString(R.string.deny), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                alert.show();
                return false;

            } else { // request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_CALLBACK_CODE);
                return false;
            }


        } else { // device does not have a camera for some reason
            barcodeView = findViewById(R.id.zxing_barcode_scanner);
            barcodeView.setStatusText(getString(R.string.no_camera));
            return false;
        }
    }
}