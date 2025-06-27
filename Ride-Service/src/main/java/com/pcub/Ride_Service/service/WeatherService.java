package com.pcub.Ride_Service.service;

import com.pcub.Ride_Service.exception.WeatherServiceException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

@Service
@Log4j2
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${weather.key}")
    private String weatherApiKey;

    public boolean isBadWeather(double lat, double lon) {
        try {
            String url = String.format(
                    "http://api.weatherapi.com/v1/current.json?key=%s&q=%f,%f",
                    weatherApiKey, lat, lon
            );

            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response);

            JSONObject current = json.getJSONObject("current");
            double temperature = current.getDouble("temp_c");
            double precipitation = current.getDouble("precip_mm");

            boolean isRainy = precipitation > 0;
            log.info("is rain at :{} {} {}",isRainy ,lat ,lon);
            boolean isTooHot = temperature >= 47;
            boolean isTooCold = temperature <= 12;
            log.info("ths weather is {}{}{}:::",isRainy,isTooCold,isTooHot);
            return isRainy || isTooHot || isTooCold;

        } catch (Exception e) {
            throw new WeatherServiceException("Failed to fetch weather data from WeatherAPI", e);
        }
    }
}

