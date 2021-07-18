import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Scanner;


public class Bip {

    private static Document getPage() throws IOException{
        String url = "https://poezdato.net/raspisanie-po-stancyi/samara/elektrichki/";
        Document page = (Document) Jsoup.parse(new URL(url), 3000);
        return page;

    }

    public static void main(String[] args) throws IOException,SQLException,ClassNotFoundException {

        String LoginBD = "root";
        String PassBD = "narutosasuke";
        String connectionURL = "jdbc:mysql://localhost:3306/prakt?useUnicode=true&characterEncoding=utf-8";
        Class.forName("com.mysql.jdbc.Driver");

        try (Connection connection = DriverManager.getConnection(connectionURL, LoginBD, PassBD);
             Statement statement = connection.createStatement()) {
        	statement.executeUpdate("TRUNCATE TABLE full;");
        	statement.executeUpdate("TRUNCATE TABLE glav;");
         
        }

        Document page = getPage();
        Element table = page.select("table[class=schedule_table stacktable desktop]").first();
        Elements td = table.select("td");

        
        int i = 0;
        int coll = td.size();
        try {
            while (i <= coll) {

                td.remove(i);
                i = i + 7;
                coll--;

            }
        } catch (Exception e) {

            i = 5;
            while (i <= coll) {
                td.remove(i);
                i = i + 6;
                coll--;
            }

            i = 5;
            while (i <= coll) {

                td.remove(i);
                i = i + 5;
                coll--;

            }
        }


        String col1 = new String();
        String col2 = new String();
        String col3 = new String();
        String col4 = new String();
        String col5 = new String();

        i = 0;
        try {
            while (i <= coll) {
                int ln = i;
                i = i + 5;


                col1 = td.get(ln).text();
                col2 = td.get(ln + 1).text();
                col3 = td.get(ln + 2).text();
                col4 = td.get(ln + 3).text();
                col5 = td.get(ln + 4).text();

                try (Connection connection = DriverManager.getConnection(connectionURL, LoginBD, PassBD);
                     Statement statement = connection.createStatement()) {
                    statement.executeUpdate("INSERT INTO glav (Num,Staninout,Timein,Timepas,Timeout) VALUES ('" + col1 + "', '" + col2 + "', '" + col3 + "', '" + col4 + "', '" + col5 + "');");
                }


            }
        }catch (Exception e){
            int nurl = 0;
            i = 0;

            try{while (coll >= i) {
                URL url = null;
                page = null;

                String http = "https://poezdato.net";
                String urln = td.get(i).select("a").attr("href");
                i=i+5;
                urln = http+urln;
                System.out.println(urln);


                Document pagen = Jsoup.parse(new URL(urln), 3000);

                Element tablen = pagen.select("table[class=train_schedule_table stacktable desktop]").first();
                Elements linen = tablen.select("td");


                String NumSting = pagen.select("h1[class=electr_schedule]").first().text();

                int NumFr = NumSting.indexOf(" ");
                String NumFin = NumSting.substring(0, NumFr);
              //  int Num = Integer.parseInt(NumFin);

                int col = linen.size();
              //  System.out.println(linen);
                col= col-1;
                int ii = 0;
                while (ii <= col) {
                    int ln = ii;
                    ii = ii + 5;

                    col1 = linen.get(ln).text();
                    col2 = linen.get(ln + 1).text();
                    col3 = linen.get(ln + 2).text();
                    col4 = linen.get(ln + 3).text();
                    col5 = linen.get(ln + 4).text();

                    try (Connection connection = DriverManager.getConnection(connectionURL, LoginBD, PassBD);
                         Statement statement = connection.createStatement()) {
                        statement.executeUpdate("INSERT INTO full (IDtrain,Stanin,Timein,Timew,Timeout,Timed) VALUES ('" + NumFin + "', '" + col1 + "', '" + col2 + "', '" + col3 + "', '" + col4 + "', '" + col5 + "');");
                    }
                }
            }
        }catch (Exception f){

        	System.out.println("Обновление базы данных завершено, хотите узнать все маршруты? Y/N");
            String approval = new String();
            Scanner in = new Scanner(System.in);
            approval = in.next();
            switch(approval){
                case ("N"):
                    System.out.println("Close the program");
                    break;
                case ("Y"):
                    System.out.println("Номер      Маршрут      Прибытие Стоянка Отправление");
                    try (Connection connection = DriverManager.getConnection(connectionURL, LoginBD, PassBD);
                         Statement statement = connection.createStatement()) {
                        ResultSet resultSet = statement.executeQuery("SELECT * FROM glav;");
                        while (resultSet.next()){
                            int ID = resultSet.getInt(1);
                            String Num = resultSet.getString(2);
                            String Statinout = resultSet.getString(3);
                            String Timein = resultSet.getString(4);
                            String Timeout = resultSet.getString(5);
                            String Timepas = resultSet.getString(6);
                            System.out.println(Num + "  " + Statinout + "  " + Timein + "  " + Timeout + "  " + Timepas);
                        }
                    }
                    default:break;

            }
                System.out.println("Хотите узнать путь следования определённого поезда? Y/N");
                //Scanner in = new Scanner(System.in);
                approval = in.next();

                switch (approval){
                    case ("Y"):
                        System.out.println("Введите номер поезда:");

                        //Scanner in = new Scanner(System.in);
                        approval = in.next();
                        System.out.println("Номер   Станция   Прибытие Стоянка Отправление");

                        try (Connection connection = DriverManager.getConnection(connectionURL, LoginBD, PassBD);
                             Statement statement = connection.createStatement()) {
                            ResultSet resultSet = statement.executeQuery("SELECT * FROM full WHERE IDtrain='" + approval + "';");
                            while (resultSet.next()){
                                int ID = resultSet.getInt(1);
                                String Num = resultSet.getString(2);
                                String Statinout = resultSet.getString(3);
                                String Timein = resultSet.getString(4);
                                String Timeout = resultSet.getString(5);
                                String Timepas = resultSet.getString(6);
                                System.out.println(Num + "  " + Statinout + "  " + Timein + "  " + Timeout + "  " + Timepas);
                            }

                        }


                }
        	
        }


    }
  }
}