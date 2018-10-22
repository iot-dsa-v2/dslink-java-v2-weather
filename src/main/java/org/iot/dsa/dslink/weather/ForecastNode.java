package org.iot.dsa.dslink.weather;

import org.iot.dsa.dslink.weather.utils.Constants;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by KA306983 on 9/10/2018.
 */

public class ForecastNode extends DSNode {

    private DSMap parameters;
    JSONArray JSONArray_weather;
    String units_temperature;

    public ForecastNode() {
    }

    public ForecastNode(JSONArray JSONArray_weather, String units_temperature) {
        this.JSONArray_weather = JSONArray_weather;
        this.units_temperature = units_temperature;
        forcastDetails(this.JSONArray_weather, units_temperature);
    }

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(Constants.ACT_REMOVE, makeRemoveAction());
    }

    private DSAction makeRemoveAction() {
        return new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((ForecastNode) info.getParent()).delete();
                return null;
            }
        };
    }

    private void delete() {
        getParent().remove(getInfo());
    }

    private void forcastDetails(JSONArray JSONArray_weather, String units_temperature) throws JSONException {
        if (JSONArray_weather.length() > 0) {
            for (int i = 0, size = JSONArray_weather.length(); i < size; i++) {
                JSONObject JSONObject_weather = JSONArray_weather.getJSONObject(i);
                String code = JSONObject_weather.getString(Constants.CODE);
                String date = JSONObject_weather.getString(Constants.DATE);
                String day = JSONObject_weather.getString(Constants.DAY);
                String high = JSONObject_weather.getString(Constants.HIGH).concat(units_temperature);
                String low = JSONObject_weather.getString(Constants.LOW).concat(units_temperature);
                String text = JSONObject_weather.getString(Constants.TEXT);

                put(date, new DateWeatherDetails(code, date, day, high, low, text));

            }
        }
    }
}
