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
    protected void declareDefaults() {
        super.declareDefaults();
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
        act.addParameter(Constants.UNITS, DSValueType.ENUM, null).setEnumRange(Constants.UNITS_IMPERIAL, Constants.UNITS_METRIC);

        return act;
    }

    private void addNewCity(DSMap parameters) {
        String city = parameters.getString(Constants.CITY);
        String units = parameters.getString(Constants.UNITS);
        if (city != null) {
            put(parameters.getString(Constants.CITY), new WeatherCityNode(city, units));
        }
    }
}
