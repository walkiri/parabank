package parabank.parasoft;
import org.apache.commons.lang3.RandomStringUtils;

public class Methods {
    public static void sleepToSee(int seconds){
        seconds = seconds*1000;
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getRandomString (int numberOfChars){
        String generatedString = RandomStringUtils.randomAlphanumeric(numberOfChars);
        return generatedString;
    }









}
