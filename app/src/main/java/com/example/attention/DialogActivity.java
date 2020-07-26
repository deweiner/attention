package com.example.attention;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class DialogActivity extends AppCompatActivity {

    public static final int SUCCESS_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_dialog);
        this.setFinishOnTouchOutside(false);

    }

    public void setName(View view) {
        TextView nameField = findViewById(R.id.person_name_field);
        CharSequence name = nameField.getText();
        if (name.length() <= 0) {
            Toast.makeText(this, R.string.no_name, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("name", name.toString());

        setResult(SUCCESS_CODE, intent);
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
