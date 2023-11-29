package assignment;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.net.*;

import static org.junit.Assert.assertEquals;

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
        try{
            String s1, s2;
            s1 = "file:///Users/rrr/git-314h/314h-prog7-prog7group16/superspoof/www.superspoof.com/blair/index.html";
            URL old = new URL(s1);
            s2 = "../../external.html";
            URL url = new URL(old, s2);
            System.out.println(url.toString());
        }
        catch(MalformedURLException e){
            System.err.println("Error: MalformedURLException");
        }

    }

    @Test
    public void testWebQuery() {

    }

}
