package br.com.infinitsolucoes.infinitvisitas.Utils;

import android.text.TextWatcher;
import android.widget.EditText;

public abstract class MaskUtil {

    public static final String unMask(final String text) {
        return text.replaceAll("[^0-9]*", "");
    }

    public abstract TextWatcher insert(final EditText editText);
}
