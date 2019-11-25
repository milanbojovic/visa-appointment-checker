package com.germanembacy;

import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.concurrent.TimeUnit;

class VisaAppointmentChecker {

    private WebDriver driver;
    private EmailClient emailClient;

    VisaAppointmentChecker(WebDriver driver, String request, EmailClient emailClient) {
        this.emailClient = emailClient;
        this.driver = driver;
        driver.manage().window().maximize();
        driver.get(request);
    }

    void executeQueryAndHandleResults() throws NotFoundException {
        Select dropdownSelector;

        WebElement outerDiv = driver.findElement(By.id("WEB_APPOINT_CASETYPELIST"));
        dropdownSelector = new Select(outerDiv.findElement(By.name("CASETYPES[Aufenthaltserlaubnis Blaue Karte EU]")));
        dropdownSelector.selectByVisibleText("1");

        WebElement submitButton = driver.findElement(By.className("WEB_APPOINT_FORWARDBUTTON"));
        submitButton.click();

        System.out.println("Submitting the form");
/*        WebElement internationalTalent = driver.findElement(By.xpath("//*[@id=\"locationContent\"]/input[2]"));
        internationalTalent.click();*/

        WebElement parrent = driver.findElement(By.id("locationContent"));
        WebElement input1;
        WebElement input2;

        input1 = parrent.findElement(By.xpath("//*[@id=\"locationContent\"]/input[1]"));
        input2 = parrent.findElement(By.xpath("//*[@id=\"locationContent\"]/input[2]"));

        if (input1 != null && input2 != null) {
            input1.click();
            traverseMonth("0d936a9bcc25c2441098e6719835d055");
            input1.click();

            input2.click();
            traverseMonth("45d56a2da948e677504112248c84c488");

            input2.click();
        } else if (input1 != null) {
            traverseMonth("45d56a2da948e677504112248c84c488");
        }

        System.out.println("Execution compleeted");
    }

    private void traverseMonth(String id) {
        WebElement nextPage;
        do {
            if (driver.findElements(By.linkText(">")).size() > 0) {
                nextPage = driver.findElement(By.linkText(">"));
                traverseDays(id);
                System.out.println("Moving to next month");
                nextPage.click();
            } else {
                nextPage = null;
                traverseDays(id);
            }
        } while (nextPage != null);
    }

    private void traverseDays(String id) {
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.MILLISECONDS);

        System.out.println("Traversing month");
//        WebElement tableParent = driver.findElement(By.className("terminbuchung"));
        WebElement tableParent = driver.findElement(By.id(id));
        WebElement tableBody = tableParent.findElement(By.xpath("//*[@id=\"" + "45d56a2da948e677504112248c84c48820199" + "\"]/table/tbody"));
        List<WebElement> tableRows = tableBody.findElements(By.tagName("tr"));

        for (WebElement row : tableRows) {
            List<WebElement> tableCells = row.findElements(By.tagName("td"));
            for (WebElement cell : tableCells) {
                if (checkAvailability(cell)) {
                    System.out.println("Found available visa appointment");
                    emailClient.sendEmail(new String[]{"dasunbuwaneka7@gmail.com", "marjan.jankovich@gmail.com"}, "Visa appointment available", "App has found the appointment for visa, please check the website for details...");
                    System.exit(0);
                }
            }
        }
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
    }

    private boolean checkAvailability(WebElement cell) {
        try {
            cell.findElement(By.xpath("//a[@class='nat_calendar nat_calendar_weekday_bookable']"));
            System.out.println("Check availability ==> true");
            return true;
        } catch (NotFoundException ignored) {
        }
        System.out.println("Check availability ==> false");
        return false;
    }
}
