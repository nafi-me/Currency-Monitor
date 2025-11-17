package com.livefx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.util.Duration;
import java.time.LocalTime;

public class Controller {

    @FXML private ComboBox<String> fromCurrency;
    @FXML private ComboBox<String> toCurrency;
    @FXML private Label rateLabel;
    @FXML private Label changeLabel;
    @FXML private LineChart<String, Number> chart;

    private XYChart.Series<String, Number> series = new XYChart.Series<>();
    private double previousRate = -1;

    @FXML
    public void initialize() {
        fromCurrency.getItems().addAll(CurrencyList.ALL);
        toCurrency.getItems().addAll(CurrencyList.ALL);

        fromCurrency.setValue("BDT");
        toCurrency.setValue("RUB");

        chart.getData().add(series);

        startLiveUpdates();
    }

    private void startLiveUpdates() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateRate()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateRate() {
        String from = fromCurrency.getValue();
        String to = toCurrency.getValue();

        double rate = ApiService.getRate(from, to);

        if (rate == -1) return;

        rateLabel.setText(String.format("1 %s = %.5f %s", from, rate, to));

        if (previousRate != -1) {
            double diff = rate - previousRate;
            double percent = (diff / previousRate) * 100;

            if (diff > 0)
                changeLabel.setText(String.format("+%.4f (%.2f%%) ▲", diff, percent));
            else
                changeLabel.setText(String.format("%.4f (%.2f%%) ▼", diff, percent));
        }

        previousRate = rate;

        // Add to chart
        String time = LocalTime.now().toString().substring(0, 8);
        series.getData().add(new XYChart.Data<>(time, rate));

        if (series.getData().size() > 60)
            series.getData().remove(0);
    }
}
