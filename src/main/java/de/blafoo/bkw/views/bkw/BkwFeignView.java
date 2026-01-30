package de.blafoo.bkw.views.bkw;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.blafoo.bkw.views.MainLayout;
import de.blafoo.growatt.controller.GrowattWebClient;
import de.blafoo.growatt.entity.TotalDataResponse;
import de.blafoo.growatt.feign.GrowattFeignClient;
import de.blafoo.growatt.feign.GrowattFeignCookieJar;
import de.blafoo.growatt.md5.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@PageTitle("BKW (Feign)")
@Route(value = "bkwfeign", layout = MainLayout.class)
public class BkwFeignView extends BkwView {
	
	private final GrowattFeignCookieJar cookieJar;
	
	private final GrowattFeignClient growatt;
	
	public BkwFeignView(@Autowired GrowattFeignClient growatt, @Autowired GrowattFeignCookieJar cookieJar, @Value("${growatt.account}") String account, @Value("${growatt.password}") String password) {
        super(account, password);
        
        this.cookieJar = cookieJar;
        this.growatt = growatt;
    }

    private String createBody(@NonNull String plantId, @Nullable String date, @Nullable Integer year, @Nullable String params) {
        var map = GrowattWebClient.createBody(plantId, date, year, params);
        return UriComponentsBuilder.newInstance()
                .queryParams(map)
                .build()
                .encode()
                .getQuery();
    }

	@Override
	protected String login() {
    	growatt.login("account=%s&passwordCrc=%s".formatted(account, MD5.md5(password)));
        return cookieJar.getCookie("onePlantId", null);
    }
    
    @Override
    protected TotalDataResponse getTotalData(@NonNull String plantId) {
    	return growatt.getTotalData(createBody(plantId, null, null, ""));
    }
    
	@Override
	protected List<Double> getYearlyProduction(@NonNull String plantId, int year) {
		return growatt.getEnergyYearChart(createBody(plantId, null, year, "")).getObj().getFirst().getDatas().getEnergy();
    }
    
    @Override
    protected List<Double> getMonthlyProduction(@NonNull String plantId, LocalDate date) {
    	return growatt.getEnergyMonthChart(createBody(plantId, date.format(DateTimeFormatter.ofPattern(GrowattWebClient.PATTERN_MONTH)), null, "energy,autoEnergy")).getObj().getFirst().getDatas().getEnergy();
    }
    
    @Override
    protected List<Double> getDailyProduction(@NonNull String plantId, LocalDate date) {
    	return growatt.getEnergyDayChart(createBody(plantId, date.format(DateTimeFormatter.ofPattern(GrowattWebClient.PATTERN_DAY)), null, "pac")).getObj().getFirst().getDatas().getPac();
    }

}