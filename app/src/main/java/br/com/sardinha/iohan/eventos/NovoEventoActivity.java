package br.com.sardinha.iohan.eventos;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;

import java.util.Arrays;
import java.util.Calendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NovoEventoActivity extends AppCompatActivity {

    DateFormat data = DateFormat.getDateInstance();
    Calendar calendario = Calendar.getInstance();
    Context cont = this;
    int hora = -1;
    int minuto = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_evento);
        try {
            Intent intent = getIntent();
            if (intent != null) {
                Evento evento = (Evento) intent.getSerializableExtra("evento");
                ((EditText) findViewById(R.id.titulo_Criacao)).setText(evento.getTitulo());
                ((EditText) findViewById(R.id.data_inicio_criacao)).setText(evento.getDataInicio());
                ((EditText) findViewById(R.id.data_encerramento_criacao)).setText(evento.getDataEncerramento());
                ((EditText) findViewById(R.id.hora_inicio_criacao)).setText(evento.getHoraInicio());
                ((EditText) findViewById(R.id.hora_encerramento_criacao)).setText(evento.getHoraEncerramento());
                ((EditText) findViewById(R.id.descricao_criacao)).setText(evento.getDescricao());
                if(evento.getLimite() != -1)
                {
                    ((EditText) findViewById(R.id.limite_de_convidados_criacao)).setText(String.valueOf(evento.getLimite()));
                }
                switch(evento.getTipo()){
                    case "Aniversário":
                        ((Spinner) findViewById(R.id.privacidade_criacao)).setSelection(1);
                        break;
                    case "Show":
                        ((Spinner) findViewById(R.id.privacidade_criacao)).setSelection(2);
                        break;
                    case "Festa":
                        ((Spinner) findViewById(R.id.privacidade_criacao)).setSelection(3);
                        break;
                    default:
                        ((Spinner) findViewById(R.id.privacidade_criacao)).setSelection(4);
                        break;
                }
            }
        }
        catch (Exception ex){

        }

        ((EditText)findViewById(R.id.data_inicio_criacao)).setKeyListener(null);
        ((EditText)findViewById(R.id.data_inicio_criacao)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    selecionarDataInicioClick(findViewById(R.id.data_inicio_criacao));
            }
        });

        ((EditText)findViewById(R.id.data_encerramento_criacao)).setKeyListener(null);
        ((EditText)findViewById(R.id.data_encerramento_criacao)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    selecionarDataEncerramentoClick(findViewById(R.id.data_encerramento_criacao));
            }
        });

        ((EditText)findViewById(R.id.hora_inicio_criacao)).setKeyListener(null);
        ((EditText)findViewById(R.id.hora_inicio_criacao)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    selecionarHoraInicioClick(findViewById(R.id.hora_inicio_criacao));
            }
        });

        ((EditText)findViewById(R.id.hora_encerramento_criacao)).setKeyListener(null);
        ((EditText)findViewById(R.id.hora_encerramento_criacao)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    selecionarHoraEncerramentoClick(findViewById(R.id.hora_encerramento_criacao));
            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.tipos_de_eventos,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        ((Spinner)findViewById(R.id.tipo_de_evento_criacao)).setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(this,R.array.tipos_de_privacidade,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        ((Spinner)findViewById(R.id.privacidade_criacao)).setAdapter(adapter);

    }

    private String formatador(int x){
        if (x < 10){
            return "0"+String.valueOf(x);
        }
        return String.valueOf(x);
    }

    public void criarEventoClick(View view) {
        Intent intent = new Intent(this,MainActivity.class);

        Map<String,String> campos = new HashMap<>();

        String titulo = ((EditText) findViewById(R.id.titulo_Criacao)).getText().toString();
        String data_inicio = ((EditText) findViewById(R.id.data_inicio_criacao)).getText().toString();
        String horaInicio = ((EditText) findViewById(R.id.hora_inicio_criacao)).getText().toString();
        String descricao = ((EditText) findViewById(R.id.descricao_criacao)).getText().toString();

        campos.put("Descrição do evento não pode ser vazia!",descricao);
        campos.put("Hora de início do evento não pode ser vazia!",horaInicio);
        campos.put("Data do evento não pode ser vazia!",data_inicio);
        campos.put("Título do evento não pode ser vazio!",titulo);

        for (String s: campos.keySet()) {
            if(campos.get(s).isEmpty()){
                Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String tipo = ((Spinner)findViewById(R.id.tipo_de_evento_criacao)).getSelectedItem().toString();
        if(tipo.equals("Tipo")){
            Toast.makeText(this,"Selecione um tipo de evento!",Toast.LENGTH_SHORT).show();
            return;
        }

        String privacidade = ((Spinner)findViewById(R.id.privacidade_criacao)).getSelectedItem().toString();
        if(privacidade.equals("Privacidade")){
            Toast.makeText(this,"Selecione a privacidade do seu evento",Toast.LENGTH_SHORT).show();
            return;
        }

        String data_encerramento = ((EditText) findViewById(R.id.data_encerramento_criacao)).getText().toString();
        String horaEncerramento = ((EditText) findViewById(R.id.hora_encerramento_criacao)).getText().toString();
        String limite = ((EditText) findViewById(R.id.limite_de_convidados_criacao)).getText().toString();

        Evento e = new Evento(titulo,data_inicio,"",horaInicio,"",tipo,privacidade,descricao,-1);
        if(!limite.isEmpty())
        {
            e.setLimite(Integer.parseInt(limite));
        }
        if(!data_encerramento.isEmpty() && !horaEncerramento.isEmpty())
        {
            e.setDataEncerramento(data_encerramento);
        }
        else if(!data_encerramento.isEmpty())
        {
            Toast.makeText(cont, "Hora de encerramento não pode estar em branco", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!horaEncerramento.isEmpty())
        {
            e.setHoraEncerramento(horaEncerramento);
        }
        else if(data_encerramento.isEmpty())
        {
            data_encerramento = data_inicio;
        }
        intent.putExtra("evento",e);
        setResult(RESULT_OK,intent);
        finish();
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendario = Calendar.getInstance();
            String data = formatador(year)+formatador(month)+formatador(dayOfMonth);
            String hoje = formatador(calendario.get(Calendar.YEAR))+formatador(calendario.get(Calendar.MONTH))+formatador(calendario.get(Calendar.DAY_OF_MONTH));
            if (Integer.parseInt(data) < Integer.parseInt(hoje))
            {
                Toast.makeText(cont, "Data não pode ser anterior ao dia de hoje!", Toast.LENGTH_SHORT).show();
                selecionarDataInicioClick(findViewById(R.id.hora_inicio_criacao));
            }
            else
            {
                ((EditText)findViewById(R.id.data_inicio_criacao)).setText(formatador(dayOfMonth)+"/"+formatador(month+1)+"/"+String.valueOf(year));
            }
        }
    };

    public void selecionarDataInicioClick(View view) {
        String campoAtual = ((EditText)findViewById(R.id.data_inicio_criacao)).getText().toString();
        if (!campoAtual.isEmpty())
        {
            List<String> tempCampoAtual = Arrays.asList(campoAtual.split("/"));
            new DatePickerDialog(cont, d, Integer.parseInt(tempCampoAtual.get(2)),Integer.parseInt(tempCampoAtual.get(1))-1,Integer.parseInt(tempCampoAtual.get(0))).show();
        }
        else
        {
            calendario = Calendar.getInstance();
            new DatePickerDialog(this,d,calendario.get(Calendar.YEAR),calendario.get(Calendar.MONTH),calendario.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    DatePickerDialog.OnDateSetListener d2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String horaInicio = ((EditText)findViewById(R.id.hora_inicio_criacao)).getText().toString().replaceAll(":","");
            String horaEncerramento = ((EditText)findViewById(R.id.hora_encerramento_criacao)).getText().toString();
            String dataInicio = ((EditText)findViewById(R.id.data_inicio_criacao)).getText().toString();
            List<String> tempData = Arrays.asList(dataInicio.split("/"));
            Collections.reverse(tempData);
            dataInicio = "";
            for(String s : tempData)
            {
                dataInicio += s;
            }
            if (!horaInicio.isEmpty())
            {
                horaInicio = horaInicio.substring(0,2)+":"+formatador(Integer.parseInt(horaInicio.substring(2,4))+1);
            }
            String dataSelecionada = String.valueOf(year)+formatador(month+1)+formatador(dayOfMonth);
            String dataEncerramento = ((EditText)findViewById(R.id.data_encerramento_criacao)).getText().toString();
            if (!horaEncerramento.isEmpty() && dataSelecionada.equals(dataInicio) && !dataEncerramento.isEmpty() && !dataEncerramento.equals(((EditText)findViewById(R.id.data_inicio_criacao)).getText().toString()))
            {
                System.out.println(horaInicio);
                ((EditText)findViewById(R.id.data_encerramento_criacao)).setText(formatador(dayOfMonth)+"/"+formatador(month+1)+"/"+String.valueOf(year));
                ((EditText)findViewById(R.id.hora_encerramento_criacao)).setText(horaInicio);

            }
            else if((Integer.parseInt(dataInicio) <= Integer.parseInt(dataSelecionada)) && !dataInicio.isEmpty())
            {
                ((EditText)findViewById(R.id.data_encerramento_criacao)).setText(formatador(dayOfMonth)+"/"+formatador(month+1)+"/"+String.valueOf(year));
            }
            else
            {
                Toast.makeText(cont, "Data de encerramento não pode ser anterior a de início!", Toast.LENGTH_SHORT).show();
                selecionarDataEncerramentoClick(findViewById(R.id.data_encerramento_criacao));
            }
        }
    };

    public void selecionarDataEncerramentoClick(View view) {
        String dataInicio = ((EditText)findViewById(R.id.data_inicio_criacao)).getText().toString();
        String campoAtual = ((EditText)findViewById(R.id.data_encerramento_criacao)).getText().toString();
        if (dataInicio.isEmpty())
        {
            Toast.makeText(cont, "É necessário adicionar uma data inicial antes!", Toast.LENGTH_SHORT).show();
        }
        else if (!campoAtual.isEmpty())
        {
            List<String> tempCampoAtual = Arrays.asList(campoAtual.split("/"));
            new DatePickerDialog(cont, d2, Integer.parseInt(tempCampoAtual.get(2)),Integer.parseInt(tempCampoAtual.get(1))-1,Integer.parseInt(tempCampoAtual.get(0))).show();
        }
        else if (!dataInicio.isEmpty()) {
            String temp = dataInicio.replaceAll("/", "");
            String d = temp.substring(0, 2);
            String m = temp.substring(2, 4);
            String a = temp.substring(4, 8);
            new DatePickerDialog(cont, d2, Integer.parseInt(a),Integer.parseInt(m)-1,Integer.parseInt(d)).show();
        }
        else {
            calendario = Calendar.getInstance();
            new DatePickerDialog(cont, d2, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    TimePickerDialog.OnTimeSetListener h = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendario = Calendar.getInstance();
            String hora_ = formatador(hourOfDay)+formatador(minute);
            String horaAtual = formatador(calendario.get(Calendar.HOUR_OF_DAY))+formatador(calendario.get(Calendar.MINUTE));
            String data = ((EditText)findViewById(R.id.data_inicio_criacao)).getText().toString().replaceAll("/","");
            String hoje = formatador(calendario.get(Calendar.DAY_OF_MONTH))+formatador(calendario.get(Calendar.MONTH)+1)+formatador(calendario.get(Calendar.YEAR));
            String horaEncerramento = ((EditText)findViewById(R.id.hora_encerramento_criacao)).getText().toString();
            if(!horaEncerramento.isEmpty())
            {
                horaEncerramento = horaEncerramento.replaceAll(":","");
            }
            if(!horaEncerramento.isEmpty() && Integer.parseInt(horaEncerramento) <= Integer.parseInt(hora_))
            {
                Toast.makeText(cont, "Hora de encerramento não pode ser anterior a hora de inicio!", Toast.LENGTH_SHORT).show();
                selecionarHoraInicioClick(findViewById(R.id.hora_inicio_criacao));
            }
            else if ((Integer.parseInt(hora_) < Integer.parseInt(horaAtual)) && data.equals(hoje))
            {
                Toast.makeText(cont, "Hora não pode ser anterior a hora atual!", Toast.LENGTH_SHORT).show();
                selecionarHoraInicioClick(findViewById(R.id.hora_inicio_criacao));
            }
            else if ((Integer.parseInt(hora_) == Integer.parseInt(horaAtual)) && data.equals(hoje))
            {
                Toast.makeText(cont, "Hora não pode ser a hora atual!", Toast.LENGTH_SHORT).show();
                selecionarHoraInicioClick(findViewById(R.id.hora_inicio_criacao));
            }
            else
            {
                ((EditText) findViewById(R.id.hora_inicio_criacao)).setText(formatador(hourOfDay) + ":" + formatador(minute));
                hora = hourOfDay;
                minuto = minute + 1;
            }
        }
    };

    public void selecionarHoraInicioClick(View view) {
        String campoAtual = ((EditText)findViewById(R.id.hora_inicio_criacao)).getText().toString();
        if (!campoAtual.isEmpty())
        {
            List<String> tempCampoAtual = Arrays.asList(campoAtual.split(":"));
            new TimePickerDialog(cont,h,Integer.parseInt(tempCampoAtual.get(0)),Integer.parseInt(tempCampoAtual.get(1)),true).show();
        }
        else {
            calendario = Calendar.getInstance();
            new TimePickerDialog(this, h, calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE)+1, true).show();
        }
    }

    TimePickerDialog.OnTimeSetListener h2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String horaInicio = ((EditText)findViewById(R.id.hora_inicio_criacao)).getText().toString().replaceAll(":","");
            String horaEncerramento = formatador(hourOfDay)+formatador(minute);

            String dataInicial = ((EditText)findViewById(R.id.data_inicio_criacao)).getText().toString();
            String dataEncerramento = ((EditText)findViewById(R.id.data_encerramento_criacao)).getText().toString();

            if(Integer.parseInt(horaInicio) > Integer.parseInt(horaEncerramento) && dataInicial.equals(dataEncerramento))
            {
                Toast.makeText(cont, "Horario de encerramento não pode ser anterior ao de início!", Toast.LENGTH_SHORT).show();
                selecionarHoraEncerramentoClick(findViewById(R.id.hora_encerramento_criacao));
            }
            else if(Integer.parseInt(horaInicio) == Integer.parseInt(horaEncerramento) && dataInicial.equals(dataEncerramento))
            {
                Toast.makeText(cont, "Horario de encerramento não pode ser igual ao de início!", Toast.LENGTH_SHORT).show();
                selecionarHoraEncerramentoClick(findViewById(R.id.hora_encerramento_criacao));
            }
            else
            {
                ((EditText)findViewById(R.id.hora_encerramento_criacao)).setText(formatador(hourOfDay)+":"+formatador(minute));
            }
        }
    };

    public void selecionarHoraEncerramentoClick(View view) {
        String dataInicio = ((EditText)findViewById(R.id.data_inicio_criacao)).getText().toString();
        String dataEncerramento = ((EditText)findViewById(R.id.data_encerramento_criacao)).getText().toString();
        String horaInicio = ((EditText)findViewById(R.id.hora_inicio_criacao)).getText().toString();
        String campoAtual = ((EditText)findViewById(R.id.hora_encerramento_criacao)).getText().toString();
        if (!campoAtual.isEmpty())
        {
            List<String> tempCampoAtual = Arrays.asList(campoAtual.split(":"));
            new TimePickerDialog(cont,h2,Integer.parseInt(tempCampoAtual.get(0)),Integer.parseInt(tempCampoAtual.get(1)),true).show();
        }
        else if(horaInicio.isEmpty())
        {
            Toast.makeText(cont, "É necessário adicionar uma hora inicial antes!", Toast.LENGTH_SHORT).show();
        }
        else if(dataEncerramento.isEmpty() && !dataInicio.isEmpty())
        {
            ((EditText)findViewById(R.id.data_encerramento_criacao)).setText(dataInicio);
            new TimePickerDialog(cont,h2,hora,minuto,true).show();
        }
        else if((!horaInicio.isEmpty()) && (dataInicio.equals(dataEncerramento) && (!dataInicio.isEmpty() && !dataEncerramento.isEmpty())))
        {
            new TimePickerDialog(cont,h2,hora,minuto,true).show();
        }
        else if((!horaInicio.isEmpty()) && (!dataInicio.equals(dataEncerramento) && (!dataInicio.isEmpty() && !dataEncerramento.isEmpty()))){
            new TimePickerDialog(cont,h2,0,0,true).show();
        }
        else if(dataEncerramento.isEmpty())
        {
            Toast.makeText(cont, "É necessário adicionar uma data inicial antes!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            calendario = Calendar.getInstance();
            new TimePickerDialog(cont,h2,calendario.get(Calendar.HOUR_OF_DAY),calendario.get(Calendar.MINUTE),true).show();
        }
    }

}
