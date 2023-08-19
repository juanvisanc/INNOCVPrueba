import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PruebaTecnica {
    final Logger LOG = Logger.getLogger("PruebaTecnica");
    WebDriver driver;
    WebDriverWait wait;



    @Before
    public void setUp(){
        System.setProperty("webdriver.chromedriver",".\\main\\resources\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--kiosk");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait (driver, Duration.ofSeconds(20));

        LOG.log(Level.INFO,"Accediendo a la web www.innocv.com");
        driver.get("https://innocv.com");

        LOG.log(Level.INFO,"Esperando que se cargue la página");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("header")));
        WebElement element = driver.findElement(By.xpath("//a[(text()='Contacto')]"));
        WebElement cookies = driver.findElement(By.id("rcc-confirm-button"));
        cookies.click();

        LOG.log(Level.INFO,"Click en botón de Contacto");
        element.click();

        LOG.log(Level.INFO,"Esperando que se cargue la página de Contacto");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span > span:nth-child(3)")));


    }

    @After
    public void tearDown(){
        driver.quit();
    }


    @Test
    public void test1 () throws InterruptedException {

        String telefono = driver.findElement(By.cssSelector("span > span:nth-child(3)")).getText();
        LOG.log(Level.INFO,"El número de teléfono es el "+telefono);

        //Hacemos scroll por partes, ya que la página tarda en cargar algunos elementos y hace que no vaya fluído el scroll
        //No he querido meter sleeps, pero con ellos no me ha fallado el test nunca
        WebElement element = driver.findElement(By.cssSelector("a.MuiButtonBase-root"));
        LOG.log(Level.INFO,"Scroll hasta el botón Ver más");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(),'-2023') or contains(text(), -2024 )]")));

        WebElement element2 = driver.findElement(By.xpath("//a[contains(text(),'Desarrollo a medida')]"));
        LOG.log(Level.INFO,"Scroll hasta Desarrollo a medida");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element2);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Desarrollo a medida')]")));

        WebElement element3 = driver.findElement(By.xpath("//li[contains(text(), 'AVISO LEGAL')]"));
        LOG.log(Level.INFO,"Scroll hasta el botón Aviso Legal");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element3);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[contains(text(), 'AVISO LEGAL')]")));

        Assert.assertTrue("No contiene el teléfono",telefono.contains("91 192 38 32"));


    }

    @Test
    public void test2 (){

        LOG.log(Level.INFO,"Contando palabras...");
        List<WebElement> elements = driver.findElements(By.xpath("//p[contains(text(),'Faraday')]"));
        int vecesAparicion = elements.size();
        LOG.log(Level.INFO,"La palabra Faraday aparece \"+vecesAparicion+\" veces");

    }

    @Test
    public void test3 () throws InterruptedException {


        WebElement element = driver.findElement(By.cssSelector(".MuiButton-contained > .MuiButton-label"));
        LOG.log(Level.INFO,"Scroll hasta el botón");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(500);
        LOG.log(Level.INFO,"Hacemos click en el botón del formulario");
        element.click();

        LOG.log(Level.INFO,"Aparece el mensaje");
        WebElement element2 = driver.findElement(By.cssSelector("p.Mui-error"));
        String color = element2.getCssValue("color");
        String c = Color.fromString(color).asHex();
        LOG.log(Level.INFO,"Validamos que el texto es del color esperado. Color: "+c);
        Assert.assertTrue("No es del color esperado", c.equals("#f44336"));


    }

    @Test
    public void test4 () throws InterruptedException, ParseException {


        WebElement element = driver.findElement(By.cssSelector("a.MuiButtonBase-root"));
        LOG.log(Level.INFO,"Scroll hasta el botón Ver más");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(),'-2023') or contains(text(), -2024 )]"))); // ponemos tb el 2024 para que valga para el año que viene

        List<WebElement> elements = driver.findElements(By.xpath("//p[contains(text(),'-2023') or contains(text(), -2024 )]"));

        for (int i = 0; i < elements.size(); i++) {
            LOG.log(Level.INFO,"Fecha a comparar: "+elements.get(i).getText());
            LOG.log(Level.INFO,"Diferencia de días: "+comparadorDeFechas(elements.get(i).getText()));
            Assert.assertTrue("No es fecha menor a dos meses", comparadorDeFechas(elements.get(i).getText()) < 60);
        }


    }

    public long comparadorDeFechas(String fecha) throws ParseException {
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fechaAhora = dateObj.format(formatter);


        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaActual = formato.parse(fechaAhora);
        Date fechaAnterior = formato.parse(fecha);

        long diffInMillies = Math.abs(fechaActual.getTime()-fechaAnterior.getTime());
        long diferencia = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        return diferencia;
    }


}
