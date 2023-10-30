package de.blafoo.views.bkw;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.blafoo.growatt.entity.EnergyRequest;
import de.blafoo.growatt.entity.LoginRequest;
import de.blafoo.growatt.entity.TotalDataResponse;
import de.blafoo.growatt.feign.GrowattFeignClient;
import de.blafoo.growatt.feign.GrowattFeignCookieJar;
import de.blafoo.views.MainLayout;

@PageTitle("BKW (Feign)")
@Route(value = "bkwfeign", layout = MainLayout.class)
public class BkwFeignView extends BkwView {
	
	private GrowattFeignCookieJar cookieJar;
	
	private GrowattFeignClient growatt;
	
	public BkwFeignView(@Autowired GrowattFeignClient growatt, @Autowired GrowattFeignCookieJar cookieJar, @Value("${growatt.account}") String account, @Value("${growatt.password}") String password) {
        super(account, password);
        
        this.cookieJar = cookieJar;
        this.growatt = growatt;
    }

	@Override
	protected String login() {
    	 growatt.login(new LoginRequest(account, password));
    	 growatt.getBusiness();
    	 return cookieJar.getCookie("onePlantId", null);
    }
    
    @Override
    protected TotalDataResponse getTotalData(@NonNull String plantId) {
    	return growatt.getTotalData(new EnergyRequest(plantId, null));
    }
    
	@Override
	protected List<Double> getYearlyProduction(@NonNull String plantId, String date) {
		return growatt.getInvEnergyYearChart(new EnergyRequest(plantId, date)).getObj().getEnergy();
    }
    
    @Override
    protected List<Double> getMonthlyProduction(@NonNull String plantId, String date) {
    	return growatt.getInvEnergyMonthChart(new EnergyRequest(plantId, date)).getObj().getEnergy();
    }
    
    @Override
    protected List<Double> getDailyProduction(@NonNull String plantId, String date) {
    	return growatt.getInvEnergyDayChart(new EnergyRequest(plantId, date)).getObj().getPac();
    }

}