package com.pfm.bootstrap.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AccountApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void accountFlow_ShouldUseAuthenticatedOwnerAndSoftDeleteAccounts() throws Exception {
        String ownerToken = registerAndGetAccessToken("owner-" + UUID.randomUUID() + "@example.com");
        String otherToken = registerAndGetAccessToken("other-" + UUID.randomUUID() + "@example.com");

        String createResponse = mockMvc.perform(post("/api/v1/accounts")
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "CASH",
                                  "name": "Wallet",
                                  "description": "Daily cash",
                                  "initialBalance": 100.00,
                                  "currency": "vnd"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(get("/api/v1/accounts").header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Wallet"))
                .andExpect(jsonPath("$[0].currency").value("VND"))
                .andExpect(jsonPath("$[0].balance").value(100.00));

        String accountId = objectMapper.readTree(mockMvc.perform(get("/api/v1/accounts")
                        .header("Authorization", bearer(ownerToken)))
                .andReturn()
                .getResponse()
                .getContentAsString()).get(0).get("id").asText();

        mockMvc.perform(delete("/api/v1/accounts/{id}", accountId).header("Authorization", bearer(otherToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCOUNT_FORBIDDEN"));

        mockMvc.perform(delete("/api/v1/accounts/{id}", accountId).header("Authorization", bearer(ownerToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/accounts").header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createAccount_ShouldRejectRequestsWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "CASH",
                                  "name": "Wallet",
                                  "initialBalance": 100.00,
                                  "currency": "VND"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    private String registerAndGetAccessToken(String email) throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "Password123!",
                                  "fullName": "Test User"
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return json.get("accessToken").asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
