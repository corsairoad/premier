package valet.digikom.com.valetparking.util;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fadlymunandar on 8/7/17.
 */

public class RegexInputFilter implements InputFilter {

    private Pattern mPattern;
    private static final String TAG = RegexInputFilter.class.getSimpleName();

    public RegexInputFilter(Pattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException(TAG + " required a regex");
        }
        this.mPattern = pattern;
    }

    public RegexInputFilter(String regex) {
        this(Pattern.compile(regex));
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        Matcher matcher = mPattern.matcher(source);

        if (!matcher.matches()) {
            return "";
        }
        return null;
    }
}
