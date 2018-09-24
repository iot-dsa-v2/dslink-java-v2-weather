package org.iot.dsa.dslink.weather;

import org.iot.dsa.dslink.weather.utils.Constants;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

/**
 * Created by KA306983 on 9/17/2018.
 */

public class DateWeatherDetails extends DSNode {

    private String code;
    private String date;
    private String day;
    private String high;
    private String low;
    private String text;

    public DateWeatherDetails() {
    }

    public DateWeatherDetails(String code, String date, String day, String high, String low, String text) {
        this.code = code;
        this.date = date;
        this.day = day;
        this.high = high;
        this.low = low;
        this.text = text;

    }

    @Override
    protected void onStable() {
        super.onStable();
        dateWeatherDetails(code, date, day, high, low, text);
    }

    private DSAction makeRemoveAction() {
        return new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((DateWeatherDetails) info.getParent()).delete();
                return null;
            }
        };
    }

    public void dateWeatherDetails(String code, String date, String day, String high, String low, String text) {

        put(Constants.CONDITION_TXT, text);
        put(Constants.CONDITION_CODE, code);
        put(Constants.DATE, date);
        put(Constants.DAY, day);
        put(Constants.HIGH, high);
        put(Constants.LOW, low);

    }

    private void delete() {
        getParent().remove(getInfo());
    }

    private void putA(String metrickey, DSIValue value) {
        DSInfo info = getInfo(metrickey);
        if (info == null) {
            declareDefault(metrickey, value).setReadOnly(true);
        } else {
            put(info, value);
        }
    }

}
