package huffman;

import java.util.*;

/**
 * Huffman instances provide reusable Huffman Encoding Maps for compressing and
 * decompressing text corpi with comparable distributions of characters.
 */
public class Huffman {

	// -----------------------------------------------
	// Construction
	// -----------------------------------------------

	private HuffNode trieRoot;
	private Map<Character, String> encodingMap;

	/**
	 * Creates a HashMap of character keys found in the corpus whose matching values
	 * are the number of times the character repeats in the corpus
	 * 
	 * @param corpus A String representing a message / document corpus with
	 *               distributions over characters that are implicitly used
	 *               throughout the methods that follow. Note: this corpus ONLY
	 *               establishes the Encoding Map; later compressed corpi may
	 *               differ. charCountPairings A HashMap of character keys and the
	 *               number of times they are repeated in the corpus as their
	 *               corresponding values
	 */
	private void constructCharToCountMap(String corpus, HashMap<Character, Integer> charCountPairings) {
		int count = 0;
		Character currentLetter;
		for (int i = 0; i < corpus.length(); i++) {
			currentLetter = corpus.charAt(i);
			if (charCountPairings.containsKey(currentLetter)) {
				count = charCountPairings.get(currentLetter);
				charCountPairings.replace(currentLetter, count, count + 1);
			} else {
				charCountPairings.put(currentLetter, 1);
			}
		}
	}

	/**
	 * Creates a priority queue, whose priority is based on the number of
	 * repetitions of each character found in the corpus
	 * 
	 * @param corpus A String representing a message / document corpus with
	 *               distributions over characters that are implicitly used
	 *               throughout the methods that follow. Note: this corpus ONLY
	 *               establishes the Encoding Map; later compressed corpi may
	 *               differ. charCountPairings A HashMap of character keys and the
	 *               number of times they are repeated in the corpus as their
	 *               corresponding values trieQueue A priorityQueue whose priority
	 *               is dictated based on the number of repetitions of each
	 *               character found in charCountPairings
	 */
	private void constructTriePriorityQueue(String corpus, HashMap<Character, Integer> charCountPairings,
			PriorityQueue<HuffNode> trieQueue) {
		HuffNode currentNode;
		for (Map.Entry<Character, Integer> entry : charCountPairings.entrySet()) {
			currentNode = new HuffNode(entry.getKey(), entry.getValue());
			trieQueue.add(currentNode);
		}
	}

	/**
	 * Creates a Huffman trie based on the previously constructed priority queue of
	 * HuffNodes, which was constructed on the basis of the number of repititions of
	 * each character found in the corpus
	 * 
	 * @param corpus A String representing a message / document corpus with
	 *               distributions over characters that are implicitly used
	 *               throughout the methods that follow. Note: this corpus ONLY
	 *               establishes the Encoding Map; later compressed corpi may
	 *               differ. trieQueue A priorityQueue whose priority is dictated
	 *               based on the number of repetitions of each character found in
	 *               charCountPairings charCountPairings A HashMap of character keys
	 *               and the number of times they are repeated in the corpus as
	 *               their corresponding values
	 */
	private void constructTrie(String corpus, PriorityQueue<HuffNode> trieQueue,
			HashMap<Character, Integer> charCountPairings) {
		HuffNode huff1, huff2, toAdd;
		if (trieQueue.size() != 1 && trieQueue.size() != 0) {
			while (trieQueue.size() != 1) {
				huff1 = trieQueue.poll();
				huff2 = trieQueue.poll();
				toAdd = new HuffNode('/', huff1.count + huff2.count);
				toAdd.left = huff1;
				toAdd.right = huff2;
				trieQueue.add(toAdd);
			}
		} else if (trieQueue.size() == 1) {
			huff1 = trieQueue.poll();
			toAdd = new HuffNode('/', huff1.count);
			toAdd.left = huff1;
			trieQueue.add(toAdd);
		} else if (trieQueue.size() == 0) {
			toAdd = new HuffNode('/', 0);
			trieQueue.add(toAdd);
		}
		trieRoot = trieQueue.poll();
	}

	/**
	 * Creates the encoding map of distinct characters found in the corpus and
	 * corresponding byte codes using the huffman trie
	 * 
	 * @param byteEncoding A String representing of the current byte encoding for a
	 *                     given character from the corpus and increases as we
	 *                     recurse through the trie currentNode a HuffNode keeping
	 *                     track of where the current position is in the trie
	 *                     encodingMap a HashMap that that is modified to include
	 *                     distinct character keys and their corresponding byte
	 *                     encoding values after recursing through the trie
	 */
	private void constructEncodingMap(String byteEncoding, HuffNode currentNode, Map<Character, String> encodingMap) {
		if (currentNode.isLeaf()) {
			encodingMap.put(currentNode.character, byteEncoding);
			return;
		}
		if (currentNode.left != null) {
			constructEncodingMap(byteEncoding + "0", currentNode.left, encodingMap);
		}
		if (currentNode.right != null) {
			constructEncodingMap(byteEncoding + "1", currentNode.right, encodingMap);
		}
	}

	/**
	 * Creates the Huffman Trie and Encoding Map using the character distributions
	 * in the given text corpus
	 * 
	 * @param corpus A String representing a message / document corpus with
	 *               distributions over characters that are implicitly used
	 *               throughout the methods that follow. Note: this corpus ONLY
	 *               establishes the Encoding Map; later compressed corpi may
	 *               differ.
	 */
	Huffman(String corpus) {
		PriorityQueue<HuffNode> trie = new PriorityQueue<>();
		HashMap<Character, Integer> charCountPairings = new HashMap<>();
		constructCharToCountMap(corpus, charCountPairings);
		constructTriePriorityQueue(corpus, charCountPairings, trie);
		constructTrie(corpus, trie, charCountPairings);
		this.encodingMap = new HashMap<>();
		constructEncodingMap("", trieRoot, this.encodingMap);
	}

	// -----------------------------------------------
	// Compression
	// -----------------------------------------------

	/**
	 * Adds padded 0s to the current string version of the compressed byte encoding
	 * of the original corpus in order to more easily separate the bits into bytes
	 * and insert them into a compressed byte array
	 * 
	 * @param byteEncoding A String representing the original compressed byte
	 *                     encoding without padded 0s
	 * @return The compressed byte encoding with padded 0s
	 */
	private String addPadded0s(String byteEncoding) {
		if (byteEncoding.length() % 8 != 0) {
			while (byteEncoding.length() % 8 != 0) {
				byteEncoding = byteEncoding + "0";
			}
		}
		return byteEncoding;
	}

	/**
	 * Creates a string consisting of bit encodings by iterating over the original
	 * corpus and adding each bit encoding for each corresponding character
	 * 
	 * @param message String that's the original corpus to iterate through
	 *                stringBitEncoding String consisting of the original corpus'
	 *                encoded bits and padded 0s
	 * @return The the string representation of the compressed encoding of the
	 *         original corpus
	 */
	private String constructStringByteEncodingForCompression(String message, String stringBitEncoding) {
		for (int i = 0; i < message.length(); i++) {
			stringBitEncoding += encodingMap.get(message.charAt(i));
		}
		return addPadded0s(stringBitEncoding);
	}

	/**
	 * Compresses the given String message / text corpus into its Huffman coded
	 * bitstring, as represented by an array of bytes. Uses the encodingMap field
	 * generated during construction for this purpose.
	 * 
	 * @param message String representing the corpus to compress.
	 * @return {@code byte[]} representing the compressed corpus with the Huffman
	 *         coded bytecode. Formatted as 3 components: (1) the first byte
	 *         contains the number of characters in the message, (2) the bitstring
	 *         containing the message itself, (3) possible 0-padding on the final
	 *         byte.
	 */
	public byte[] compress(String message) {
		int currentByte = 0;
		int beginningOfCurrentByteIndex = 0;
		int indexOfCompressedEncodingArray = 1;
		int END_OF_FIRST_BYTE_INDEX_IN_STRING_ENCODING = 7;
		Byte lengthOfMessage = (byte) message.length();
		String stringByteEncoding = constructStringByteEncodingForCompression(message, "");
		byte[] compressedByteArrayEncoding = new byte[(stringByteEncoding.length() / 8) + 1];
		compressedByteArrayEncoding[0] = lengthOfMessage;
		for (int endOfCurrentByteIndex = END_OF_FIRST_BYTE_INDEX_IN_STRING_ENCODING; endOfCurrentByteIndex < stringByteEncoding
				.length(); endOfCurrentByteIndex += 8) {
			currentByte = Integer
					.parseInt(stringByteEncoding.substring(beginningOfCurrentByteIndex, endOfCurrentByteIndex + 1), 2);
			compressedByteArrayEncoding[indexOfCompressedEncodingArray] = (byte) currentByte;
			beginningOfCurrentByteIndex = endOfCurrentByteIndex;
			indexOfCompressedEncodingArray++;
		}
		return compressedByteArrayEncoding;
	}

	// -----------------------------------------------
	// Decompression
	// -----------------------------------------------

	/**
	 * Creates the string version of the compressed encoding consisting of bits by
	 * iterating over the byte array representation the compressed encoding of the
	 * original corpus
	 * 
	 * @param compressedMsg {@code byte[]} representing the compressed byte encoding
	 *                      of the original corpus compressedMessage the String
	 *                      representation of the byte encoding of the original
	 *                      corpus
	 * @return The the string representation of the compressed encoding of the
	 *         original corpus
	 */
	private String constructStringByteEncodingForDecompression(byte[] compressedMsg, String compressedMessage) {
		for (int i = 1; i < compressedMsg.length; i++) {
			compressedMessage += String.format("%8s", Integer.toBinaryString(compressedMsg[i] & 0xFF)).replace(' ',
					'0');
		}
		return compressedMessage;
	}

	/**
	 * Decompresses the given compressed array of bytes into their original, String
	 * representation. Uses the trieRoot field (the Huffman Trie) that generated the
	 * compressed message during decoding.
	 * 
	 * @param compressedMsg {@code byte[]} representing the compressed corpus with
	 *                      the Huffman coded bytecode. Formatted as 3 components:
	 *                      (1) the first byte contains the number of characters in
	 *                      the message, (2) the bitstring containing the message
	 *                      itself, (3) possible 0-padding on the final byte.
	 * @return Decompressed String representation of the compressed bytecode
	 *         message.
	 */
	public String decompress(byte[] compressedMsg) {
		int lengthWithoutPadding = compressedMsg[0];
		String decompressedMessage = "";
		HuffNode currentNodeInTrie = trieRoot;
		String currentByteString = constructStringByteEncodingForDecompression(compressedMsg, "");
		for (int positionInStringByteEncoding = 0; positionInStringByteEncoding < currentByteString
				.length(); positionInStringByteEncoding++) {
			if (decompressedMessage.length() == lengthWithoutPadding) {
				break;
			}
			if (currentByteString.charAt(positionInStringByteEncoding) == '0') {
				currentNodeInTrie = currentNodeInTrie.left;
			} else if (currentByteString.charAt(positionInStringByteEncoding) == '1') {
				currentNodeInTrie = currentNodeInTrie.right;
			}
			if (currentNodeInTrie.isLeaf()) {
				decompressedMessage += Character.toString(currentNodeInTrie.character);
				currentNodeInTrie = trieRoot;
			}

		}
		return decompressedMessage;
	}

	// -----------------------------------------------
	// Huffman Trie
	// -----------------------------------------------

	/**
	 * Huffman Trie Node class used in construction of the Huffman Trie. Each node
	 * is a binary (having at most a left and right child), contains a character
	 * field that it represents (in the case of a leaf, otherwise the null character
	 * \0), and a count field that holds the number of times the node's character
	 * (or those in its subtrees) appear in the corpus.
	 */
	private static class HuffNode implements Comparable<HuffNode> {
//    	The HuffNode class implements the Comparable interface, meaning you do not 
		// need to worry about constructing the PriorityQueue during this step
		// with its own Comparator (new trick to have up your sleeve!)

		HuffNode left, right;
		char character;
		int count;

		HuffNode(char character, int count) {
			this.count = count;
			this.character = character;
		}

		public boolean isLeaf() {
			return left == null && right == null;
		}

		public int compareTo(HuffNode other) {
			return this.count - other.count;
		}

	}

}
