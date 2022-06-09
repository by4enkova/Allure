package ru.netology.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.data.DataGenerator;
import ru.netology.data.UserInfo;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class DeliveryCardTest {

   @BeforeAll
   static void setUpAll() {
       SelenideLogger.addListener("allure", new AllureSelenide());
   }
   @AfterAll
   static void tearDownAll() {
       SelenideLogger.removeListener("allure");
   }

    @Test
    void shouldRegisterTheCard() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
        UserInfo info = DataGenerator.Registration.generateByCard("ru");
        String firstDate = DataGenerator.generateTheDate(4);
        String secondDate = DataGenerator.generateTheDate(7);

        $("[data-test-id=city] input").setValue(info.getCity()); //город
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE);//дата
        $("[data-test-id=date] input").setValue(firstDate);
        $("[data-test-id=name] input").setValue(info.getName());//фамилия и имя
        $("[data-test-id=phone] input").setValue(info.getPhone());//моб телефон
        $("[data-test-id=agreement] span").click();//галочка
        $(withText("Запланировать")).click();//забронировать
        $("[data-test-id=success-notification]").shouldHave(Condition.text("Успешно! Встреча успешно запланирована на "
                + firstDate), Duration.ofSeconds(15));

        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE);//дата
        $("[data-test-id=date] input").setValue(secondDate);
        $(withText("Запланировать")).click();
        $("[data-test-id=replan-notification]").shouldBe(Condition.visible).shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $$(".button__text").find(exactText("Перепланировать")).click();
        $(".notification__content").shouldBe(Condition.visible).shouldHave(exactText("Встреча успешно запланирована на " + secondDate),
                Duration.ofSeconds(15));

    }
}
