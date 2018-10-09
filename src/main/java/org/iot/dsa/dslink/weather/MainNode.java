package org.iot.dsa.dslink.weather;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSMainNode;
import org.iot.dsa.dslink.weather.utils.Constants;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSInt;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

/**
 * The main and only node of this link.
 */
public class MainNode extends DSMainNode {

    public MainNode() {
    }

    @Override
    public ActionResult onInvoke(DSInfo actionInfo, ActionInvocation invocation) {
        if (actionInfo == reset) {
            synchronized (counter) {
                put(counter, DSInt.valueOf(0));
            }
            return null;
        }
        return super.onInvoke(actionInfo, invocation);
    }

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(Constants.COUNTER, DSInt.valueOf(0))
                .setTransient(true)
                .setReadOnly(true);
        declareDefault(Constants.WRITABLE, DSInt.valueOf(0));
        declareDefault(Constants.RESET, DSAction.DEFAULT);
        // Change the following URL to your README
        declareDefault("Docs",
                DSString.valueOf(Constants.WEATHER_DOC_URL))
                .setTransient(true)
                .setReadOnly(true);

        declareDefault(Constants.CREATE_TRACKER, makeCreateTracerAction());
    }


    private DSAction makeCreateTracerAction() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((MainNode) info.getParent()).addNewCity(invocation.getParameters());
                return null;
            }
        };
        act.addParameter(Constants.CITY, DSValueType.STRING, null);
        act.addParameter(Constants.UNITS, DSValueType.ENUM, null).setEnumRange(Constants.UNITS_IMPERIAL,Constants.UNITS_METRIC);

         return act;
    }

    private void addNewCity(DSMap parameters) {
        String city = parameters.getString(Constants.CITY);
        String units = parameters.getString(Constants.UNITS);
        if (city != null) {
            put(parameters.getString(Constants.CITY), new WeatherCityNode(city,units));
        }
    }

    /**
     * Cancels an active timer if there is one.
     */
    @Override
    protected void onStopped() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Starts the timer.
     */
    @Override
    protected void onSubscribed() {
        // Use DSRuntime for timers and its thread pool.
        timer = DSRuntime.run(this, System.currentTimeMillis() + 1000, 1000);
    }

    /**
     * Cancels the timer.
     */
    @Override
    protected void onUnsubscribed() {
        timer.cancel();
        timer = null;
    }


}
