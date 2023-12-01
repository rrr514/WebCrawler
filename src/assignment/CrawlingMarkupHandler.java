package assignment;

import java.util.*;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;
import java.net.*;
import org.attoparser.simple.*;

/**
 * A markup handler which is called by the Attoparser markup parser as it parses
 * the input;
 * responsible for building the actual web index.
 *
 * TODO: Implement this!
 */
public class CrawlingMarkupHandler extends AbstractSimpleMarkupHandler {

    // stores the page that is currently being crawled through
    URL url;
    private WebIndex ind = new WebIndex();
    HashSet<URL> visitedURLs = new HashSet<>();
    private HashSet<URL> newURLs = new HashSet<>();
    ArrayList<String> words = new ArrayList<>();
    int ignoreCount = 0;

    public CrawlingMarkupHandler() {
    }

    /**
     * This method returns the complete index that has been crawled thus far when
     * called.
     */
    public Index getIndex() {
        return ind;
    }

    /**
     * This method returns any new URLs found to the Crawler; upon being called, the
     * set of new URLs
     * should be cleared.
     */
    public List<URL> newURLs() {
        LinkedList<URL> ret = new LinkedList<>();
        for (URL url : newURLs) {
            ret.add(url);
        }
        newURLs = new HashSet<>();
        return ret;
    }

    /**
     * These are some of the methods from AbstractSimpleMarkupHandler.
     * All of its method implementations are NoOps, so we've added some things
     * to do; please remove all the extra printing before you turn in your code.
     *
     * Note: each of these methods defines a line and col param, but you probably
     * don't need those values. You can look at the documentation for the
     * superclass to see all of the handler methods.
     */

    /**
     * Called when the parser first starts reading a document.
     * 
     * @param startTimeNanos the current time (in nanoseconds) when parsing starts
     * @param line           the line of the document where parsing starts
     * @param col            the column of the document where parsing starts
     */
    public void handleDocumentStart(long startTimeNanos, int line, int col) {
        // TODO: Implement this.
        words = new ArrayList<>();
        System.out.println("Start of document");
    }

    /**
     * Called when the parser finishes reading a document.
     * 
     * @param endTimeNanos   the current time (in nanoseconds) when parsing ends
     * @param totalTimeNanos the difference between current times at the start
     *                       and end of parsing
     * @param line           the line of the document where parsing ends
     * @param col            the column of the document where the parsing ends
     */
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) {
        // TODO: Implement this.
        ind.pageContents.put(url, words);
        System.out.println("End of document");
    }

    /**
     * Called at the start of any tag.
     * 
     * @param elementName the element name (such as "div")
     * @param attributes  the element attributes map, or null if it has no
     *                    attributes
     * @param line        the line in the document where this elements appears
     * @param col         the column in the document where this element appears
     */
    public void handleOpenElement(String elementName, Map<String, String> attributes, int line, int col) {
        // TODO: Implement this.
        // System.out.println("Start element: " + elementName);
        switch (elementName) {
            case "style":
                ignoreCount++;
                break;
            case "script":
                ignoreCount++;
                break;
        }

        if (attributes != null && attributes.containsKey("HREF")) {
            // check if already visited the url
            String link = attributes.get("HREF");
            
            try {
                URL urlAdd = new URL(url, link);
                // System.out.println("URL: " + urlAdd.toString());
                // File file = new File(urlAdd.toString());
                // System.out.println("File tested: " + file.toString());
                // if (!file.exists()){
                //     System.out.println("File does not exist.");
                //     return;
                // }
                File f = new File(urlAdd.getFile());
                if(!f.exists()){
                    return;
                }

                // if the URL hasn't been visited, add it to the new URLs list
                if (!visitedURLs.contains(urlAdd)) {
                    // check if its an html webpage
                    if (urlAdd.toString().endsWith("html")) {
                        visitedURLs.add(urlAdd);
                        newURLs.add(urlAdd);
                        System.out.printf("Successfully added URL '%s' from page.\n", urlAdd);
                    }
                }
            } catch (MalformedURLException e) {
                // Throw this one out!
                System.err.printf("Error: Malfored URL when adding '%s' from page.\n", link);
            }
        }
    }

    /**
     * Called at the end of any tag.
     * 
     * @param elementName the element name (such as "div").
     * @param line        the line in the document where this elements appears.
     * @param col         the column in the document where this element appears.
     */
    public void handleCloseElement(String elementName, int line, int col) {
        // TODO: Implement this.
        // System.out.println("End element: " + elementName);
        switch (elementName) {
            case "style":
                ignoreCount--;
                break;
            case "script":
                ignoreCount--;
                break;
        }
    }

    /**
     * Called whenever characters are found inside a tag. Note that the parser is
     * not
     * required to return all characters in the tag in a single chunk. Whitespace is
     * also returned as characters.
     * 
     * @param ch     buffer containint characters; do not modify this buffer
     * @param start  location of 1st character in ch
     * @param length number of characters in ch
     */
    public void handleText(char ch[], int start, int length, int line, int col) {
        // TODO: Implement this.
        // System.out.print("Characters: \"");

        // for(int i = start; i < start + length; i++) {
        // // Instead of printing raw whitespace, we're escaping it
        // switch(ch[i]) {
        // case '\\':
        // System.out.print("\\\\");
        // break;
        // case '"':
        // System.out.print("\\\"");
        // break;
        // case '\n':
        // System.out.print("\\n");;
        // case '\r':
        // System.out.print("\\r");
        // break;
        // case '\t':
        // System.out.print("\\t");
        // break;
        // default:
        // System.out.print(ch[i]);
        // break;
        // }
        // }

        // System.out.print("\"\n");

        // iterate through ch array and add all words to the index
        // System.out.println(ch);
        StringBuilder sb = new StringBuilder();
        
        for (int i = start; i < start + length; i++) {
            if (ignoreCount != 0)
                continue;
            // punctuation mark case
            if (Pattern.matches("\\p{Punct}", "" + ch[i])) {
                // if first char is punctuation, then skip
                if (sb.length() == 0) continue;
                // if last char is punctuation, then add the word to the index
                if (i == ch.length - 1) {
                    if(sb.length() == 0) continue;
                    sb = trimPunctuation(sb);
                    ind.insert(sb.toString(), url);
                    words.add(sb.toString());
                    // clear the StringBuilder
                    sb.setLength(0);
                }
                // else check to see if the following char is a whitespace
                else {
                    // if it is, we have a word, so add it to the index
                    if (Character.isWhitespace(ch[i + 1])) {
                        if(sb.length() == 0) continue;
                        sb = trimPunctuation(sb);
                        ind.insert(sb.toString(), url);
                        words.add(sb.toString());
                        // clear the StringBuilder
                        sb.setLength(0);
                    }
                    // else the word is not finished, and append the next character
                    else {
                        sb.append(ch[i]);
                    }
                }
            }
            // whitespace case
            else if (Character.isWhitespace(ch[i])) {
                if(sb.length() == 0) continue;
                sb = trimPunctuation(sb);
                ind.insert(sb.toString(), url);
                words.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(ch[i]);
            }
        }
    }

    private StringBuilder trimPunctuation(StringBuilder sb){
        int left = 0, right = sb.length()-1;
        while(Pattern.matches("\\p{Punct}", "" + sb.charAt(left))){
            left++;
        }
        while(Pattern.matches("\\p{Punct}", "" + sb.charAt(right))){
            right--;
        }
        return new StringBuilder(sb.substring(left, right+1));
    }
}
