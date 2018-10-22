package org.iot.dsa.dslink.weather;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.weather.utils.Constants;
import org.iot.dsa.dslink.weather.utils.DegreeUnit;
import org.iot.dsa.dslink.weather.utils.TempConverterUtil;
import org.iot.dsa.node.DSDouble;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by KA306983 on 9/10/2018.
 */

public class WeatherCityNode extends DSNode implements Runnable {

    private String city = "";
    private String units = "";
    JSONArray JSONArray_weather;
    private String units_temperature = "";
    private DSRuntime.Timer timer;


    public WeatherCityNode() {

    }

    public WeatherCityNode(String city, String units) {
        this.city = city;
        this.units = units;
    }

    @Override
    public ActionResult onInvoke(DSInfo actionInfo, ActionInvocation invocation) {
        return super.onInvoke(actionInfo, invocation);
    }

    @Override
    protected void declareDefaults() {

        super.declareDefaults();

        declareDefault(Constants.ACT_REMOVE, makeRemoveAction());
        declareDefault(Constants.CONDITION_CODE, DSDouble.valueOf(0));
        declareDefault(Constants.HUMIDITY, DSDouble.valueOf(0));
        declareDefault(Constants.PRESSURE, DSDouble.valueOf(0));
        declareDefault(Constants.SUNRISE, DSDouble.valueOf(0));
        declareDefault(Constants.SUNSET, DSDouble.valueOf(0));
        declareDefault(Constants.TEMPERATURE, DSDouble.valueOf(0));
        declareDefault(Constants.VISIBILITY, DSDouble.valueOf(0));
        declareDefault(Constants.WIND_DIRECTION, DSDouble.valueOf(0));
        declareDefault(Constants.WINDSPEED, DSDouble.valueOf(0));
    }

    @Override
    protected void onStable() {
        makeWeatherDetails(city, units);
        makeForecastNode(JSONArray_weather, units_temperature);
        timer = DSRuntime.run(this, System.currentTimeMillis() + (1800 * 1000), (1800 * 1000));
    }

    private void makeWeatherDetails(String city, String units) {
        boolean f2c_converter = false;

        if (city != null) {
            String url = "";
            WeatherService service = new WeatherService();
            try {
                if (units.equalsIgnoreCase(Constants.UNITS_IMPERIAL)) {
                    url = service.getForecastForLocation(city, DegreeUnit.FAHRENHEIT).first(Constants.LIMITATION);
                } else if (units.equalsIgnoreCase(Constants.UNITS_METRIC)) {
                    url = service.getForecastForLocation(city, DegreeUnit.CELSIUS).first(Constants.LIMITATION);
                    f2c_converter = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String result = "";
            try {
                URL url_weather = new URL(url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url_weather.openConnection();

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStreamReader inputStreamReader =
                            new InputStreamReader(httpURLConnection.getInputStream());
                    BufferedReader bufferedReader =
                            new BufferedReader(inputStreamReader, Constants.BUFFER_SIZE);
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }

                    bufferedReader.close();
                    String parsedResult = "";
                    JSONObject jsonObject = new JSONObject(result);

                    JSONObject query = jsonObject.getJSONObject(Constants.QUERY);
                    JSONObject results = query.getJSONObject(Constants.RESULTS);
                    JSONObject channel = results.getJSONObject(Constants.CHANNEL);

                    JSONObject units_json = channel.getJSONObject(Constants.UNITS.toLowerCase());
                    String units_distance = Constants.SPACE_CHAR.concat(units_json.getString(Constants.DISTANCE));
                    String units_pressure = Constants.SPACE_CHAR.concat(units_json.getString(Constants.PRESSURE.toLowerCase()));
                    String units_speed = Constants.SPACE_CHAR.concat(units_json.getString(Constants.SPEED));
                    units_temperature = Constants.DEGREE_SYMBOL.concat(units_json.getString(Constants.TEMPERATURE.toLowerCase()));


                    JSONObject location = channel.getJSONObject(Constants.LOCATION);

                    JSONObject wind = channel.getJSONObject(Constants.WIND);
                    String chill = wind.getString(Constants.CHILL);

                    if (f2c_converter == true) {
                        chill = TempConverterUtil.fahrenheitToCelsius(chill);
                    }
                    chill = chill.concat(units_temperature);
                    String direction = wind.getString(Constants.DIRECTION);
                    String speed = wind.getString(Constants.SPEED).concat(units_speed);
                    //String temperature = wind.getString("chill");

                    JSONObject atmosphere = channel.getJSONObject(Constants.ATMOSPHERE);
                    String humidity = atmosphere.getString(Constants.HUMIDITY.toLowerCase());
                    String pressure = atmosphere.getString(Constants.PRESSURE.toLowerCase()).concat(units_pressure);
                    String visibility = atmosphere.getString(Constants.VISIBILITY.toLowerCase()).concat(units_distance);

                    JSONObject astronomy = channel.getJSONObject(Constants.ASTRONOMY);
                    String sunrise = astronomy.getString(Constants.SUNRISE.toLowerCase());
                    String sunset = astronomy.getString(Constants.SUNSET.toLowerCase());

                    JSONObject item = channel.getJSONObject(Constants.ITEM);

                    JSONObject condition = item.getJSONObject(Constants.CONDITION_TXT.toLowerCase());
                    String conditioncode = condition.getString(Constants.CODE);
                    String temperature = condition.getString(Constants.TEMP).concat(units_temperature);
                    String conditionTxt = condition.getString(Constants.TEXT);

                    JSONArray_weather = item.getJSONArray(Constants.FORECAST.toLowerCase());

                    put(Constants.CONDITION_TXT, conditionTxt);
                    put(Constants.CONDITION_CODE, conditioncode);
                    put(Constants.HUMIDITY, humidity);
                    put(Constants.PRESSURE, pressure);
                    put(Constants.SUNRISE, sunrise);
                    put(Constants.SUNSET, sunset);
                    put(Constants.TEMPERATURE, temperature);
                    put(Constants.VISIBILITY, visibility);
                    put(Constants.WIND_CHILL, chill);
                    put(Constants.WIND_DIRECTION, direction);
                    put(Constants.WINDSPEED, speed);

                } else {
                    Logger.getLogger("Error in httpURLConnection.getResponseCode()!!!");
                }

            } catch (MalformedURLException ex) {
                Logger.getLogger(WeatherCityNode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(WeatherCityNode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(WeatherCityNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void makeForecastNode(JSONArray JSONArray_weather, String units_temperature) {
        put(Constants.FORECAST, new ForecastNode(JSONArray_weather, units_temperature));
    }

    private DSAction makeRemoveAction() {
        return new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                ((WeatherCityNode) info.getParent()).delete();
                return null;
            }
        };
    }

    private void delete() {
        getParent().remove(getInfo());
    }

    @Override
    public void run() {
        makeWeatherDetails(city, units);
        makeForecastNode(JSONArray_weather, units_temperature);
    }
}
