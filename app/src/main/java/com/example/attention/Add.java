package com.example.attention;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class Add extends CaptureActivity {

    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.activity_add);


        QRCodeWriter writer = new QRCodeWriter();
        String id = getSharedPreferences(MainActivity.USER_INFO, Context.MODE_PRIVATE).getString("id", null);
        String name = getSharedPreferences(MainActivity.USER_INFO, Context.MODE_PRIVATE).getString("name", null);
        String user = id + " " + name;
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

        return findViewById(R.id.zxing_barcode_scanner);
    }
}