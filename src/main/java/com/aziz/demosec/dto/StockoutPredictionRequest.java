package com.aziz.demosec.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockoutPredictionRequest {

    @JsonProperty("national_inv")
    private Double nationalInv;

    @JsonProperty("lead_time")
    private Double leadTime;

    @JsonProperty("in_transit_qty")
    private Double inTransitQty;

    @JsonProperty("forecast_3_month")
    private Double forecast3Month;

    @JsonProperty("forecast_6_month")
    private Double forecast6Month;

    @JsonProperty("sales_1_month")
    private Double sales1Month;

    @JsonProperty("sales_3_month")
    private Double sales3Month;

    @JsonProperty("sales_6_month")
    private Double sales6Month;

    @JsonProperty("min_bank")
    private Double minBank;

    @JsonProperty("pieces_past_due")
    private Double piecesPastDue;

    @JsonProperty("perf_6_month_avg")
    private Double perf6MonthAvg;

    @JsonProperty("perf_12_month_avg")
    private Double perf12MonthAvg;

    @JsonProperty("avg_daily_sales")
    private Double avgDailySales;

    @JsonProperty("days_until_stockout")
    private Double daysUntilStockout;
}
