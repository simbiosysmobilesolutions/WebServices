package com.simbiosyscorp.tutorials.webservices;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button toRest, toSoap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toRest = (Button) findViewById(R.id.toRest);
        toSoap = (Button) findViewById(R.id.toSoap);

        //To RestActivity
        toRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RestActivity.class));
            }
        });

        //To SOAP Activity
        toSoap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SOAPActivity.class));

            }
        });
    }

}
