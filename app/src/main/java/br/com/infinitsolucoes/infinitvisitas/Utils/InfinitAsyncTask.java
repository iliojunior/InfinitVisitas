package br.com.infinitsolucoes.infinitvisitas.Utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Size;

import org.jetbrains.annotations.NotNull;

public abstract class InfinitAsyncTask<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Result> {
    private final Context mContext;
    @NotNull
    @Size(min = 2, max = 22)
    private final String mTitleActivity;
    private final ProgressDialog mProgressDialog;
    private final AlertDialog.Builder mBuilder;

    public InfinitAsyncTask(final Context context,
                                  @NotNull
                                  @Size(min = 2, max = 22)
                                  final String titleActivity) {
        this.mContext = context;
        this.mTitleActivity = titleActivity;
        mProgressDialog = new ProgressDialog(mContext);
        mBuilder = new AlertDialog.Builder(mContext);
    }

    @Override
    protected void onCancelled() {
        getProgressDialog().dismiss();
        getAlertDialog().setTitle(getTitleActivity());
        getAlertDialog().setMessage("Cancelado pelo usuário");
        getAlertDialog().setPositiveButton("OK", null);
        getAlertDialog().create().show();
    }

    protected abstract void onAsyncFinish(Result result);

    protected Context getContext() {
        return mContext;
    }

    protected ProgressDialog getProgressDialog() {
        return mProgressDialog;
    }

    protected AlertDialog.Builder getAlertDialog() {
        return mBuilder;
    }

    protected String getTitleActivity(){
        return mTitleActivity;
    }
}
