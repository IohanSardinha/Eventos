package br.com.sardinha.iohan.eventos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iohan.soares on 24/02/2017.
 */

public class ItemListaEventosAdapter extends ArrayAdapter{

    List list = new ArrayList();

    public ItemListaEventosAdapter(Context context, int resource)
    {
        super(context, resource);
    }

    static class DataHandler
    {
        TextView titulo;
        TextView data;
        TextView hora;
        ImageView imagem;
    }

    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
        this.sort();;
    }

    public void sort(){

        if(list.size() < 2)
            return;

        //Usar um sort nativo
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j< list.size()-1;j++)
            {
                double e1 = ((Evento)list.get(j)).getDataHora();
                double e2 = ((Evento)list.get(j+1)).getDataHora();
                System.out.println("("+String.valueOf(e1)+","+String.valueOf(e2)+")");
                if (e1 > e2){
                    Object o = list.get(j);
                    list.set(j,list.get(j+1));
                    list.set(j+1,o);
                }
            }
        }
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row ;
        row = convertView;
        DataHandler handler;
        if(row == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_lista_eventos,parent,false);
            handler = new DataHandler();
            handler.titulo = (TextView)row.findViewById(R.id.titulo_evento_item);
            handler.data = (TextView)row.findViewById(R.id.data_evento_item);
            handler.hora = (TextView)row.findViewById(R.id.horario_evento_item);
            handler.imagem = (ImageView)row.findViewById(R.id.imagem_evento_item);

            row.setTag(handler);
        }
        else
        {
            handler = (DataHandler) row.getTag();

        }
        Evento dataProvider;
        dataProvider = (Evento) this.getItem(position);
        handler.titulo.setText(dataProvider.getTitulo());
        handler.data.setText(dataProvider.getDataInicio());
        handler.hora.setText(dataProvider.getHoraInicio());
        if (dataProvider.getImagem() != -1)
        {
            handler.imagem.setImageResource(dataProvider.getImagem());
        }
        return row;
    }
}
