package de.blafoo.bkw.growatt.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import de.blafoo.bkw.growatt.entity.DayResponse;
import de.blafoo.bkw.growatt.entity.EnergyRequest;
import de.blafoo.bkw.growatt.entity.LoginRequest;
import de.blafoo.bkw.growatt.entity.MonthResponse;
import de.blafoo.bkw.growatt.entity.TotalDataResponse;
import de.blafoo.bkw.growatt.entity.YearResponse;

@FeignClient(name = "growatt-client", url = "http://server.growatt.com")
public interface GrowattFeignClient {
	
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	String login(@RequestBody LoginRequest login);
	
	@GetMapping("/selectPlant/getBusiness") 
	void getBusiness();
	
	@PostMapping(value = "/indexbC/getTotalData", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	TotalDataResponse getTotalData(EnergyRequest energyRequest);
	
	@PostMapping(value = "/indexbC/inv/getInvTotalData", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	TotalDataResponse getInvTotalData(@RequestBody EnergyRequest request);
	
	@PostMapping(value = "indexbC/inv/getInvEnergyYearChart", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	YearResponse getInvEnergyYearChart(@RequestBody EnergyRequest request);

	@PostMapping(value = "/indexbC/inv/getInvEnergyMonthChart", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	MonthResponse getInvEnergyMonthChart(@RequestBody EnergyRequest request);
	
	@PostMapping(value = "/indexbC/inv/getInvEnergyDayChart", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	DayResponse getInvEnergyDayChart(@RequestBody EnergyRequest request);
}
