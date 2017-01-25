package br.com.infinitsolucoes.infinitvisitas.Utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class TelefoneMaskUtil extends MaskUtil {

    private static final String mask11 = "(##) #####-####";
    private static final String mask10 = "(##) ####-####";

    @Override
    public TextWatcher insert(final EditText editText) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String stringUnMask = TelefoneMaskUtil.unMask(s.toString());
                String mask = getDefaultMask(stringUnMask);
                switch (stringUnMask.length()) {
                    case 11:
                        mask = mask11;
                        break;
                    case 10:
                        mask = mask10;
                        break;
                }

                String mascara = "";
                if (isUpdating) {
                    old = stringUnMask;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if ((m != '#' && stringUnMask.length() > old.length()) || (m != '#' && stringUnMask.length() < old.length() && stringUnMask.length() != i)) {
                        mascara += m;
                        continue;
                    }
                    try {
                        mascara += stringUnMask.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(mascara);
                editText.setSelection(mascara.length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private static String getDefaultMask(String string) {
        String defaultMask = mask10;
        if (string.length() > 11)
            defaultMask = mask11;
        return defaultMask;
    }
}
