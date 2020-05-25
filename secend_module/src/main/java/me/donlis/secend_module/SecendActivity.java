package me.donlis.secend_module;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import me.donlis.annotation.Router;

@Router(path = "/module/secend")
public class SecendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secend);
    }
}
