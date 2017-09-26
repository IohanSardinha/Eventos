package br.com.sardinha.iohan.eventos;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class TrocarSenhaActivity extends AppCompatActivity {

    private TextView emailTV;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trocar_senha);
        setTitle(R.string.redefinir_senha);
        emailTV = (TextView)findViewById(R.id.email_redefinir);
        auth = FirebaseAuth.getInstance();
    }

    public void enviarOnClick(View view) {
        String email = emailTV.getText().toString().trim();

        if(email.isEmpty())
        {
            Toast.makeText(this, R.string.insira_email, Toast.LENGTH_SHORT).show();
        }
        else
        {
            final ProgressDialog progress = ProgressDialog.show(this,"Um momento por favor","Enviando...",true);
            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        progress.dismiss();
                        new AlertDialog.Builder(TrocarSenhaActivity.this)
                                .setTitle("Enviado")
                                .setMessage("Email para redefinição de senha enviado")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                    else
                    {
                        Toast.makeText(TrocarSenhaActivity.this, R.string.email_invalido, Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                }
            });
        }
    }
}
