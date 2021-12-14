package src.model;

public class Company {
    //    {
//        "Symbol": "VTO",
//            "CompanyName": "Công ty Cổ phần Vận tải Xăng dầu VITACO",
//            "TradeCenter": "HSX",
//            "Price": 9.24,
//            "PE": 7.94,
//            "EPS": 1.16
//    },
    private String symbol;
    private String companyName;
    private String tradeCenter;
    private Double price;
    private Double pe;
    private Double eps;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTradeCenter() {
        return tradeCenter;
    }

    public void setTradeCenter(String tradeCenter) {
        this.tradeCenter = tradeCenter;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPe() {
        return pe;
    }

    public void setPe(Double pe) {
        this.pe = pe;
    }

    public Double getEps() {
        return eps;
    }

    public void setEps(Double eps) {
        this.eps = eps;
    }
}
