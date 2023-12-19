package com.example.db;

import org.json.JSONObject;

public class DBAgentTest {
    public static void main(String[] args) {
        try {
            DBAgentAbutmentUtil.assignDBResource();
            DBAgentAbutmentUtil.getAllDBResouceByAccount();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
