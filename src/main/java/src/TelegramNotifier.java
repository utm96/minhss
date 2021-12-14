package src;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

public class TelegramNotifier {

    private static final String INSTANCE_ID = "YOUR_INSTANCE_ID_HERE";
    private static final String CLIENT_ID = "YOUR_CLIENT_ID_HERE";
    private static final String CLIENT_SECRET = "YOUR_CLIENT_SECRET_HERE";
    private static final String GATEWAY_URL = "http://api.whatsmate.net/v1/telegram/single/message/" + INSTANCE_ID;

    /**
     * Entry Point
     */
    public static void main(String[] args) throws Exception {
        String number = "12025550108";  // Specify the recipient's number (NOT the gateway number) here.
        String message = "Have a nice day! Loving you:)";  // FIXME

        TelegramNotifier.sendMessage("sadhkSASJDHSKAD ASKD ASJKDASKDH KASHD KASK DHASKJDHJKD KHSAKL HDJKS HDJK HS HSk hsdkjsdhkhkSHS hskdh kHD JKASDHs skhdkHDHhdsjkAHDKh dh DK hdjkhAKHDKSDJHKSj hdkjHkdh sHDS j");
    }

    /**
     * Sends out a Telegram message via WhatsMate Telegram Gateway.
     */
    public static void sendMessage(String code) throws Exception {
        // TODO: Should have used a 3rd party library to make a JSON string from an object
        TelegramBot bot = new TelegramBot("1761811276:AAGrnieV12HNIrr28gioET-8PKpSUCATExQ");
        SendResponse response = bot.execute(new SendMessage(-599432035, code));

    }

}
