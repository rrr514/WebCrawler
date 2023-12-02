package assignment;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

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
        String query = "(\" START          END\" words ) (words \"oihwefihoiweh\" more) (word word2) ";
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

        //adding implicit ands
        sbTemp.append(sb.toString().charAt(0)); //assuming not empty
        boolean temp = false;
        for (int i = 1; i < sb.toString().length(); i++) {
            //add implicit & between opposing parentheses
            if (sb.toString().charAt(i-1) == ')' && sb.toString().charAt(i) == '(') {
                sbTemp.append('&'); 
                System.out.println("Case 4");
            }
            //if not in quotations
            if(!temp){
                //add implicit & between phrase query and regular word
                if (sb.toString().charAt(i-1) == '"' && !isOperator(sb.toString().charAt(i))) {
                    sbTemp.append('&'); 
                    System.out.println("Case 1");
                }
                //add implicit & between regular word and phrase query
                if (!isOperator(sb.toString().charAt(i-1)) && sb.toString().charAt(i) == '"') {
                    sbTemp.append('&'); 
                    System.out.println("Case 2");
                }
                //add implicit & between two phrase queries
                if (sb.toString().charAt(i-1) == '"' && sb.toString().charAt(i) == '"') {
                    sbTemp.append('&'); 
                    System.out.println("Case 3");
                }
            }  
            sbTemp.append(""+sb.charAt(i));
            if(sb.toString().charAt(i) == '"') temp = !temp;
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
        //debugging
        System.out.println(sb);
        System.out.println(sb2);
        System.out.println("Infix Expression: " + (infix));

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

    @Test
    public void testWebQuery() {

    }

}
