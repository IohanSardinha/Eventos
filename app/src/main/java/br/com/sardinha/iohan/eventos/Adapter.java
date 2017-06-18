package br.com.sardinha.iohan.eventos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    ArrayList<Evento> list;
    Context context;
    public Adapter(ArrayList<Evento> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_child,parent,false));
    }

    @Override
    public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
        holder.info.setText(String.valueOf(list.get(position).getTitulo()));
        holder.description.setText(String.valueOf(list.get(position).getDescricao()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView info;
        TextView description;
        public ViewHolder(View itemView) {
            super(itemView);
            info = (TextView)itemView.findViewById(R.id.info_text);
            description = (TextView)itemView.findViewById(R.id.info_text2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context,DetalhesEventoActivity.class);
            intent.putExtra("evento",list.get(getAdapterPosition()));
            context.startActivity(intent);
        }
    }
}
