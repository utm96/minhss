package src.model;

import src.FileConverToObject;
import src.MyGETRequest;
import src.ParameterStringBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CheckCodeRunable implements Runnable {
    private ConcurrentHashMap<String, Info> listCode;
    private String code;
    private int threshHold = 3;
    private int volDefault = 20;
    private int dayLast = 0;

    public CheckCodeRunable(ConcurrentHashMap<String, Info> listCode, String code, int threshHold, int volDefault, int dayLast) {
        this.listCode = listCode;
        this.code = code;
        this.threshHold = threshHold;
        this.volDefault = volDefault;
        this.dayLast = dayLast;
    }

    @Override
    public void run() {
        try {
            runCheckCode(listCode, code);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void runCheckCode(ConcurrentHashMap<String, Info> listCode, String code)
            throws UnsupportedEncodingException {
        Map<String, String> key = new HashMap<String, String>();
        key.put("type", "stock");
        key.put("resolution", "5");
        Long nowTime = new Date().getTime() / 1000;
        Long fromTime = nowTime - 60 * 60 * 24 * 150;
        key.put("from", fromTime.toString());
        key.put("to", nowTime.toString());
//        FileConverToObject fileConverToObject = new FileConverToObject();

        key.put("ticker", code);
        String request = ParameterStringBuilder.getParamsString(key);
        int counter = 0;
        try {
            StringBuffer response = MyGETRequest.callRequest(request);
            double recommendPrice = 0d;
            StringBuilder stringBuilderBigMoney = new StringBuilder();
            double maxPoint = 0;
            double maxPointOfDay = 0;
            for (int i = 1500; i > -1; i--) {
                DataFrame s = MyGETRequest.checkRequestBuy(response, i, threshHold);
                if (s != null) {
                    if (maxPoint < s.getPoint()) {
                        maxPoint = s.getPoint();
                    }
                    if (maxPointOfDay < s.getPoint() && i < 56) {
                        maxPointOfDay = s.getPoint();
                    }
                    counter++;
//                    System.out.println(s.toString());
                    stringBuilderBigMoney.append(s.toString("")).append("\n");
                }
            }
            Info info = null;
            if (!stringBuilderBigMoney.toString().isEmpty()) {
                info = new Info();
                info.setPointBigMoney(counter);
                info.setCode(code);
                info.setMaxBigPoint(maxPoint);
                info.setMaxPointOfDay(maxPointOfDay);
                info.setListBigMoney(stringBuilderBigMoney.toString());
            }
            if (info != null) {
                Map<String, String> keyDay = new HashMap<String, String>();
                keyDay.put("type", "stock");
                keyDay.put("ticker", code);
                keyDay.put("from", fromTime.toString());
                keyDay.put("to", nowTime.toString());
                keyDay.put("resolution", "D");
                String requestDay = ParameterStringBuilder.getParamsString(keyDay);
                StringBuffer responseDay = MyGETRequest.callRequestDay(requestDay);
                double currentPrice = MyGETRequest.getCurrentPrice(responseDay);
                info.setCurrentPrice(currentPrice);

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i <= dayLast; i++) {
                    DataFrame s = MyGETRequest.checkRequestBuyDay(responseDay, i, volDefault, info);
                    if (s != null) {
                        info.getListDayVol().put(s.getTradingDate(), String.valueOf(s.getPoint()));
                    }
                }
                listCode.put(code, info);
            }
            System.out.println("finish");
        } catch (Exception e) {
            System.out.println("error: " + code + e.getMessage());
        }
    }

}
