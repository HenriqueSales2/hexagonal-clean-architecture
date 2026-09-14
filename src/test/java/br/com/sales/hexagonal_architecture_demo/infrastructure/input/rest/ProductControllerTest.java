package br.com.sales.hexagonal_architecture_demo.infrastructure.input.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import br.com.sales.hexagonal_architecture_demo.domain.exception.InvalidProductException;
import br.com.sales.hexagonal_architecture_demo.domain.exception.ProductNotFoundException;
import br.com.sales.hexagonal_architecture_demo.infrastructure.input.request.UpdateProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import br.com.sales.hexagonal_architecture_demo.infrastructure.input.ProductController;
import br.com.sales.hexagonal_architecture_demo.application.port.input.ProductManagementUseCase;
import br.com.sales.hexagonal_architecture_demo.application.port.input.response.ProductResponse;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductId;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductStatus;
import br.com.sales.hexagonal_architecture_demo.infrastructure.input.request.CreateProductRequest;

 @WebMvcTest(ProductController.class)
 public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductManagementUseCase productManagementUseCase;

    private ProductResponse productResponse;
    private ProductId productId;

    @BeforeEach
    void setUp() {
            productId = new ProductId("123");

            productResponse = ProductResponse.builder()
                    .id(productId.getValue())
                    .name("Test Product")
                    .price(BigDecimal.valueOf(100.00))
                    .status(ProductStatus.ACTIVE.name())
                    .build();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Test Product", 100.00);
        when(productManagementUseCase.createProduct(any())).thenReturn(productResponse);

        mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(productId.getValue())))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.price", is(100.00)))
                .andExpect(jsonPath("$.status", is(ProductStatus.ACTIVE.name())));
    }

    @Test
    void shouldReturnBadRequestWhenCreateProductWithInvalidData() throws Exception {
         CreateProductRequest request = new CreateProductRequest("", -100.00);
         when(productManagementUseCase.createProduct(any())).thenThrow(new InvalidProductException("Invalid product data"));

         mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                 .andExpect(status().isBadRequest())
                 .andExpect(jsonPath("$.message", is("Invalid product data")));
    }

    @Test
    void shouldGetProduct() throws Exception {
         when(productManagementUseCase.findProduct(any(ProductId.class))).thenReturn(productResponse);

         mockMvc.perform(get("/api/products/{id}", productId.getValue()))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.id", is(productId.getValue())))
                 .andExpect(jsonPath("$.name", is("Test Product")))
                 .andExpect(jsonPath("$.price", is(100.00)))
                 .andExpect(jsonPath("$.status", is(ProductStatus.ACTIVE.name())));
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
         when(productManagementUseCase.findProduct(any(ProductId.class)))
                 .thenThrow(new ProductNotFoundException(productId));

         mockMvc.perform(get("/api/products/{id}", productId.getValue()))
                 .andExpect(status().isNotFound())
                 .andExpect(jsonPath("$.message", is("Product not found with id: " + productId.getValue())));
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        ProductResponse anotherProduct = ProductResponse.builder()
                .id("234")
                .name("Another Product")
                .price(BigDecimal.valueOf(200.00))
                .status(ProductStatus.ACTIVE.name())
                .build();

        when(productManagementUseCase.findAllProducts()).thenReturn(List.of(productResponse, anotherProduct));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(productId.getValue())))
                .andExpect(jsonPath("$[0].name", is("Test Product")))
                .andExpect(jsonPath("$[0].price", is(100.00)))
                .andExpect(jsonPath("$[0].status", is(ProductStatus.ACTIVE.name())))
                .andExpect(jsonPath("$[1].id", is("234")))
                .andExpect(jsonPath("$[1].name", is("Another Product")))
                .andExpect(jsonPath("$[1].price", is(200.00)))
                .andExpect(jsonPath("$[1].status", is(ProductStatus.ACTIVE.name())));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        UpdateProductRequest request = new UpdateProductRequest("Updated Product", 200.00);

        ProductResponse updatedResponse = ProductResponse.builder()
                .id(productId.getValue())
                .name("Updated Product")
                .price(BigDecimal.valueOf(200.00))
                .status(ProductStatus.ACTIVE.name())
                .build();

        when(productManagementUseCase.updateProduct(any(ProductId.class), any())).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/products/{id}", productId.getValue())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(productId.getValue())))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.price", is(200.00)))
                .andExpect(jsonPath("$.status", is(ProductStatus.ACTIVE.name())));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", productId.getValue()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentProduct() throws Exception {
        doThrow(new ProductNotFoundException(productId))
                .when(productManagementUseCase).deleteProduct(any(ProductId.class));

        mockMvc.perform(delete("/api/products/{id}", productId.getValue()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Product not found with id: " + productId.getValue())));
    }

    @Test
    void shouldActivateProduct() throws Exception {
        when(productManagementUseCase.activateProduct(any(ProductId.class))).thenReturn(productResponse);

        mockMvc.perform(patch("/api/products/{id}/activate", productId.getValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(productId.getValue())))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.price", is(100.00)))
                .andExpect(jsonPath("$.status", is(ProductStatus.ACTIVE.name())));
    }

    @Test
    void shouldReturnBadRequestWhenActivatingProductWithNegativePrice() throws Exception {
        when(productManagementUseCase.activateProduct(any(ProductId.class)))
                .thenThrow(new InvalidProductException("Cannot activate product with negative price"));

        mockMvc.perform(patch("/api/products/{id}/activate", productId.getValue()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Cannot activate product with negative price")));
    }

    @Test
    void shouldDeactivateProduct() throws Exception {
        ProductResponse deactivatedResponse = ProductResponse.builder()
                .id(productId.getValue())
                .name("Test Product")
                .price(BigDecimal.valueOf(100.00))
                .status(ProductStatus.INACTIVE.name())
                .build();

    when(productManagementUseCase.deactivateProduct(any(ProductId.class))).thenReturn(deactivatedResponse);

    mockMvc.perform(patch("/api/products/{id}/deactivate", productId.getValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(productId.getValue())))
            .andExpect(jsonPath("$.name", is("Test Product")))
            .andExpect(jsonPath("$.price", is(100.00)))
            .andExpect(jsonPath("$.status", is(ProductStatus.INACTIVE.name())));
    }
}