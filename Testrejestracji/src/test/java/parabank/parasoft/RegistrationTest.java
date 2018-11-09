package parabank.parasoft;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.assertj.core.api.Assertions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;


public class RegistrationTest {

    static WebDriver driver;

    @BeforeClass
    public static void classSetup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @Before
    public void testSetup() {
        driver.get("http://parabank.parasoft.com");
        registerLink().click();
    }

    @After
    public void testTeardown() {
        try {
            logoutLink().click();
        } catch (Exception e) {
            //no logout necessary in this case;
        }
    }

    @AfterClass
    public static void classTeardown() {
        driver.quit();
    }

    @Test
    public void openRegistrationFormTest() {
        WebElement registerForm = driver.findElement(By.xpath("//form[@action='/parabank/register.htm']"));
        assertEquals("Register link is not working or form is not visible", true,
                registerForm.isDisplayed());
    }

    @Test
    public void accountCreatedSuccessfullyTest() {
        firstNameField().sendKeys("Jan");
        lastNameField().sendKeys("Nowak");
        addressField().sendKeys("Sesame Street");
        cityField().sendKeys("Kraków");
        stateField().sendKeys("Best State");
        zipCodeField().sendKeys("31-543");
        phoneNumberField().sendKeys("777888999");
        ssnField().sendKeys("1234567890");

        String randomString = new Methods().getRandomString(7);
        String usernameIncoming = "user_" + randomString;
        usernameField().sendKeys(usernameIncoming);     //specific: random username
        passwordField().sendKeys("1");
        confirmPasswordField().sendKeys("1");
        registerButton().click();

        String confirmMessage = driver.findElement(By.xpath("//div[@id='rightPanel']")).getText();
        assertEquals("Registration fail, no user welcome", "Welcome " + usernameIncoming + "\nYour account was created successfully. You are now logged in.", confirmMessage);
    }

    @Test
    public void usernameAlreadyExistsTest() {
        firstNameField().sendKeys("Jan");
        lastNameField().sendKeys("Nowak");
        addressField().sendKeys("Sesame Street");
        cityField().sendKeys("Kraków");
        stateField().sendKeys("Best State");
        zipCodeField().sendKeys("31-543");
        phoneNumberField().sendKeys("777888999");
        ssnField().sendKeys("1234567890");
        String randomString = new Methods().getRandomString(7);    //first we create random but saved user
        String usernameIncoming = "user_" + randomString;
        usernameField().sendKeys(usernameIncoming);
        passwordField().sendKeys("1");
        confirmPasswordField().sendKeys("1");
        registerButton().click();

        logoutLink().click();
        registerLink().click();

        firstNameField().sendKeys("Jan");
        lastNameField().sendKeys("Nowak");
        addressField().sendKeys("Sesame Street");
        cityField().sendKeys("Kraków");
        stateField().sendKeys("Best State");
        zipCodeField().sendKeys("31-543");
        phoneNumberField().sendKeys("777888999");
        ssnField().sendKeys("1234567890");
        usernameField().sendKeys(usernameIncoming);         //then we try to register the same user
        passwordField().sendKeys("1");
        confirmPasswordField().sendKeys("1");
        registerButton().click();

        String rejectMessage = driver.findElement(By.xpath("//span[@id='customer.username.errors']")).getText();
        assertEquals("No error message that user already exists", rejectMessage , "This username already exists.");
        assertThat(rejectMessage).contains("This username already exists."); //compare in fail report
    }

    @Test
    public void formNotFilledTest() {
        registerButton().click();
        List<WebElement> validationErrors = driver.findElements(By.cssSelector(".error"));
        for (WebElement listElement : validationErrors) {
            assertThat(listElement.getText())
                    .isIn("First name is required.", "Last name is required.", "Address is required.", "City is required.",
                            "State is required.", "Zip Code is required.", "Social Security Number is required.",
                            "Username is required.", "Password is required.", "Password confirmation is required.");
        }
    }

    @Test
    public void passwordsDidNotMatchTest() {
        firstNameField().sendKeys("Jan");
        lastNameField().sendKeys("Nowak");
        addressField().sendKeys("Sesame Street");
        cityField().sendKeys("Kraków");
        stateField().sendKeys("Best State");
        zipCodeField().sendKeys("31-543");
        phoneNumberField().sendKeys("777888999");
        ssnField().sendKeys("1234567890");

        String randomString = new Methods().getRandomString(10);
        String usernameIncoming = "user_" + randomString;
        usernameField().sendKeys(usernameIncoming);     //specific: random username
        passwordField().sendKeys("1");
        confirmPasswordField().sendKeys("2");   //specific: different pass
        registerButton().click();

        String rejectMessage = driver.findElement(By.xpath("//span[@id='repeatedPassword.errors']")).getText();
        assertThat(rejectMessage).contains("Passwords did not match.");
     }


    private WebElement firstNameField() {
        WebElement element = driver.findElement(By.id("customer.firstName"));
        return element;
    }

    private WebElement lastNameField() {
        WebElement element = driver.findElement(By.id("customer.lastName"));
        return element;
    }

    private WebElement addressField() {
        WebElement element = driver.findElement(By.id("customer.address.street"));
        return element;
    }

    private WebElement cityField() {
        WebElement element = driver.findElement(By.id("customer.address.city"));
        return element;
    }

    private WebElement stateField() {
        WebElement element = driver.findElement(By.id("customer.address.state"));
        return element;
    }

    private WebElement zipCodeField() {
        WebElement element = driver.findElement(By.id("customer.address.zipCode"));
        return element;
    }

    private WebElement phoneNumberField() {
        WebElement element = driver.findElement(By.id("customer.phoneNumber"));
        return element;
    }

    private WebElement ssnField() {
        WebElement element = driver.findElement(By.id("customer.ssn"));
        return element;
    }

    private WebElement usernameField() {
        WebElement element = driver.findElement(By.id("customer.username"));
        return element;
    }

    private WebElement passwordField() {
        WebElement element = driver.findElement(By.id("customer.password"));
        return element;
    }

    private WebElement confirmPasswordField() {
        WebElement element = driver.findElement(By.id("repeatedPassword"));
        return element;
    }

    private WebElement registerButton() {
        WebElement element = driver.findElement(By.xpath(
                "//form[@action='/parabank/register.htm']//input[@type='submit'][@class='button']"));
        return element;
    }

    private static WebElement logoutLink() {
        WebElement element = driver.findElement(By.xpath(
                "//a[@href='/parabank/logout.htm']"));
        return element;
    }

    private WebElement registerLink() {
        WebElement element = driver.findElement(By.xpath(
                "//div[@id='loginPanel']/p[last()]/a"));
        return element;
    }

}
