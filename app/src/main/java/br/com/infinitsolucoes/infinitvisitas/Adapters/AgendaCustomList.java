package br.com.infinitsolucoes.infinitvisitas.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Models.Visitas;
import br.com.infinitsolucoes.infinitvisitas.R;

public class AgendaCustomList extends ArrayAdapter<Visitas> {
    private final Context context;
    private final List<Visitas> listaVisitas;

    public AgendaCustomList(Context context, int resource, List<Visitas> listaVisitas) {
        super(context, resource, listaVisitas);
        this.context = context;
        this.listaVisitas = listaVisitas;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        final Visitas visita = listaVisitas.get(position);
        final View rowView = ((Activity) context).getLayoutInflater().inflate(R.layout.list_item_agenda, null, true);
        final TextView nomeEmpresaTextView = (TextView) rowView.findViewById(R.id.list_agenda_nome_empresa);
        final TextView enderecoTextView = (TextView) rowView.findViewById(R.id.list_agenda_endereco);
        final TextView dataVisitaTextView = (TextView) rowView.findViewById(R.id.list_agenda_data_visita);
        final TextView horaVisitaTextView = (TextView) rowView.findViewById(R.id.list_agenda_hora_visita);
        final DateFormat dataFormat = new SimpleDateFormat("dd/MM");
        final DateFormat horaFormat = new SimpleDateFormat("HH:mm");

        nomeEmpresaTextView.setText(visita.getEmpresa().getNome());
        enderecoTextView.setText(visita.getEmpresa().getEndereco().getLogradouroCompleto());
        dataVisitaTextView.setText(dataFormat.format(visita.getData().getTime()));
        horaVisitaTextView.setText(horaFormat.format(visita.getData().getTime()));

        return rowView;
    }
}
