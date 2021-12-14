package src;

import com.google.gson.Gson;
import src.model.DataFrame;
import src.model.ResponseTcbs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class CheckCashFlowByWeek {
    public static DataFrame MyGetRequest(String param, int i) throws IOException {
        String url = "https://apipubaws.tcbs.com.vn/stock-insight/v1/stock/bars?" + param;
        System.out.println(url);
        URL urlForGetRequest = new URL(url);
//        System.out.println(urlForGetRequest.get);
        String readLine = null;
//        List<DataFrame> result = new ArrayList<DataFrame>();
        HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
        conection.setRequestMethod("GET");
//        conection.setRequestProperty("userId", "a1bcdef"); // set userId its a sample here
        int responseCode = conection.getResponseCode();


        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();
            // print result
            Gson gson = new Gson();
            ResponseTcbs responseTcbs = gson.fromJson(response.toString(), ResponseTcbs.class);
            if (responseTcbs.getData().size() >= 100 + i) {
                List<DataFrame> data50 = responseTcbs.getData().subList(responseTcbs.getData().size() - 100 - i, responseTcbs.getData().size() - i);
//                System.out.println("JSON String Result " + response.toString());
                //todo get ma50
                Double vol50 = caculateVol50(data50);
                DataFrame lastestRecord = data50.get(99);
                Double lastestVol = lastestRecord.getVolume();
                //todo detect candle
                Boolean checkCandle = checkCandleValue(lastestRecord);

                //todo push noti tele
                //GetAndPost.POSTRequest(response.toString());
                try {
                    if (checkCandle && lastestVol > 2 * vol50 && lastestVol * lastestRecord.getLow() > 1000000000d) {
                        lastestRecord.setSymbol(responseTcbs.getTicker());
                        return lastestRecord;
//                        return responseTcbs.getTicker() + "- lastestVol: " + lastestVol
//                                + "- closePrice: " + lastestRecord.getClose() + "time: " + lastestRecord.getTradingDate();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } else {
            System.out.println("GET NOT WORKED");
        }
        return null;
    }

    public static DataFrame checkCashFlowByWeek(String param, int i) throws IOException {
        String url = "https://apipubaws.tcbs.com.vn/stock-insight/v1/stock/bars-long-term?" + param;
        System.out.println(url);
        URL urlForGetRequest = new URL(url);
//        System.out.println(urlForGetRequest.get);
        String readLine = null;
//        List<DataFrame> result = new ArrayList<DataFrame>();
        HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
        conection.setRequestMethod("GET");
//        conection.setRequestProperty("userId", "a1bcdef"); // set userId its a sample here
        int responseCode = conection.getResponseCode();


        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();
            // print result
            Gson gson = new Gson();
            ResponseTcbs responseTcbs = gson.fromJson(response.toString(), ResponseTcbs.class);
            if (responseTcbs.getData().size() >= 20 + i) {
                List<DataFrame> data20 = responseTcbs.getData().subList(responseTcbs.getData().size() - 20 - i, responseTcbs.getData().size() - i);
//                System.out.println("JSON String Result " + response.toString());
                //todo get ma50
                Double vol20 = caculateVolI(data20, 20);
                DataFrame lastestRecord = data20.get(19);
                Double lastestVol = lastestRecord.getVolume();
                //todo detect candle
//                Boolean checkCandle = checkCandleValue(lastestRecord);

                //todo push noti tele
                //GetAndPost.POSTRequest(response.toString());
                try {
                    if (lastestVol > 2 * vol20 && lastestVol > 10000000) {
                        lastestRecord.setSymbol(responseTcbs.getTicker());
                        return lastestRecord;
//                        return responseTcbs.getTicker() + "- lastestVol: " + lastestVol
//                                + "- closePrice: " + lastestRecord.getClose() + "time: " + lastestRecord.getTradingDate();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } else {
            System.out.println("GET NOT WORKED");
        }
        return null;
    }

    private static Boolean checkCandleValue(DataFrame lastestRecord) {
        Double high = lastestRecord.getHigh();
        Double low = lastestRecord.getLow();
        Double close = lastestRecord.getClose();
        Double open = lastestRecord.getOpen();
        Double cellingCandle = Math.min(close, open);
        Double tailCandle = cellingCandle - low;
        Double candleLength = high - low;
        if (tailCandle / candleLength >= 0.5) {
//            System.out.println("OK");
            return true;
        }
//        System.out.println("OK");

        return false;
    }

    private static Double caculateVol50(List<DataFrame> data50) {
        Double sum = 0d;
        for (DataFrame dataFrame : data50) {
            sum += dataFrame.getVolume();
        }
        return sum / 50;
    }
    private static Double caculateVolI(List<DataFrame> data50,int i) {
        Double sum = 0d;
        for (DataFrame dataFrame : data50) {
            sum += dataFrame.getVolume();
        }
        return sum / i;
    }

    public static void main(String[] args) throws IOException {
        Map<String, String> key = new HashMap<String, String>();
        key.put("type", "stock");
        key.put("resolution", "W");
        Long nowTime = new Date().getTime() / 1000;
        Long fromTime = nowTime - 60 * 60 * 24 * 365;
        key.put("from", fromTime.toString());
        key.put("to", nowTime.toString());
        FileConverToObject fileConverToObject = new FileConverToObject();
//        List<String> listSymbol = fileConverToObject.getListStringFromFile("./hnx.json");
        List<String> listSymbol = fileConverToObject.getListStringFromFile("D:\\minhut\\java\\bot-stock\\src\\main\\resources\\hnx.json");
        for (int i = 12; i > -1; i--) {
            List<DataFrame> result = new ArrayList<DataFrame>();
            for (String code : listSymbol) {
                key.put("ticker", code);
                String request = ParameterStringBuilder.getParamsString(key);
                try {
                    DataFrame s = checkCashFlowByWeek(request, i);
                    if (s != null) {
                        result.add(s);
                    }

                } catch (IOException e) {
                    System.out.println("error: " + code);
                }
            }
            try {
                if (result.size() > 0) {
                    String time = result.get(0).getTradingDate();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (DataFrame dataFrame : result) {
                        stringBuffer.append(dataFrame.getSymbol() + " ");
                    }
                    TelegramNotifier.sendMessage(time + " : " + stringBuffer.toString());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }
}
