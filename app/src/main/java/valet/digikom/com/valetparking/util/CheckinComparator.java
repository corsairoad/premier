package valet.digikom.com.valetparking.util;

import java.util.Comparator;

import valet.digikom.com.valetparking.domain.EntryCheckinResponse;

/**
 * Created by fadlymunandar on 7/26/17.
 */

public class CheckinComparator implements Comparator<EntryCheckinResponse> {
    @Override
    public int compare(EntryCheckinResponse e1, EntryCheckinResponse e2) {
        String e1Checkin = e1.getData().getAttribute().getCheckinTime();
        String e2Checkin = e2.getData().getAttribute().getCheckinTime();

        return e2Checkin.compareTo(e1Checkin);
    }
}
