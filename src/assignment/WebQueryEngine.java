package assignment;

import java.net.URL;
import java.util.*;

/**
 * A query engine which holds an underlying web index and can answer textual
 * queries with a
 * collection of relevant pages.
 *
 * TODO: Implement this!
 */
public class WebQueryEngine {

    WebIndex webInd;

    /**
     * Returns a WebQueryEngine that uses the given Index to constructe answers to
     * queries.
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
     * Returns a Collection of URLs (as Strings) of web pages satisfying the query
     * expression.
     *
     * @param query A query expression.
     * @return A collection of web pages satisfying the query.
     */
    public Collection<Page> query(String query) {
        // query = query.toUpperCase();
        // LinkedList<Page> ret = new LinkedList<>();
        // Page p;
        // for(URL u: webInd.ind.get(query)){
        // p = new Page(u);
        // ret.add(p);
        // }
        // return ret;
        query = query.trim();
        String tokens[] = query.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.length-1; i++) {
            sb.append(tokens[i]);
            if (!isOperator(tokens[i].charAt(tokens[i].length()-1)) && 
            !isOperator(tokens[i+1].charAt(0))){ 
                sb.append(" ");
            }
        }
        sb.append(tokens[tokens.length-1]);

        StringBuilder sbTemp = new StringBuilder();

        //TODO what if query is empty ^ go to top

        sbTemp.append(sb.toString().charAt(0)); //assuming not empty
        for (int i = 1; i < sb.toString().length(); i++) {
            if (sb.toString().charAt(i-1) == ')' && sb.toString().charAt(i) == '(') {
                sbTemp.append('&'); //add implicit & between opposing parentheses
            }
            sbTemp.append(""+sb.charAt(i));
        }
        sb = sbTemp;
        ArrayList<String> infix = new ArrayList<String>();
        StringBuilder sb2 = new StringBuilder();
        boolean flag = false;
        for (int i = 0; i < sb.length(); i++) {
            //if it is a double quotation, flip flag
            if (sb.charAt(i) == '"') flag = !flag;
            //if it is a operator, add to infix array and reset word builder
            if (isOperator(sb.charAt(i)) && sb.charAt(i) != '"'){ 
                if (sb2.length() != 0){
                    infix.add(sb2.toString());
                    sb2.setLength(0);
                }
                infix.add("" + sb.charAt(i));
            }
            //if inside quotations, continue to next letter
            if (flag && sb.charAt(i) == ' ') sb2.append(" ");
            //if outside quotations, add an implicit 'and'
            if (!flag && sb.charAt(i) == ' '){
                if (sb2.length() != 0){
                    infix.add(sb2.toString());
                    sb2.setLength(0);
                }
                infix.add("&");
            }
            //if it is a letter, append to word builder
            if (!isOperator(sb.charAt(i)) && sb.charAt(i) != ' ') sb2.append(""+sb.charAt(i));
        }
        //add whatever's left in the word builder
        if (sb2.length() != 0){
            infix.add(sb2.toString());
        }

        Stack<Character> ops = new Stack<>();
        Queue<String> output = new LinkedList<String>();
        char[] q = sb.toString().toCharArray();

        for (int i = 0; i < q.length; i++) {
            // TODO if word add to queue
            switch (q[i]) {
                case '(':
                    ops.push(q[i]);
                    break;
                case ')':
                    while (ops.peek() != '(') {
                        output.add("" + ops.pop());
                    }
                    ops.pop();
                    break;
                
            }

        }

        return null;

        /*
        pseudo solution for parsing queries
        want to turn nonsense query into proper infix notation
        turn all whitespaces into a single space if sandwhiched between two words, else delete space
        iterate through query
        if it is a operator (includes quotation marks), add to infix array
        if it is a word, add to array
        for spaces, if inside quotations, continue to next word
        if outside quotations, add an implicit 'and'
        use shunting yard to switch to RPN
        */
        
    }

    private boolean isOperator(char c){
        switch(c){
            case '(': return true;
            case ')': return true;
            case '!': return true;
            case '&': return true;
            case '|': return true;
            case '"': return true;
            default: return false;
        }
    }
}
