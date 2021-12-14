package src.model;

public class DataFrame {
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double volume;
    private String tradingDate;
    private String symbol;
    private double point;

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public String getTradingDate() {
        return tradingDate;
    }

    public void setTradingDate(String tradingDate) {
        this.tradingDate = tradingDate;
    }

//    @Override
    public String toString(String prefix) {
        return prefix + "DataFrame{" +
                "point" + point +
                "open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                ", tradingDate='" + tradingDate + '\'' +
                ", symbol='" + symbol + '\'' +
                '}';
    }
    public String toString() {
        return "DataFrame{" +
                "point" + point +
                "open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                ", tradingDate='" + tradingDate + '\'' +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
