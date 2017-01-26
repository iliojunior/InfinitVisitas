package br.com.infinitsolucoes.infinitvisitas.Utils.AsyncTasks;

import android.content.Context;
import android.support.annotation.Size;

import org.jetbrains.annotations.NotNull;

public abstract class InfinitGetAsyncTask<Params, Progress, Result>
        extends InfinitAsyncTask<Params, Progress, Result> {
    public InfinitGetAsyncTask(Context context, @NotNull @Size(min = 2, max = 22) String titleActivity) {
        super(context, titleActivity);
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
