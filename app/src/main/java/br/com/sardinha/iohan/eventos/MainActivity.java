package br.com.sardinha.iohan.eventos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    ListView eventos;
    ItemListaEventosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eventos = (ListView)findViewById(R.id.listaEventosMain);
        adapter = new ItemListaEventosAdapter(this,R.layout.item_lista_eventos);
        eventos.setAdapter(adapter);
        eventos.setOnItemClickListener(new ListItemClick());

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://api-eventos.herokuapp.com/users.json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(responseBody == null)
                {
                    Toast.makeText(MainActivity.this, "Sem resposta", Toast.LENGTH_SHORT).show();
                    return;
                }
                Type listType = new TypeToken<ArrayList<User>>(){}.getType();
                Gson gson = new Gson();
                List<User> l = gson.fromJson(new String(responseBody),listType);
                Toast.makeText(MainActivity.this, l.get(0).getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, "erro", Toast.LENGTH_SHORT).show();
            }
        });

        /*RequestParams params = new RequestParams();
        params.put("name","user");
        params.put("email","email");
        params.put("password","password");

        client.post("http://api-eventos.herokuapp.com/users", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(MainActivity.this, new String(responseBody), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, "erro", Toast.LENGTH_SHORT).show();
            }
        });*/

    }
    class ListItemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ViewGroup vg = (ViewGroup) view;
            ItemListaEventosAdapter tempAdapter = (ItemListaEventosAdapter)((ListView) parent).getAdapter();
            Evento evento = (Evento) tempAdapter.getItem(position);
            Intent intent = new Intent(MainActivity.this,DetalhesEventoActivity.class);
            intent.putExtra("evento",evento);
            startActivityForResult(intent,2);
        }
    }

    public void novoEventoClick(View view) {
        Intent intent = new Intent(this, NovoEventoActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            Evento evento = (Evento)data.getSerializableExtra("evento");
            DBHandler db = new DBHandler(MainActivity.this);
            db.addEvento(evento);
            adapter.add(db.getEvento(evento.getLimite()));
        }

    }

}
