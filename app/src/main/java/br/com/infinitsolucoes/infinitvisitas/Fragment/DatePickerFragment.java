package br.com.infinitsolucoes.infinitvisitas.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

import br.com.infinitsolucoes.infinitvisitas.R;

import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.getDataAtual;

public class DatePickerFragment extends DialogFragment {
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    int year, month, day;
    Calendar calendar = getDataAtual();

    public int getYear() {
        if (year != 0)
            return year;

        year = calendar.get(Calendar.YEAR);
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        if (month != 0)
            return month;

        month = calendar.get(Calendar.MONTH);
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        if (day != 0)
            return day;

        day = calendar.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), R.style.DialogTheme, mOnDateSetListener, getYear(), getMonth(), getDay());
    }

    public DatePickerDialog.OnDateSetListener getOnDateSetListener() {
        if (mOnDateSetListener != null)
            return mOnDateSetListener;

        mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            }
        };
        return mOnDateSetListener;
    }

    public void setmOnDateSetListener(DatePickerDialog.OnDateSetListener mOnDateSetListener) {
        this.mOnDateSetListener = mOnDateSetListener;
    }
}
