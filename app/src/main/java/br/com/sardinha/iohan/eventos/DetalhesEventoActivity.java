package br.com.sardinha.iohan.eventos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetalhesEventoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_evento);
        Intent intent = getIntent();
        Evento evento = (Evento) intent.getSerializableExtra("evento");

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
}
