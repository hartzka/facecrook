package projekti;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProjektiTest extends org.fluentlenium.adapter.junit.FluentTest {

    @Autowired
    private MockMvc mockMvc;

    @LocalServerPort
    private Integer port;

    @Test
    public void Test() {
        goTo("http://localhost:" + port + "/accounts");
        assertFalse(pageSource().contains("Welcome"));
        assertTrue(pageSource().contains("Create a new account"));

        find("#name").fill().with("Uuno Turhapuro");
        find("#username").fill().with("uuno");
        find("#password").fill().with("salis");
        find("form").first().submit();

        assertFalse(pageSource().contains("Welcome"));
        assertTrue(pageSource().contains("Create a new account"));

        goTo("http://localhost:" + port + "/login");
        find("#username").fill().with("uuno");
        find("#password").fill().with("salis");
        find("button").first().click();
        
        System.out.println("formfirts");
        System.out.println(find("button").first().getElement());
        System.out.println(find("button").first().getClass());
        System.out.println(find("button").first().getElement().getTagName());
        System.out.println(find("button").first().getElement().getText());
        System.out.println("pagesource");
        System.out.println(pageSource());

        assertFalse(pageSource().contains("wall"));
    }

    @Test
    public void statusOk() throws Exception {
        mockMvc.perform(get("/homepage"))
                .andExpect(status().isOk());
    }

    @Test
    public void responseContainsTextWall() throws Exception {
        MvcResult res = mockMvc.perform(get("/homepage"))
                .andReturn();

        String content = res.getResponse().getContentAsString();
        assertTrue(content.contains("Welcome"));
    }

}
