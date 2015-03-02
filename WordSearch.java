/*
 * Class Name:        WordSearch
 *
 * @author            Thomas McKeesick
 * Creation Date:     Wednesday, January 21 2015, 01:57
 * Last Modified:     Monday, March 02 2015, 13:09
 *
 * @version 0.2.4     See CHANGELOG
 * Class Description: A java program that will solve a
 *         word search puzzle given in the form of a grid.
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.FileReader;
import java.io.BufferedReader;

import java.io.IOException;

public class WordSearch {
    public static class WordInfo {

        private String word;
        private int rowFrom;
        private int rowTo;
        private int colFrom;
        private int colTo;

        WordInfo(String word, int rowFrom, int colFrom, int rowTo, int colTo) {
            this.word = word;
            this.rowFrom = rowFrom;
            this.rowTo = rowTo;
            this.colFrom = colFrom;
            this.colTo = colTo;
        }

        public String getWord() { return word; }
        public int getRowFrom() { return rowFrom; }
        public int getRowTo() { return rowTo; }
        public int getColFrom() { return colFrom; }
        public int getColTo() { return colTo; }

        public String toString() {
            String info = "[" + (char) (colFrom + 'A') + ", " +
                    String.format("%-2s", (rowFrom+1)) + "] -> [" +
                    (char)(colTo+'A') + ", " +
                    String.format("%-2s", (rowTo+1)) + "] : " +
                    word.toUpperCase();
            return info;
        }
        public int compareTo(WordInfo word2) {
            return word.compareTo(word2.word);
        }
    }

    public static final int MIN_LETTERS = 4;
    private static int numLetters = MIN_LETTERS;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        if( args.length < 2 ) {
            System.err.println("Must input a dictionary AND puzzle" +
                    "file to read");
            System.err.println("Usage: java WordSearch <dictionary> <puzzle>" +
                               "<minimum_number_of_letters>");
            System.exit(1);
        }

        if( args.length == 3 ) {
            numLetters = Integer.parseInt(args[2]);
        }

        List<String> dict = loadDict(args[0]);
        char[][] grid = loadPuzzle(args[1]);
        List<WordInfo> solutions = findWords(grid, dict, numLetters);

        printGrid(grid);
        printSolutions(solutions, numLetters);

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) +
                           " milliseconds");
        System.out.println("Memory used: " +
                    ((runtime.totalMemory() - runtime.freeMemory()) / 1024) +
                    " kB");
        System.exit(0);
    }

    /**
     * A method that loads a dictionary text file into List structure
     * @param filename The dictionary file to load
     * @return The List containing the dictionary
     */
    private static List<String> loadDict(String filename) {
        List<String> dict = new ArrayList<String>();
        try {
            BufferedReader in = new BufferedReader(
                    new FileReader(filename));
            String word;
            while( (word = in.readLine()) != null ) {
                dict.add(word);
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
    public static char[][] loadPuzzle(String filename) {
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
                    grid[rowNum][colNum] =
                            Character.toLowerCase( s.charAt(0) );
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
    private static List<WordInfo> findWords(char[][] grid,
                                                 List<String> dict,
                                                 int numLetters) {
        int cols = grid[0].length;
        int rows = grid.length;
        List<WordInfo> results = new ArrayList<WordInfo>();

        for( int i = 0; i < rows; i++ ) {
            for( int j = 0; j < cols; j++ ) {
                if(i - (numLetters-1) >= 0) {
                    results.addAll(moveN(grid, dict, i, j));
                }
                if(j - (numLetters-1) >= 0) {
                    results.addAll(moveW(grid, dict, i, j));
                }
                if( j - (numLetters-1) >= 0 && i - (numLetters-1) > 0) {
                    results.addAll(moveNW(grid, dict, i, j));
                }
                if( i - (numLetters-1) >= 0 && j + (numLetters-1) < cols) {
                    results.addAll(moveNE(grid, dict, i, j, rows, cols));
                }
            }
        }
        return results;
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
    private static List<WordInfo> moveN(char[][] grid,
                                             List<String> dict,
                                             int row, int col) {

        List<WordInfo> results = new ArrayList<WordInfo>();
        StringBuilder word = new StringBuilder();
        for( int i = row; i >= 0; i-- ) {
            word.append(grid[i][col]);
            if(word.length() >= numLetters) {
                if(Collections.binarySearch(dict, word.toString()) >= 0) {
                    results.add(new WordInfo(word.toString(),
                                row, col, i, col));
                }
                word.reverse();
                if(Collections.binarySearch(dict, word.toString()) >= 0) {
                    results.add(new WordInfo(word.toString(),
                                i, col, row, col));
                }
                word.reverse();
            }
        }
        return results;
    }

    /**
     * Private method that returns all West and reverse-West (East) strings
     * found for the supplied position in the word puzzle
     */
    private static List<WordInfo> moveW(char[][] grid,
                                             List<String> dict,
                                             int row, int col ) {

        List<WordInfo> results = new ArrayList<WordInfo>();
        StringBuilder word = new StringBuilder();

        for( int j = col; j >= 0; j-- ) {
            word.append(grid[row][j]);
            if(word.length() >= numLetters) {
                if(Collections.binarySearch(dict, word.toString()) >= 0) {
                    results.add(new WordInfo(word.toString(),
                                row, col, row, j));
                }
                word.reverse();
                if(Collections.binarySearch(dict, word.toString()) >= 0) {
                    results.add(new WordInfo(word.toString(),
                                row, j, row, col));
                }
                word.reverse();
            }
        }
        return results;
    }

    /**
     * Private method that returns all North-West and reverse-North-West
     * (South-East) strings found for the supplied position in the word puzzle
     */
    private static List<WordInfo> moveNW(char[][] grid,
                                              List<String> dict,
                                              int row, int col) {

        List<WordInfo> results = new ArrayList<WordInfo>();
        StringBuilder word = new StringBuilder();

        for( int i = row, j = col; i >= 0 && j >= 0; i--, j-- ) {
            word.append(grid[i][j]);
            if(word.length() >= numLetters) {
                if(Collections.binarySearch(dict, word.toString()) >= 0) {
                    results.add(new WordInfo(word.toString(), row, col, i, j));
                }
                word.reverse();
                if(Collections.binarySearch(dict, word.toString()) >= 0) {
                    results.add(new WordInfo(word.toString(), i, j, row, col));
                }
                word.reverse();
            }
        }
        return results;
    }

    /**
     * Private method that returns all North-East and reverse-North-East
     * (South-West) strings found for the supplied position in the word puzzle
     */
    private static List<WordInfo> moveNE(char[][] grid,
                                              List<String> dict,
                                              int row, int col,
                                              int numRows, int numCols) {

        List<WordInfo> results = new ArrayList<WordInfo>();
        StringBuilder word = new StringBuilder();
        for( int i = row, j = col; i >= 0 && j < numCols; i--, j++) {
            word.append(grid[i][j]);
            if(word.length() >= numLetters) {
                if(Collections.binarySearch(dict, word.toString()) >= 0) {
                    results.add(new WordInfo(word.toString(), row, col, i, j));
                }
                word.reverse();
                if(Collections.binarySearch(dict, word.toString()) >= 0) {
                    results.add(new WordInfo(word.toString(), i, j, row, col));
                }
                word.reverse();
            }
        }
        return results;
    }

    /*-----------------------------------------------------------------------\.
     *        FORMAT AND PRINT METHODS                                        |
     *********^^^^^^^^^^^^^^^^^^^^^^^^***************************************/

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
            System.out.printf("%02d |", rowNum);
            for( char c : row ) {
                System.out.print(Character.toUpperCase(c) + " ");
            }
            System.out.println();
            rowNum++;
        }
        System.out.println();
    }

    /**
     * Sorts the solutions into alphabetical order, then prints them to stdout
     * @param words The ArrayList of solutions to print
     */
    public static void printSolutions(List<WordInfo> words,
    								  int numLetters) {

        Collections.sort(words, new Comparator<WordInfo>() {
            @Override
            public int compare(WordInfo word1, WordInfo word2) {
                return word1.compareTo(word2);
            }
        });
        System.out.println("Found " + words.size() + " words with " +
        					numLetters + " letters or more");
        for(WordInfo w: words) {
            System.out.println(w.toString());
        }
    }
}
