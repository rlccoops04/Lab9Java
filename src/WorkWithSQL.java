import java.sql.*;

public class WorkWithSQL {
    private final Connection connection;
    private PreparedStatement preparedStatement;

    private void setBookParametersInStatement(
            String author, String publication, String publishingHouse, int yearPublic,
            int pages, int yearWrite, int weight) throws SQLException {
        preparedStatement.setString(1, author);
        preparedStatement.setString(2, publication);
        preparedStatement.setString(3, publishingHouse);
        preparedStatement.setInt(4, yearPublic);
        preparedStatement.setInt(5, pages);
        preparedStatement.setInt(6, yearWrite);
        preparedStatement.setInt(7, weight);
    }

    public WorkWithSQL(Connection connection) {
        this.connection = connection;
    }

    public String CreateNewLocation(int floor, int closet, int shelf) {
        try {
            preparedStatement = connection.prepareStatement(
                    "insert into Location(floor, closet, shelf) values (?, ?, ?)");
            preparedStatement.setInt(1, floor);
            preparedStatement.setInt(2, closet);
            preparedStatement.setInt(3, shelf);
            preparedStatement.executeUpdate();
            return "Новое место хранения создано!";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }


    public String CreateNewBook(
            String author, String publication, String publicshingHouse,
            int yearPublic, int pages, int yearWrite, int weight, int locationId) {
        try {
            System.out.println("Создаём книгу в скл!");
            preparedStatement = connection.prepareStatement(
                    "insert into book(author, publication, publicshingHouse," +
                            " yearPublic, pages, yearWrite, weight, locationId)" +
                            " values (?, ?, ?, ?, ?, ?, ?, ?)");
            setBookParametersInStatement(author, publication, publicshingHouse, yearPublic, pages, yearWrite, weight);
            preparedStatement.setInt(8, locationId);
            preparedStatement.executeUpdate();
            System.out.println("Новая книга создана!");
            return "Новая книга создана!";
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    public ResultSet ShowAllLocations() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "select * from location order by floor");
            return resultSet;
        } catch (SQLException e) {
            System.out.println("ОШибка");
            return null;
        }
    }

    public ResultSet ShowAllBooks() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "select * from book order by id");
            return resultSet;
        } catch (SQLException e) {
            System.out.println("ОШибка");
            return null;
        }
    }

    public boolean CheckExistsLocationById(int id) {
        System.out.println(id);
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select count(*) as countOfLocations from location where id = " + id );
            resultSet.next();
            return resultSet.getInt("countOfLocations") == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean CheckExistsBookById(int id) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select count(*) as countOfBooks from book where id = " + id);
            resultSet.next();
            return resultSet.getInt("countOfBooks") == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public String UpdateLocation(int id, int floor, int closet, int shelf) {
        try {
            preparedStatement = connection.prepareStatement(
                    "update location set floor = ?, closet = ?, shelf = ? where id = ?");
            preparedStatement.setInt(1, floor);
            preparedStatement.setInt(2, closet);
            preparedStatement.setInt(3, shelf);
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();
            return "Место обновлено!";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    public void UpdateBook(int id, String author, String publication, String publicshingHouse,
                             int yearPublic, int pages, int yearWrite, int weight, int locationId) {
        try {
            preparedStatement = connection.prepareStatement(
                    "update book set author = ?, publication = ?, publicshingHouse = ?," +
                            " yearPublic = ?, pages = ?, yearWrite = ?, weight = ?, locationId = ? where id = ?");
            setBookParametersInStatement(author, publication, publicshingHouse,
                    yearPublic, pages, yearWrite, weight);
            preparedStatement.setInt(8, locationId);
            preparedStatement.setInt(9, id);
            preparedStatement.executeUpdate();
            System.out.println("Книга обновлена!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void DeleteLocation(int id) {
        System.out.println(id);
        try {
            preparedStatement = connection.prepareStatement(
                    "delete from location where id = ?");
            preparedStatement.setInt(1, id);
            if (preparedStatement.executeUpdate() == 0)
                System.out.println("Ошибка при удалении места!");
            else {
                System.out.println("Место удалено!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void DeleteBook(int id) {
        try {
            preparedStatement = connection.prepareStatement(
                    "delete from book where id = ?");
            preparedStatement.setInt(1, id);
            if (preparedStatement.executeUpdate() == 0)
                System.out.println("Ошибка при удалении книги!");
            else {System.out.println("Книга удалена!");}
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String ShowFirstFieldInAllPalces()
    {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select floor from location");
            StringBuilder table = new StringBuilder();
            table.append("Этажи:\n");
            while (resultSet.next()) {
                table.append(resultSet.getString("floor"))
                        .append("\n");
            }
            return table.toString();

        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    public ResultSet ShowAllPublicationInCurrentFloorByLexicOrder(int floor) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "select PublicshingHouse from book where locationId in(select id from location where" +
                            " floor = " + floor + ") order by publication");
            return resultSet;
        } catch (SQLException e) {
            return null;
        }
    }


    public ResultSet ShowAllPublicationByAuthor(String author) {
        try {
            Statement statement = connection.createStatement();
            author = "'" + author + "'";
            System.out.println(author);
            ResultSet resultSet = statement.executeQuery(
                    "select publication from book where Author = " + author + " order by YearWrite asc");
            return resultSet;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public void DeleteAll() {
        deleteBooks();
        deleteLocations();
    }

    private void deleteLocations()
    {
        try
        {
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(
                    "delete from location");
        }catch (SQLException e)
        {
        }
    }


    private void deleteBooks()
    {
        try
        {
            var statement1 = connection.createStatement();
            ResultSet resultSet1 = statement1.executeQuery(
                    "delete from book");
        }catch (SQLException e)
        {

        }
    }

    public String InsertDefaultData(int id, int floor, int closet, int shelf) {
        try {
            preparedStatement = connection.prepareStatement(
                    "SET IDENTITY_INSERT Location ON; insert into location(id, floor, closet, shelf) values (?, ?, ?, ?); SET IDENTITY_INSERT Location OFF;");
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, floor);
            preparedStatement.setInt(3, closet);
            preparedStatement.setInt(4, shelf);
            preparedStatement.executeUpdate();
            return "Новое место хранения создано!";
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

}