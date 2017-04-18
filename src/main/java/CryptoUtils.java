package main.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.util.Scanner;
import javax.crypto.Cipher;
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

	public static String encrypt(File keyFile, File inputFile) throws Exception {
		return prepCrypto(Cipher.ENCRYPT_MODE, keyFile, inputFile);
	}

	public static String decrypt(File keyFile, File inputFile) throws Exception {
		return prepCrypto(Cipher.DECRYPT_MODE, keyFile, inputFile);
	}

	private static String prepCrypto(int cipherMode, File keyFile, File inputFile) throws Exception{
        String fileFullName = inputFile.getName();
        int pos = fileFullName.lastIndexOf(".");
        String extension = fileFullName.substring(pos, fileFullName.length());
        String filename = fileFullName.substring(0, pos);
        String outputFileName = null;
        if(cipherMode == Cipher.ENCRYPT_MODE) {
            outputFileName = inputFile.getParent() + File.separator + filename + ".encrypted" + extension;
        } else {
            String decryptedFilename = null;
            if(filename.lastIndexOf(".") != -1) {
                if(filename.substring(filename.lastIndexOf("."), filename.length()).equals(".encrypted")) {
                    decryptedFilename = filename.substring(0, filename.lastIndexOf("."));
                } else {
                    decryptedFilename = filename + ".decrypted";
                }
            } else {
                decryptedFilename = filename + ".decrypted";
            }
            outputFileName = inputFile.getParent() + File.separator  + decryptedFilename + extension;
        }
		File outputFile = new File(outputFileName);

		byte[] keyBytes = keyExtract(keyFile);

		crypto(cipherMode, keyBytes, inputFile, outputFile);
		return outputFile.getAbsolutePath();
	}

	private static void crypto(int cipherMode, byte[] keyBytes, File inputFile, File outputFile) throws Exception {
		try {
			FileInputStream inputStream = new FileInputStream(inputFile);
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			Key secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
			IvParameterSpec ivSpec = new IvParameterSpec(IVBYTES);
			cipher.init(cipherMode, secretKey, ivSpec);
			
			byte[] buffer = new byte[8192];
			int noBytes = 0;
			byte[] cipherBlock = 
				new byte[cipher.getOutputSize(buffer.length)];
			int cipherBytes;
			while((noBytes = inputStream.read(buffer)) != -1){
				cipherBytes = cipher.update(buffer, 0, noBytes, cipherBlock);
				outputStream.write(cipherBlock, 0, cipherBytes);
			}
			cipherBytes = cipher.doFinal(cipherBlock,0);
			outputStream.write(cipherBlock, 0, cipherBytes);

			outputStream.close();
			inputStream.close();
			
		} catch (Exception ex) {
			throw new CryptoException("Error encrypting/decrypting file", ex);
		}
	}

	public static byte[] keyExtract(File keyFile) throws Exception{
		String keyDir = keyFile.getAbsolutePath();
		String key = "";

		try (Scanner scanner = new Scanner(new File(keyDir))){
			while (scanner.hasNext()){
				key = scanner.nextLine();
			}
		} catch (IOException ex){
			throw new CryptoException("Error extracting key", ex);
		}

		int len = key.length();
		byte[] keyBytes = new byte[(len+1)/2];

		int i = 0, j = 0;
        if ((len % 2) == 1)
            keyBytes[j++] = (byte) hexDigit(key.charAt(i++));

        while (i < len) {
            keyBytes[j++] = (byte) ((hexDigit(key.charAt(i++)) << 4) |
                                hexDigit(key.charAt(i++)));
        }
        return keyBytes;
	}

	public static int hexDigit(char ch) {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        if (ch >= 'A' && ch <= 'F')
            return ch - 'A' + 10;
        if (ch >= 'a' && ch <= 'f')
            return ch - 'a' + 10;

        return(0);	// any other char is treated as 0
    }
}
