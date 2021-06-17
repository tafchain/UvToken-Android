package com.yongqi.wallet.utils;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.yongqi.wallet.filter.InputNumLengthFilter;

public class EditTextUtils {
    private static final int DECIMAL_DIGITS = 8;//小数的位数

    /**
     * 让一个输入框只能输入指定位数小数 和整数位
     *
     * @param editText   EditText
     * @param maxInteger 最大整数位数
     * @param maxPoint   最大小数位数
     */
    public static void setPointWithInteger(final EditText editText, final int maxPoint, final int maxInteger, InputFilter... inputFilters) {
        if (inputFilters == null || inputFilters.length == 0) {
            editText.setFilters(new InputFilter[]{new InputNumLengthFilter(maxPoint, maxInteger)});
        } else {
            InputFilter[] newInputFilters = new InputFilter[inputFilters.length + 1];
            System.arraycopy(inputFilters, 0, newInputFilters, 0, inputFilters.length);
            newInputFilters[inputFilters.length] = new InputNumLengthFilter(maxPoint, maxInteger);
            editText.setFilters(newInputFilters);
        }

    }

    public static void setPoint(final EditText editText,int number) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > number) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + number + 1);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    //隐藏键盘
    public static void hideKeyboard(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
