package com.consultoriaglobal.automatizacion;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import java.awt.Desktop;
import java.io.File;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AutomatizacionApplicationTests {

	public static void main(String[] args) {
		AutomatizacionApplicationTests test = new AutomatizacionApplicationTests();
		test.SetUp();
		openReport();
	}

	private WebDriver driver;

	public void SetUp() {
		// Configurar el sistema para usar el controlador de Chrome
		System.setProperty("webdriver.chrome.driver", "resources/chromedriver");
		driver = new ChromeDriver();

		// Configurar Extent Reports
		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("informe.html");
		htmlReporter.config().setTheme(Theme.STANDARD);
		ExtentReports extent = new ExtentReports();
		extent.attachReporter(htmlReporter);

		// Crear un nuevo caso de prueba en el informe
		ExtentTest test = extent.createTest("Prueba de contacto", "Completar formulario de contacto con dirección de correo inválida");

		try {
			// Abrir la página de Consultoría Global
			driver.get("https://www.consultoriaglobal.com.ar");
			test.log(Status.INFO, "Página de Consultoría Global abierta");

			// Ir a la sección de contacto
			WebElement contactoLink = driver.findElement(By.linkText("Contáctenos"));
			contactoLink.click();
			System.out.println("Sección de contacto abierta");

			// Completar el formulario de contacto con una dirección de correo inválida
			WebElement nameInput = driver.findElement(By.name("your-name"));
			nameInput.sendKeys("Luciano");
			WebElement emailInput = driver.findElement(By.name("your-email"));
			emailInput.sendKeys("luciano.com");
			WebElement subjectInput = driver.findElement(By.name("your-subject"));
			subjectInput.sendKeys("Este es un asunto de prueba");
			System.out.println("Dirección de correo electrónico ingresada");

			// Hacer click en el botón de enviar
			WebElement enviarButton = driver.findElement(By.cssSelector("input.wpcf7-form-control.wpcf7-submit"));
			enviarButton.click();
			System.out.println("Botón de enviar presionado");

			// Esperar a que aparezca el mensaje de error de dirección de correo inválida
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
			By errorSpanLocator = By.cssSelector("span.wpcf7-not-valid-tip");
			wait.until(ExpectedConditions.visibilityOfElementLocated(errorSpanLocator));

			WebElement errorSpan = driver.findElement(By.cssSelector("span.wpcf7-not-valid-tip"));
			String errorMessage = errorSpan.getText();
			if (errorMessage.equals("La dirección e-mail parece inválida.")) {
				// La dirección de correo electrónico es inválida
				System.out.println("La dirección de correo electrónico es inválida.");
				test.log(Status.PASS, "La dirección de correo ingresada es inválida");
			} else {
				// La dirección de correo electrónico es válida
				System.out.println("La dirección de correo electrónico es válida.");
				test.log(Status.FAIL, "La dirección de correo ingresada es válida");
			}

		} catch (Exception e) {
			test.log(Status.ERROR, e.getMessage());
		} finally {
			// Cerrar el WebDriver al finalizar
			driver.quit();
			// Finalizar el informe
			extent.flush();
		}
	}

	public static void openReport() {
		String reportPath = "informe.html";
		File reportFile = new File(reportPath);

		try {
			// Verificar si Desktop es compatible con la apertura del archivo
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
				Desktop.getDesktop().open(reportFile);
			} else {
				System.out.println("No se puede abrir el informe automáticamente. Abre el archivo manualmente: " + reportPath);
			}
		} catch (Exception e) {
			System.out.println("Error al abrir el informe: " + e.getMessage());
		}
	}
}
