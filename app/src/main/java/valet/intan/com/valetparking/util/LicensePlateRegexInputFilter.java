package valet.intan.com.valetparking.util;

/**
 * Created by fadlymunandar on 8/7/17.
 */

public class LicensePlateRegexInputFilter extends RegexInputFilter {

    private static final String REGEX_LICENSE_PLATE = "[a-zA-Z0-9]+";

    public LicensePlateRegexInputFilter() {
        super(REGEX_LICENSE_PLATE);
    }
}
