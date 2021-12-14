package src;

import com.google.gson.Gson;
import src.model.Company;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileConverToObject {
    public List<String> getListStringFromFile(String fileName) throws IOException {
        List<String> result = new ArrayList<String>();
        //read from file
        File file = new File(fileName);
        BufferedReader br = new BufferedReader(new FileReader(file));

        StringBuffer stringBuffer = new StringBuffer();
        String st;
        while ((st = br.readLine()) != null) {
            stringBuffer.append(st);
        }
        Gson gson = new Gson();
        Company[] jsonArray = gson.fromJson(stringBuffer.toString().toLowerCase(), Company[].class);
        for (Company company : jsonArray) {
            if (company.getSymbol() != null) {
                result.add(company.getSymbol().toUpperCase());
            }
        }
//        System.out.println(result.toString());
        return result;
    }

    public static void main(String[] args) throws IOException {
        FileConverToObject fileConverToObject = new FileConverToObject();
        fileConverToObject.getListStringFromFile("D:\\minhut\\java\\bot-stock\\src\\main\\resources\\hnx.json");
    }
}
