import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.mockserver.model.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@Testcontainers
public class EndToEndTest {

    private static final String MOCK_SERVER_ALIAS = "mockserver";
    private static final int MOCK_SERVER_PORT = 1080;


    @Container
    static MockServerContainer mockServer = new MockServerContainer(
            DockerImageName.parse("mockserver/mockserver:5.15.0"))
            .withNetwork(new Network() {
                @Override
                public Statement apply(Statement base, Description description) {
                    return null;
                }

                public String getId() {
                    return "e2e-test-network";
                }

                public void close() {
                }
            })
            .withNetworkAliases(MOCK_SERVER_ALIAS)
            .withExposedPorts(1080)
            .withEnv("MOCKSERVER_PORT", String.valueOf(MOCK_SERVER_PORT));

    @Container
    static ComposeContainer environment = new ComposeContainer(
            new File("../docker-compose.yml"),
            new File("../docker-compose.e2e.yml")
    )
            .withExposedService("web-socket-server", 8080)
            .withEnv("TKA_MOCKSERVER_BASE_URL", "http://" + MOCK_SERVER_ALIAS + ":" + MOCK_SERVER_PORT);

    private MockServerClient mockServerClient;

    @BeforeAll
    static void setup() {
        mockServer.start();
        environment.start();
    }

    @BeforeEach
    void setupTest() {
        mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    }

    @Test
    void testEndToEnd() {
        mockIdResponses();
        mockPostResponses();

        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        WebDriver driver = new ChromeDriver(options);
        driver.get("http://localhost:8080/client.html");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement element = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("word-counts-list"))
        );
        wait.until(ExpectedConditions.attributeToBeNotEmpty(element, "innerText"));

        assertThat(element.getText()).contains("Beitrag: 4\n" +
                "Drei: 3\n" +
                "Dritter: 1\n" +
                "Eins: 1\n" +
                "Erster: 1\n" +
                "Nummer: 4\n" +
                "Titel: 4\n" +
                "Vier: 4\n" +
                "Vierter: 1\n" +
                "Zwei: 2\n" +
                "Zweiter: 1");

        String initialText = element.getText();
        wait.until(CustomExpectedConditions.textToBeDifferentFrom(element, initialText));

        assertThat(element.getText()).contains("Beitrag: 3\n" +
                "Eins: 1\n" +
                "Erster: 1\n" +
                "Nummer: 3\n" +
                "Titel: 3\n" +
                "Vier: 4\n" +
                "Vierter: 1\n" +
                "Zwei: 2\n" +
                "Zweiter: 1");

        driver.quit();
    }

    private void mockPostResponses() {
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/wp-json/wp/v2/posts")
                        .withQueryStringParameters(
                                new Parameter("page", "1"),
                                new Parameter("_fields", "id,modified_after,title,content")
                        )
                )
                .respond(response()
                        .withHeaders(List.of(
                                Header.header("Content-Type", "application/json"),
                                Header.header("x-wp-totalpages", 2)
                        ))
                        .withBody("""
                                [
                                  {
                                    "id": 1,
                                    "modified_gmt": "2025-01-28T08:35:50",
                                    "title": {
                                      "rendered": "Erster Titel"
                                    },
                                    "content": {
                                      "rendered": "<div class=\\"elementor-widget-text-editor\\">Beitrag Nummer Eins.</div>",
                                      "protected": false
                                    }
                                  },
                                  {
                                    "id": 2,
                                    "modified_gmt": "2024-12-16T11:45:59",
                                    "title": {
                                      "rendered": "Zweiter Titel"
                                    },
                                    "content": {
                                      "rendered": "<div class=\\"elementor-widget-text-editor\\">Beitrag Nummer Zwei Zwei.</div>",
                                      "protected": false
                                    }
                                  }
                                ]
                                """));

        mockServerClient
                .when(request()
                                .withMethod("GET")
                                .withPath("/wp-json/wp/v2/posts")
                                .withQueryStringParameters(
                                        new Parameter("page", "2"),
                                        new Parameter("_fields", "id,modified_after,title,content")
                                ),
                        Times.once()
                )
                .respond(response()
                        .withHeaders(List.of(
                                Header.header("Content-Type", "application/json"),
                                Header.header("x-wp-totalpages", 2)
                        ))
                        .withBody("""
                                [
                                  {
                                    "id": 3,
                                    "modified_gmt": "2024-12-02T12:49:25",
                                    "title": {
                                      "rendered": "Dritter Titel"
                                    },
                                    "content": {
                                      "rendered": "<div class=\\"elementor-widget-text-editor\\">Beitrag Nummer Drei Drei Drei.</div>",
                                      "protected": false
                                    }
                                  },
                                  {
                                    "id": 4,
                                    "modified_gmt": "2024-11-21T08:45:09",
                                    "title": {
                                      "rendered": "Vierter Titel"
                                    },
                                    "content": {
                                      "rendered": "<div class=\\"elementor-widget-text-editor\\">Beitrag Nummer Vier Vier Vier Vier.</div>",
                                      "protected": false
                                    }
                                  }
                                ]
                                """));

        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/wp-json/wp/v2/posts")
                        .withQueryStringParameters(
                                new Parameter("page", "2"),
                                new Parameter("_fields", "id,modified_after,title,content")
                        )
                )
                .respond(response()
                        .withHeaders(List.of(
                                Header.header("Content-Type", "application/json"),
                                Header.header("x-wp-totalpages", 2)
                        ))
                        .withBody("""
                                [
                                  {
                                    "id": 4,
                                    "modified_gmt": "2024-11-21T08:45:09",
                                    "title": {
                                      "rendered": "Vierter Titel"
                                    },
                                    "content": {
                                      "rendered": "<div class=\\"elementor-widget-text-editor\\">Beitrag Nummer Vier Vier Vier Vier.</div>",
                                      "protected": false
                                    }
                                  }
                                ]
                                """));
    }

    private void mockIdResponses() {
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/wp-json/wp/v2/posts")
                        .withQueryStringParameters(
                                new Parameter("page", "1"),
                                new Parameter("_fields", "id")
                        )
                )
                .respond(response()
                        .withHeaders(List.of(
                                Header.header("Content-Type", "application/json"),
                                Header.header("x-wp-totalpages", 2)
                        ))
                        .withBody("[{ \"id\": 1 }, { \"id\": 2 }]")
                );
        mockServerClient
                .when(request()
                                .withMethod("GET")
                                .withPath("/wp-json/wp/v2/posts")
                                .withQueryStringParameters(
                                        new Parameter("page", "2"),
                                        new Parameter("_fields", "id")
                                ),
                        Times.once()
                )
                .respond(response()
                        .withHeaders(List.of(
                                Header.header("Content-Type", "application/json"),
                                Header.header("x-wp-totalpages", 2)
                        ))
                        .withBody("[{ \"id\": 3 }, { \"id\": 4 }]")
                );

        mockServerClient
                .when(request()
                                .withMethod("GET")
                                .withPath("/wp-json/wp/v2/posts")
                                .withQueryStringParameters(
                                        new Parameter("page", "2"),
                                        new Parameter("_fields", "id")
                                ),
                        Times.unlimited()
                )
                .respond(response()
                        .withHeaders(List.of(
                                Header.header("Content-Type", "application/json"),
                                Header.header("x-wp-totalpages", 2)
                        ))
                        .withBody("[{ \"id\": 4 }]")
                );
    }
}
