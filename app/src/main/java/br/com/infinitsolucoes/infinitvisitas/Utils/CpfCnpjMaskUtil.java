package br.com.infinitsolucoes.infinitvisitas.Utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CpfCnpjMaskUtil extends MaskUtil {

    private static final String maskCpf = "###.###.###-##";
    private static final String maskCnpj = "##.###.###/####-##";
    private static final int cpfLength = 11;
    private static final int cnpjLength = 14;

    @Override
    public TextWatcher insert(EditText editText) {
        final TextWatcher textWatcher = new TextWatcher() {
            private boolean isUpdating;
            private String old = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String stringUnMask = unMask(s.toString());
                final String mask = getDefaultMask(stringUnMask);
                String mascara = "";
                if (isUpdating) {
                    old = stringUnMask;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m :
                        mask.toCharArray()) {
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
        return textWatcher;
    }

    private String getDefaultMask(final String s) {
        if (s.length() > cpfLength)
            return maskCnpj;

        return maskCpf;
    }
}
