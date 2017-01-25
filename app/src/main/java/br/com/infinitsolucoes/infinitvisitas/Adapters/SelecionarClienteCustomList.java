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

public final class SelecionarClienteCustomList extends ArrayAdapter<Empresas> {
    private final Context mContext;
    private final List<Empresas> mEmpresasList;

    public SelecionarClienteCustomList(Context context, int resource, List<Empresas> empresasList) {
        super(context, resource, empresasList);
        mContext = context;
        mEmpresasList = empresasList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Empresas empresa = mEmpresasList.get(position);
        final View rowView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.list_item_selecionar_cliente, null, true);
        final TextView nomeEmpresa = (TextView) rowView.findViewById(R.id.nome_empresa);
        final TextView telefoneEmpresa = (TextView) rowView.findViewById(R.id.telefone_empresa);
        final TextView enderecoEmpresa = (TextView) rowView.findViewById(R.id.endereco_empresa);

        nomeEmpresa.setText(empresa.getNome());
        telefoneEmpresa.setText(empresa.getTelefoneWithMask());
        enderecoEmpresa.setText(empresa.getEndereco().getLogradouroCompleto());

        return rowView;
    }
}
