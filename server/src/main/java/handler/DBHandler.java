package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.DBService;

import java.util.Map;

public class DBHandler {

    private final DBService service;
    private final Gson gson = new Gson();

    public DBHandler(DBService service) {
        this.service = service;
    }

    public void clear(Context ctx) {
        try {
            service.clear();
            ctx.status(200);
            ctx.result("{}");

        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(
                    Map.of("message", "Error: " + e.getMessage())
            ));
        }
    }
}
