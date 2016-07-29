package com.studiodjb.wormwalker;

import java.util.Comparator;

/**
 * Created by DjB on 2016-07-29.
 */
public class LineStorageDateSort implements Comparator<LineStorage> {
    @Override
    public int compare(LineStorage l1, LineStorage l2) {
        return l1.AddTime.compareTo(l2.AddTime);
    }
}

