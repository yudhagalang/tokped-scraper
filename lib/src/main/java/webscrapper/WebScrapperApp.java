package webscrapper;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import webscrapper.model.Product;

public class WebScrapperApp {
	private static final String USER_AGENT =
            "user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36";
    private static final String TOKPED_URL = "https://www.tokopedia.com/p/handphone-tablet/handphone?ob=5&page=";
    
    private static final String ADS_PRODUCT_LABEL = "taProduct";

    private static final String SCROLL_DOWN = "window.scrollBy(0,300)";

    private static final String FILE_NAME = "tokped-data";
    
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static JavascriptExecutor jsExecutor;
    
	public static void main(String[] args) throws InterruptedException, IOException {
		java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
		System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver");
		System.setProperty("webdriver.chrome.silentOutput", "true");
		List<Product> listProducts = new ArrayList<Product>();
		
		ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        options.addArguments(USER_AGENT);
        
        int count = 0;
        int page = 1;
        int max = 100;
        while(count < max) {
        	driver = new ChromeDriver(options);
        	wait = new WebDriverWait(driver, 5);
            jsExecutor = (JavascriptExecutor) driver;
        	driver.get(TOKPED_URL + page);
        	jsExecutor.executeScript(SCROLL_DOWN);
        	
        	List<WebElement> listLinkProduct = driver.findElements(By.xpath("//a[@data-testid='lnkProductContainer']"));
        	
        	for(WebElement ele: listLinkProduct) {
        		if(count == max) break;
        		if(ele.getAttribute("label-atm") != null && ele.getAttribute("label-atm").equals(ADS_PRODUCT_LABEL)) {
        			continue;   		
        		}
        		WebDriver driver2 = new ChromeDriver(options);
                wait = new WebDriverWait(driver2, 5);
                jsExecutor = (JavascriptExecutor) driver2;
                driver2.get(ele.getAttribute("href"));
        		jsExecutor.executeScript(SCROLL_DOWN);
        		Product produk = new Product();
        		produk.setId(++count);
        		produk.setName(driver2.findElement(By.xpath("//h1[@data-testid='lblPDPDetailProductName']")).getText());
        		produk.setDescription(driver2.findElement(By.xpath("//div[@data-testid='lblPDPDescriptionProduk']")).getText().replace(",", " ").replace("\n", " ").replace("\t", " "));
        		produk.setImagelink(driver2.findElement(By.xpath("//div[@data-testid='PDPImageMain']//img")).getAttribute("src"));
        		produk.setRating(driver2.findElement(By.xpath("//span[@data-testid='lblPDPDetailProductRatingNumber']")).getText());
        		produk.setPrice(driver2.findElement(By.xpath("//div[@data-testid='lblPDPDetailProductPrice']")).getText());
        		produk.setMerchant(driver2.findElement(By.xpath("//a[@data-testid='llbPDPFooterShopName']/h2")).getText());
        		driver2.close();
        		listProducts.add(produk);
        		System.out.println(count);
        	}
        	page++;
        	driver.close();
        }
        printCsv(listProducts);
	}
	
	static void printCsv(List<Product> rows) throws IOException {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyMMddHHmmss");
		FileWriter csvWriter = new FileWriter("output/"+ FILE_NAME + LocalDateTime.now().format(format)+".csv");
		csvWriter.write("id,name,description,imagelink,rating,price,merchant\n");

		for (Product rowData : rows) {
		    csvWriter.write(rowData.toString());
		}

		csvWriter.flush();
		csvWriter.close();
	}
}
