package com.aracroproducts.attention;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class MainActivity extends AppCompatActivity {

    public static final String USER_INFO = "user";
    public static final String FRIENDS = "listen";

    public static final int NAME_CALLBACK  = 0;
    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(this.getClass().getName(), "Attempting to add");
                Intent intent = new Intent(view.getContext(), Add.class);
                startActivity(intent);
            }
        });

        SharedPreferences prefs = getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        if (!prefs.contains("name") || !prefs.contains("id")) {
            Intent intent = new Intent(this, DialogActivity.class);
            startActivityForResult(intent, NAME_CALLBACK);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NAME_CALLBACK:
                SharedPreferences prefs = getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("name", data.getStringExtra("name"));
                editor.putString("id", makeId(data.getStringExtra("name")));
                editor.apply();
                break;
            /*case CUSTOMIZED_REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("MainActivity", "Cancelled scan");
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("MainActivity", "Scanned");
                    String friendTag = result.getContents();
                    String friendName = friendTag.substring(friendTag.indexOf(' ') + 1);
                    String  friendId = friendTag.substring(0, friendTag.indexOf(' ') + 1);
                    SharedPreferences friends = getSharedPreferences(FRIENDS, Context.MODE_PRIVATE);
                    HashSet<String> friendIdSet = new HashSet<>(friends.getStringSet("ids", new HashSet<String>()));
                    HashSet<String> friendNameSet = new HashSet<>(friends.getStringSet("names", new HashSet<String>()));
                    friendIdSet.add(friendId);
                    friendNameSet.add(friendName);
                    SharedPreferences.Editor editor1 = friends.edit();
                    editor1.putStringSet("ids", friendIdSet);
                    editor1.putStringSet("names", friendNameSet);
                    editor1.apply();
                }

             */
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String makeId(String name) {
        String fullString = name + Build.FINGERPRINT;
        byte[] salt = {69, 42, 0, 37, 10, 127, 34, 85, 83, 24, 98, 75, 49, 8, 67};

        try {
            SecretKeyFactory secretKeyFactory;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512"); //not available to Android 7.1 and lower
            } else {
                secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            }
            PBEKeySpec spec = new PBEKeySpec(fullString.toCharArray(), salt, 32, 64);
            SecretKey key = secretKeyFactory.generateSecret(spec);
            byte[] hashed = key.getEncoded();
            StringBuilder builder = new StringBuilder();
            for (byte letter : hashed) {
                builder.append(letter);
            }
            return builder.toString();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences friends = getSharedPreferences(FRIENDS, Context.MODE_PRIVATE);
        HashSet<String> friendNameSet = new HashSet<>(friends.getStringSet("names", new HashSet<String>()));

        RecyclerView friendList = findViewById(R.id.friends_list);
        friendList.setLayoutManager(new LinearLayoutManager(this));

        String[] dataset = new String[friendNameSet.size()];
        if (!friendNameSet.isEmpty()) {
            int position = 0;
            for (String name: friendNameSet) {
                dataset[position] = name;
                position++;
            }
        }
        FriendAdapter adapter = new FriendAdapter(dataset);
        friendList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}