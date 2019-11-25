package com.germanembacy;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        WebDriver driver = null;
        WebDriverManager.chromedriver().version("78").setup();

        try {
            driver = new ChromeDriver();
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

            String query = "https://www46.muenchen.de/termin/index.php?cts=1080627";

            driver.manage().window().maximize();
            driver.get(query);

            EmailClient emaiClient = new EmailClient(	"gmailusername", "gmailapppass");

            VisaAppointmentChecker visaAppointmentChecker = new VisaAppointmentChecker(driver, query, emaiClient);
            visaAppointmentChecker.executeQueryAndHandleResults();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
