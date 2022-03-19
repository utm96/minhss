package src.model;

import java.util.HashMap;
import java.util.Map;

public class Info {
    private String code;
    private String listBigMoney;
    private Double currentPrice;
    private Integer pointBigMoney;
    private Integer pf = 0;
    private Double maxBigPoint = 0d;
    private Double maxPointOfDay = 0d;
//    String today


    public double getMaxPointOfDay() {
        return maxPointOfDay;
    }

    public void setMaxPointOfDay(double maxPointOfDay) {
        this.maxPointOfDay = maxPointOfDay;
    }

    public double getMaxBigPoint() {
        return maxBigPoint;
    }

    public void setMaxBigPoint(double maxBigPoint) {
        this.maxBigPoint = maxBigPoint;
    }

    private Map<String, String> listDayVol = new HashMap<>();

    public Integer getPf() {
        return pf;
    }

    public void setPf(Integer pf) {
        this.pf = pf;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getListBigMoney() {
        return listBigMoney;
    }

    public void setListBigMoney(String listBigMoney) {
        this.listBigMoney = listBigMoney;
    }

    public Integer getPointBigMoney() {
        return pointBigMoney;
    }

    public void setPointBigMoney(Integer pointBigMoney) {
        this.pointBigMoney = pointBigMoney;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Map<String, String> getListDayVol() {
        return listDayVol;
    }

    public void setListDayVol(Map<String, String> listDayVol) {
        this.listDayVol = listDayVol;
    }
}
