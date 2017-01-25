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

import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.R;

public final class ContatosCustomList extends ArrayAdapter<Empresas> {
    private final Context mContext;
    private final List<Empresas> mListEmpresas;

    public ContatosCustomList(Context context, int resource, List<Empresas> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mListEmpresas = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View rowView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.list_item_contatos, null, true);
        final Empresas empresa = mListEmpresas.get(position);
        final TextView nomeEmpresa = (TextView) rowView.findViewById(R.id.nome_empresa);
        final ImageButton telefonar = (ImageButton) rowView.findViewById(R.id.image_button_oportunidade_disk);

        nomeEmpresa.setText(empresa.getNome());
        telefonar.setOnClickListener(v -> {
            final Uri uriPhone = Uri.parse("tel:" + empresa.getTelefoneUnMasked());
            final Intent intent = new Intent(Intent.ACTION_DIAL, uriPhone);
            mContext.startActivity(intent);
        });

        return rowView;
    }
}
