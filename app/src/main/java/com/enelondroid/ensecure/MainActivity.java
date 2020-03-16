package com.enelondroid.ensecure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {

    private ImageButton mainBut;
    private TextView endisText, butText, intrusionCountText;
    private FirebaseDatabase database;
    private DatabaseReference nodemcu, secEnabled, intrusionCount, userToken;
    private Boolean isSecEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainBut = findViewById(R.id.main_but);
        endisText = findViewById(R.id.endistext);
        butText = findViewById(R.id.but_text);
        intrusionCountText = findViewById(R.id.intr_count);

        database = FirebaseDatabase.getInstance();

        nodemcu = database.getReference("nodemcu");
        secEnabled = database.getReference("secEnabled");
        intrusionCount = database.getReference("intrusionCount");
        userToken = database.getReference("userToken");


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Failed to get token", Toast.LENGTH_SHORT).show();
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        userToken.setValue(token);
                    }
                });


        intrusionCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                intrusionCountText.setText(String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Synchronization error",
                        Toast.LENGTH_LONG).show();
            }
        });

        secEnabled.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (String.valueOf(dataSnapshot.getValue()).equals("2")) {
                    isSecEnabled = true;
                    mainBut.setColorFilter(Color.rgb(255, 0, 0));
                    endisText.setText("Security Enabled");
                    endisText.setTextColor(Color.parseColor("#FF0000"));
                    butText.setText("Disable Security");
                    butText.setTextColor(Color.parseColor("#FF0000"));
                } else {
                    isSecEnabled = false;
                    mainBut.setColorFilter(null);
                    endisText.setText("Security Disabled");
                    endisText.setTextColor(Color.parseColor("#FFFFFF"));
                    butText.setText("Enable Security");
                    butText.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Synchronization error",
                        Toast.LENGTH_LONG).show();
            }
        });

        mainBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSecEnabled) {
                    secEnabled.setValue(2);
                    intrusionCount.setValue(0);
                } else {
                    secEnabled.setValue(1);
                }
            }
        });
    }
}
