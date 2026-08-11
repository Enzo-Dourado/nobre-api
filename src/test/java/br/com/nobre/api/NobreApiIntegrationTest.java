package br.com.nobre.api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
    "app.admin-email=admin@nobre.local",
    "spring.datasource.url=jdbc:h2:mem:nobre;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false"
})
@AutoConfigureMockMvc
class NobreApiIntegrationTest {
    @Autowired MockMvc mvc;

    @Test
    void completeCustomerJourneyAndAdminAuthorization() throws Exception {
        var product = mvc.perform(post("/api/products").cookie(register("Administrador", "admin@nobre.local", "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON).content(productJson("produto-base")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.slug").value("produto-base"))
            .andReturn();
        var productId = com.jayway.jsonpath.JsonPath.<Integer>read(product.getResponse().getContentAsString(), "$.id");

        mvc.perform(get("/api/products").header("Origin", "http://localhost:3000"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
            .andExpect(jsonPath("$", hasSize(1)));

        var loginResult = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@nobre.local\",\"password\":\"senha123\"}"))
            .andExpect(status().isOk()).andReturn();
        var adminCookie = loginResult.getResponse().getCookie("nobre_session");

        mvc.perform(get("/api/auth/me").cookie(adminCookie))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("admin@nobre.local"))
            .andExpect(jsonPath("$.role").value("ADMIN"));

        var order = """
            {
              "items": [{"id": %d, "size": "Único", "qty": 2}],
              "shippingAddress": {"street": "Rua Teste, 10", "city": "São Paulo", "state": "SP", "zip": "01000-000"},
              "paymentMethod": "card"
            }
            """.formatted(productId);
        mvc.perform(post("/api/orders").cookie(adminCookie)
                .contentType(MediaType.APPLICATION_JSON).content(order))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.total").value(20.00))
            .andExpect(jsonPath("$.items[0].productName").value("Produto de teste"))
            .andExpect(jsonPath("$.items[0].quantity").value(2));

        mvc.perform(get("/api/orders").cookie(adminCookie))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].paymentMethod").value("card"));

        var customerCookie = register("Cliente", "cliente@nobre.local", "CUSTOMER");
        mvc.perform(post("/api/products").cookie(customerCookie)
                .contentType(MediaType.APPLICATION_JSON).content(productJson("produto-bloqueado")))
            .andExpect(status().isForbidden());

        mvc.perform(post("/api/products").cookie(adminCookie)
                .contentType(MediaType.APPLICATION_JSON).content(productJson("produto-admin")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.slug").value("produto-admin"));

        mvc.perform(post("/api/auth/logout").cookie(adminCookie))
            .andExpect(status().isOk())
            .andExpect(cookie().maxAge("nobre_session", 0));
    }

    @Test
    void rejectsUnauthenticatedAndInvalidOrders() throws Exception {
        mvc.perform(get("/api/orders"))
            .andExpect(status().isUnauthorized());

        var customerCookie = register("Outro Cliente", "outro@nobre.local", "CUSTOMER");
        var invalidOrder = """
            {
              "items": [{"id": 999999, "size": "P", "qty": 1}],
              "shippingAddress": {"street": "Rua Teste", "city": "São Paulo"},
              "paymentMethod": "pix"
            }
            """;
        mvc.perform(post("/api/orders").cookie(customerCookie)
                .contentType(MediaType.APPLICATION_JSON).content(invalidOrder))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Um ou mais produtos são inválidos."));
    }

    private Cookie register(String name, String email, String role) throws Exception {
        var result = mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"%s","email":"%s","password":"senha123"}
                    """.formatted(name, email)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Set-Cookie"))
            .andExpect(jsonPath("$.role").value(role))
            .andReturn();
        return result.getResponse().getCookie("nobre_session");
    }

    private String productJson(String slug) {
        return """
            {
              "slug":"%s", "name":"Produto de teste", "category":"testes",
              "categoryLabel":"Testes", "price":10.00, "sizes":["Único"]
            }
            """.formatted(slug);
    }
}
