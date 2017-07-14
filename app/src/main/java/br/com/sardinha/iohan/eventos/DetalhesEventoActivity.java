package br.com.sardinha.iohan.eventos;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

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
        if(evento.getImagem() == null)
        {
            ((ImageView)findViewById(R.id.imagem_detalhes)).setVisibility(View.GONE);
        }
        else
        {
            Picasso.with(this).load(Uri.parse(evento.getImagem())).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    ((ImageView)findViewById(R.id.imagem_detalhes)).setImageBitmap(bitmap);
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getDominantColor(bitmap)));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    System.out.println("ERRO");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
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


        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.lista_usuarios_detalhes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ArrayList<Usuario> users = new ArrayList<>();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Participations").child(evento.getId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    DatabaseReference ref = database.getReference("Users").child(ds.getValue(String.class));
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            users.add(dataSnapshot.getValue(Usuario.class));
                            recyclerView.setAdapter(new ListaUsuariosAdapter(users,DetalhesEventoActivity.this));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(evento.getUserID().equals(userId))
        {
            getMenuInflater().inflate(R.menu.menu_detalhes, menu);
        }
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
                new AlertDialog.Builder(this).setTitle("Evento será cancelado")
                        .setMessage("Tem certeza que quer cancelar o evento?")
                        .setNegativeButton("Não",null)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String USER_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                StorageReference storage = FirebaseStorage.getInstance().getReference("Events").child(evento.getId());
                                storage.delete();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Events").child(USER_ID).child(evento.getId());
                                reference.removeValue();
                                finish();
                            }
                        }).show();
            default:
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

    public static int getDominantColor(Bitmap bitmap) {
        if (bitmap == null) {
            return Color.TRANSPARENT;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int pixels[] = new int[size];
        //Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int color;
        int r = 0;
        int g = 0;
        int b = 0;
        int a;
        int count = 0;
        for (int i = 0; i < pixels.length; i++) {
            color = pixels[i];
            a = Color.alpha(color);
            if (a > 0) {
                r += Color.red(color);
                g += Color.green(color);
                b += Color.blue(color);
                count++;
            }
        }
        r /= count;
        g /= count;
        b /= count;
        r = (r << 16) & 0x00FF0000;
        g = (g << 8) & 0x0000FF00;
        b = b & 0x000000FF;
        color = 0xFF000000 | r | g | b;
        return color;
    }

}
