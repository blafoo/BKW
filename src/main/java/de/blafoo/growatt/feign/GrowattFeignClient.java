package de.blafoo.growatt.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import de.blafoo.growatt.entity.DayResponse;
import de.blafoo.growatt.entity.EnergyRequest;
import de.blafoo.growatt.entity.LoginRequest;
import de.blafoo.growatt.entity.MonthResponse;
import de.blafoo.growatt.entity.TotalDataResponse;
import de.blafoo.growatt.entity.YearResponse;

@FeignClient(name = "growatt-client", url = "http://server.growatt.com")
public interface GrowattFeignClient {
	
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	String login(@RequestBody LoginRequest login);
	
	@GetMapping("/selectPlant/getBusiness") 
	void getBusiness();
	
	@PostMapping(value = "/indexbC/inv/getInvTotalData", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	TotalDataResponse getInvTotalData(@RequestBody EnergyRequest request);
	
	@PostMapping(value = "indexbC/inv/getInvEnergyYearChart", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	YearResponse getInvEnergyYearChart(@RequestBody EnergyRequest request);

	@PostMapping(value = "/indexbC/inv/getInvEnergyMonthChart", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	MonthResponse getInvEnergyMonthChart(@RequestBody EnergyRequest request);
	
	@PostMapping(value = "/indexbC/inv/getInvEnergyDayChart", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	DayResponse getInvEnergyDayChart(@RequestBody EnergyRequest request);

}
