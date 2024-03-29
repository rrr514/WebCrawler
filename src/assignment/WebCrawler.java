package assignment;

import java.io.*;
import java.net.*;
import java.util.*;

import org.attoparser.simple.*;
import org.attoparser.config.ParseConfiguration;

/**
 * The entry-point for WebCrawler; takes in a list of URLs to start crawling from and saves an index
 * to index.db.
 */
public class WebCrawler {

    /**
    * The WebCrawler's main method starts crawling a set of pages.  You can change this method as
    * you see fit, as long as it takes URLs as inputs and saves an Index at "index.db".
    */
    public static void main(String[] args) {
        long timeStart = System.currentTimeMillis();
        // Basic usage information
        if (args.length == 0) {
            System.err.println("Error: No URLs specified.");
            System.exit(1);
        }

        // We'll throw all of the args into a queue for processing.
        Queue<URL> remaining = new LinkedList<>();
        for (String url : args) {
            try {
                URI uri = URI.create(url);
                remaining.add(uri.toURL());
            } catch (MalformedURLException e) {
                // Throw this one out!
                System.err.printf("Error: URL '%s' was malformed and will be ignored!%n", url);
            }
        }

        // Create a parser from the attoparser library, and our handler for markup.
        ISimpleMarkupParser parser = new SimpleMarkupParser(ParseConfiguration.htmlConfiguration());
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();

        // Try to start crawling, adding new URLS as we see them.
        //debug code
        int urlCount = 0;
        try {
            while (!remaining.isEmpty()) {
                //skip non html pages
                if(!remaining.peek().toString().endsWith("html")){
                    remaining.poll();
                    continue;
                }
                //debug code - printing out current url
                // System.out.println(remaining.peek());
                //update the page
                handler.url = remaining.peek();
                //add to visited URLs
                handler.ind.visitedURLs.add(remaining.peek());
                // Parse the next URL's page
                parser.parse(new InputStreamReader(remaining.poll().openStream()), handler);

                // Add any new URLs
                remaining.addAll(handler.newURLs());
                //debug code
                urlCount++;
            }

            WebIndex index = (WebIndex) handler.getIndex();
            //debug code - printing out the index
            //System.out.println(index);
            // System.out.println("Contains \"MINI-MEAN\": " + index.ind.containsKey("MINI-MEAN"));
            //debug code - printing out how many urls were crawled
            System.out.println("Pages Traversed: " + urlCount);
            //debug code - printing out how many words are in the index
            System.out.println("Number of words in index: " + index.ind.size());
            // //debug code - printing out the page contents
            // System.out.println(index.pageContents);
            System.out.println("Page Contents Size: " + index.pageContents.size());
            // System.out.println("Visited URLs: " + handler.visitedURLs);
            //debug code - printing out the number of visited urls
            System.out.println("Number of visited URLs: " + handler.ind.visitedURLs.size());
            // System.out.println(handler.words);
            handler.getIndex().save("index.db");
        } catch (Exception e) {
            // Bad exception handling :(
            System.err.println("Error: Index generation failed!");
            e.printStackTrace();
            System.exit(1);
        }
        long timeEnd = System.currentTimeMillis();
        long timeTaken = timeEnd - timeStart;
        System.out.println("Time taken: " + timeTaken + "ms");
    }
}
