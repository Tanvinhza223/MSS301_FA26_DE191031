package com.mss301.watch;

import com.mss301.watch.entity.*;
import com.mss301.watch.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final WatchCategoryRepository categories; private final WatchRepository watches;
    public DataInitializer(WatchCategoryRepository categories, WatchRepository watches) { this.categories = categories; this.watches = watches; }
    @Override public void run(String... args) {
        if (categories.count() == 0) categories.saveAll(List.of(
            new WatchCategory("Dive Watches", "Designed for underwater diving and feature high water resistance.", "Automatic", "$500 - $10,000"),
            new WatchCategory("Chronograph Watches", "Watches with a stopwatch function in addition to the main display.", "Automatic or Quartz", "$300 - $8,000"),
            new WatchCategory("Dress Watches", "Elegant and simple, intended to be worn with formal wear.", "Automatic or Manual", "$400 - $20,000"),
            new WatchCategory("Digital Watches", "Watches with an LCD or LED screen that show time in a digital format.", "Quartz", "$50 - $500"),
            new WatchCategory("Pilot Watches", "Originally designed for aviators, featuring large, easy-to-read dials.", "Automatic", "$700 - $15,000")));
        if (watches.count() != 0) return;
        List<WatchCategory> c = List.of(
            categories.findById(1).orElseThrow(), categories.findById(2).orElseThrow(),
            categories.findById(3).orElseThrow(), categories.findById(4).orElseThrow(),
            categories.findById(5).orElseThrow());
        watches.saveAll(List.of(
            watch("Submariner Date", c.get(0), 1, "Oystersteel", "Oystersteel", "300 meters", "9550.00", "2020-09-01", 50),
            watch("Speedmaster Moonwatch Professional", c.get(1), 2, "Stainless Steel", "Stainless Steel", "50 meters", "7200.00", "2021-01-05", 75),
            watch("Presage Cocktail Time", c.get(2), 3, "Stainless Steel", "Leather", "50 meters", "425.00", "2019-06-01", 200),
            watch("G-Shock DW-5600E-1V", c.get(3), 4, "Resin", "Resin", "200 meters", "69.95", "1996-06-12", 500),
            watch("Carrera Calibre Heuer 02", c.get(1), 5, "Stainless Steel", "Alligator Leather", "100 meters", "5900.00", "2022-03-30", 60),
            watch("Seamaster Diver 300M", c.get(0), 2, "Stainless Steel", "Rubber", "300 meters", "5400.00", "2018-02-15", 120),
            watch("Prospex SPB143", c.get(0), 3, "Stainless Steel", "Stainless Steel", "200 meters", "1200.00", "2020-07-01", 150),
            watch("Daytona", c.get(1), 1, "Oystersteel", "Oystersteel", "100 meters", "14550.00", "2016-03-16", 30),
            watch("F-91W", c.get(3), 4, "Resin", "Resin", "30 meters", "19.99", "1989-01-01", 1000),
            watch("IWC Pilot's Watch Mark XVIII", c.get(4), 4, "Stainless Steel", "Leather", "60 meters", "4500.00", "2016-01-18", 80)));
    }
    private Watch watch(String name, WatchCategory category, int brand, String caseMaterial, String strap, String water, String price, String date, int stock) {
        return new Watch(name, category, brand, caseMaterial, strap, water, new BigDecimal(price), LocalDate.parse(date), stock);
    }
}
