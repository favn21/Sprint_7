# Sprint_7

JUnit4 + RestAssured + Allure. Генерация данных через Java Faker, Allure environment, GitHub Actions workflow.

## Быстрый старт
```bash
mvn -v
mvn clean test
```

## Allure отчет
```bash
allure serve target/allure-results
# или
allure generate target/allure-results -o target/allure-report --clean
allure open target/allure-report
```