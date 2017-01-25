package br.com.infinitsolucoes.infinitvisitas.Utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {
    public static final String EXTRA_EMPRESA_ID = "idEmpresa";
    public static final String EXTRA_VISITA_ID = "idVisita";
    public static final String EXTRA_FOLLOW_UP_ID = "idFollowUp";
    private static Locale LOCALE_BRAZILIAN = new Locale("pt", "BR");

    public static Locale getLocaleBrazilian() {
        if (LOCALE_BRAZILIAN != null)
            return LOCALE_BRAZILIAN;
        LOCALE_BRAZILIAN = new Locale("pt", "BR");
        return LOCALE_BRAZILIAN;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static Calendar getDataAtual() {
        return Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"), new Locale("pt", "BR"));
    }

    @NonNull
    public static Boolean HavePermission(@NotNull Context context, @NotNull String permission) {
        return (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
    }
}
