package uci.team86.cs122bmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SearchActivity extends AppCompatActivity {
    EditText searchcontent ;
    Button searchbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchbutton = (Button)findViewById(R.id.buttonSearch);
        searchcontent =(EditText)findViewById(R.id.editTextSearch);

    }

    //OnClick
    public void doSearch(View view){
        Intent intent = new Intent(this,MovieListActivity.class);
        String content =  searchcontent.getText().toString();
        intent.putExtra("searchContent",content);
        startActivity(intent);
    }


}
