package br.com.sardinha.iohan.eventos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetalhesEventoActivity extends AppCompatActivity {
    Evento evento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_evento);
        Intent intent = getIntent();
        evento = (Evento) intent.getSerializableExtra("evento");
        Draw();
    }

    void Draw(){
        if(evento.getImagem() == -1)
        {
            ((ImageView)findViewById(R.id.imagem_detalhes)).setVisibility(View.GONE);
        }
        else
        {
            ((ImageView)findViewById(R.id.imagem_detalhes)).setImageResource(evento.getImagem());
        }

        this.setTitle(evento.getTitulo());

        if(evento.getDataInicio().equals(evento.getDataEncerramento()))
        {
            ((TextView)findViewById(R.id.data_detalhes)).setText(getString(R.string.dia)+" "+evento.getDataInicio() + " " + getString(R.string.de) + " " + evento.getHoraInicio() + " " + getString(R.string.as) + " " + evento.getHoraEncerramento());
        }
        else if(evento.getDataEncerramento().isEmpty())
        {
            ((TextView)findViewById(R.id.data_detalhes)).setText(getString(R.string.dia)+" "+evento.getDataInicio() + " " + getString(R.string.as) + " " + evento.getHoraInicio());
        }
        else
        {
            ((TextView)findViewById(R.id.data_detalhes)).setText(getString(R.string.dia)+" "+evento.getDataInicio() + " " + getString(R.string.as) + " " + evento.getHoraInicio() + "\nà\n" + evento.getDataEncerramento() + " " + getString(R.string.as) + " " + evento.getHoraEncerramento());
        }

        ((TextView)findViewById(R.id.privacidade_detalhes)).setText(getString(R.string.evento)+" "+evento.getPrivacidade());
        if(evento.getLimite() == -1)
        {
            ((TextView)findViewById(R.id.limite_detalhes)).setText("");
        }
        else
        {
            ((TextView)findViewById(R.id.limite_detalhes)).setText(getString(R.string.limite_de_convidados)+":\n"+String.valueOf(evento.getLimite()));
        }
        ((TextView)findViewById(R.id.descricao_detalhes)).setText(evento.getDescricao());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_detalhes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_action:
                Intent intent = new Intent(this, NovoEventoActivity.class);
                intent.putExtra("evento",evento);
                startActivityForResult(intent,1);
                return true;
            case R.id.delete_action:
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Events").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(evento.getId());
                reference.removeValue();
                finish();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            Evento e = (Evento)data.getSerializableExtra("evento");
            evento = e;
            Draw();

        }

    }

}
