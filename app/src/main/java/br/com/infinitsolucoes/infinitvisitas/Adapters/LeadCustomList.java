package br.com.infinitsolucoes.infinitvisitas.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.R;

public class LeadCustomList extends ArrayAdapter<Empresas> {
    private final Context activity;
    private final List<Empresas> empresasList;
    private TextView nomeEmpresaTextView;
    private TextView telefoneEmpresaTextView;
    private Empresas empresas;
    private View rowView;

    public LeadCustomList(Context activity, int resource, List<Empresas> empresasList) {
        super(activity, resource, empresasList);
        this.activity = activity;
        this.empresasList = empresasList;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        empresas = empresasList.get(position);
        rowView = ((Activity) activity).getLayoutInflater().inflate(R.layout.list_item_leads, null, true);

        nomeEmpresaTextView = (TextView) rowView.findViewById(R.id.list_lead_nome_empresa);
        telefoneEmpresaTextView = (TextView) rowView.findViewById(R.id.list_lead_telefone);

        nomeEmpresaTextView.setText(empresas.getNome());
        telefoneEmpresaTextView.setText(empresas.getTelefoneWithMask());

        return rowView;
    }
}
