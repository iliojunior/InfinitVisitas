package br.com.infinitsolucoes.infinitvisitas.Fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

import br.com.infinitsolucoes.infinitvisitas.R;

import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.getDataAtual;

public class TimePickerFragment extends DialogFragment {
    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener;
    int hour, minute;
    Calendar calendar = getDataAtual();

    public int getMinute() {
        if (minute != 0)
            return minute;

        minute = calendar.get(Calendar.MINUTE);
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        if (hour != 0)
            return hour;

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new TimePickerDialog(getActivity(), R.style.DialogTheme, mOnTimeSetListener, getHour(), getMinute(),
                DateFormat.is24HourFormat(getActivity()));
    }

    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener listener) {
        this.mOnTimeSetListener = listener;
    }
}
