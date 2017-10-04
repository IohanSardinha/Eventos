package br.com.sardinha.iohan.eventos.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import br.com.sardinha.iohan.eventos.Adapter.ListaUsuariosAdapter;
import br.com.sardinha.iohan.eventos.Adapter.ListaUsuariosConvidadosAdapter;
import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.Class.OneShotClickListener;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.R;

public class DetalhesEventoActivity extends AppCompatActivity {
    private Evento evento;
    private Usuario user;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_evento);
        database = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        evento = (Evento) intent.getSerializableExtra("evento");
        database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(Usuario.class);
                Draw();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void Draw(){
        if(evento.getImagem() == null)
        {
            ((ImageView)findViewById(R.id.imagem_detalhes)).setVisibility(View.GONE);
        }
        else
        {
            Glide.with(getApplicationContext())
                    .load(Uri.parse(evento.getImagem()))
                    .asBitmap()
                    .placeholder(R.drawable.ic_file_download_black_24dp)
                    .into(new SimpleTarget<Bitmap>(1000,1000) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            ((ImageView) findViewById(R.id.imagem_detalhes)).setImageBitmap(resource);
                            (findViewById(R.id.progressBar4)).setVisibility(View.GONE);
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getDominantColor(resource)));
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

        ((TextView)findViewById(R.id.endereco_detalhes)).setText(evento.getEndereco());


        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.lista_usuarios_detalhes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        final ArrayList<Usuario> users = new ArrayList<>();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("UsersParticipating").child(evento.getId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    users.add(ds.getValue(Usuario.class));
                }
                if(users.size() <= 0)
                {
                    findViewById(R.id.ninguem_confirmou_detalhes).setVisibility(View.VISIBLE);
                }
                else
                {
                    findViewById(R.id.ninguem_confirmou_detalhes).setVisibility(View.GONE);
                    users.add(new Usuario("","Ver todos",""));
                }
                recyclerView.setAdapter(new ListaUsuariosConvidadosAdapter(users,DetalhesEventoActivity.this,user,evento));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final Button participate = (Button)findViewById(R.id.participar_evento_detalhes);
        if(evento.getUserID().equals(user.getId()))
        {
            participate.setVisibility(View.GONE);
        }
        else
        {
            final DatabaseReference userParticipating = FirebaseDatabase.getInstance().getReference("UsersParticipating").child(evento.getId());
            final DatabaseReference eventsParticipating = FirebaseDatabase.getInstance().getReference("EventsParticipating").child(user.getId()).child(evento.getId());
            userParticipating.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(user.getId()) && (evento.getLimite()-dataSnapshot.getChildrenCount() > 0 || evento.getLimite() == -1))
                    {
                        participate.setText("Participar");
                        participate.setBackgroundResource(R.color.button);
                        participate.setTextColor(Color.BLACK);
                        participate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                eventsParticipating.setValue(evento);
                                userParticipating.child(user.getId()).setValue(user);
                            }
                        });
                    }
                    else if(!dataSnapshot.hasChild(user.getId()) && evento.getLimite()-dataSnapshot.getChildrenCount() <= 0 && evento.getLimite() != -1)
                    {
                        participate.setText("Lotado");
                        participate.setBackgroundResource(R.color.button);
                        participate.setTextColor(Color.BLACK);
                        participate.setOnClickListener(null);
                    }
                    else
                    {
                        participate.setText("Participando");
                        participate.setTextColor(Color.WHITE);
                        participate.setBackgroundResource(R.color.buttonPressed);
                        participate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                eventsParticipating.removeValue();
                                userParticipating.child(user.getId()).removeValue();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            ((TextView)findViewById(R.id.Perticiparao)).setOnClickListener(new OneShotClickListener() {
                @Override
                public void performClick(View v) {
                    Intent intent = new Intent(DetalhesEventoActivity.this, ConvidadosActivity.class);
                    intent.putExtra("evento",evento);
                    intent.putExtra("usuario",user);
                    intent.putExtra("confirmados",true);
                    startActivity(intent);
                }
            });

        }

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
                                StorageReference storage = FirebaseStorage.getInstance().getReference("Events").child(evento.getId());
                                storage.delete();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Events").child(evento.getId());
                                reference.removeValue();
                                startActivity(new Intent(DetalhesEventoActivity.this,EventosActivity.class));
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
