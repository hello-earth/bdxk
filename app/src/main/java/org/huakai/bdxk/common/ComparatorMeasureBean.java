package org.huakai.bdxk.common;

import java.util.Comparator;

/**
 * Created by Administrator on 2017/8/17.
 */

public class ComparatorMeasureBean implements Comparator {

    @Override
    public int compare(Object obj0, Object obj1) {
        MeasureBean user0=(MeasureBean)obj0;
        MeasureBean user1=(MeasureBean)obj1;
        return user0.getSensorName().compareTo(user1.getSensorName());
    }

}