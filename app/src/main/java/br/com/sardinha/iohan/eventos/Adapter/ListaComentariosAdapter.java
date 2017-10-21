package br.com.sardinha.iohan.eventos.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.sardinha.iohan.eventos.Class.Comentario;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ListaComentariosAdapter extends RecyclerView.Adapter<ListaComentariosAdapter.ViewHolder>{

    private ArrayList<Comentario> list;
    private DatabaseReference userReference;
    private Context context;

    public ListaComentariosAdapter(ArrayList<Comentario> list, Context context)
    {
        this.list = list;
        this.context = context;
    }

    @Override
    public ListaComentariosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario,parent,false));
    }

    @Override
    public void onBindViewHolder(final ListaComentariosAdapter.ViewHolder holder, int position)
    {
        holder.comentario.setText(list.get(position).getComentario());
        userReference.child(list.get(position).getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario user = dataSnapshot.getValue(Usuario.class);
                holder.nomeUsuario.setText(user.getNome());
                if(user.getImagem() != null)
                {
                    Glide.with(context).load(user.getImagem()).into(holder.fotoPerfil);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView fotoPerfil;
        TextView nomeUsuario;
        TextView comentario;

        public ViewHolder(View itemView)
        {
            super(itemView);
            fotoPerfil = (CircleImageView)itemView.findViewById(R.id.foto_perfil_comentario);
            nomeUsuario = (TextView)itemView.findViewById(R.id.nome_usuario_comentario);
            comentario = (TextView)itemView.findViewById(R.id.texto_comentario);
            userReference = FirebaseDatabase.getInstance().getReference("Users");
        }


    }
}

