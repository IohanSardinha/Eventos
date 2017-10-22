package br.com.sardinha.iohan.eventos.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import br.com.sardinha.iohan.eventos.Adapter.ListaComentariosAdapter;
import br.com.sardinha.iohan.eventos.Adapter.ListaUsuariosConvidadosAdapter;
import br.com.sardinha.iohan.eventos.Class.Comentario;
import br.com.sardinha.iohan.eventos.Class.Evento;
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
        final Intent intent = getIntent();
        try
        {
            evento = (Evento) intent.getSerializableExtra("evento");
            if(getSupportActionBar() != null)
            {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
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
        }catch (Exception ex)
        {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    evento = (Evento) intent.getSerializableExtra("evento");
                    if(getSupportActionBar() != null)
                    {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setDisplayShowHomeEnabled(true);
                    }
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
            },200);
        }


    }

    void Draw(){
        if(evento.getImagem() == null)
        {
            ((ImageView)findViewById(R.id.imagem_detalhes)).setVisibility(View.GONE);
        }
        else
        {
            Glide.with(getApplicationContext())
                    .load(Uri.parse(evento.getImagemLow()))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            ((ImageView) findViewById(R.id.imagem_detalhes)).setImageBitmap(resource);
                            //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getDominantColor(resource)));
                            Glide.with(getApplicationContext())
                                    .load(Uri.parse(evento.getImagem()))
                                    .asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.ic_file_download_black_24dp)
                                    .into(new SimpleTarget<Bitmap>(1000,1000) {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                            ((ImageView) findViewById(R.id.imagem_detalhes)).setImageBitmap(resource);
                                            //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getDominantColor(resource)));
                                            findViewById(R.id.progressBar4).setVisibility(View.GONE);
                                        }
                                    });
                        }
                    });
        }

        this.setTitle("");

        ((TextView)findViewById(R.id.titulo_detalhes)).setText(evento.getTitulo());

        String data = evento.getDataInicio();
        List<String> tempDataInicio = Arrays.asList(data.split("/"));

        ((TextView)findViewById(R.id.dia_detalhes)).setText(tempDataInicio.get(0));
        ((TextView)findViewById(R.id.mes_detalhes)).setText(getStringMonth(tempDataInicio.get(1)));
        if(!String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).equals(tempDataInicio.get(2)))
        {
            ((TextView)findViewById(R.id.ano_detalhes)).setText(tempDataInicio.get(2));
        }

        TextView hora = ((TextView)findViewById(R.id.hora_detalhes));
        if(!evento.getHoraEncerramento().isEmpty())
        {
            if(evento.getDataEncerramento().equals(evento.getDataInicio()))
            {
                hora.setText(evento.getHoraInicio() +" "+ getString(R.string.as) +" "+ evento.getHoraEncerramento());
            }
            else
            {
                hora.setText(evento.getHoraInicio() +" "+ getString(R.string.a) +" "+ evento.getDataEncerramento() +" "+ getString(R.string.as) +" "+ evento.getHoraEncerramento() );
            }
        }
        else
        {
            hora.setText(evento.getHoraInicio());
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
        Query reference = database.getReference("UsersParticipating").child(evento.getId()).limitToFirst(4);
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
                    users.add(new Usuario("",getString(R.string.ver_todos),""));
                }
                recyclerView.setAdapter(new ListaUsuariosConvidadosAdapter(users,DetalhesEventoActivity.this,user,evento));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final RecyclerView recyclerViewComentarios = (RecyclerView)findViewById(R.id.lista_discussao_detalhes);
        recyclerViewComentarios.setHasFixedSize(true);
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        final ArrayList<Comentario> comentarios = new ArrayList<>();
        DatabaseReference commentsReference = database.getReference("Comments").child(evento.getId());
        commentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comentarios.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    comentarios.add(ds.getValue(Comentario.class));
                }
                recyclerViewComentarios.setAdapter(new ListaComentariosAdapter(comentarios,DetalhesEventoActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final View participate = findViewById(R.id.participarButton_detalhes);
        final ImageView participateImage= (ImageView)findViewById(R.id.imagemParticipandodetalhes);
        final TextView participateText = (TextView)findViewById(R.id.textoParticipandodetalhes);
        if(!evento.getUserID().equals(user.getId()))
        {
            participate.setVisibility(View.VISIBLE);
            final DatabaseReference userParticipating = FirebaseDatabase.getInstance().getReference("UsersParticipating").child(evento.getId());
            final DatabaseReference eventsParticipating = FirebaseDatabase.getInstance().getReference("EventsParticipating").child(user.getId()).child(evento.getId());
            userParticipating.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(user.getId()) && (evento.getLimite()-dataSnapshot.getChildrenCount() > 0 || evento.getLimite() == -1))
                    {
                        participateImage.setImageResource(R.drawable.calendar_plus);
                        participateImage.setColorFilter(ContextCompat.getColor(DetalhesEventoActivity.this,R.color.button));
                        participateText.setText(R.string.ir);
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
                        participateImage.setImageResource(R.drawable.ic_event_busy_black_24dp);
                        participateImage.setColorFilter(Color.BLACK);
                        participateText.setText(R.string.lotado);
                        participate.setOnClickListener(null);
                    }
                    else
                    {
                        participateImage.setImageResource(R.drawable.ic_event_available_black_24dp);
                        participateImage.setColorFilter(Color.BLACK);
                        participateText.setText(R.string.confirmado);
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

        }

    }

    public void participaraoOnClick(View view) {
        Intent intent = new Intent(DetalhesEventoActivity.this, ConvidadosActivity.class);
        intent.putExtra("evento",evento);
        intent.putExtra("usuario",user);
        intent.putExtra("confirmados",true);
        startActivity(intent);
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
                new AlertDialog.Builder(this).setTitle(R.string.evento_sera_cancelado)
                        .setMessage(R.string.certeza_que_quer_cancelar)
                        .setNegativeButton(R.string.nao,null)
                        .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
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
                return true;
            case android.R.id.home:
                finish();
                return true;
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

    private String getStringMonth(String month)
    {
        String[] meses = getResources().getStringArray(R.array.meses);
        switch (month)
        {
            case "01":
                return meses[0];
            case "02":
                return meses[1];
            case "03":
                return meses[2];
            case "04":
                return meses[3];
            case "05":
                return meses[4];
            case "06":
                return meses[5];
            case "07":
                return meses[6];
            case "08":
                return meses[7];
            case "09":
                return meses[8];
            case "10":
                return meses[9];
            case "11":
                return meses[10];
            default:
                return meses[11];
        }
    }

    public void comentarOnClick(View view) {
        Intent intent = new Intent(this, NovoComentarioActivity.class);
        intent.putExtra("Evento",evento);
        startActivity(intent);
    }
}
