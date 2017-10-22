package br.com.sardinha.iohan.eventos.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.sardinha.iohan.eventos.Class.Comentario;
import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.R;

public class NovoComentarioActivity extends AppCompatActivity {

    Evento evento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_comentario);
        evento = (Evento)getIntent().getSerializableExtra("Evento");
    }

    public void backOnClick(View view) {
        finish();
    }

    public void enviarOnClick(View view) {
        String commentText = ((TextView)findViewById(R.id.caixa_comentário)).getText().toString();
        if(commentText.isEmpty())
        {
            Toast.makeText(this, "Comentário não pode ficar em branco!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments").child(evento.getId());
            String id = ref.push().getKey();
            Comentario comentario = new Comentario(id,uID,commentText);
            final ProgressDialog progressDialog = ProgressDialog.show(this,"","Enviando...",true);
            ref.child(id).setValue(comentario).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    finish();
                }
            });
        }
    }
}
