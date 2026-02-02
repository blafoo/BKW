package de.blafoo.bkw.views.bkw;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.blafoo.bkw.views.MainLayout;
import de.blafoo.growatt.controller.GrowattWebClient;
import de.blafoo.growatt.entity.DevicesResponse;
import de.blafoo.growatt.entity.YearResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.List;

@PageTitle("BKW (Growatt)")
@Route(value = "bkwgrowatt", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class BkwGrowattView extends BkwView {
	
	private final GrowattWebClient growatt;
	
	public BkwGrowattView(GrowattWebClient growatt, @Value("${growatt.account}") String account, @Value("${growatt.password}") String password) {
        super(account, password);
        
        this.growatt = growatt;
    }

	@Override
	protected String login() {
		growatt.login(account, password);
        return growatt.getPlantId();
    }

    @Override
    protected DevicesResponse getDevicesByPlantList(@NonNull String plantId) {
        return growatt.getDevicesByPlantList(plantId);
    }

    @Override
    protected YearResponse getEnergyTotalChart(@NonNull String plantId, int year) {
        return growatt.getEnergyTotalChart(plantId, year);
    }

    @Override
	protected List<Double> getYearlyProduction(@NonNull String plantId, int year) {
		return growatt.getEnergyYearChart(plantId, year).getObj().getFirst().getDatas().getEnergy();
    }
    
    @Override
    protected List<Double> getMonthlyProduction(@NonNull String plantId, LocalDate date) {
    	return growatt.getEnergyMonthChart(plantId, date).getObj().getFirst().getDatas().getEnergy();
    }
    
    @Override
    protected List<Double> getDailyProduction(@NonNull String plantId, LocalDate date) {
        return growatt.getEnergyDayChart(plantId, date).getObj().getFirst().getDatas().getPac();
    }

}