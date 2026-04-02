package com.example.Lab5.controller;

import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Scanner;

import com.example.Lab5.controller.HttpCilent;

@Controller
public class RestClient {
    static String host = "https://testconnection-6d86a-default-rtdb.firebaseio.com";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean run = true;

        while(run){
            System.out.println("1. Get All");
            System.out.println("2. Get By Key");
            System.out.println("3. Post");
            System.out.println("4. Put");
            System.out.println("5. Delete");
            System.out.println("6. Exit");
            System.out.print("Enter: ");
            int option = sc.nextInt();
            sc.nextLine();
            switch(option){
                case 1: getAll(); break;
                case 2: getByKey(); break;
                case 3: post(); break;
                case 4: put(); break;
                case 5: delete(); break;
                case 6: run = false;
                    System.out.println("Đã thoát"); break;
                default: System.out.println("No hợp lệ!");
            }
        }
        sc.close();
    }

    private static void getAll(){
        var url = host + "/students.json";
        try {
            var connection = HttpCilent.openConnection("GET",url);
            var response = HttpCilent.readData(connection);
            System.out.println(new String(response));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private static void getByKey(){
        var url = host + "/students/-Op5rEAEw4fnhw8U2sQb.json";
        try {
            var connection = HttpCilent.openConnection("GET",url);
            var response = HttpCilent.readData(connection);
            System.out.println(new String(response));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private static void post(){
        var url = host + "/students.json";
        var data = "{\"id\": \"SV1\",\"name\": \"Sinh viên 1\",\"mark\": 4.5,\"gender\": true}".getBytes();
        try {
            var connection = HttpCilent.openConnection("POST", url);
            var response = HttpCilent.writeData(connection, data);
            System.out.println(new String(response));
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
    private static void put(){
        var url = host + "/students/-Op5rEAEw4fnhw8U2sQb.json";
        var data = "{\"id\": \"SV02\",\"name\": \"Sinh viên 2\",\"mark\": 4.5,\"gender\": true}".getBytes();
        try {
            var connection = HttpCilent.openConnection("PUT", url);
            var response = HttpCilent.writeData(connection, data);
            System.out.println(new String(response));
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
    private static void delete(){
        var url = host + "/students/-Op5rEAEw4fnhw8U2sQb.json";
        try {
            var connection = HttpCilent.openConnection("DELETE",url);
            HttpCilent.readData(connection);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
