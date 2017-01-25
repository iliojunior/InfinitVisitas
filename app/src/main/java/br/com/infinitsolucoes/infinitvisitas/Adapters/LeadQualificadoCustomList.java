package br.com.infinitsolucoes.infinitvisitas.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.R;

public class LeadQualificadoCustomList extends ArrayAdapter<Empresas> {
    private final Context context;
    private final List<Empresas> empresasList;


    public LeadQualificadoCustomList(final Context context, int res, List<Empresas> empresasList) {
        super(context, res, empresasList);
        this.context = context;
        this.empresasList = empresasList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Empresas empresas = empresasList.get(position);
        final View rowView = ((Activity) context).getLayoutInflater().inflate(R.layout.list_item_leads_qualificados, null, true);
        final TextView nomeEmpresa = (TextView) rowView.findViewById(R.id.nome_empresa);
        final TextView telefoneEmpresa = (TextView) rowView.findViewById(R.id.telefone_empresa);
        final TextView enderecoEmpresa = (TextView) rowView.findViewById(R.id.endereco_empresa);
        final TextView nivelInteresse = (TextView) rowView.findViewById(R.id.nivel_interesse);

        nomeEmpresa.setText(empresas.getNome());
        telefoneEmpresa.setText(empresas.getTelefoneWithMask());
        enderecoEmpresa.setText(empresas.getEndereco().getLogradouroCompleto());
        nivelInteresse.setText(String.format("%d.0", empresas.getNivelInteresse()));

        Drawable drawable = context.getResources().getDrawable(R.drawable.circle_textview);

        return rowView;
    }
}
