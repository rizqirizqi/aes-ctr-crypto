package main;

import java.io.File;

/**
 * A tester for the CryptoUtils class.
 * @author www.codejava.net
 *
 */
public class CryptoUtilsTest {

	public static void main(String[] args) throws Exception{
		//Modify file input here
		File inputFile = new File("encryption.flac");
		File keyFile = new File("key.txt");
		
		try {
			CryptoUtils.decrypt(keyFile, inputFile);
		} catch (CryptoException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
}
