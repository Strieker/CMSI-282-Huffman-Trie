package huffman;

import java.util.*;

/**
 * Huffman instances provide reusable Huffman Encoding Maps for
 * compressing and decompressing text corpi with comparable
 * distributions of characters.
 */
public class Huffman {
    
    // -----------------------------------------------
    // Construction
    // -----------------------------------------------

	private HuffNode trieRoot;
	private Map<Character, String> encodingMap;
	    
	public Map<Character, String> getEncodingMap(){
		return this.encodingMap;
	}
	
    private void constructCharToCountMap(String corpus, HashMap<Character, Integer> charCountPairings) {
    	int count = 0;
    	Character currentLetter;
    	for(int i = 0; i < corpus.length(); i++) {
    		currentLetter = corpus.charAt(i);
    		for(int j = 1; j < corpus.length(); j++) {
    			if(corpus.charAt(j) == currentLetter) {
        			if(charCountPairings.containsKey(currentLetter)) {
        				count = charCountPairings.get(currentLetter);
        				charCountPairings.replace(currentLetter, count, count+1);
        			} else {
        				charCountPairings.put(currentLetter, 1);
        			}
        		}
    		}
    		
    	}
    }
    
    private void constructTriePriorityQueue(String corpus, PriorityQueue<HuffNode> trie, HashMap<Character, Integer> charCountPairings) {
		HuffNode currentNode;
		for(Map.Entry<Character,Integer> entry: charCountPairings.entrySet()) {
			currentNode =  new HuffNode(entry.getKey(), entry.getValue());
			trie.add(currentNode);
		}
    }
    
    private void constructTrie(String corpus, PriorityQueue<HuffNode> trie, 
    		HashMap<Character, Integer> charCountPairings) {
    	HuffNode huff1, huff2, toAdd;
    	while(trie.size() != 1) {
    		huff1 = trie.poll();
    		huff2 = trie.poll();
    		toAdd = new HuffNode('/', huff1.count + huff2.count);
    		toAdd.left = huff1;
    		toAdd.right = huff2;
    		trie.add(toAdd);
    	}
    	trieRoot = trie.poll();
    	System.out.println();
    }
    
    private void constructEncodingMap(String byteEncoding, HuffNode iterator, 
    		Map<Character, String> encodingMap) {
    	if(iterator.isLeaf()) {
        		encodingMap.put(iterator.character, byteEncoding);
        		return;
    	}
    	if (iterator.left != null) {
        	String newByteEncoding = byteEncoding + "0";
        	constructEncodingMap(newByteEncoding, iterator.left, encodingMap);
    	}
    	if (iterator.right != null) {
        	String newByteEncoding = byteEncoding + "1";
        	constructEncodingMap(newByteEncoding, iterator.right, encodingMap);
    	}
    }
    
    /**
     * Creates the Huffman Trie and Encoding Map using the character
     * distributions in the given text corpus
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     */
    Huffman (String corpus) {
    	// AM I MESSING UP THE PRIORITY QUEUE? IS IT A MAX HEAP THE WAY IM DOING IT?
    	PriorityQueue<HuffNode> trie = new PriorityQueue<>();
    	HashMap<Character, Integer> charCountPairings = new HashMap<>();
    	constructCharToCountMap(corpus, charCountPairings);
    	constructTriePriorityQueue(corpus, trie, charCountPairings);
    	constructTrie(corpus, trie, charCountPairings);
    	this.encodingMap = new HashMap<>();
    	constructEncodingMap("",trieRoot, this.encodingMap);
    	for(Map.Entry<Character,String> entry: encodingMap.entrySet()) {
    		System.out.println(entry.getKey());
    		System.out.println(entry.getValue());
    		System.out.println("yup");
    	}
    }
    
    // -----------------------------------------------
    // Compression
    // -----------------------------------------------
    
    /**
     * Compresses the given String message / text corpus into its Huffman coded
     * bitstring, as represented by an array of bytes. Uses the encodingMap
     * field generated during construction for this purpose.
     * @param message String representing the corpus to compress.
     * @return {@code byte[]} representing the compressed corpus with the
     *         Huffman coded bytecode. Formatted as 3 components: (1) the
     *         first byte contains the number of characters in the message,
     *         (2) the bitstring containing the message itself, (3) possible
     *         0-padding on the final byte.
     */
    public byte[] compress (String message) {
        throw new UnsupportedOperationException();

        // IF % 8 IS NOT 0 ADD PADDED 0S UNTIL IT IS AND YOU ALSO HAVE TO STORE THE ORIGINAL LENGTH SO YOU MAKE SURE THE 
        // PADDING DOESNT MESS ANYTHING UP 
        
//        Message Length: the first byte will represent 
//        a positive number with a maximum value of 127 indicating 
//        the number of characters that were encoded in the message content that follows.
//        Message Content: the bytes that follow the first will 
//        be filled as-needed to represent the contiguous Huffman Coded, compressed text corpus.
//        Padding: the last byte in the compressed corpus may contain some 
//        0-padding if not all bits of the final byte are used. 
//        Note that this padding will never be larger than 7 bits.
        
        //LENGTH|ENCODING|PADDED0S
        
//        For constructing the resulting byte array, look into using Java's ByteArrayOutputStream
//        For pesky end bytes in the content that require padding, look into Java's bitshift operator: <<
//        In constructing the bytes to write using ByteArrayOutputStream, 
//        you may find Integer.parseInt and Byte.parseByte methods useful 
//        (consider using the signatures with radix 2 for binary!).
    }
    
    
    // -----------------------------------------------
    // Decompression
    // -----------------------------------------------
    
    /**
     * Decompresses the given compressed array of bytes into their original,
     * String representation. Uses the trieRoot field (the Huffman Trie) that
     * generated the compressed message during decoding.
     * @param compressedMsg {@code byte[]} representing the compressed corpus with the
     *        Huffman coded bytecode. Formatted as 3 components: (1) the
     *        first byte contains the number of characters in the message,
     *        (2) the bitstring containing the message itself, (3) possible
     *        0-padding on the final byte.
     * @return Decompressed String representation of the compressed bytecode message.
     */
    public String decompress (byte[] compressedMsg) {
        throw new UnsupportedOperationException();
        
//        Remember that you have access to the following: 
//        (1) the first byte of the compressedMsg indicates 
//        how many characters need to be decoded in what follows, 
//        and (2) the trieRoot holds (after construction) the Huffman 
//        Trie that compressed the message that you are decompressing.
//        You might find the String.format method useful for unpacking 
//        a particular byte into its bitstring representation for ease 
//        of traversing the Huffman Trie.
    }
    
    
    // -----------------------------------------------
    // Huffman Trie
    // -----------------------------------------------
    
    /**
     * Huffman Trie Node class used in construction of the Huffman Trie.
     * Each node is a binary (having at most a left and right child), contains
     * a character field that it represents (in the case of a leaf, otherwise
     * the null character \0), and a count field that holds the number of times
     * the node's character (or those in its subtrees) appear in the corpus.
     */
    private static class HuffNode implements Comparable<HuffNode> {
//    	The HuffNode class implements the Comparable interface, meaning you do not 
    	// need to worry about constructing the PriorityQueue during this step 
    	// with its own Comparator (new trick to have up your sleeve!)

        HuffNode left, right;
        char character;
        int count;
        
        HuffNode (char character, int count) {
            this.count = count;
            this.character = character;
        }
        
        public boolean isLeaf () {
            return left == null && right == null;
        }
        
        public int compareTo (HuffNode other) {
            return this.count - other.count;
        }
        
    }
    

}
