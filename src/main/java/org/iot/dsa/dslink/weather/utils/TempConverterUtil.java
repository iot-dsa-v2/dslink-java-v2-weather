package org.iot.dsa.dslink.weather.utils;
import java.text.DecimalFormat;
/**
 * Created by KA306983 on 9/22/2018.
 */

public class TempConverterUtil {
    public static String fahrenheitToCelsius(String chill) {

        DecimalFormat df = new DecimalFormat("###.##");
       // String celius=df.format((Float.parseFloat(chill)-32)* 5 / 9);

        return df.format((Float.parseFloat(chill)-32)* 5 / 9);
    }

}
