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

import br.com.infinitsolucoes.infinitvisitas.Models.FollowUp;
import br.com.infinitsolucoes.infinitvisitas.R;

public class FollowUpCustomList extends ArrayAdapter<FollowUp> {
    private final Context context;
    private final List<FollowUp> followUpList;
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM");

    public FollowUpCustomList(Context context, int resource, List<FollowUp> objects) {
        super(context, resource, objects);
        this.context = context;
        this.followUpList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FollowUp followUp = followUpList.get(position);
        final View rowView = ((Activity) context).getLayoutInflater().inflate(R.layout.list_item_follow_up, null, true);
        final TextView input_data = (TextView) rowView.findViewById(R.id.input_data);
        final TextView input_interesse = (TextView) rowView.findViewById(R.id.nivel_interesse);
        final TextView input_tempo_retorno = (TextView) rowView.findViewById(R.id.tempo_retorno);
        final TextView input_observacao = (TextView) rowView.findViewById(R.id.input_observacao);

        input_data.setText(SIMPLE_DATE_FORMAT.format(followUp.getData().getTime()));
        input_interesse.setText("Nível de interesse: "
                .concat(
                        String.valueOf(
                                followUp.getNivelInteresse()
                        )
                )
        );
        String mensagemRetorno = followUp.isNaoRetornarMais() ?
                "Não retornar mais." :
                "Retornar em: "
                        + followUp.getRetornoTempo()
                        + " "
                        + followUp.getRetornoMedida();
        input_tempo_retorno.setText(mensagemRetorno);
        int visibility = followUp.getObservacao().isEmpty() ? View.GONE : View.VISIBLE;
        input_observacao.setVisibility(visibility);
        input_observacao.setText("Obs: ".concat(followUp.getObservacao()));

        return rowView;
    }
}
