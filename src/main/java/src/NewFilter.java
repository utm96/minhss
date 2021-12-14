package src;

import com.google.gson.Gson;
import src.model.DataFrame;
import src.model.ResponseTcbs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public class NewFilter {
    public static void main(String[] args) throws Exception {

        String companyCode = "";
        int offset = 0; //-> check ngay trong qua khu
        boolean check = false;

        //todo Filter 1 hour

//        convertTimeToLong();
        int offSetDay = 7;
        Long nowTime = new Date().getTime() / 1000;
        Long fromTime = nowTime - 60 * 60 * 24 * (20 + offSetDay);
        companyCode = "SSI";
        List<DataFrame> dataFrameList1Hour = filterCheck(companyCode, "60", 1, 60*4, fromTime, nowTime);
        //todo filter 30 minute
        if (dataFrameList1Hour.size() > 0) {
            for (DataFrame dataFrame : dataFrameList1Hour) {
                System.out.println("PASS 1/3: " + dataFrameList1Hour.toString());
                Long mineStone = convertTimeToLong(dataFrame.getTradingDate()) - 3600 * 24 * 7;
                Long mineStoneFromTime = mineStone + 3600 * (24 * 7 + 1);
                List<DataFrame> dataFrameList30 = callApiGetValue(companyCode, "30", mineStone, mineStoneFromTime);
                if (dataFrameList30.size() > 0) {
                    int i = 1;
                    if (dataFrame.getTradingDate().contains("T07:00:00.000Z")) {
                        i = 0;
                    }
                    List<DataFrame> dataFrameList30ToCheck = checkListData(i, dataFrameList30);
                    for (DataFrame dataFrame30 : dataFrameList30ToCheck) {
                        System.out.println("PASS 2/3: " + dataFrame30.toString());
                        Long mineStone15 = convertTimeToLong(dataFrame30.getTradingDate()) - 3600 * 24 * 7;
                        Long mineStoneFromTime15 = mineStone + 3600 * (24 * 7) + 1801;
                        List<DataFrame> dataFrameList15Raw = callApiGetValue(companyCode, "15", mineStone15, mineStoneFromTime15);
                        List<DataFrame> dataFrameList15ToCheck = checkListData(1, dataFrameList15Raw);
                        if (dataFrameList15ToCheck.size() > 0) {
                            System.out.println("PASS 3/3: " + dataFrameList15ToCheck.toString());
                        }

                    }

                    System.out.println(dataFrameList30.size());
                }
            }
        }


        //todo filter 15 minute
        // send message
    }

    public static List<DataFrame> checkListData(int i, List<DataFrame> dataFrameList) {
        List<DataFrame> result = new ArrayList<>();
        while (i >= 0) {
//            System.out.println("Checking");
            List<DataFrame> data50 = dataFrameList.subList(dataFrameList.size() - 50 - i, dataFrameList.size() - i);
            //todo get ma20
            Double vol50 = caculateVol50(data50);
            DataFrame lastestRecord = data50.get(49);
            Double lastestVol = lastestRecord.getVolume();
            //todo detect candle
            Boolean checkCandle = checkCandleValueBuy(lastestRecord);
            if (checkCandle && lastestVol > 2 * vol50 && lastestVol * lastestRecord.getLow() > 1000000000d) {
                result.add(lastestRecord);
//                        return responseTcbs.getTicker() + "- lastestVol: " + lastestVol
//                                + "- closePrice: " + lastestRecord.getClose() + "time: " + lastestRecord.getTradingDate();
            }
            i--;
        }
        return result;
    }


    private static Long convertTimeToLong(String timeString) {
//        String s = "2021-10-01T03:00:00.000Z";
//        check = filter1Hour(companyCode);
        TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(timeString);
        Instant i = Instant.from(ta);
        Date d = Date.from(i);
        return d.getTime() / 1000;
    }

    private static List<DataFrame> filterCheck(String companyCode, String resolution, int type, int offSetDayTimeFrame,
                                               Long fromTime, Long toTime) {
        List<DataFrame> dataFrameList = new ArrayList<>();
        Map<String, String> key = new HashMap<String, String>();
        key.put("type", "stock");
        key.put("resolution", resolution);

        key.put("from", fromTime.toString());
        key.put("to", toTime.toString());
        key.put("ticker", companyCode);
        try {
            String request = ParameterStringBuilder.getParamsString(key);
            StringBuffer response = MyGETRequest.callRequest(request);
            System.out.println(response.toString());
            for (int i = offSetDayTimeFrame; i >= 0; i--) {
                DataFrame dataFrame = checkRequestBuy(response, i);
                if (dataFrame != null) {
//                    String time = dataFrame.getTradingDate();
//                    Long fromTimenow = convertTimeToLong(time);
//                    Long toTimenow = fromTimenow + 3600;
                    dataFrameList.add(dataFrame);
//                    System.out.println(dataFrame.toString("BUY "));
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return dataFrameList;
    }
    //todo Filter 1 hour

    public static List<DataFrame> callApiGetValue(String companyCode, String resolution,
                                                  Long fromTime, Long toTime) throws Exception {
//        List<DataFrame> dataFrameList = new ArrayList<>();
        Map<String, String> key = new HashMap<String, String>();
        key.put("type", "stock");
        key.put("resolution", resolution);
        key.put("from", fromTime.toString());
        key.put("to", toTime.toString());
        key.put("ticker", companyCode);
        String request = ParameterStringBuilder.getParamsString(key);
        StringBuffer response = MyGETRequest.callRequest(request);
        Gson gson = new Gson();
        ResponseTcbs responseTcbs = gson.fromJson(response.toString(), ResponseTcbs.class);
        return responseTcbs.getData();
    }

    public static DataFrame checkRequestBuy(StringBuffer response, int i) throws IOException {
        if (response != null) {
            // print result
            Gson gson = new Gson();
            ResponseTcbs responseTcbs = gson.fromJson(response.toString(), ResponseTcbs.class);
            if (responseTcbs.getData().size() >= 50 + i) {
                List<DataFrame> data50 = responseTcbs.getData().subList(responseTcbs.getData().size() - 50 - i, responseTcbs.getData().size() - i);
//                System.out.println("JSON String Result " + response.toString());
                //todo get ma50
                Double vol50 = caculateVol50(data50);
                DataFrame lastestRecord = data50.get(29);
                Double lastestVol = lastestRecord.getVolume();
                //todo detect candle
                Boolean checkCandle = checkCandleValueBuy(lastestRecord);

                //todo push noti tele
                //GetAndPost.POSTRequest(response.toString());
                try {
                    if (checkCandle && lastestVol > 2 * vol50 && lastestVol * lastestRecord.getLow() > 1000000000d) {
                        lastestRecord.setPoint(lastestVol / vol50);
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

    private static Double caculateVol50(List<DataFrame> data50) {
        Double sum = 0d;
        for (DataFrame dataFrame : data50) {
            sum += dataFrame.getVolume();
        }
        return sum / data50.size();
    }

    private Long convertTimeStringToTimeStamp(String timeStamp) {

        return null;
    }

    private static Boolean checkCandleValueBuy(DataFrame lastestRecord) {
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

}
