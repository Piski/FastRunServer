
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

@SuppressWarnings("ALL")
public class Main {
    public static void main(String[] args) {
        post("/run", (req, res) -> {
            MysqlDataSource dataSource = connectDatabase();
            Connection conn = null;
            Statement stmt;
            System.out.println(req.body());
            try {
                conn = dataSource.getConnection();
                stmt = conn.createStatement();

                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(req.body());
                System.out.println(obj);

                String sql = "INSERT into run (name, time, steps) " +
                        "values('" + obj.get("name") + "'," + obj.get("time") + "," + obj.get("steps") + ")";
                System.out.println(sql);
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });

        get("/run", (req, res) -> {
            MysqlDataSource dataSource = connectDatabase();
            Connection conn = null;
            Statement stmt;
            List<JSONObject> arr = new ArrayList<>();
            try {
                conn = dataSource.getConnection();
                stmt = conn.createStatement();
                String sql = "SELECT name, time, steps FROM run ORDER BY time ASC";
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    JSONObject obj = new JSONObject();
                    //Retrieve by column name
                    String name  = rs.getString("name");
                    Double time = rs.getDouble("time");
                    int steps = rs.getInt("steps");

                    obj.put("name", name);
                    obj.put("time", time);
                    obj.put("steps", steps);
                    arr.add(obj);
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            res.body(arr.toString());
            res.type("application/json");
            System.out.println(res.body());
            return res.body();
        });
    }

    private static MysqlDataSource connectDatabase() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("root");
        dataSource.setPassword("");
        dataSource.setDatabaseName("run");
        dataSource.setServerName("localhost");
        return dataSource;
    }
}

