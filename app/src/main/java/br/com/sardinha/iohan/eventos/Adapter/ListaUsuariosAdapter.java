package br.com.sardinha.iohan.eventos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.sardinha.iohan.eventos.R;
import br.com.sardinha.iohan.eventos.Class.Usuario;
import br.com.sardinha.iohan.eventos.Activity.UsuarioActivity;

public class ListaUsuariosAdapter extends RecyclerView.Adapter<ListaUsuariosAdapter.ViewHolder> {

    private ArrayList<Usuario> list;
    Context context;
    DatabaseReference database;
    String user;

    public ListaUsuariosAdapter(ArrayList<Usuario> users, Context context) {
        list = users;
        this.context = context;
    }

    @Override
    public ListaUsuariosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuarios,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.userName.setText(list.get(position).getNome());
        if(list.get(position).getId().equals(user))
        {
            holder.followButton.setVisibility(View.GONE);
        }
        else {
            holder.followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    database.child(list.get(position).getId()).setValue(list.get(position));
                    holder.followButton.setText("Seguindo");
                    holder.followButton.setBackgroundColor(ContextCompat.getColor(context, R.color.buttonPressed));
                    holder.followButton.setTextColor(Color.WHITE);
                    notifyDataSetChanged();

                }
            });
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(list.get(position).getId())) {
                        holder.followButton.setText("Seguindo");
                        holder.followButton.setBackgroundColor(ContextCompat.getColor(context, R.color.buttonPressed));
                        holder.followButton.setTextColor(Color.WHITE);
                        holder.followButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                database.child(list.get(position).getId()).removeValue();
                                holder.followButton.setText("Seguir");
                                holder.followButton.setTextColor(Color.BLACK);
                                holder.followButton.setBackgroundColor(ContextCompat.getColor(context, R.color.button));
                                holder.followButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        database.child(list.get(position).getId()).setValue(list.get(position));
                                        holder.followButton.setText("Seguindo");
                                        holder.followButton.setTextColor(Color.WHITE);
                                        holder.followButton.setBackgroundColor(ContextCompat.getColor(context, R.color.buttonPressed));
                                        notifyDataSetChanged();

                                    }
                                });
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
        public ViewHolder(View itemView) {
            super(itemView);
            database = FirebaseDatabase.getInstance().getReference("Followings").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userName = (TextView)itemView.findViewById(R.id.nome_usuario_item);
            followButton = (Button)itemView.findViewById(R.id.seguir_usuario_item);
            user = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
}
