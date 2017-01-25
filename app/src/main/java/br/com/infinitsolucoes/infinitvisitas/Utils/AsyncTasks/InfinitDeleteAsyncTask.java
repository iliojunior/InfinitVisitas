package br.com.infinitsolucoes.infinitvisitas.Utils.AsyncTasks;

import android.content.Context;
import android.support.annotation.Size;

import org.jetbrains.annotations.NotNull;

import br.com.infinitsolucoes.infinitvisitas.Business.Enumerador;
import br.com.infinitsolucoes.infinitvisitas.R;
import br.com.infinitsolucoes.infinitvisitas.Utils.InfinitAsyncTask;


public abstract class InfinitDeleteAsyncTask<Params, Progress, Result>
        extends InfinitAsyncTask<Params, Progress, Result> {

    public InfinitDeleteAsyncTask(Context context, @NotNull @Size(min = 2, max = 22) String titleActivity) {
        super(context, titleActivity);
    }

    @Override
    protected void onPreExecute() {
        getProgressDialog().setTitle(getTitleActivity());
        getProgressDialog().setMessage(getContext().getString(R.string.deleteing_data));
        getProgressDialog().setIndeterminate(true);
        getProgressDialog().show();
    }

    @Override
    protected void onPostExecute(Result result) {
        getProgressDialog().dismiss();
        String title = "";
        String message = "";
        if (result.getClass() == Enumerador.ResponseCode.class) {
            final Enumerador.ResponseCode responseCode = (Enumerador.ResponseCode) result;
            switch (responseCode) {
                case ERROR:
                    title = "Erro";
                    message = "Não é possível apagar este registro.";
                    break;
                case SUCCESS:
                    title = "Sucesso";
                    message = "Apagado com sucesso.";
                    break;
                case UNSUCCESS:
                    title = "Falha";
                    message = "Tente novamente.";
                    break;
            }
            getAlertDialog().setTitle(title);
            getAlertDialog().setMessage(message);
            getAlertDialog().setPositiveButton("OK", (v, w) -> onAsyncFinish(result));
            getAlertDialog().create().show();
        } else {
            onAsyncFinish(result);
        }

    }
}
