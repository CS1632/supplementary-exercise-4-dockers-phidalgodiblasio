package edu.pitt.cs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConnectTest {
    private WebDriver driver;
    private Map<String, Object> vars;
    JavascriptExecutor js;

    @Before
    public void setUp() {
        driver = new ChromeDriver();
        js = (JavascriptExecutor) driver;
        vars = new HashMap<String, Object>();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void tEST1LINKS() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1280, 752));
        {
            WebElement element = driver.findElement(By.xpath("//a[contains(text(),\'Reset\')]"));
            String attribute = element.getAttribute("href");
            vars.put("href", attribute);
        }
        assertEquals("http://localhost:8080/reset", vars.get("href").toString());
    }

    @Test
    public void tEST2RESET() {
        driver.get("http://localhost:8080/");
        js.executeScript("document.cookie = \"1=true\";document.cookie = \"2=true\";document.cookie = \"3=true\";");
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1296, 768));
        driver.findElement(By.linkText("Reset")).click();
        assertThat(driver.findElement(By.xpath("//div[@id=\'listing\']/ul/li")).getText(), is("ID 1. Jennyanydots"));
        assertThat(driver.findElement(By.xpath("//div[@id=\'listing\']/ul/li[2]")).getText(),
                is("ID 2. Old Deuteronomy"));
        assertThat(driver.findElement(By.xpath("//div[@id=\'listing\']/ul/li[3]")).getText(), is("ID 3. Mistoffelees"));
    }

    @Test
    public void tEST3CATALOG() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1296, 768));
        driver.findElement(By.xpath("//a[contains(@href, \'/\')]")).click();
        {
            WebElement element = driver.findElement(By.xpath("//li[2]/img"));
            String attribute = element.getAttribute("src");
            vars.put("src", attribute);
        }
        assertEquals("http://localhost:8080/images/cat2.jpg", vars.get("src").toString());
    }

    @Test
    public void tEST4LISTING() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1280, 752));
        driver.findElement(By.xpath("//a[contains(@href, \'/\')]")).click();
        {
            List<WebElement> elements = driver.findElements(By.xpath("//div/ul/li[4]"));
            assert (elements.size() == 0);
        }
        assertThat(driver.findElement(By.xpath("//div/ul/li[3]")).getText(), is("ID 3. Mistoffelees"));
    }

    @Test
    public void tEST5RENTACAT() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1296, 768));
        driver.findElement(By.linkText("Rent-A-Cat")).click();
        {
            List<WebElement> elements = driver.findElements(By.xpath("//button[contains(.,\'Rent\')]"));
            assert (elements.size() > 0);
        }
        {
            List<WebElement> elements = driver.findElements(By.xpath("//button[contains(.,\'Return\')]"));
            assert (elements.size() > 0);
        }
    }

    @Test
    public void tEST6RENT() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1296, 768));
        driver.findElement(By.linkText("Rent-A-Cat")).click();
        driver.findElement(By.id("rentID")).click();
        driver.findElement(By.id("rentID")).sendKeys("1");
        driver.findElement(By.xpath("//button[contains(.,\'Rent\')]")).click();
        assertThat(driver.findElement(By.xpath("//div[@id=\'listing\']/ul/li")).getText(), is("Rented out"));
        assertThat(driver.findElement(By.xpath("//div[@id=\'listing\']/ul/li[2]")).getText(),
                is("ID 2. Old Deuteronomy"));
        assertThat(driver.findElement(By.xpath("//div[@id=\'listing\']/ul/li[3]")).getText(), is("ID 3. Mistoffelees"));
        assertThat(driver.findElement(By.xpath("//div[@id=\'rentResult\']")).getText(), is("Success!"));
    }

    @Test
    public void tEST7RETURN() {
        driver.get("http://localhost:8080/");
        js.executeScript("document.cookie = \"1=false\";document.cookie = \"2=true\";document.cookie = \"3=false\";");
        driver.get("http://localhost:8080/");
        driver.findElement(By.linkText("Rent-A-Cat")).click();
        {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//main/div")));
        }
        driver.findElement(By.id("returnID")).click();
        driver.findElement(By.id("returnID")).sendKeys("2");
        driver.findElement(By.xpath("//div[3]/div[3]/button")).click();
        {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[3]/div[4]")));
        }
        {
            List<WebElement> elements = driver.findElements(By.xpath("//div/ul/li"));
            assert (elements.size() > 0);
        }
        {
            List<WebElement> elements = driver.findElements(By.xpath("//div/ul/li[2]"));
            assert (elements.size() > 0);
        }
        {
            List<WebElement> elements = driver.findElements(By.xpath("//div/ul/li[3]"));
            assert (elements.size() > 0);
        }
        {
            List<WebElement> elements = driver.findElements(By.xpath("//div[@id=\'returnResult\']"));
            assert (elements.size() > 0);
        }
    }

    @Test
    public void tEST8FEEDACAT() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(766, 554));
        driver.findElement(By.linkText("Feed-A-Cat")).click();
        {
            List<WebElement> elements = driver.findElements(By.cssSelector(".btn"));
            assert (elements.size() > 0);
        }
    }

    @Test
    public void tEST9FEED() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(766, 554));
        driver.findElement(By.linkText("Feed-A-Cat")).click();
        {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//main/div")));
        }
        driver.findElement(By.id("catnips")).click();
        driver.findElement(By.id("catnips")).sendKeys("6");
        driver.findElement(By.xpath("//button")).click();
        {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("feedResult")));
        }
        {
            List<WebElement> elements = driver.findElements(By.id("feedResult"));
            assert (elements.size() > 0);
        }
    }

    @Test
    public void tEST10GREETACAT() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(766, 554));
        driver.findElement(By.linkText("Greet-A-Cat")).click();
        {
            List<WebElement> elements = driver.findElements(By.cssSelector("#greeting > h4"));
            assert (elements.size() > 0);
        }
    }

    @Test
    public void tEST11GREETACATWITHNAME() {
        driver.get("http://localhost:8080/greet-a-cat/Jennyanydots");
        {
            List<WebElement> elements = driver.findElements(By.cssSelector("#greeting > h4"));
            assert (elements.size() > 0);
        }
    }
}
