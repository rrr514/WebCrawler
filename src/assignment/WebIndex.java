package assignment;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A web-index which efficiently stores information about pages. Serialization is done automatically
 * via the superclass "Index" and Java's Serializable interface.
 */
public class WebIndex extends Index {
    /**
     * Needed for Serialization (provided by Index) - don't remove this!
     */
    private static final long serialVersionUID = 1L;

    //indexes
    HashMap<String, HashSet<URL>> ind;
    HashMap<URL, ArrayList<String>> pageContents;
    HashSet<URL> visitedURLs;

    //initialize instance vars
    public WebIndex(){
        ind = new HashMap<>();
        pageContents = new HashMap<>();
        visitedURLs = new HashSet<>();
    }

    /**
     * inserts a word and its page into the index
     *
     * @param word the word to be inserted
     * @param page the page to be inserted
     * @return whether the word and page were successfully inserted or not
     */
    public boolean insert(String word, URL url){
        if(word == null || url == null) return false;
        //change word to uppercase so that it is case insensitive
        word = word.toUpperCase();
        //check to see if the word already exists in the index
        if(ind.containsKey(word)){
            HashSet<URL> wordPages = ind.get(word);
            //check to see if the page already exists in the index
            if(wordPages.contains(url)){
                return false;
            }
            else {
                wordPages.add(url);
                visitedURLs.add(url);
                return true;
            }
        }
        //create new mapping and set and add the page to that set
        else{
            HashSet<URL> newSet = new HashSet<>();
            newSet.add(url);
            ind.put(word, newSet);
            return true;
        }
    }

    public String toString(){
        StringBuilder ret = new StringBuilder();
        for(String s: ind.keySet()){
            ret.append(s + "->" + ind.get(s) + "\n");
        }
        return ret.toString();
    }

    public int getIndSize(){
        return ind.size();
    }
}
