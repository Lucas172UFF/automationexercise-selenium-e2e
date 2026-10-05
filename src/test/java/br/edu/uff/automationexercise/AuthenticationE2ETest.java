package br.edu.uff.automationexercise;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

class AuthenticationE2ETest {

    private static final String BASE_URL = "https://automationexercise.com/";
    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1440,1000", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, TIMEOUT);
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Cinco representantes de classes de equivalência válidas para o campo e-mail,
     * porém inexistentes no sistema. Os comprimentos 64 (parte local) e 63
     * (rótulo do domínio) exercitam os valores-limite usuais do formato de e-mail.
     */
    static Stream<Arguments> invalidLoginEmails() {
        String localPart64 = "a".repeat(64);
        String domainLabel63 = "b".repeat(63);
        return Stream.of(
                Arguments.of("usuario.inexistente@exemplo.com", "classe: e-mail comum válido"),
                Arguments.of("a@exemplo.com", "limite inferior prático da parte local"),
                Arguments.of(localPart64 + "@exemplo.com", "limite superior da parte local: 64 caracteres"),
                Arguments.of("usuario@" + domainLabel63 + ".com", "limite superior do rótulo de domínio: 63 caracteres"),
                Arguments.of("usuario+teste@exemplo.com", "classe: endereço válido com sinal de mais"));
    }

    @ParameterizedTest(name = "TC3 - login recusado ({1})")
    @MethodSource("invalidLoginEmails")
    @DisplayName("TC3: Login com e-mail e senha incorretos")
    void shouldShowErrorForIncorrectEmailAndPassword(String email, String ignoredDescription) {
        openLoginPage();

        type("login-email", email);
        type("login-password", "senha-incorreta-123");
        click("login-button");

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(., 'Your email or password is incorrect!')]")));
        assertTrue(error.getText().contains("Your email or password is incorrect!"));
    }

    @Test
    @DisplayName("TC1: Registrar usuário e excluir a conta criada")
    void shouldRegisterAndDeleteUser() {
        String suffix = Instant.now().toEpochMilli() + "-" + (int) (Math.random() * 10_000);
        String name = "Teste Selenium " + suffix;
        String email = "selenium." + suffix + "@example.com";

        openLoginPage();
        assertVisibleText("New User Signup!");

        type("signup-name", name);
        type("signup-email", email);
        click("signup-button");

        assertVisibleText("ENTER ACCOUNT INFORMATION");
        clickById("id_gender1");
        type("password", "SenhaSegura123!");
        selectByValue("days", "15");
        selectByValue("months", "6");
        selectByValue("years", "1995");
        clickById("newsletter");
        clickById("optin");

        type("first_name", "Teste");
        type("last_name", "Selenium");
        type("company", "UFF");
        type("address", "Rua de Testes, 123");
        type("address2", "Apartamento 45");
        selectByText("country", "Canada");
        type("state", "Rio de Janeiro");
        type("city", "Niteroi");
        type("zipcode", "24000-000");
        type("mobile_number", "21999999999");
        click("create-account");

        assertVisibleText("ACCOUNT CREATED!");
        click("continue-button");
        assertVisibleText("Logged in as " + name);

        driver.findElement(By.linkText("Delete Account")).click();
        assertVisibleText("ACCOUNT DELETED!");
        click("continue-button");
    }

    private void openLoginPage() {
        driver.get(BASE_URL);
        assertTrue(driver.getTitle().toLowerCase(Locale.ROOT).contains("automation exercise"),
                "A página inicial deveria estar visível");
        driver.findElement(By.linkText("Signup / Login")).click();
        assertVisibleText("Login to your account");
    }

    private void type(String dataQa, String value) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-qa='" + dataQa + "']")));
        field.clear();
        field.sendKeys(value);
    }

    private void click(String dataQa) {
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-qa='" + dataQa + "']"))).click();
    }

    private void clickById(String id) {
        wait.until(ExpectedConditions.elementToBeClickable(By.id(id))).click();
    }

    private void selectByValue(String dataQa, String value) {
        new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-qa='" + dataQa + "']")))).selectByValue(value);
    }

    private void selectByText(String dataQa, String text) {
        new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-qa='" + dataQa + "']")))).selectByVisibleText(text);
    }

    private void assertVisibleText(String text) {
        String caseInsensitiveText = "translate(normalize-space(.), "
                + "'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')";
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(" + caseInsensitiveText + ", '" + text.toUpperCase(Locale.ROOT) + "')]")));
    }
}
