package huffman;

import static org.junit.Assert.*;
import org.junit.Test;

public class HuffmanTests {

	// Compression Tests
	// -----------------------------------------------

	@Test
	public void comp_t0() {
		Huffman h = new Huffman("AB");
		// byte 0: 0000 0010 = 2 (message length = 2)
		// byte 1: 0100 0000 = 64 (0 = "A", 1 = "B")
		// [!] Only first 2 bits of byte 1 are meaningful
		byte[] compressed = { 2, 64 };
		assertArrayEquals(compressed, h.compress("AB"));
	}

	@Test
	public void comp_t1() {
		Huffman h = new Huffman("BA");
		// byte 0: 0000 0010 = 2 (message length = 2)
		// byte 1: 1000 0000 = -128 (0 = "A", 1 = "B")
		// [!] Only first 2 bits of byte 1 are meaningful
		byte[] compressed = { 2, -128 };
		assertArrayEquals(compressed, h.compress("BA"));
	}

	@Test
	public void comp_t2() {
		Huffman h = new Huffman("ABBBCC");
		// byte 0: 0000 0110 = 6 (message length = 6)
		// byte 1: 1000 0111 = -121 (10 = "A", 0 = "B", C = 11)
		// byte 2: 1000 0000 = -128
		// [!] Only first bit of byte 2 is meaningful
		byte[] compressed = { 6, -121, -128 };
		assertArrayEquals(compressed, h.compress("ABBBCC"));
	}

	@Test
	public void comp_t3() {
		Huffman h = new Huffman("ABBBCC");
		// byte 0: 0000 0110 = 6 (message length = 6)
		// byte 1: 0100 1101 = 77 (10 = "A", 0 = "B", C = 11)
		// byte 2: 1000 0000 = -128
		byte[] compressed = { 6, 77, -128 };
		assertArrayEquals(compressed, h.compress("BABCBC"));
	}

	@Test
	public void comp_t4() {
		Huffman h = new Huffman("AAAAAAAAAAAAAAAAA");
		// byte 0: 0001 0001 = 17 (message length = 17)
		// byte 1: 00000000
		// byte 2: 00000000
		// byte 3: 00000000
		byte[] compressed = { 17, 0, 0, 0 };
		assertArrayEquals(compressed, h.compress("AAAAAAAAAAAAAAAAA"));
	}

	@Test
	public void comp_t5() {
		Huffman h = new Huffman("BBBBBBBAAAAAAACCCCCCC");
		// byte 0: 0001 0101 = 21 (message length = 21)
		// byte 1: 00000001 = 1
		// byte 2: 01010101 = 85
		// byte 3: 01010111 = 87
		// byte 4: 11111111 = -1
		// byte 5: 11100000 = -32
		byte[] compressed = { 21, 1, 85, 87, -1, -32 };
		assertArrayEquals(compressed, h.compress("BBBBBBBAAAAAAACCCCCCC"));
	}

	@Test
	public void comp_t6() {
		Huffman h = new Huffman("                 ");
		// byte 0: 0001 0001 = 6 (message length = 17)
		// byte 1: 00000000
		// byte 2: 00000000
		// byte 3: 00000000
		byte[] compressed = { 17, 0, 0, 0 };
		assertArrayEquals(compressed, h.compress("                 "));
	}

	@Test
	public void comp_t7() {
		Huffman h = new Huffman("");
		// byte 0: 0000 0000 = 0 (message length = 0)
		// byte 1: 00000000
		// byte 2: 00000000
		// byte 3: 00000000
		byte[] compressed = { 0 };
		assertArrayEquals(compressed, h.compress(""));
	}

	// Decompression Tests
	// -----------------------------------------------
	@Test
	public void decomp_t0() {
		Huffman h = new Huffman("AB");
		// byte 0: 0000 0010 = 2 (message length = 2)
		// byte 1: 0100 0000 = 64 (0 = "A", 1 = "B")
		byte[] compressed = { 2, 64 };
		assertEquals("AB", h.decompress(compressed));
	}

	@Test
	public void decomp_t1() {
		Huffman h = new Huffman("AB");
		// byte 0: 0000 0010 = 2 (message length = 2)
		// byte 1: 1000 0000 = -128 (0 = "A", 1 = "B")
		byte[] compressed = { 2, -128 };
		assertEquals("BA", h.decompress(compressed));
	}

	@Test
	public void decom_t3() {
		Huffman h = new Huffman("ABBBCC");
		// byte 0: 0000 0110 = 6 (message length = 6)
		// byte 1: 1000 0111 = -121 (10 = "A", 0 = "B", C = 11)
		// byte 2: 1000 0000 = -128
		byte[] compressed = { 6, -121, -128 };
		assertEquals("ABBBCC", h.decompress(compressed));
	}

	@Test
	public void decom_t4() {
		Huffman h = new Huffman("ABBBCC");
		// byte 0: 0000 0110 = 6 (message length = 6)
		// byte 1: 0100 1101 = 77 (10 = "A", 0 = "B", C = 11)
		// byte 2: 1000 0000 = -128
		byte[] compressed = { 6, 77, -128 };
		assertEquals("BABCBC", h.decompress(compressed));
	}

	@Test
	public void decom_t5() {
		Huffman h = new Huffman("AAAAAAAAAAAAAAAAA");
		// byte 0: 0001 0001 = 17 (message length = 17)
		// byte 1: 00000000
		// byte 2: 00000000
		// byte 3: 00000000
		byte[] compressed = { 17, 0, 0, 0 };
		assertEquals("AAAAAAAAAAAAAAAAA", h.decompress(compressed));
	}

	@Test
	public void decom_t6() {
		Huffman h = new Huffman("BBBBBBBAAAAAAACCCCCCC");
		// byte 0: 0001 0101 = 21 (message length = 21)
		// byte 1: 00000001 = 1
		// byte 2: 01010101 = 85
		// byte 3: 01010111 = 87
		// byte 4: 11111111 = -1
		// byte 5: 11100000 = -32
		byte[] compressed = { 21, 1, 85, 87, -1, -32 };
		assertEquals("BBBBBBBAAAAAAACCCCCCC", h.decompress(compressed));
	}

	@Test
	public void decom_t7() {
		Huffman h = new Huffman("                 ");
		// byte 0: 0001 0001 = 6 (message length = 17)
		// byte 1: 00000000
		// byte 2: 00000000
		// byte 3: 00000000
		byte[] compressed = { 17, 0, 0, 0 };
		assertEquals("                 ", h.decompress(compressed));
	}

	@Test
	public void decom_t8() {
		Huffman h = new Huffman("");
		// byte 0: 0000 0000 = 0 (message length = 0)
		// byte 1: 00000000
		// byte 2: 00000000
		// byte 3: 00000000
		byte[] compressed = { 0 };
		assertEquals("", h.decompress(compressed));
	}

	@Test
	public void decom_t9() {
		Huffman h = new Huffman("SSHHAANNYY");

		// byte 0: 0000 1010 = 10 (message length = 10)
		// byte 1: 11111110
		// byte 2: 10000001
		// byte 2: 01110110
		byte[] compressed = { 10, -2, -127, 118 };
		assertEquals("SSHHAANNYY", h.decompress(compressed));
	}

}
