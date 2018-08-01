package aspire2droneteam.com.dronescanningapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.loadLibrary("openCV_java3");
        setContentView(R.layout.activity_main);
    }
}
