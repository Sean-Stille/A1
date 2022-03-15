import java.io.*;
import java.util.*;
import net.datastructures.*;

/**
 * Description: A1, a spellchecker which checks every word in inputText.txt
 * Date: 3/15/22
 * Author: Sean Stille
 * Bugs: None that I'm aware of
 * Reflection:  I'm pretty happy with how I designed this. Early on I decided to create a WrongWord class, which represented words found
 *              in the text file that weren't in the dictionary. I made the class so that I could just call toString and certain getter methods
 *              for the output. It really streamlined the process. This project really helped me get a better grasp on static vs non-static
 *              methods and variables. Before this, I understood the difference, but perhaps not how to apply that knowledge.
 */
public class App {
    static LinkedPositionalList<String> dictionary; //The list of words considered to be correctly spelled
    static LinkedList<String> text; //The text entry
    static LinkedList<WrongWord> incorrectWords; //A list of my custom WrongWord objects to keep track of them and allow for easy iteration
    
    public static void main(String[] args) throws Exception {
        App app = new App();
        app.runSpellCheck();
    }
    /**
     * Runs the entire spellcheck, first it calls seperate methods to load the two files, then it
     * identifies incorrectly spelled words and adds them to the IncorrectWords list.
     */
    void runSpellCheck(){
        incorrectWords = new LinkedList<>();
        boolean isWord; //True if found in dictionary    
        double averageSubs = 0; //Used to print average number of subsitutions
        getDictionary();
        getText();
    
        for ( String t : text ){ //For every string in text, check if it matches a dictionary word
            isWord = false;
            for (String d : dictionary){
                if ( d.equalsIgnoreCase(t) ){
                    isWord = true;
                }
            }
            if (! isWord ){     // If it doesn't match a dictionary word, create a new WrongWord object and add it to incorrectWords
                incorrectWords.add( new WrongWord(t) );
            }
            
        }
        for( WrongWord w : incorrectWords ){ //Prints all incorrect words and their possible corrections
            System.out.println(w);           //See WrongWord.toString() for formatting
        }
        
        System.out.printf("\n# of words spellchecked: %d", text.size());
        System.out.printf("\n%% of words misspelled: %.1f",((double)incorrectWords.size()/text.size())*100);

        for( int i = 0; i < incorrectWords.size(); i++){        //getting average substitution count
            averageSubs += incorrectWords.get(i).getSubCount();
        }
        averageSubs /= incorrectWords.size();

        System.out.printf("\nAverage # of suggestions / misspelled word: %.1f", averageSubs);
        System.out.printf("\nSwaps: %d\nInsertions: %d\nDeletions: %d\nReplacements: %d\n", 
            WrongWord.getSwaps(),WrongWord.getInsertions(),WrongWord.getDeletions(), WrongWord.getReplacements());
    }   
    /**
     * Reads in inputText.txt and stores it in the text list.
     */
    public void getText(){
        text = new LinkedList<>();
        Scanner scan = null;
        File file =  null;
        try{
            file = new File("inputText.txt");
            scan = new Scanner(file);
            scan.useDelimiter("[^a-zA-Z]+");
            while( scan.hasNext() ){
                text.addLast(scan.next().toLowerCase());
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            scan.close();
        }
    }
    /**
     * reads in jlawler-wordlist.txt and stores it in the dictionary positional list
     */
    public void getDictionary(){
        Scanner scan = null;
        dictionary = new LinkedPositionalList<>();
        File file =  null;
        try{
            file = new File("jlawler-wordlist.txt" );
            scan = new Scanner(file);
            while( scan.hasNext() ){
                dictionary.addLast(scan.nextLine());
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            scan.close();
        }
      
    }
    /**
     * A class used to represent a word that is not in the dictionary, but is present in the text
     */
    private class WrongWord{
        String word;
        double subCount;                //The number of potential substitutions for a given word
        static int swaps = 0;           //The number of swap errors
        static int insertions = 0;      //The number of insertion errors
        static int deletions = 0;       //The number of deletion errors
        static int replacements = 0;    //The number of replacement errors
        LinkedList<String> substitutions;
        /**
         * Constructor for the word class
         * @param word The incorrectly spelt word
         */
        protected WrongWord(String word){
            this.word = word;
            substitutions = new LinkedList<>();
            generateSubStats();
        }
        /**
         * getter for the number of swaps
         * @return the number of swaps
         */
        public static int getSwaps(){
            return swaps;
        }
        /**
         * getter for the number of insertions
         * @return the number of insertions
         */
        public static int getInsertions(){
            return insertions;
        }
        /**
         * getter for the number of deletions
         * @return the number of deletions
         */
        public static int getDeletions(){
            return deletions;
        }
        /**
         * getter for the number of replacements
         * @return the number of replacements
         */
        public static int getReplacements(){
            return replacements;
        }
        /**
         * getter for subCount
         * @return The amount of possible substitutions a WrongWord has
         */
        protected double getSubCount(){
            return subCount;
        }
        /**
         * Generates the stats for a given WrongWord, calls the methods that check for the individual errors
         */
        private void generateSubStats(){
            isSwap();
            isDeletion();
            isReplacement();
            isInsertion();
            
        }
        /**
         * Checks to see if the word has any swaps, if it does, the swaps are added to the substitutions list
         */
        private void isSwap(){
            StringBuffer sbWord = new StringBuffer(word);
            String swappedList;
            for( int i = 0; i < word.length()-1; i++){
                sbWord.append("," + word.substring(0,i) + word.charAt(i+1) + "" + word.charAt(i) + word.substring(i+2) );
            }
            swappedList = sbWord.toString();

            for( String d : dictionary ){
                if(swappedList.contains(d) && d.length() == word.length() ){
                    substitutions.add(d);
                    swaps++;
                    subCount++;
                }
            }
        }
        /**
         * Checks to see if the word has any insertions, if it does, the swaps are added to the substitutions list
         */
        private void isInsertion(){
            boolean insertionFound;
            for( String d : dictionary ){
                insertionFound = false;
                if (word.length() == d.length()+1){
                    for ( int i = 0; i < word.length(); i++){
                        if ( (word.substring(0,i)+word.substring(i+1, word.length())).contains(d)){
                            insertionFound = true;
                        }
                    }
                }

                if (insertionFound){
                    substitutions.add(d);
                    insertions++;
                    subCount++;
                }
            }
        }
        /**
         * Checks to see if the word has any deletions, if it does, the swaps are added to the substitutions list
         */
        private void isDeletion(){
            boolean deletionFound;
            for (String d : dictionary ){
                deletionFound = false;
                if(word.length() == d.length()-1){
                    for ( int i = 0; i < d.length(); i++){ //Everything in this loop is kinda just the inverse of the insertion loop
                        if ( (d.substring(0,i)+d.substring(i+1, d.length())).contains(word)){
                            deletionFound = true;
                        }
                    }
                }

                if(deletionFound){
                    substitutions.add(d);
                    deletions++;
                    subCount++;
                }
            }
        }
        /**
         * Checks to see if the word has any replacement, if it does, the swaps are added to the substitutions list
         */
        private void isReplacement(){
           boolean replacementFound;
           for (String d : dictionary){
               replacementFound = false;
               if(word.length() == d.length() ){
                   for( int i = 0; i < word.length(); i++){
                        if (  (d.substring(0,i) + d.substring(i+1)).contains(word.substring(0,i) + word.substring(i+1)) ){
                            replacementFound = true;
                        }
                   }
               }
               if (replacementFound){
                   substitutions.add(d);
                   replacements++;
                   subCount++;
               }
           }
        }
        /**
         * ToString method, returns the incorrectly spelt word, followed by a formatted list of its possible substitutions
         */
        @Override
        public String toString(){
            String wordAndSubs = "";
            wordAndSubs += word + " - ";
            if( substitutions.isEmpty()){
                wordAndSubs += "No Suggestions  ";
            }
            else{
                for ( String s : substitutions){
                    wordAndSubs += s + ", ";
                }
            }
            
            return wordAndSubs.substring(0, wordAndSubs.length()-2);
        }
    }
}
