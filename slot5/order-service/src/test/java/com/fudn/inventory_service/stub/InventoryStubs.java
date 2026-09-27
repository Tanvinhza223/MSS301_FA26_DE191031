package com.fudn.inventory_service.stub;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.experimental.UtilityClass;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@UtilityClass
public class InventoryStubs {

    public void stubInventoryCall(WireMockServer wireMockServer, String skuCode, Integer quantity, boolean inStock) {
        wireMockServer.stubFor(get(urlPathEqualTo("/api/inventory"))
                .withQueryParam("skuCode", equalTo(skuCode))
                .withQueryParam("quantity", equalTo(quantity.toString()))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(Boolean.toString(inStock))));
    }
}
