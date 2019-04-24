package huffman;

import java.util.*;

import java.math.*;

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
    		if(charCountPairings.containsKey(currentLetter)) {
        		count = charCountPairings.get(currentLetter);
        		charCountPairings.replace(currentLetter, count, count+1);
        	} else {
        		charCountPairings.put(currentLetter, 1);
    		}	
    	}
    }
    
    private void constructTriePriorityQueue(String corpus, PriorityQueue<HuffNode> trie, HashMap<Character, Integer> charCountPairings) {
		HuffNode currentNode;
		for(Map.Entry<Character,Integer> entry: charCountPairings.entrySet()) {
			currentNode =  new HuffNode(entry.getKey(), entry.getValue());
//			System.out.println(entry.getKey() + " " + entry.getValue());
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
//    	System.out.println();
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
    
    private String addPadded0s(String byteEncoding) {
    	if(byteEncoding.length() % 8 != 0) {
    		while( byteEncoding.length() % 8 != 0) {
    			byteEncoding = byteEncoding + "0";
    		}
    	}
    	return byteEncoding;
    }
    
    private String constructStringBitEncoding(String message, String stringBitEncoding) {
    	for(int i = 0; i < message.length(); i++) {
    		stringBitEncoding += encodingMap.get(message.charAt(i));
    	}
    	return addPadded0s(stringBitEncoding);
    }
    
    
    
    public byte[] compress (String message) {
    	// WEIRD TEST CASES
    	int currentByte = 0; 
    	int leadingIndex  = 0;
    	int indexOfByteArray = 1;
    	Byte lengthOfMessage =(byte)message.length();
    	String stringByteEncoding = constructStringBitEncoding(message, "");
    	byte[] compressedEncodingArray = new byte[(stringByteEncoding.length() / 8) + 1];
    	compressedEncodingArray[0] = lengthOfMessage;
    	for(int trailingIndex = 7; trailingIndex < stringByteEncoding.length(); trailingIndex += 8) {
    		currentByte = Integer.parseInt(stringByteEncoding.substring(leadingIndex,trailingIndex + 1), 2);
    		compressedEncodingArray[indexOfByteArray] = (byte)currentByte;
    		leadingIndex = trailingIndex;
    		indexOfByteArray++;
    	} 
    	return compressedEncodingArray;
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
    
    
    private char findCharBasedOnByteValue(String byteEncoding) {
    	for(Map.Entry<Character,String> entry: encodingMap.entrySet()) {
    		if(entry.getValue().equals(byteEncoding)) {
    			return entry.getKey();
    		}
    	}
    	return '/';
    }
    
    private String stringBitEncoding(byte[] compressedMsg, String decompressedMessage) {
    	  for(int i = 1; i < compressedMsg.length; i++) {
          	decompressedMessage += String.format("%8s", Integer.toBinaryString(compressedMsg[i] & 0xFF)).replace(' ', '0');
          }
    	  return decompressedMessage;
    }
    
    private String findOriginalMessage(String decompressedMessage, byte[] compressedMsg, int lengthWithoutPadding) {
    	int firstEncodedBit = 0;
        int lastEncodedBit = 0;
        int counter = 0; 
        String currentByteString = stringBitEncoding(compressedMsg, "");
    	while((counter < lengthWithoutPadding)) {
        	if(encodingMap.containsValue(currentByteString.substring(firstEncodedBit, lastEncodedBit))) {
        		decompressedMessage = decompressedMessage + Character.toString(findCharBasedOnByteValue(currentByteString.substring(firstEncodedBit, lastEncodedBit)));
        		firstEncodedBit = lastEncodedBit;
            	counter++;
        	}
        	lastEncodedBit++;
        }
    	return decompressedMessage;
  }
    
    public String decompress (byte[] compressedMsg) {
    	int lengthWithoutPadding = compressedMsg[0];
    	String decompressedMessage = "";
        return findOriginalMessage(decompressedMessage, compressedMsg, lengthWithoutPadding);
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
