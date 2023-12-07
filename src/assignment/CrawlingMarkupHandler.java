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
 */
public class CrawlingMarkupHandler extends AbstractSimpleMarkupHandler {

    // stores the page that is currently being crawled through
    URL url;
    WebIndex ind = new WebIndex();
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
        words = new ArrayList<>();
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
        for (int i = 0; i < words.size(); i++) {
            words.set(i, words.get(i).toUpperCase());
        }
        ind.pageContents.put(url, words);
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
        switch (elementName) {
            case "style":
                ignoreCount++;
                break;
            case "script":
                ignoreCount++;
                break;
        }
        if (attributes != null && (attributes.containsKey("HREF") || attributes.containsKey("href"))) {
            String link;
            if(attributes.containsKey("HREF")){
                link = attributes.get("HREF");
            }
            else{
                link = attributes.get("href");
            }
            //remove #s and ?s
            int hashtagIndex = link.indexOf('#');
            int questionMarkIndex = link.indexOf('?');
            if(hashtagIndex != -1 && questionMarkIndex != -1) {
                link = link.substring(0, Math.min(hashtagIndex, questionMarkIndex));
            }
            else if(hashtagIndex != -1) link = link.substring(0, hashtagIndex);
            else if(questionMarkIndex != -1) link = link.substring(0, questionMarkIndex);  
            if(!link.endsWith("html")) return;
            try {
                URI urli = URI.create(url.toString());
                URL urlAdd = urli.resolve(link).normalize().toURL();
                File f = new File(urlAdd.getFile());
                if(!f.exists()){
                    return;
                }

                // if the URL hasn't been visited, add it to the new URLs list
                if (!ind.visitedURLs.contains(urlAdd)) {
                    // check if its an html webpage
                    if (urlAdd.toString().endsWith("html")) {
                        ind.visitedURLs.add(urlAdd);
                        newURLs.add(urlAdd);
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
        StringBuilder sb = new StringBuilder();
        
        for (int i = start; i < start + length; i++) {
            if (ignoreCount != 0) continue;
            //handling nbsp case
            if(sb.toString().equals("&nbsp") && ch[i] == ';'){
                sb.setLength(0);
                continue;
            }
            // punctuation mark case
            if (Pattern.matches("\\p{Punct}", "" + ch[i])) {
                // if last char is punctuation, then add the word to the index
                if (i == start + length - 1) {
                    sb = trimPunctuation(sb);
                    if(sb.length() == 0) continue;
                    ind.insert(sb.toString(), url);
                    words.add(sb.toString());
                    // clear the StringBuilder
                    sb.setLength(0);
                }
                // else check to see if the following char is a whitespace
                else {
                    // if it is, we have a word, so add it to the index
                    if (Character.isWhitespace(ch[i + 1])) {
                        sb = trimPunctuation(sb);
                        if(sb.length() == 0) continue;
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
                sb = trimPunctuation(sb);
                if(sb.length() == 0) continue;
                ind.insert(sb.toString(), url);
                words.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(ch[i]);
            }
        }
        //if stuff leftover in stringbuilder, add it in to words
        if(sb.length() != 0){
            words.add(sb.toString());
            ind.insert(sb.toString(), url);
        }
    }

    //removes the punctuation off the ends of a StringBuilder
    private StringBuilder trimPunctuation(StringBuilder sb){
        int left = 0, right = sb.length()-1;
        while(left < sb.length() && Pattern.matches("\\p{Punct}", "" + sb.charAt(left))){
            left++;
        }
        while(right >= 0 && Pattern.matches("\\p{Punct}", "" + sb.charAt(right))){
            right--;
        }
        if(left > right) return new StringBuilder();
        else return new StringBuilder(sb.substring(left, right+1));
    }
}
