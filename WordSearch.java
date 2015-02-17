/**
 * Class Name:    WordSearch
 * Class Description: A java program that will solve a
 *     word search puzzle given in the form of a grid.
 *  
 *     NOTE: This is still a work in progress
 *
 * @author            Thomas McKeesick
 * Creation Date:     Wednesday, January 21 2015, 01:57
 * Last Modified:     Tuesday, February 17 2015, 17:20
 *
 * @version 0.2.1     See CHANGELOG
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.FileReader;
import java.io.BufferedReader;

import java.io.IOException;

public class WordSearch {
    public static void main(String[] args) {
        if( args.length < 2 ) {
            System.err.println("Must input a dictionary AND puzzle" +
                    "file to read");
            System.err.println("Usage: java WordSearch <dictionary> <puzzle>");
            System.exit(1);
        }
        int numLetters;
        if( args.length == 3 ) {
            numLetters = Integer.parseInt(args[2]);
        } else {
            numLetters = -1;
        }

        RBTree<String> dict = loadDict(args[0]);
        char[][] grid = loadPuzzle(args[1]);
        ArrayList<String[]> results = findStrings(grid);
        ArrayList<String[]> solutions = findWords(results, dict, numLetters);

        printGrid(grid);

        if( args.length == 3 ) {
            System.out.println("Searching for words with " + args[2] 
                    + " or more letters");    
        }
        //System.out.println("Total possible strings generated: " + results.size());
        System.out.println("Total solutions found: " + solutions.size() + "\n");
        printSolutions(solutions);
    
        System.exit(0);
    }

    /**
     * A method that loads a dictionary text file into a tree structure
     * @param filename The dictionary file to load
     * @return The Red-Black tree containing the dictionary
     */
    private static RBTree<String> loadDict(String filename) {
        RBTree<String> dict = new RBTree<String>();
        try {
            BufferedReader in = new BufferedReader(
                    new FileReader(filename));
            String word;
            while( (word = in.readLine()) != null ) {
                dict.insert(word);
            }
        } catch( IOException e ) {
            System.err.println("A file error occurred: " + filename );
            System.exit(1);
        }
        return dict;
    }

    /**
     * Loads a word puzzle from a text file into a 2D char array
     * @param filename The puzzle file to load
     * @return The word-search puzzle in a char array
     */
    private static char[][] loadPuzzle(String filename) {
        char[][] grid = new char[0][0];
        try {
            BufferedReader in = new BufferedReader(
                    new FileReader(filename));
            
            String line = in.readLine();
            
            int rows = Integer.parseInt(line.split(" ")[0]);
            int cols = Integer.parseInt(line.split(" ")[1]); 
            grid = new char[rows][cols];
            
            int rowNum = 0;
            int colNum = 0;

            while( (line = in.readLine()) != null ) {
                colNum = 0;
                String[] letters = line.split(" ");
                for( String s : letters ) {
                    grid[rowNum][colNum] = Character.toLowerCase( s.charAt(0) );
                    colNum++;
                }
                rowNum++;
            }
        } catch( IOException e ) {
            System.err.println("A file error occurred: " + filename );
            System.exit(1);
        }
        return grid;
    }

    /**
     * Private method that returns all possible solution strings from the word
     * puzzle
     * @param grid The word puzzle to search
     * @return The ArrayList of strings found by the method
     */
    private static ArrayList<String[]> findStrings(char[][] grid) {
        int cols = grid[0].length;
        int rows = grid.length;
        ArrayList<String[]> results = new ArrayList<String[]>();
        
        for( int i = 0; i < rows; i++ ) {

            for( int j = 0; j < cols; j++ ) {
                if(i - 1 > 0) {
                    ArrayList<String[]> line = moveN(grid, i, j);
                    for( String[] array : line ) {
                        results.add(array);
                    }
                }
                if(j - 1 > 0) {
                    ArrayList<String[]> line = moveW(grid, i, j);
                    for( String[] array: line ) {
                        results.add(array);
                    }
                }
                if( j - 1 > 0 && i - 1 > 0) {
                    ArrayList<String[]> line = moveNW(grid, i, j);
                    for( String[] array : line ) {
                        results.add(array);
                    }
                }
                if( i - 1 > 0 && j + 1 < cols) {
                    ArrayList<String[]> line = moveNE(grid, i, j, rows, cols);
                    for( String[] array : line ) {
                        results.add(array);
                    }
                }
            }
        }
        return results;
    }

    /**
     * Private method that finds all dictionary words from all possible words
     * in the word search
     * @param results The list of all possible words in the puzzle
     * @param dict The Red-Black tree containing the dictionary file
     * @return The ArrayList of all dictionary words found.
     */
    private static ArrayList<String[]> findWords(ArrayList<String[]> results, 
            RBTree<String> dict, int numLetters) {

        ArrayList<String[]> words = new ArrayList<String[]>();
        for( String[] array : results ) {
            if( dict.contains(array[0]) != null 
                    && array[0].length() > numLetters ) {
                words.add(array);
            }
        }
        return words;
    }

    /*-----------------------------------------------------------------------\.
     *        MOVE METHODS                                                    |
     *********^^^^^^^^^^^^***************************************************/

    /**
     * Private method that returns all North and reverse-North (South) strings
     * found for the supplied position in the word puzzle
     * @param grid The word puzzle to use
     * @param row The row number of the letter
     * @param col The column number of the letter
     * @return The ArrayList of the north and south strings above the point
     * supplied
     */
    private static ArrayList<String[]> moveN(char[][] grid, int row, int col) {
        ArrayList<String[]> results = new ArrayList<String[]>();
        StringBuilder word = new StringBuilder();
        for( int i = row; i >= 0; i-- ) {
            word.append(grid[i][col]);
            if(word.length() > 2) {
                results.add(formatString(word.toString(), row, col, i, col));
                word.reverse();
                results.add(formatString(word.toString(), i, col, row, col));
                word.reverse();
            }
        }
        return results;
    }
  
    /**
     * Private method that returns all West and reverse-West (East) strings
     * found for the supplied position in the word puzzle
     */
    private static ArrayList<String[]> moveW( char[][] grid, int row, int col ) {
        ArrayList<String[]> results = new ArrayList<String[]>();
        StringBuilder word = new StringBuilder();
        for( int j = col; j >= 0; j-- ) {
            word.append(grid[row][j]);
            if(word.length() > 2) {
                results.add(formatString(word.toString(), row, col, row, j));
                word.reverse();
                results.add(formatString(word.toString(), row, j, row, col));
                word.reverse();
            }
        }
        return results;
    }

    /**
     * Private method that returns all North-West and reverse-North-West 
     * (South-East) strings found for the supplied position in the word puzzle
     */
    private static ArrayList<String[]> moveNW( char[][] grid, int row, int col) {
        ArrayList<String[]> results = new ArrayList<String[]>();
        StringBuilder word = new StringBuilder();

        for( int i = row, j = col; i >= 0 && j >= 0; i--, j-- ) {
                word.append(grid[i][j]);
                if(word.length() > 2) {
                    results.add(formatString(word.toString(), row, col, i, j));
                    word.reverse();
                    results.add(formatString(word.toString(), i, j, row, col));
                    word.reverse();
                }
        }
        return results;
    }

    /**
     * Private method that returns all North-East and reverse-North-East 
     * (South-West) strings found for the supplied position in the word puzzle
     */
    private static ArrayList<String[]> moveNE(char[][] grid, int row, int col, int numRows, int numCols) {
        ArrayList<String[]> results = new ArrayList<String[]>();
        StringBuilder word = new StringBuilder();
        for( int i = row, j = col; i >= 0 && j < numCols; i--, j++) {
            word.append(grid[i][j]);
            if(word.length() > 2) {
                results.add(formatString(word.toString(), row, col, i, j));
                word.reverse();
                results.add(formatString(word.toString(), i, j, row, col));
                word.reverse();
            }
        }
        return results;
    }

    /*-----------------------------------------------------------------------\.
     *        FORMAT AND PRINT METHODS                                        |
     ************************************************************************/

    private static String[] formatString(String str, int rowFrom, int colFrom,
            int rowTo, int colTo) {

            String[] tmp = new String[2];
            
            tmp[0] = str;           
            tmp[1] = "[" + (char) (colFrom + 'A') + ", " +
                    String.format("%-2s", (rowFrom+1)) + "] -> [" + 
                    (char)(colTo+'A') + ", " + 
                    String.format("%-2s", (rowTo+1)) + "]";
            return tmp;
    }

    /**
     * Prints the word search grid in human-readable form with coordinates.
     * All characters in the grid are upper case, rows numbers are alphabetic
     * and colums are integers 
     * @param grid The word search grid to print
     */
    public static void printGrid(char[][] grid) {     
        System.out.print("    ");
        for( int i = 0; i < grid.length; i++ ) {
            System.out.print((char) ('A' + i) + " ");
        }
        System.out.println();
        System.out.print("   ");
        for( int i = 0; i < grid.length; i ++ ) {
            System.out.print("--");
        }
        System.out.println();
        
        int rowNum = 1;
        for( char[] row : grid ) {
            System.out.printf("%02d", rowNum);
            System.out.print(" |");
            for( char c : row ) {
                System.out.print(Character.toUpperCase(c) + " ");
            }
            System.out.println();
            rowNum++;
        }
        System.out.println();
    }

    /**
     * Sorts the solutions into alphabetical order, then prints them to the
     * user
     * @param words The ArrayList of solutions to print 
     */
    public static void printSolutions(ArrayList<String[]> words) {

        Collections.sort(words, new Comparator<String[]>() {
            @Override
            public int compare(String[] word1, String[] word2) {
                return word1[0].compareTo(word2[0]);
            }
        });

        for(String[] array: words) {
            System.out.println(array[1] + ": " + array[0].toUpperCase());
        }
    }

}
