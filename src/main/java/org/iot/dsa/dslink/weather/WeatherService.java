/**
 *
 */
package org.iot.dsa.dslink.weather;

import org.iot.dsa.dslink.weather.utils.Constants;
import org.iot.dsa.dslink.weather.utils.DegreeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class WeatherService {



    public interface LimitDeclaration {

        String first(int count) throws IOException;

        String last(int count) throws IOException;

        String all() throws IOException;
    }

    private Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private Proxy proxy;

    public String getForecast(String woeid, DegreeUnit unit) throws IOException {
        WeatherYQLBuilder query = new WeatherYQLBuilder();
        query.woeid(woeid).unit(unit);
        String newquery = execute(query.build());
        return newquery;
    }

    public LimitDeclaration getForecastForLocation(String location, DegreeUnit unit) {
        final WeatherYQLBuilder query = new WeatherYQLBuilder();
        query.location(location).unit(unit);
        query.location(location);

        return new LimitDeclaration() {

            @Override
            public String last(int count) throws IOException {
                query.last(count);
                return execute(query.build());
            }

            @Override
            public String first(int count) throws IOException {
                query.first(count);
                return execute(query.build());
            }

            @Override
            public String all() throws IOException {
                return execute(query.build());
            }
        };
    }

    private String composeUrl(String query) {
        logger.trace("query: {}", query);
        StringBuilder url = new StringBuilder(Constants.YAHOO_WEATHER_SERVICE_URL);
        try {
            url.append("?q=").append(URLEncoder.encode(query, "UTF-8")).append("&format=json");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Url encoding failed", e);
        }
        return url.toString();
    }

    private String execute(String query) {
        String url = composeUrl(query);
        return url;
    }

    private String retrieveRSS(String serviceUrl) throws IOException {
        URL url = new URL(serviceUrl);
        URLConnection connection = url.openConnection(proxy);
        InputStream is = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(is);
        StringWriter writer = new StringWriter();
        copy(reader, writer);
        reader.close();
        is.close();

        return writer.toString();
    }

    private static long copy(Reader input, Writer output) throws IOException {
        char[] buffer = new char[Constants.DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
