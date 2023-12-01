package assignment;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.net.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
 * Any comments and methods here are purely descriptions or suggestions.
 * This is your test file. Feel free to change this as much as you want.
 */

public class WebCrawlerTest {

    // This will run ONCE before all other tests. It can be useful to setup up
    // global variables and anything needed for all of the tests.

    @BeforeAll
    public static void setupAll() {

    }

    // This will run before EACH test.
    @BeforeEach
    public void setupEach() {
    }


    @Test
    public void testWebCrawler() {
        File f = new File("/Users/rrr/git-314h/314h-prog7-prog7group16/superspoof/www.superspoof.com/index.html");
        try {
            URL url = new URL("/Users/rrr/git-314h/314h-prog7-prog7group16/superspoof/www.superspoof.com/index.html");
            File g = new File(url.toString());
            assertTrue(f.exists());
            assertTrue(g.exists());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    public void testWebQuery() {

    }

}
