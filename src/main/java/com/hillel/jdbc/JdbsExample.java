package com.hillel.jdbc;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import javax.sql.PooledConnection;
import java.sql.*;
import java.util.Properties;

public class JdbsExample {

    public static void main(String[] args) throws InterruptedException {
        try {

            //
            Properties properties = DatabasesProperties.load();
            //Class.forName("com.msql.jdbc.Driver");
//            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/school?serverTimezone = UTC&useSSL = false",
//                    "root",
//                    "root");



            //Создание пула коннекшенов
            MysqlConnectionPoolDataSource mysqlConnectionPoolDataSource = new MysqlConnectionPoolDataSource();
            mysqlConnectionPoolDataSource.setUrl(properties.getProperty("url"));
            mysqlConnectionPoolDataSource.setUser(properties.getProperty("user"));
            mysqlConnectionPoolDataSource.setPassword(properties.getProperty("password"));

            PooledConnection pooledConnection = mysqlConnectionPoolDataSource.getPooledConnection();
            Connection connection = pooledConnection.getConnection();
            System.out.println("Connection successful");
           printStudents(connection);
            //addStudent(connection, "Богдан", "Кулебякин",36,"Рени");
           // transactionExample(connection);
           // bathExample(connection);
            //storedProcedureExample(connection);

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

    //транзакции
    private static void transactionExample(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        connection.setAutoCommit(false);//откл автокомит, чтобы не нарушать целостность транзакции

        try {

        statement.executeUpdate("insert into students(firstname, lastname, age, grants, city)" +
                "values ('Педро','Потрашенко', 50, 113, 'Киев')");

        System.out.println("After first insert");

        statement.executeUpdate("insert into students(firstname, lastname, age, city)" +
                "values ('Луи','Армстронг', 90, 'Измаил')");

        System.out.println("After second insert");

        connection.commit();
            System.out.println("All data saved");
        }
        catch (Exception e){
            connection.rollback();

            System.out.println("After rollback");
            e.printStackTrace();
        }
        finally {
            connection.setAutoCommit(true);//включаем автокомит обратно
        }
        statement.close();
    }
//bath - заполнение таблицы скопом
    private static void bathExample(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into students (firstname, lastname, age, grants, city)"+
                     "values (?,?,?,?,?)");
        connection.setAutoCommit(false);

        for (int i = 0; i < 5; i++){
            String firstname = "Нкто";
            String lastname  = "Лукас";
            int age = 30+i;
            int grants = 30*i;
            String city  = "Одесса";
            preparedStatement.setString(1, firstname);
            preparedStatement.setString(2,lastname);
            preparedStatement.setInt(3,age);
            preparedStatement.setInt(4,grants);
            preparedStatement.setString(5,city);
            preparedStatement.addBatch();//отправ в буфер, чтобы потом все скопом записать
        }

        preparedStatement.executeBatch();//записываем батч
        connection.setAutoCommit(true);
        preparedStatement.close();
    }

    //выполнение хранимой процедуры
    private static void storedProcedureExample(Connection connection) throws SQLException {
        //объект для вызова хранимой процедуры
        CallableStatement callableStatement = connection.prepareCall("{call addNewTeacher(?,?,?)}");
        callableStatement.setString(1, "Петренко Игорь");
        callableStatement.setInt(2, 35);
        callableStatement.registerOutParameter(3, Types.INTEGER);//выходной параметр порядковый номер "?", и тип выходного параметра
        callableStatement.execute();
        int teacherID = callableStatement.getInt(3);//записывает параметра 3
        System.out.println("A new teacher has " + teacherID);

        callableStatement.close();

    }
}
