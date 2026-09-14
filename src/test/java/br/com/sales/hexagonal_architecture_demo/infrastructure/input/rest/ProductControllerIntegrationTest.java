package br.com.sales.hexagonal_architecture_demo.infrastructure.input.rest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import tools.jackson.databind.ObjectMapper;

import br.com.sales.hexagonal_architecture_demo.infrastructure.input.request.CreateProductRequest;
import br.com.sales.hexagonal_architecture_demo.infrastructure.input.request.UpdateProductRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAndRetrieveProduct() throws Exception {
        CreateProductRequest createRequest = new CreateProductRequest("Test Product", 100.00);

        String productJson = mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.price", is(100.00)))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String productId = objectMapper.readTree(productJson).get("id").asString();

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(productId)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.price", is(100.00)))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void shouldReturnAllProducts() throws Exception {
        mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new CreateProductRequest("Test Product 1", 100.0))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                .   content(objectMapper.writeValueAsString(new CreateProductRequest("Test Product 2", 200.0))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test Product 1")))
                .andExpect(jsonPath("$[1].name", is("Test Product 2")));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        String productJson = mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new CreateProductRequest("Test Product", 100.00))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String productId = objectMapper.readTree(productJson).get("id").asString();

        UpdateProductRequest updateRequest = new UpdateProductRequest("Test Product Updated", 200.00);

        mockMvc.perform(put("/api/products/{id}", productId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(productId)))
                .andExpect(jsonPath("$.name", is("Test Product Updated")))
                .andExpect(jsonPath("$.price",is(200.00)))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Product Updated")))
                .andExpect(jsonPath("$.price", is(200.0)));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        String productJson = mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new CreateProductRequest("Test Product Deleted", 100.0))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String productId = objectMapper.readTree(productJson).get("id").asString();

        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldActivateAndDeactivateProduct() throws Exception {
        String productJson = mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new CreateProductRequest("Test Product Toggle", 100.0))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String productId = objectMapper.readTree(productJson).get("id").asString();

        mockMvc.perform(patch("/api/products/{id}/deactivate", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("INACTIVE")));

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("INACTIVE")));

        mockMvc.perform(patch("/api/products/{id}/activate", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void shouldFailToActivateProductWithNegativePrice() throws Exception {
        String productJson = mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new CreateProductRequest("Test Product Negative Price", -100.00))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("INACTIVE")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String productId = objectMapper.readTree(productJson).get("id").asString();

        mockMvc.perform(patch("/api/products/{id}/activate" , productId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Cannot activate product with negative price")));
    }
}