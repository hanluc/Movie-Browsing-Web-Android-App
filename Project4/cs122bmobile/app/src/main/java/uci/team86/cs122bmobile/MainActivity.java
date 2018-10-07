package uci.team86.cs122bmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "uci.team86.cs122b.MESSAGE";
    Button gotoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gotoLogin = (Button)findViewById(R.id.gotologin);
    }

    //OnClick
    public void gotoLoginPage(View view){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
}
