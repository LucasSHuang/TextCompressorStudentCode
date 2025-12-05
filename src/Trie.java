public class Trie {
    private static int ASCII_VALUES = 256;
    private Node root;

    public Trie() {
        root = new Node();
    }

    // Inserts word into trie
    public void insert(String word) {
        Node current = root;

        // Iterates through word
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            int index = c;

            // If the letter isn't already filled out in the index where it should be add it
            if (current.getChild(index) == null) {
                current.setChild(index, new Node());
            }
            // Move to child
            current = current.getChild(index);
        }
        // Makes it so that the last letter shows that it is a real word
        current.setWord(true);
    }

    // Finds of the word is real or not
    public boolean find(String word) {
        Node current = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            int index = c;

            // If there is no letter in the index where it should be it can't be a word
            if (current.getChild(index) == null) {
                return false;
            }
            current = current.getChild(index);
        }
        // At the end of the word check last letter to see if it is a word
        return current.isWord();
    }

    // Node class for trie
    private class Node {
        private boolean isWord;
        private Node[] next;

        // Getters and setters
        private Node() {
            next = new Node[ASCII_VALUES];
            isWord = false;
        }

        private boolean isWord() {
            return isWord;
        }

        private void setWord(boolean word) {
            isWord = word;
        }

        private Node getChild(int index) {
            return next[index];
        }

        private void setChild(int index, Node child) {
            next[index] = child;
        }
    }
}
