package de.blafoo.bkw.views.bkw;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import de.blafoo.growatt.entity.DevicesResponse;
import de.blafoo.growatt.entity.YearResponse;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@PreserveOnRefresh
abstract class BkwView extends Main implements AfterNavigationObserver {
	
	protected String account;
	
	protected String password;
	
	private String plantId;

	private DevicesResponse totalData;
	
	private Select<Integer> yearSelect;
	
	private Integer month;
	
	private Chart yearChart;
	
    private Chart monthChart;

	public BkwView(String account, String password) {
		if ( StringUtils.isBlank(account) || StringUtils.isBlank(password) )
			throw new IllegalArgumentException("account and password must not be empty - add them to the application.properites");
		
        this.account = account;
        this.password = password;
	}

	@PostConstruct
	protected void init() {
        addClassName("bkw-view");
        int thisYear = LocalDate.now().getYear();
        
        plantId = login();

        totalData = getDevicesByPlantList(plantId);
        YearResponse years = getEnergyTotalChart(plantId, thisYear);
        long yearsWithProduction = years.getObj().getFirst().getDatas().getEnergy().stream().filter(energy -> energy > 0).count();
        
        yearSelect = new Select<>();
        yearSelect.setItems(IntStream.range(thisYear-(int)yearsWithProduction+1, thisYear+1).boxed().toList());
        yearSelect.setValue(thisYear);
        yearSelect.addValueChangeListener(e -> UI.getCurrent().navigate(this.getClass()));
	}
	
	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		this.removeAll();

        Board board = new Board();
        board.addRow(
        		createHighlight("Total production", totalData.getObj().getDatas().getFirst().getETotal(), null),
        		createHighlight("Monthly production", totalData.getObj().getDatas().getFirst().getEMonth(), null),
        		createHighlight("Daily production", totalData.getObj().getDatas().getFirst().getEToday(), null),
        		createHighlight("Current production", totalData.getObj().getDatas().getFirst().getPac(), null)
        		);
        board.addRow(createYearlyOverview(plantId));
        board.addRow(createMonthlyOverview(plantId), createDailyOverview(plantId));
        
        add(board);
    }

    private Component createHighlight(String title, Double value, Double average) {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName(Padding.SMALL);
        layout.setPadding(false);
        layout.setSpacing(false);

        H2 h2 = new H2(title);
        h2.addClassNames(FontWeight.NORMAL, Margin.NONE, TextColor.SECONDARY, FontSize.XSMALL);
        layout.add(h2);

        HorizontalLayout hl = new HorizontalLayout();
        layout.add(hl);
        Span span = new Span(String.format("%.2f", value));
        span.addClassNames(FontWeight.SEMIBOLD, FontSize.XXXLARGE);
        hl.add(span);

        if (average != null) {
        	VaadinIcon icon = VaadinIcon.ARROW_UP;
            String prefix = "";
            String theme = "badge";

            if (average == null || value == average) {
            	icon = VaadinIcon.ARROW_RIGHT;
                prefix = "±";
            } else if (value > average) {
                prefix = "+";
                theme += " success";
            } else {
                icon = VaadinIcon.ARROW_DOWN;
                theme += " error";
            }
	      
            Icon i = icon.create();
	        i.addClassNames(BoxSizing.BORDER, Padding.XSMALL);
	
	        Span badge = new Span(i, new Span(String.format("%s %.2f", prefix, average)));
	        badge.getElement().getThemeList().add(theme);
	        
	        hl.add(badge);
        }

        return layout;
    }

    private Component createYearlyOverview(String plantId) {
 
        HorizontalLayout header = createHeader("Yearly production", "kWh/month");
        TextField user = new TextField();
        user.setEnabled(false);
        user.setValue(account);
        header.add(user, yearSelect);

        yearChart = new Chart(ChartType.COLUMN);
        Configuration conf = yearChart.getConfiguration();
        conf.getChart().setStyledMode(true);

        String[] categories = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        conf.getxAxis().setCategories(categories);
        conf.getyAxis().setTitle("kWh");

        var yearlyProduction = getYearlyProduction(plantId, yearSelect.getValue());
        
        DataSeries series = new DataSeries(String.valueOf(yearSelect.getValue()));
        for (int m = 1; m <= 12; m++) {
        	DataSeriesItem monthItem = new DataSeriesItem(categories[m-1], yearlyProduction.get(m-1));
            series.addItemWithDrilldown(monthItem);
        }
        conf.addSeries(series);

        VerticalLayout viewEvents = new VerticalLayout(header, yearChart);
        viewEvents.addClassName(Padding.SMALL);
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-l");
        return viewEvents;
    }
    
    private Component createMonthlyOverview(String plantId) {
    	
        HorizontalLayout header = createHeader("Monthly production", "kWh/day");

        monthChart = new Chart(ChartType.COLUMN);
        Configuration conf = monthChart.getConfiguration();
        conf.getChart().setStyledMode(true);
        conf.getyAxis().setTitle("kWh");

        DataSeries series = new DataSeries("no month selected");
        conf.addSeries(series);
        
        yearChart.addDrilldownListener(dde -> {
        	
        	month = dde.getItemIndex()+1;
       	
        	var monthlyProduction = getMonthlyProduction(plantId, LocalDate.of(yearSelect.getValue(), month, 1));
        	
        	String[] categories = IntStream.range(1, monthlyProduction.size()+1).mapToObj(String::valueOf).toArray(String[]::new);
            conf.getxAxis().setCategories(categories);
        	
        	DataSeries monthlyDrillDownSeries = new DataSeries(dde.getCategory());
            for (int day = 1; day <= monthlyProduction.size(); day++) {
             	monthlyDrillDownSeries.addItemWithDrilldown(new DataSeriesItem(String.valueOf(day), monthlyProduction.get(day-1)));
            }
            
            monthChart.getConfiguration().setSeries(monthlyDrillDownSeries);
            monthChart.drawChart();
        });
        
        VerticalLayout viewEvents = new VerticalLayout(header, monthChart);
        viewEvents.addClassName(Padding.SMALL);
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-l");
        return viewEvents;
    }
    
    private Component createDailyOverview(String plantId) {
        HorizontalLayout header = createHeader("Daily production", "kWh/5min");

        Chart dayChart = new Chart(ChartType.LINE);
        Configuration conf = dayChart.getConfiguration();
        conf.getChart().setStyledMode(true);
        
        List<String> categories = new ArrayList<>();
        IntStream.rangeClosed(0, 24*12 -1).forEach(i -> categories.add(LocalTime.ofSecondOfDay(i*5*60).format(DateTimeFormatter.ofPattern("HH:mm"))));
        conf.getxAxis().setCategories(categories.toArray(new String[0]));
        conf.getyAxis().setTitle("kWh");

        DataSeries series = new DataSeries("no day selected");
        conf.addSeries(series);
        
        monthChart.addDrilldownListener(dde -> {
        	var dailyProduction = getDailyProduction(plantId, LocalDate.of(yearSelect.getValue(), month, dde.getItemIndex() + 1));
        	
        	DataSeries dailyDrillDownSeries = new DataSeries(dde.getCategory());
            for (int day = 1; day <= dailyProduction.size(); day++) {
            	dailyDrillDownSeries.add(new DataSeriesItem(String.valueOf(day), dailyProduction.get(day-1)));
            }
            
            dayChart.getConfiguration().setSeries(dailyDrillDownSeries);
            dayChart.drawChart();
        });
        
        VerticalLayout viewEvents = new VerticalLayout(header, dayChart);
        viewEvents.addClassName(Padding.SMALL);
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-l");
        return viewEvents;
    }

    private HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassNames(FontSize.XLARGE, Margin.NONE);

        Span span = new Span(subtitle);
        span.addClassNames(TextColor.SECONDARY, FontSize.XSMALL);

        VerticalLayout column = new VerticalLayout(h2, span);
        column.setPadding(false);
        column.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(column);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setSpacing(false);
        header.setWidthFull();
        return header;
    }
    
    protected abstract String login();

    protected abstract DevicesResponse getDevicesByPlantList(@NonNull String plantId);

    protected abstract YearResponse getEnergyTotalChart(@NonNull String plantId, int year);

    protected abstract List<Double> getYearlyProduction(@NonNull String plantId, int year);
    
    protected abstract List<Double> getMonthlyProduction(@NonNull String plantId, LocalDate date);
    
    protected abstract List<Double> getDailyProduction(@NonNull String plantId, LocalDate date);

}