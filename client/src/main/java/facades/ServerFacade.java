package facades;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import dto.game.*;
import dto.session.LoginRequest;
import dto.session.LoginResult;
import dto.user.RegisterRequest;
import dto.user.RegisterResult;

public class ServerFacade {

    private final String serverUrl;
    private final HttpClient client;
    private final Gson gson = new Gson();

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = HttpClient.newHttpClient();
    }

    public RegisterResult register(RegisterRequest request) throws Exception {
        String body = gson.toJson(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();

        return sendRequest(httpRequest, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws Exception {
        String body = gson.toJson(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();

        return sendRequest(httpRequest, LoginResult.class);
    }

    public void logout(String authToken) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .DELETE()
                .header("authorization", authToken)
                .build();

        sendRequest(httpRequest, null);
    }

    public ListGamesResult listGames(ListGamesRequest request) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .GET()
                .header("authorization", request.authToken())
                .build();

        return sendRequest(httpRequest, ListGamesResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws Exception {
        String body = gson.toJson(
                new CreateGameRequest(null, request.gameName())
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("authorization", request.authToken())
                .header("Content-Type", "application/json")
                .build();

        return sendRequest(httpRequest, CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest request) throws Exception {
        String body = gson.toJson(
                new JoinGameRequest(
                        null,
                        request.playerColor(),
                        request.gameID()
                )
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .header("authorization", request.authToken())
                .header("Content-Type", "application/json")
                .build();

        sendRequest(httpRequest, null);
    }

    private <T> T sendRequest(HttpRequest request, Class<T> responseClass) throws Exception {
        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {

                String message;

                try {
                    ErrorResponse err =
                            gson.fromJson(response.body(), ErrorResponse.class);

                    message =
                            (err != null && err.message != null)
                                    ? err.message
                                    : response.body();

                } catch (Exception ignored) {
                    message = response.body();
                }

                throw new RuntimeException(message);
            }

            if (responseClass == null) {
                return null;
            }

            return gson.fromJson(response.body(), responseClass);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/db"))
                .DELETE()
                .build();

        sendRequest(request, null);
    }

    private static class ErrorResponse {
        String message;
    }

    public String getServerUrl() {
        return serverUrl;
    }
}