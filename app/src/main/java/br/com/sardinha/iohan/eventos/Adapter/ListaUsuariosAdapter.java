package br.com.sardinha.iohan.eventos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.sardinha.iohan.eventos.Class.Evento;
import br.com.sardinha.iohan.eventos.Class.NotificationSender;
import br.com.sardinha.iohan.eventos.R;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.Activity.UsuarioActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class ListaUsuariosAdapter extends RecyclerView.Adapter<ListaUsuariosAdapter.ViewHolder> {

    private ArrayList<Usuario> list;
    Context context;
    DatabaseReference database;
    Usuario currentUser;

    public ListaUsuariosAdapter(ArrayList<Usuario> users, Context context,Usuario currentUser) {
        list = users;
        this.currentUser = currentUser;
        this.context = context;
    }

    @Override
    public ListaUsuariosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuarios,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.userName.setText(list.get(position).getNome());
        if(!list.get(position).getImagem().isEmpty())
        {
            Glide.with(context).load(Uri.parse(list.get(position).getImagem())).into(holder.userPhotoCirc);

        }
        if(list.get(position).getId().equals(currentUser.getId()))
        {
            holder.followButton.setVisibility(View.GONE);
        }
        else {
            database.child(list.get(position).getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        holder.followButton.setText(R.string.seguindo);
                        holder.followButton.setBackgroundColor(ContextCompat.getColor(context, R.color.buttonPressed));
                        holder.followButton.setTextColor(Color.WHITE);
                        holder.followButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                database.child(list.get(position).getId()).removeValue();
                            }
                        });
                    }
                    else
                    {
                        holder.followButton.setText(R.string.seguir);
                        holder.followButton.setTextColor(Color.BLACK);
                        holder.followButton.setBackgroundColor(ContextCompat.getColor(context, R.color.button));
                        holder.followButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                database.child(list.get(position).getId()).setValue(list.get(position));
                                new NotificationSender().sendFollowMessage(context,null,list.get(position),currentUser);
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        Button followButton;
        CircleImageView userPhotoCirc;
        public ViewHolder(View itemView) {
            super(itemView);
            database = FirebaseDatabase.getInstance().getReference("Followings").child(currentUser.getId());
            userName = (TextView)itemView.findViewById(R.id.nome_usuario_item);
            followButton = (Button)itemView.findViewById(R.id.seguir_usuario_item);
            userPhotoCirc = (CircleImageView)itemView.findViewById(R.id.foto_usuario_item_circ);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,UsuarioActivity.class);
                    intent.putExtra("Usuario",list.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }

    public void setFilter(ArrayList<Usuario> newList)
    {
        list = new ArrayList<>();
        list.addAll(newList);
        notifyDataSetChanged();
    }
}
