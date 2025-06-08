package com.driverService.config;

import com.cloudinary.Cloudinary;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DriverConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Cloudinary getCloudinary() {
        Map mp = new HashMap();
        mp.put("cloud_name", "dixp2snwc");
        mp.put("api_key", "746938466252928");
        mp.put("api_secret", "Jzjt2MsMGRREx94CFcoVA_TIXoo");
        mp.put("secure", true);
        return new Cloudinary(mp);
    }
}
