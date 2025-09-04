package ru.yandex.praktikum.scooter;

import io.restassured.RestAssured;
import org.junit.BeforeClass;

import util.AllureEnv;

public class BaseTest {
    @BeforeClass
    public static void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        AllureEnv.write("https://qa-scooter.praktikum-services.ru", "qa-stand");
    }
}
