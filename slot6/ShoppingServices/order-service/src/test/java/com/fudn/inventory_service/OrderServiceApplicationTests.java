package com.fudn.inventory_service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.fudn.inventory_service.stub.InventoryStubs;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MySQLContainer;

import static io.restassured.http.ContentType.JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests {

    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.3.0");
    static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());

    @LocalServerPort
    private int port;

    static {
        mySQLContainer.start();
        wireMockServer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("inventory.url", () -> "http://localhost:" + wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void rejectsAnOrderWhenInventoryReportsOutOfStock() {
        InventoryStubs.stubInventoryCall(wireMockServer, "iphone_15", 101, false);

        RestAssured.given()
                .contentType(JSON)
                .body("""
                        {
                          "skuCode": "iphone_15",
                          "price": 1000,
                          "quantity": 101
                        }
                        """)
                .when()
                .post("/api/order")
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void submitsAnOrderWhenInventoryReportsStockAvailable() {
        InventoryStubs.stubInventoryCall(wireMockServer, "iphone_15", 1, true);

        RestAssured.given()
                .contentType(JSON)
                .body("""
                        {
                          "skuCode": "iphone_15",
                          "price": 1000,
                          "quantity": 1
                        }
                        """)
                .when()
                .post("/api/order")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }
}
