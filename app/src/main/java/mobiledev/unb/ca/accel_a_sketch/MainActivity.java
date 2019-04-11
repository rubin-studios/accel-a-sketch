package mobiledev.unb.ca.accel_a_sketch;

import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import static mobiledev.unb.ca.accel_a_sketch.R.id.btn_draw;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Started.");

        ImageButton btnStart = (ImageButton) findViewById(R.id.btn_draw);
        ImageButton btnSettings = (ImageButton) findViewById(R.id.btn_set);
        ConstraintLayout cl = (ConstraintLayout)findViewById(R.id.main_layout_id);
        cl.setBackgroundColor(Color.RED);
        
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked start button");
                openDrawActivity();

            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: cliecked settings bucckon");
                openSettingsActivity();

            }
        });

    }
    public void openDrawActivity(){
        Intent intent = new Intent(this, Draw.class);
        startActivity(intent);
    }
    public void openSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
