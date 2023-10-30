package de.blafoo.views.bkw;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import de.blafoo.growatt.controller.GrowattWebClient;
import de.blafoo.growatt.entity.EnergyRequest;
import de.blafoo.growatt.entity.LoginRequest;
import de.blafoo.growatt.entity.TotalDataResponse;
import de.blafoo.views.MainLayout;

@PageTitle("BKW (Growatt)")
@Route(value = "bkwgrowatt", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class BkwGrowattView extends BkwView {
	
	private GrowattWebClient growatt;
	
	public BkwGrowattView(@Autowired GrowattWebClient growatt, @Value("${growatt.account}") String account, @Value("${growatt.password}") String password) {
        super(account, password);
        
        this.growatt = growatt;
    }

	@Override
	protected String login() {
		growatt.login(new LoginRequest(account, password));
        return growatt.getPlantId();
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