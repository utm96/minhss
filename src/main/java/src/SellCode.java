package src;

import src.model.DataFrame;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SellCode {
    public static void main(String[] args) throws Exception {
        Map<String, String> key = new HashMap<String, String>();
        key.put("type", "stock");
        key.put("resolution", "5");
        Long nowTime = new Date().getTime() / 1000;
        Long fromTime = nowTime - 60 * 60 * 24 * 150;
        key.put("from", fromTime.toString());
        key.put("to", nowTime.toString());
//        FileConverToObject fileConverToObject = new FileConverToObject();

        key.put("ticker", "BSI");
        String request = ParameterStringBuilder.getParamsString(key);
        int counter = 0;
        try {
            StringBuffer response = MyGETRequest.callRequest(request);
            for (int i = 0; i < 1000; i++) {
                DataFrame dataFrame = MyGETRequest.checkRequestSell(response, i);
                if (dataFrame != null) {
                    System.out.println(dataFrame.toString());
                }
            }
        } catch (Exception e) {

        }

    }
}
