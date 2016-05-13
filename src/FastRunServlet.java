import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

@WebServlet(name = "FastRunServlet")
public class FastRunServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Save data to database
        MysqlDataSource dataSource = connectDatabase();
        Connection conn = null;
        Statement stmt;
        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();

            // Mock data
            JSONObject obj = new JSONObject();
            obj.put("name", "sergei");
            obj.put("time", 20.0);
            obj.put("steps", 200);

            String sql = "INSERT into run values(obj.name, obj.time, obj.steps)";
            stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MysqlDataSource dataSource = connectDatabase();
        Connection conn = null;
        Statement stmt;
        List<JSONObject> arr = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT name, time, steps FROM run";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                JSONObject obj = new JSONObject();
                //Retrieve by column name
                String name  = rs.getString("name");
                Double time = rs.getDouble("time");
                int steps = rs.getInt("steps");

                //Display values
                //System.out.print("name: " + name);
                //System.out.print(", time: " + time);
                //System.out.print(", steps: " + steps);

                obj.put("name", name);
                obj.put("time", time);
                obj.put("steps", steps);
                arr.add(obj);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(arr);
        out.flush();
    }

    private MysqlDataSource connectDatabase() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("root");
        dataSource.setPassword("");
        dataSource.setDatabaseName("run");
        dataSource.setServerName("localhost");
        return dataSource;
    }
}
