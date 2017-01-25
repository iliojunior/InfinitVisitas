package br.com.infinitsolucoes.infinitvisitas.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Models.Vendas;
import br.com.infinitsolucoes.infinitvisitas.R;

public class VendasCustomList extends ArrayAdapter<Vendas> {
    private final Context mContext;
    private final List<Vendas> mVendasList;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");

    public VendasCustomList(final Context context, final int resource, final List<Vendas> vendasList) {
        super(context, resource, vendasList);
        this.mContext = context;
        this.mVendasList = vendasList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View rowView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.list_item_vendas, null, true);
        final Vendas mVenda = mVendasList.get(position);
        final TextView nomeEmpresa = (TextView) rowView.findViewById(R.id.nome_empresa);
        final TextView dataVenda = (TextView) rowView.findViewById(R.id.data_venda);
        final TextView cpfCnpj_Empresa = (TextView) rowView.findViewById(R.id.cpf_cnpj_empresa);
        final TextView telefoneEmpresa = (TextView) rowView.findViewById(R.id.telefone_empresa);

        nomeEmpresa.setText(mVenda.getEmpresa().getNome());
        dataVenda.setText(dateFormat.format(mVenda.getDataVenda().getTime()));
        cpfCnpj_Empresa.setText(mVenda.getEmpresa().getCpf_cnpjWithMask());
        telefoneEmpresa.setText(mVenda.getEmpresa().getEndereco().getLogradouroCompleto());
        return rowView;
    }
}
