package br.com.sardinha.iohan.eventos.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.R;

public class NovoUsuarioAcivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference database;
    private Uri image;
    private Usuario user;
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
        final String senha = ((EditText)findViewById(R.id.senha_registro)).getText().toString();
        String confSenha = ((EditText)findViewById(R.id.confirmar_senha_registro)).getText().toString();
        if(nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confSenha.isEmpty())
        {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
        }
        else if(!senha.equals(confSenha))
        {
            Toast.makeText(this, "Senhas não batem", Toast.LENGTH_SHORT).show();
        }
        else
        {
            progress = ProgressDialog.show(this,"","Registrando",true);
            auth.createUserWithEmailAndPassword(email,senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(NovoUsuarioAcivity.this, "Usuario "+nome+" registrado", Toast.LENGTH_SHORT).show();
                        final String id = task.getResult().getUser().getUid();
                        user = new Usuario(id,nome,email);
                        database.child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progress.dismiss();
                                new AlertDialog.Builder(NovoUsuarioAcivity.this)
                                        .setTitle("Foto de perfil")
                                        .setMessage("Gostaria de adicionar uma foto de perfil agora?")
                                        .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(Intent.ACTION_PICK);
                                                intent.setType("image/*");
                                                startActivityForResult(intent,2);
                                            }
                                        })
                                        .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(NovoUsuarioAcivity.this, "Algo deu errado", Toast.LENGTH_SHORT).show();
                            }
                        });
//                        if(image != null)
//                        {
//                            reference.child(id).putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                    user.setImagem(taskSnapshot.getDownloadUrl().toString());
//                                    database.child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            finish();
//                                            progress.dismiss();
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(NovoUsuarioAcivity.this, "Algo deu errado", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(NovoUsuarioAcivity.this, "Erro no upload de imagem", Toast.LENGTH_SHORT).show();
//                                            finish();
//                                            progress.dismiss();
//                                        }
//                                    });
//                        }
//                        else
//                        {
//
//                        }
                    }
                    else if(senha.length() < 8)
                    {
                        Toast.makeText(NovoUsuarioAcivity.this, "Senha deve ser maior que oito caracteres", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                    else if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        progress.dismiss();
                        new AlertDialog.Builder(NovoUsuarioAcivity.this)
                                .setTitle("Email já está registrado")
                                .setMessage("Esqueceu sua senha?")
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(NovoUsuarioAcivity.this,TrocarSenhaActivity.class));
                                        finish();
                                    }
                                })
                                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .show();
                    }
                    else
                    {
                        Toast.makeText(NovoUsuarioAcivity.this, "Não foi possível registrar o usuário, tente mais tarde", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == RESULT_OK)
        {
            progress = ProgressDialog.show(this,"","Salvando",true);
            image = data.getData();
            StorageReference reference = FirebaseStorage.getInstance().getReference("Users").child(user.getId());
            reference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    user.setImagem(taskSnapshot.getDownloadUrl().toString());
                    database.child(user.getId()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            finish();
                            progress.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NovoUsuarioAcivity.this, "Algo deu errado", Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }
                    });
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
    }

    public void backOnClick(View view) {
        finish();
    }
}
