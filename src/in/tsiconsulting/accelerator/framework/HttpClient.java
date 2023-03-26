package in.tsiconsulting.accelerator.framework;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClient {

    private final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
            .version(java.net.http.HttpClient.Version.HTTP_2)
            .build();

    public static void main(String[] args) throws Exception {

        HttpClient obj = new HttpClient();

       // System.out.println("Testing 1 - Send Http GET request");
       // obj.sendGet("https://www.google.com");

        /*String clientId = "35733256";
        String secret = "OyPgi31ol2Ia8Dczy0A7NnVu5UbKGLcc";

        String originalInput = clientId+":"+secret;
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        System.out.println("encoded:"+encodedString);
        String authorization = "Basic "+encodedString;
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String decodedString = new String(decodedBytes);
        System.out.println("decoded:"+ decodedString);*/

        //System.out.println("Testing 2 - Send Http POST request");
        /*JSONObject test = new JSONObject();
        test.put("client_ref_num","abcd1235");
        test.put("pan","AFTPA2071H");
        test.put("name","Satish Ayyaswami");
        JSONObject output = obj.sendPost("https://svcdemo.digitap.work/validation/kyc/v1/pan_basic", authorization,test);
        System.out.println(output);*/

        String in = "{\"_method\":\"pan_basic\",\"client_user_id\": \"12321321\",  \"client_ref_num\": \"abcd1236\",    \"pan\": \"AFTPA2071H\",    \"name\": \"Satish Ayyaswami\"}";
        System.out.println((JSONObject) new JSONParser().parse(in));

    }

    public void sendGet(String url) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // print status code
        System.out.println(response.statusCode());

        // print response body
        System.out.println(response.body());

    }
    
    public JSONObject sendGet(String url,String authorization) throws Exception {
    	JSONObject res = null;
        String resstring = null;
        JSONParser parser = new JSONParser();
        //HttpRequest request = null;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .setHeader("ent_authorization", authorization)
                .setHeader("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // print status code
        System.out.println(response.statusCode());

        // print response body
        System.out.println(response.body());
        resstring = response.body();
        res = (JSONObject) parser.parse(resstring);
        return res;
    }

    public JSONObject sendPost(String url, String authorization, JSONObject data) throws Exception {
        JSONObject res = null;
        String resstring = null;
        JSONParser parser = new JSONParser();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(data.toString()))
                .uri(URI.create(url))
                .setHeader("authorization", authorization)
                .setHeader("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        resstring = response.body();
        res = (JSONObject) parser.parse(resstring);
        return res;
    }
    
    public JSONObject sendPost(String url, JSONObject data,String authheader, String authheadervalue) throws Exception {
        JSONObject res = null;
        String resstring = null;
        JSONParser parser = new JSONParser();
        HttpRequest request = null;
        System.out.println(url);
        //System.out.println(authorization);
        System.out.println(data);
        request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(data.toString()))
                    .uri(URI.create(url))
                    .setHeader(authheader, authheadervalue)
                    .setHeader("Content-Type", "application/json")
                    .build();
        System.out.println("Request "+request);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        resstring = response.body();
        res = (JSONObject) parser.parse(resstring);
        return res;
    }
}
