package com.hillel.jdbc;

import java.sql.*;

public class JdbsExample {

    public static void main(String[] args) {
        try {
            //Class.forName("com.msql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/school?serverTimezone = UTC&useSSL = false",
                    "root",
                    "root");

            System.out.println("Connection successful");
           printStudents(connection);
            //addStudent(connection, "Богдан", "Кулебякин",36,"Рени");

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    public static void printStudents(Connection connection) throws SQLException {
//        Statement statement = null;
//        ResultSet resultSet = null;
        try(ResultSet resultSet = connection.createStatement().executeQuery("select*from students")) {
            //statement = connection.createStatement();
            //resultSet = statement.executeQuery("select*from students");

            while (resultSet.next()) {
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                int age = resultSet.getInt("age");
                System.out.println(firstName + " " + lastName + " is " + age + " years old");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    //вставка данных с учётом исключения инъекций
    public static void addStudent(Connection connection,
                                  String firstname,
                                  String lastname, int age, String city) throws SQLException{

        String sql = "insert into students (firstname, lastname, age, city) values (?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);//PreparedStatement - для исключения SQLИньекций
        preparedStatement.setString(1, firstname);
        preparedStatement.setString(2, lastname);
        preparedStatement.setInt(3, age);
        preparedStatement.setString(4, city);

        preparedStatement.execute();

        preparedStatement.close();
    }
}
