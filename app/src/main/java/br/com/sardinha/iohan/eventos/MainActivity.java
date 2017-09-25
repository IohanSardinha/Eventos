package br.com.sardinha.iohan.eventos;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = (ProgressBar)findViewById(R.id.progressBar1);
        progress.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null)
                {
                    //Toast.makeText(MainActivity.this, "Logado com: "+user.getEmail(), Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                    startActivity(new Intent(MainActivity.this,EventosActivity.class));
                    ((EditText)findViewById(R.id.email)).setText("");
                    ((EditText)findViewById(R.id.password)).setText("");
                    DatabaseReference notificationGroupReference = FirebaseDatabase.getInstance().getReference("NotificationGroups").child(user.getUid());
                    notificationGroupReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
                            for(DataSnapshot ds:dataSnapshot.getChildren())
                            {
                                firebaseMessaging.subscribeToTopic(ds.getKey()+"-WhenCreateEvent");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    //Toast.makeText(MainActivity.this, "Saiu", Toast.LENGTH_SHORT).show();
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null)
        {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    public void login(View view) {
        progress.setVisibility(View.VISIBLE);
        String email = ((EditText)findViewById(R.id.email)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();
        if(!email.isEmpty() && !password.isEmpty())
        {
            auth.signInWithEmailAndPassword(email,password);
        }
        else
        {
            Toast.makeText(this, "Preencha os campos", Toast.LENGTH_SHORT).show();
        }
    }

    public void novoUsuario(View view) {
        startActivity(new Intent(MainActivity.this,NovoUsuarioAcivity.class));
    }
}
