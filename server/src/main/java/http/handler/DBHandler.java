package http.handler;

import java.util.Map;

import com.google.gson.Gson;
import io.javalin.http.Context;

import http.service.DBService;

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

        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(
                    Map.of("message", "Error: " + e.getMessage())
            ));
        }
    }
}
