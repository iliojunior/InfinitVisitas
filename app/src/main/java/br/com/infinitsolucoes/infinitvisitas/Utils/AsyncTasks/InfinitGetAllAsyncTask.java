package br.com.infinitsolucoes.infinitvisitas.Utils.AsyncTasks;

import android.content.Context;
import android.support.annotation.Size;

import org.jetbrains.annotations.NotNull;

import br.com.infinitsolucoes.infinitvisitas.R;
import br.com.infinitsolucoes.infinitvisitas.Utils.InfinitAsyncTask;

public abstract class InfinitGetAllAsyncTask<Params, Progress, Result>
        extends InfinitAsyncTask<Params, Progress, Result> {


    public InfinitGetAllAsyncTask(Context context, @NotNull @Size(min = 2, max = 22) String titleActivity) {
        super(context, titleActivity);
    }

    @Override
    protected void onPreExecute() {
        getProgressDialog().setTitle(getTitleActivity());
        getProgressDialog().setMessage(getContext().getString(R.string.geting_data));
        getProgressDialog().setIndeterminate(true);
        getProgressDialog().show();
    }

    @Override
    protected void onPostExecute(Result result) {
        getProgressDialog().dismiss();
        if (result == null) {
            getAlertDialog().setTitle(getTitleActivity());
            getAlertDialog().setMessage("Ocorreu um erro interno.");
            getAlertDialog().setPositiveButton("OK", null);
            getAlertDialog().create().show();
        }

        onAsyncFinish(result);
    }
}
