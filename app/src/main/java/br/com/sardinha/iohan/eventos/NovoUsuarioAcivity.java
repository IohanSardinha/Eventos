package br.com.sardinha.iohan.eventos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class NovoUsuarioAcivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference database;
    private Uri image;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_usuario_acivity);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Users");
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
        else if(true) {
            progress = ProgressDialog.show(this,"","Registrando",true);
            final StorageReference reference = FirebaseStorage.getInstance().getReference("Users");
            auth.createUserWithEmailAndPassword(email,senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(NovoUsuarioAcivity.this, "Usuario "+nome+" registrado", Toast.LENGTH_SHORT).show();
                        String id = auth.getCurrentUser().getUid();
                        database.child(id).setValue(new Usuario(id,nome,email.toLowerCase()));
                        reference.child(id).putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                finish();
                                progress.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(NovoUsuarioAcivity.this, "Erro no upload de imagem", Toast.LENGTH_SHORT).show();
                                finish();
                                progress.dismiss();
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(NovoUsuarioAcivity.this, "Não foi possivel registrar usuário", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else
        {
            Toast.makeText(this, "Insira uma foto", Toast.LENGTH_SHORT).show();
        }
    }

    public void selecionarFotoPerfil(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == RESULT_OK)
        {
            image = data.getData();
            ((ImageView)findViewById(R.id.fotoPerfilCriacao)).setImageURI(image);
        }
    }
}
