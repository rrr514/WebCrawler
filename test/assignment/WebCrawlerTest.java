package assignment;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Pattern;

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
        // String query = " ( ! \" START          END\" words ) 6 ( a & ( b ( c | d ) ) \"     f g      \" ( 0 & 1 ) ) ! 2 4 ";
        // String query = "! ( ( 4 ) ! 3) ( ! 7 \" 57439 84329\" ! 6) ( 1 ! 9 ) )";
        // String query = " .a ( h | w ) .b ( h. | ( g.. ! .f ) ) ( \" ...e        b... \" ) . \"    h       b\" . ";
        // String query = "(hello | world & (! not & your mom)) (hi | globe)";
        // String query = " ( \" phrase phrase2 \" ) ! \" your \" ";
        // String query = " ! \" not \" ( ! your ! mom ) ( hi & ! \" phrase phrase \" ) ! no ";
        String query = "(hello | world) (hi | globe)";
        //String query = "\"I am\"";
        // String query = "(\" \" | \" \")";
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

        System.out.println("toString: " + sb.toString());

        //return all URLs if query has no words and is all empty
        boolean isEmptyPhraseQuery = true;
        for (int i = 0; i < sb.length(); i++) {
            if(!isOperator(sb.charAt(i))){
                isEmptyPhraseQuery = false;
            }
        }
        if(isEmptyPhraseQuery){
            System.out.println("EMPTY PHARS QUERY");
        }

        StringBuilder sbTemp = new StringBuilder();

        //TODO what if query is empty ^ go to top

        //adding implicit ands
        sbTemp.append(sb.toString().charAt(0)); //assuming not empty
        boolean temp = false;
        if(sb.charAt(0) == '"') temp = true;
        boolean implicitAndBetweenTwoWords = false;
        for (int i = 1; i < sb.toString().length(); i++) {
            //add implicit & between opposing parentheses
            if (sb.toString().charAt(i-1) == ')' && sb.toString().charAt(i) == '(') {
                sbTemp.append('&'); 
                // System.out.println("Case 4");
            }
            //if not in quotations
            if(!temp){
                //add implicit & between phrase query and regular word, including the case where the regular word
                //begins in a parenthesis
                if ((sb.toString().charAt(i-1) == '"') && 
                (!isOperator(sb.toString().charAt(i)) || sb.toString().charAt(i) == '(')) {
                    sbTemp.append('&'); 
                    // System.out.println("Case 1");
                }
                //add implicit & between regular word and phrase query, including the case where the phrase query
                //ends in a parenthesis
                if ((!isOperator(sb.toString().charAt(i-1)) || sb.toString().charAt(i-1) == ')') 
                && sb.toString().charAt(i) == '"') {
                    sbTemp.append('&'); 
                    // System.out.println("Case 2");
                }
                //add implicit & between regular word and open parentheses
                if(!isOperator(sb.toString().charAt(i-1)) && sb.toString().charAt(i) == '('){
                    sbTemp.append('&');
                }
                //add implicit & between closing parentheses and regular word
                if(sb.toString().charAt(i-1) == ')' && !isOperator(sb.toString().charAt(i))){
                    sbTemp.append('&');
                }
                //add implicit & between closing parentheses and not operator
                if(sb.toString().charAt(i-1) == ')' && sb.toString().charAt(i) == '!'){
                    sbTemp.append('&');
                }
                //add implicit & between phrase query and not operator
                if(sb.toString().charAt(i-1) == '"' && sb.toString().charAt(i) == '!'){
                    sbTemp.append('&');
                }
                //add implicit & between regular word and not operator
                if(!isOperator(sb.toString().charAt(i-1)) && sb.toString().charAt(i) == '!'){
                    sbTemp.append('&');
                }
                //add implicit & between two phrase queries
                if (sb.toString().charAt(i-1) == '"' && sb.toString().charAt(i) == '"') {
                    sbTemp.append('&'); 
                    // System.out.println("Case 3");
                }
                //add implicit & between two regular words
                if(sb.toString().charAt(i) == ' '){
                    sbTemp.append('&');
                    implicitAndBetweenTwoWords = true;
                }
            }
            if(!implicitAndBetweenTwoWords) sbTemp.append(""+sb.charAt(i));
            implicitAndBetweenTwoWords = false;
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
            // if (!flag && sb.charAt(i) == ' '){
            //     if (sb2.length() != 0){
            //         infix.add(sb2.toString());
            //         sb2.setLength(0);
            //     }
            //     infix.add("&");
            // }
            //if it is a letter, append to word builder
            if (!isOperator(sb.charAt(i)) && sb.charAt(i) != ' ') sb2.append(""+sb.charAt(i));
        }
        //add whatever's left in the word builder
        if (sb2.length() != 0){
            infix.add(sb2.toString());
        }

        //shunting yard algorithm to convert infix to RPN
        Stack<String> ops = new Stack<>();
        Queue<String> output = new LinkedList<String>();
        //char[] q = sb.toString().toCharArray();

        for (int i = 0; i < infix.size(); i++) {
            switch (infix.get(i)) {
                case "!":
                case "&":
                case "|": 
                    while(!ops.empty() && ops.peek().equals("!")){
                        output.add(ops.pop());
                    }
                    ops.push(infix.get(i));
                    break;
                case "(":
                    ops.push(infix.get(i));
                    break;
                case ")":
                    while (!ops.empty() && !ops.peek().equals("(")) {
                        output.add("" + ops.pop());
                    }
                    if(!ops.empty()) ops.pop();
                    break;
                default:
                    output.add(infix.get(i));
            }
        }
        while(!ops.empty()){
            if (ops.peek().equals("(")){
                ops.pop();
                continue;
            }
            output.add(ops.pop());
        }


        
        //debugging
        System.out.println(sb);
        System.out.println(sb2);
        System.out.println("Infix Expression: " + (infix));
        System.out.println("RPN: " + output.toString());

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

    private boolean containsPhrase(URL url, String[] phrase){
        ArrayList<String> page = new ArrayList<>();
        // page.add("word");
        // page.add("bee");
        // page.add("hi");
        // page.add("apple");
        // page.add("bear");
        // page.add("wordhi");
        outer:
        for (int i = 0; i < page.size()-phrase.length+1; i++) {
            for (int j = 0; j < phrase.length; j++) {
                if (!page.get(i+j).equals(phrase[j])){
                    continue outer;
                }
            }
            return true;
        }
        return false;
    }

    @Test
    public void testWebQuery() {
        String s = "..hh.";
        StringBuilder test = new StringBuilder(s);
        System.out.println("Trimmed String: " + trimPunctuation(test) + ":");
    }

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
