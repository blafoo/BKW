package de.blafoo.growatt.feign;

import de.blafoo.growatt.entity.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "growatt-client", url = "https://server.growatt.com")
public interface GrowattFeignClient {
	
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	ResultResponse login(@RequestBody String userPassword);

	@PostMapping(value = "/panel/getDevicesByPlantList", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	DevicesResponse getDevicesByPlantList(@RequestBody String request);

	@PostMapping(value = "/energy/compare/getDevicesTotalChart", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	YearResponse getEnergyTotalChart(@RequestBody String request);

	@PostMapping(value = "/energy/compare/getDevicesYearChart", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	YearResponse getEnergyYearChart(@RequestBody String request);

	@PostMapping(value = "/energy/compare/getDevicesMonthChart", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	MonthResponse getEnergyMonthChart(@RequestBody String request);
	
	@PostMapping(value = "/energy/compare/getDevicesDayChart", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	DayResponse getEnergyDayChart(@RequestBody String request);
}
