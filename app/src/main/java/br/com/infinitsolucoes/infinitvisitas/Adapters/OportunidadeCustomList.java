package br.com.infinitsolucoes.infinitvisitas.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Models.Visitas;
import br.com.infinitsolucoes.infinitvisitas.R;

public class OportunidadeCustomList extends ArrayAdapter<Visitas> {
    private final Context context;
    private final List<Visitas> visitasList;

    public OportunidadeCustomList(Context context, int res, List<Visitas> visitasList) {
        super(context, res, visitasList);
        this.context = context;
        this.visitasList = visitasList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Visitas visita = visitasList.get(position);
        final View rowView = ((Activity) context).getLayoutInflater().inflate(R.layout.list_item_oportunidade, null, true);

        final TextView nomeEmpresa = (TextView) rowView.findViewById(R.id.list_oportunidade_nome_empresa);
        final TextView telefoneEmpresa = (TextView) rowView.findViewById(R.id.list_oportunidade_telefone);
        final TextView dataVisita = (TextView) rowView.findViewById(R.id.list_oportunidade_data_visita);
        final TextView horaVisita = (TextView) rowView.findViewById(R.id.list_oportunidade_hora_visita);
        final TextView enderecoEmpresa = (TextView) rowView.findViewById(R.id.list_oportunidade_endereco);
        final ImageButton imageButton = (ImageButton) rowView.findViewById(R.id.image_button_oportunidade_disk);
        final DateFormat dataFormat = new SimpleDateFormat("dd/MM");
        final DateFormat horaFormat = new SimpleDateFormat("HH:mm");


        nomeEmpresa.setText(visita.getEmpresa().getNome());
        telefoneEmpresa.setText(visita.getEmpresa().getTelefoneWithMask());
        dataVisita.setText(dataFormat.format(visita.getData().getTime()));
        horaVisita.setText(horaFormat.format(visita.getData().getTime()));
        enderecoEmpresa.setText(visita.getEmpresa().getEndereco().getLogradouroCompleto());
        imageButton.setOnClickListener(v -> {
            final Uri uri = Uri.parse("tel:" + visita.getEmpresa().getTelefoneUnMasked());
            final Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            context.startActivity(intent);
        });

        return rowView;
    }
}
