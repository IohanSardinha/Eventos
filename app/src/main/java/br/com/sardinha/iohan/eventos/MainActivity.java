package br.com.sardinha.iohan.eventos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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
