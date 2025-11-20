/*
 * Developing Java Applications using Amazon Bedrock
 * https://www.youtube.com/watch?v=Vv2J8N0-eHc 
 */

package com.example.JavaBedrockDemo;

import org.json.JSONObject;

import com.example.util.BedrockRequestBody;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrock.model.ValidationException;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

public class AmazonTitanLite {

	private static final String MODEL_ID = "amazon.titan-text-lite-v1";
	
	private static final String PROMPT = """
			Extract the band name from the contract:
			
			This Music Recording Agreement ("Agreement") is made effctive as of the 13th day of December,
			2021 by and between Good Kid, a Toronto-based musical Group ("Artist") and Universal Music Group,
			a record label with license number 545345 ("Recording Label"). Artist and RecoringLabel may each
			be referred to in this Agreement individually as a "Party" and collectively as the "Parties."
			Work under this Agreement shall begin on March 15, 2022.
			
			""";
	
	public static void main(String[] args) {
		
		try(BedrockRuntimeClient bedrockClient = BedrockRuntimeClient.builder()
				.region(Region.US_EAST_1)
				.credentialsProvider(ProfileCredentialsProvider.create("BedrockDemoUser"))
				.build()){
			
			String bedRockBody = BedrockRequestBody.builder()
					.withModelId(MODEL_ID)
					.withPrompt(PROMPT)
					// Titan-specific keys;
					.withInferenceParameter("maxTokenCount", 200)
					.withInferenceParameter("temperature", 0.7)
					.build();
					
			System.out.println("---Sending Payload ---");
			System.out.println(bedRockBody);		
			
			InvokeModelRequest invokeModelRequest = InvokeModelRequest.builder()
					.modelId(MODEL_ID)
					.contentType("application/json")
					.accept("application/json")
					.body(SdkBytes.fromUtf8String(bedRockBody))
					.build();
			
			InvokeModelResponse invokeModelResponse = bedrockClient.invokeModel(invokeModelRequest);
			String responseBody = invokeModelResponse.body().asUtf8String();
			
			JSONObject responseAsJson = new JSONObject(responseBody);
			String generatedText = responseAsJson
					.getJSONArray("results")
					.getJSONObject(0)
					.getString("outputText");
            
            System.out.println("\n--- Model Response ---");
            System.out.println(generatedText.trim());
            
				
		} catch(ValidationException e) {
			System.err.println("Valiation Error: " + e.getMessage());
			System.err.println("Ensure your JSON payload matches the requirements for the specific MODEL_ID you are using.");
		} catch(Exception e) {
			System.err.println("An unexpected error occurred: " + e.getMessage());
		
		}
		
	}

}