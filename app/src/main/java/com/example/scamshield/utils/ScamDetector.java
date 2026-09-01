package com.example.scamshield.utils;

import java.util.ArrayList;
import java.util.List;

public class ScamDetector {

    // All scam keywords
    private static final String[] SCAM_KEYWORDS = {
            // Original keywords
            "tuma", "shilingi", "droo", "zawadi", "pesa",
            "maganaga", "mwenye nyumba", "kodi", "mkopo",
            "benki", "akaunti", "namba", "tuzo",

            // New keywords
            "0614159000", "0623638092", "0784062546",
            "ABEL MASIMA", "abel masima",
            "tiba", "uzazi", "masomo", "pete", "mali",
            "kuludisha mke", "kuludisha mume",
            "asili", "kafala", "mtaalam",
            "AirtelMoney", "ela", "Nitumie",
            "malipo", "baada ya tiba"
    };

    // Phone numbers to detect
    private static final String[] SCAM_PHONES = {
            "0614159000", "0623638092", "0784062546"
    };

    public static ScamResult detect(String message, String sender) {
        boolean isScam = false;
        String scamType = "Unknown";
        String dangerLevel = "Low";
        List<String> indicators = new ArrayList<>();

        // Check for scam phone numbers
        for (String phone : SCAM_PHONES) {
            if (message.contains(phone) || (sender != null && sender.contains(phone))) {
                isScam = true;
                scamType = "Fake Healer/Traditional Scam";
                dangerLevel = "High Risk";
                indicators.add("Suspicious phone number: " + phone);
                break;
            }
        }

        // Check for scam keywords
        if (!isScam) {
            for (String keyword : SCAM_KEYWORDS) {
                if (message.toLowerCase().contains(keyword.toLowerCase())) {
                    isScam = true;
                    indicators.add("Contains keyword: " + keyword);
                }
            }
        }

        // Determine scam type and danger level
        if (isScam) {
            ScamResult result = determineScamType(message, sender);
            scamType = result.scamType;
            dangerLevel = result.dangerLevel;
            indicators.addAll(result.indicators);
        }

        return new ScamResult(isScam, scamType, dangerLevel, indicators);
    }

    private static ScamResult determineScamType(String message, String sender) {
        String msg = message.toLowerCase();
        List<String> indicators = new ArrayList<>();
        String scamType = "General Scam";
        String dangerLevel = "Medium Risk";

        // Traditional Healer Scam
        if (msg.contains("tiba") || msg.contains("uzazi") || msg.contains("pete") ||
                msg.contains("mali") || msg.contains("asili") || msg.contains("kafala") ||
                msg.contains("mtaalam") || msg.contains("kuludisha")) {
            scamType = "Fake Traditional Healer Scam";
            dangerLevel = "High Risk";
            indicators.add("Fake healer/traditional promises");
            indicators.add("Requests for money");
        }

        // Money Request Scam
        if (msg.contains("ela") || msg.contains("nitumie") || msg.contains("airtelmoney") ||
                msg.contains("malipo") || msg.contains("tuma")) {
            scamType = "Money Request Scam";
            dangerLevel = "High Risk";
            indicators.add("Direct money request");
            indicators.add("Suspicious payment method (AirtelMoney)");
        }

        // Phone Number Scam
        if (msg.contains("0614159000") || msg.contains("0623638092") ||
                msg.contains("0784062546") || msg.contains("ABEL MASIMA")) {
            scamType = "Fake Phone Number Scam";
            dangerLevel = "High Risk";
            indicators.add("Fake contact number");
            indicators.add("Known scam phone number");
        }

        // Prize Scam
        if (msg.contains("zawadi") || msg.contains("droo") || msg.contains("tuzo") ||
                msg.contains("shilingi") || msg.contains("pesa")) {
            if (scamType.equals("General Scam")) {
                scamType = "Prize Scam / Fake Promotion";
                dangerLevel = "High Risk";
                indicators.add("Prize/Money claim");
                indicators.add("Urgent request");
            }
        }

        // Bank/Loan Scam
        if (msg.contains("benki") || msg.contains("akaunti") || msg.contains("mkopo")) {
            if (scamType.equals("General Scam")) {
                scamType = "Bank/Loan Scam";
                dangerLevel = "High Risk";
                indicators.add("Fake bank/loan offer");
                indicators.add("Request for personal information");
            }
        }

        return new ScamResult(scamType, dangerLevel, indicators);
    }

    // Result class
    public static class ScamResult {
        public boolean isScam;
        public String scamType;
        public String dangerLevel;
        public List<String> indicators;

        public ScamResult(boolean isScam, String scamType, String dangerLevel, List<String> indicators) {
            this.isScam = isScam;
            this.scamType = scamType;
            this.dangerLevel = dangerLevel;
            this.indicators = indicators;
        }

        public ScamResult(String scamType, String dangerLevel, List<String> indicators) {
            this.isScam = true;
            this.scamType = scamType;
            this.dangerLevel = dangerLevel;
            this.indicators = indicators;
        }
    }
}