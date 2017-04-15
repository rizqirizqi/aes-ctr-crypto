package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * A utility class that encrypts or decrypts a file. 
 * @author www.codejava.net
 *
 */
public class CryptoUtils {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/CTR/PKCS5Padding";
	private static final byte[] IVBYTES = new byte[] 
		{ 0x00, 0x01, 0x02, 0x03, 0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01 };

	public static void encrypt(File keyFile, File inputFile) throws Exception {
		prepCrypto(Cipher.ENCRYPT_MODE, keyFile, inputFile);
	}

	public static void decrypt(File keyFile, File inputFile) throws Exception {
		prepCrypto(Cipher.DECRYPT_MODE, keyFile, inputFile);
	}

	private static void prepCrypto(int cipherMode, File keyFile, File inputFile) throws Exception{
		String extension = inputFile.getName();
		int pos = extension.lastIndexOf(".");
		extension = extension.substring(pos, extension.length());

		String outputFileName = ((cipherMode == Cipher.ENCRYPT_MODE) ? "encryption" + extension :  "decryption" + extension);
		File outputFile = new File(outputFileName);

		byte[] keyBytes = keyExtract(keyFile);

		crypto(cipherMode, keyBytes, inputFile, outputFile);
	}

	private static void crypto(int cipherMode, byte[] keyBytes, File inputFile, File outputFile) throws Exception {
		try {

			Key secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
			IvParameterSpec ivSpec = new IvParameterSpec(IVBYTES);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(cipherMode, secretKey, ivSpec);
			
			FileInputStream inputStream = new FileInputStream(inputFile);
			byte[] inputBytes = new byte[(int) inputFile.length()];
			inputStream.read(inputBytes);
			
			byte[] outputBytes = cipher.doFinal(inputBytes);
			
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			outputStream.write(outputBytes);
			
			inputStream.close();
			outputStream.close();
			
		} catch (NoSuchPaddingException | NoSuchAlgorithmException
				| InvalidKeyException | BadPaddingException
				| IllegalBlockSizeException | IOException ex) {
			throw new CryptoException("Error encrypting/decrypting file", ex);
		}
	}

	private static byte[] keyExtract(File keyFile) throws Exception{
		String keyDir = keyFile.getAbsolutePath();
		String key = "";

		try (Scanner scanner = new Scanner(new File(keyDir))){
			while (scanner.hasNext()){
				key = scanner.nextLine();
			}
		} catch (IOException ex){
			throw new CryptoException("Cannot read key", ex);
		}

		byte[] keyBytes = new byte[key.length()/2];
		String buffer;
		for(int i = 0; i < key.length(); i += 2){
			buffer = key.substring(i, i+2);
			keyBytes[i/2] = Byte.parseByte(buffer, 16);
		}

		return keyBytes;
	}
}
