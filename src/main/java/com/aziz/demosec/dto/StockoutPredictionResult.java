package com.aziz.demosec.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StockoutPredictionResult {

    @JsonProperty("will_stockout")
    private Boolean willStockout;

    @JsonProperty("probability")
    private Double probability;

    @JsonProperty("days_until_stockout")
    private Double daysUntilStockout;

    @JsonProperty("badge")
    private String badge;
}
