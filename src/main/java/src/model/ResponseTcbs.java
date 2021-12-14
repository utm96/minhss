package src.model;

import java.util.List;

public class ResponseTcbs {
    private String ticker;
    private List<DataFrame> data;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public List<DataFrame> getData() {
        return data;
    }

    public void setData(List<DataFrame> data) {
        this.data = data;
    }
}
