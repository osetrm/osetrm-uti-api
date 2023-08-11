package org.osetrm.api.uti.generator;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class UtiGeneratorResourceTest {

    private static final Logger logger = LoggerFactory.getLogger(UtiGeneratorResourceTest.class);

    private static final String LEI = "00000000000000000000";

    @Test
    void generateDoddFrank() {
        UtiRequest utiRequest = new UtiRequest(RegulatoryRegime.DODD_FRANK, LEI);
        Uti uti = given().body(utiRequest).contentType(ContentType.JSON).post("/uti-generator").then().statusCode(200).extract().as(Uti.class);
        assertThat(uti).isNotNull()
                .extracting(Uti::uti)
                .asString()
                .hasSizeLessThanOrEqualTo(42)
                .startsWith(LEI);
        logger.info("DODD_FRANK: {} ({})", uti.uti(), uti.uti().length());
    }

    @Test
    void generateEmir() {
        UtiRequest utiRequest = new UtiRequest(RegulatoryRegime.EMIR, LEI);
        Uti uti = given().body(utiRequest).contentType(ContentType.JSON).post("/uti-generator").then().statusCode(200).extract().as(Uti.class);
        assertThat(uti).isNotNull()
                .extracting(Uti::uti)
                .asString()
                .hasSizeLessThanOrEqualTo(52)
                .startsWith("E02" + LEI);
        logger.info("EMIR: {} ({})", uti.uti(), uti.uti().length());
    }

    @Test
    void invalidLEI() {
        UtiRequest utiRequest = new UtiRequest(RegulatoryRegime.DODD_FRANK, "0000000000000000019");
        Response response = given().body(utiRequest).contentType(ContentType.JSON).post("/uti-generator");
        assertThat(response.statusCode()).isEqualTo(400);
    }

}