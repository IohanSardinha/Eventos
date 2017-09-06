package br.com.sardinha.iohan.eventos;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
        if(list.get(position).getId().equals(user))
        {
            holder.userName.setText(list.get(position).getNome());
            holder.followButton.setVisibility(View.GONE);
        }
        else {
            holder.userName.setText(list.get(position).getNome());
            holder.followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    database.child(list.get(position).getId()).setValue(list.get(position).getId());
                    holder.followButton.setText("Seguindo");
                    holder.followButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    notifyDataSetChanged();

                }
            });
            database.child(list.get(position).getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(String.class) != null) {
                        holder.followButton.setText("Seguindo");
                        holder.followButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
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
        }
    }
}