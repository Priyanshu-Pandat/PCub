package com.pcub.Ride_Service;

import com.pcub.Ride_Service.modals.VehicleType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@SpringBootApplication
@EnableFeignClients
public class RideServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RideServiceApplication.class, args);

	}
	@FeignClient(name = "DRIVER-SERVICE")
	public interface DriverServiceClient {
		@GetMapping("/api/drivers/nearby")
		List<Long> findNearbyDrivers(@RequestParam double lat,
									 @RequestParam double lon,
									 @RequestParam double radiusKm,
									 @RequestParam VehicleType vehicleType);
	}


}
