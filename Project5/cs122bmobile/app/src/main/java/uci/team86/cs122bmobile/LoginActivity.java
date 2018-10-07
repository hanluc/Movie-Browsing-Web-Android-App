package uci.team86.cs122bmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.password);

        signIn = (Button) findViewById(R.id.signin);

    }

    //OnClick [button signIn]: sign in function
    public void doSignIn(View view){

        final Map<String,String> params = new HashMap<String,String>();

        //--get basic infomation from UI
        String uname = username.getText().toString();
        String pwd = password.getText().toString();
        params.put("username",uname);
        params.put("password",pwd);
        Log.d("username",uname);
        Log.d("password",pwd);

        //--get http connections
        //----1. get the queue
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        //----2.declare post request& callback functions
        final boolean[] respSuccess = new boolean[1];
        //final Boolean resp = new Boolean(false);
        Log.d("params",new JSONObject(params).toString());
        //JSONObject parameters = new JSONObject(params);
//        final JsonObjectRequest loginRequestbyJson =  new JsonObjectRequest(Request.Method.POST,
//                "http://10.0.2.2:8080/Project2/android-login",
//                parameters,  //parmas converted to jsonobject
//                new Response.Listener<JSONObject>()
//                {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("response",response.toString());
//                        String status = "";
//                        try {
//                            status = response.getString("status");
//                        }catch(Exception e){
//                            e.printStackTrace();
//                        }
//                        if(status.equals("success")){
//                            Toast.makeText(getApplicationContext(), "login success!", Toast.LENGTH_SHORT).show();
//                            jumptoMovieList(true);
//                        }
//                        else{
//                            Log.d("servlet.error", response.toString());
//                            Toast.makeText(getApplicationContext(), "login failed!Please check network and try again!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("security.error", error.toString());
//                        Toast.makeText(getApplicationContext(), "login failed!Please check network and try again!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                );

        final StringRequest loginRequest =  new StringRequest(Request.Method.POST,
                //"http://10.0.2.2:8080/Project2/android-login",
                "https://54.153.65.169:8443/Project4/android-login",
                //parameters,  //parmas converted to jsonobject
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response",response.toString());
                        String status = "";
                        try {
                            JSONObject result = new JSONObject(response.toString());
                            status = result.getString("status");
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        if(status.equals("success")){
                            Toast.makeText(getApplicationContext(), "login success!", Toast.LENGTH_SHORT).show();
                            jumptoMovieList(true);
                        }
                        else{
                            Log.d("servlet.error", response.toString());
                            Toast.makeText(getApplicationContext(), "login failed!The user do not exist or the password is wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.d("security.error", error.toString());
                        Toast.makeText(getApplicationContext(), "login failed!Please check network and try again!", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            //set params in the function
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };



        //----3.put the request into the queue
        queue.add(loginRequest);

    }

    //callback for login: jump to another activity after successfully
    public void jumptoMovieList(boolean result){
        Log.d("request result",result?"true":"false");
        if(result){
            Intent intent = new Intent(this,MovieListActivity.class);
            intent.putExtra("searchContent","a");
            startActivity(intent);

        }
    }


}
