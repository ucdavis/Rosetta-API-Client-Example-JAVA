package edu.ucdavis.coeitss;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.cdimascio.dotenv.Dotenv;


public class Main {
    public static void main(String[] args) throws Exception {

        //Load the .env File
        Dotenv dotenv = Dotenv.load();

        //Initiate Instance Rosetta API Info and Load Required Values
        RosettaAPIInfo rosettaAPIInfo = new RosettaAPIInfo();
        rosettaAPIInfo.base_url = dotenv.get("ROSETTA_BASE_URL");
        rosettaAPIInfo.client_id = dotenv.get("ROSETTA_CLIENT_ID");
        rosettaAPIInfo.client_secret = dotenv.get("ROSETTA_CLIENT_SECRET");
        rosettaAPIInfo.token_url = dotenv.get("ROSETTA_OAUTH_URL");
        rosettaAPIInfo.test_id = dotenv.get("ROSETTA_TEST_ID");

        //Check for Required Client ID and Secret Before Making API Calls
        if(rosettaAPIInfo.client_id.isEmpty() == false && rosettaAPIInfo.client_secret.isEmpty() == false)
        {
            
            //##########################################
            //Retreiving OAuth Token
            //##########################################
        
            //HttpClient for API Call to Rosetta API
            HttpClient raHttpClient  = HttpClient.newHttpClient();

            //Initiate Object Mapper to Parse Returned Json
            ObjectMapper joMapper = new ObjectMapper();

            //Build Request with Custom Header for OAuth Call
            HttpRequest raHttpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(rosettaAPIInfo.token_url))
                        .header("client_id", rosettaAPIInfo.client_id)
                        .header("client_secret", rosettaAPIInfo.client_secret)
                        .header("grant_type","CLIENT_CREDENTIALS")
                        .POST(BodyPublishers.noBody())
                        .build();

            //Send Request via HTTP Client
            HttpResponse<String> raHttpResponse = raHttpClient.send(raHttpRequest, HttpResponse.BodyHandlers.ofString());

            //Create Json Object of Returned Json
            JsonNode jnOAuthToken = joMapper.readTree(raHttpResponse.body());

            //Load OAuth Access Token
            rosettaAPIInfo.oauth_token = jnOAuthToken.get("access_token").asText();

            //Check on Returned OAuth Access Token
            if(rosettaAPIInfo.oauth_token.isEmpty() == false)
            {

                //########################################
                // Viewing Accounts Endpoint Information
                //########################################

                //Var for Accounts URL
                String accountsURL = rosettaAPIInfo.base_url + "accounts?iamid=" + rosettaAPIInfo.test_id;

                //Build Request for Accounts Lookup
                HttpRequest accntsHttpRequest = HttpRequest.newBuilder()
                            .uri(URI.create(accountsURL))
                            .header("Authorization","Bearer " + rosettaAPIInfo.oauth_token)
                            .GET()
                            .build();

                //Send Accounts Request 
                HttpResponse<String> accntsHttpResponse = raHttpClient.send(accntsHttpRequest, HttpResponse.BodyHandlers.ofString());

                //Create Json Object of Accounts Json Data
                JsonNode jnAccountsData = joMapper.readTree(accntsHttpResponse.body());

                //Loop Through Accounts Information
                for(JsonNode jnAccount : jnAccountsData)
                {

                    if(jnAccount.get("AccountName").asText().equalsIgnoreCase("UCPath Position Entitlement") == true)
                    {
                        String prettyJson = joMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jnAccount);
                        System.out.println(prettyJson);
                    }

                }//End of jnAccountData For




            }//End of OAuth Access Token Empty Check

        }//End of Empty Checks on Client ID and Secret 

    }
}