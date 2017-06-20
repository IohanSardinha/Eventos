package br.com.sardinha.iohan.eventos;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NovoUsuarioAcivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference database;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_usuario_acivity);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Users");
        progress = (ProgressBar)findViewById(R.id.progressBar3);
    }

    public void registrar(View view) {
        final String nome = ((EditText)findViewById(R.id.nome_registro)).getText().toString();
        final String email = ((EditText)findViewById(R.id.email_registro)).getText().toString();
        String senha = ((EditText)findViewById(R.id.senha_registro)).getText().toString();
        String confSenha = ((EditText)findViewById(R.id.confirmar_senha_registro)).getText().toString();
        if(nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confSenha.isEmpty())
        {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
        }
        else if(!senha.equals(confSenha))
        {
            Toast.makeText(this, "Senhas não batem", Toast.LENGTH_SHORT).show();
        }
        else {
            progress.setVisibility(View.VISIBLE);
            auth.createUserWithEmailAndPassword(email,senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(NovoUsuarioAcivity.this, "Usuario "+nome+" registrado", Toast.LENGTH_SHORT).show();
                        String id = auth.getCurrentUser().getUid();
                        database.child(id).setValue(new Usuario(id,nome,email.toLowerCase()));
                        finish();
                        progress.setVisibility(View.GONE);
                    }
                    else
                    {
                        Toast.makeText(NovoUsuarioAcivity.this, "Não foi possivel registrar usuário", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
