package br.com.sardinha.iohan.eventos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = (ProgressBar)findViewById(R.id.progressBar1);
        progress.setVisibility(View.GONE);
    }

    public void login(View view) {


        AsyncHttpClient client = new AsyncHttpClient();
        progress.setVisibility(View.VISIBLE);
        final String email = ((EditText)findViewById(R.id.email)).getText().toString();
        final String pasword = ((EditText)findViewById(R.id.password)).getText().toString();
        client.get("http://api-eventos.herokuapp.com/users", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progress.setVisibility(View.GONE);
                Type listType = new TypeToken<List<User>>() {}.getType();
                List<User> list;
                Gson gson = new Gson();
                list = gson.fromJson(new String(responseBody),listType);
                for (User user:list){
                    System.out.println(user.getEmail());
                    System.out.println(user.getEmail());
                    if(user.getEmail().equals(email) && user.getPassword().equals(pasword))
                    {
                        ((TextView)findViewById(R.id.result)).setText(user.getName());
                    }
                    else{
                        ((TextView)findViewById(R.id.result)).setText("Email ou senha incorretos");
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ((TextView)findViewById(R.id.result)).setText("Erro");
                progress.setVisibility(View.GONE);
            }
        });
    }
}
