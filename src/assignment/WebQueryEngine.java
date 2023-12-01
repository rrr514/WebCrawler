package assignment;
import java.net.URL;
import java.util.*;

/**
 * A query engine which holds an underlying web index and can answer textual queries with a
 * collection of relevant pages.
 *
 * TODO: Implement this!
 */
public class WebQueryEngine {

    WebIndex webInd;

    /**
     * Returns a WebQueryEngine that uses the given Index to constructe answers to queries.
     *
     * @param index The WebIndex this WebQueryEngine should use.
     * @return A WebQueryEngine ready to be queried.
     */
    public static WebQueryEngine fromIndex(WebIndex index) {
        WebQueryEngine ret = new WebQueryEngine();
        ret.webInd = index;
        return ret;
    }

    /**
     * Returns a Collection of URLs (as Strings) of web pages satisfying the query expression.
     *
     * @param query A query expression.
     * @return A collection of web pages satisfying the query.
     */
    public Collection<Page> query(String query) {
        query = query.toUpperCase();
        LinkedList<Page> ret = new LinkedList<>();
        Page p;
        for(URL u: webInd.ind.get(query)){
            p = new Page(u);
            ret.add(p);
        }
        return ret;
    }
}
