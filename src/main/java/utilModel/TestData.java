package utilModel;

import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

public class TestData {
    private static final Faker RU = new Faker(new Locale("ru"));
    private static final Faker EN = new Faker(new Locale("en"));

    public static String randomLogin() {
        return "courier_" + UUID.randomUUID().toString().substring(0,8);
    }

    public static String randomPassword() {
        return EN.internet().password(8, 12, true, true);
    }

    public static String randomFirstName() {
        return RU.name().firstName();
    }

    public static String randomLastName() {
        return RU.name().lastName();
    }

    public static String randomAddress() {
        return "Москва, " + RU.address().streetAddress();
    }

    public static String randomPhone() {
        return "+79" + EN.number().digits(9);
    }

    public static String futureDate(int plusDays) {
        return LocalDate.now().plusDays(plusDays).toString();
    }
}
