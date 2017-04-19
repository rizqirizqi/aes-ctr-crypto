package main.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.InvalidKeyException;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class CryptoUtils {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/CTR/NoPadding";
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
			byte[] cipherBlock = new byte[cipher.getOutputSize(buffer.length)];
			int cipherBytes;

			int lastNoBytes = 0;
			int currNoBytes = inputStream.read(buffer);
			int nextNoBytes = 0;
			while(currNoBytes != -1){
				cipherBytes = cipher.update(buffer, 0, currNoBytes, cipherBlock);
				nextNoBytes = inputStream.read(buffer);
				if(nextNoBytes == -1 && cipherMode == Cipher.DECRYPT_MODE){
					cipherBytes -= cipherBlock[cipherBytes-1];
				}

				outputStream.write(cipherBlock, 0, cipherBytes);
				lastNoBytes = currNoBytes;
				currNoBytes = nextNoBytes;
			}

			if(cipherMode == Cipher.ENCRYPT_MODE){
				int pad = 16 - (lastNoBytes % 16);
				byte[] padding = addPadding(pad);

				System.out.println(pad);
				cipherBytes = cipher.update(padding, 0, pad, cipherBlock);
				outputStream.write(cipherBlock, 0, cipherBytes);
			}

			cipherBytes = cipher.doFinal(cipherBlock,0);
			outputStream.write(cipherBlock, 0, cipherBytes);

			outputStream.close();
			inputStream.close();
			
		} catch (InvalidKeyException ex){
			throw new CryptoException("Caught InvalidKeyException: Key must have length of 32, 48, or 64 byte\n" + ex.getMessage(), ex);
		} catch (IOException ex){
			throw new CryptoException("Caught IOException: Error while processing file\n" + ex.getMessage(), ex);
		}catch (Exception ex) {
			throw new CryptoException("Error encrypting/decrypting file", ex);
		}
	}

	public static byte[] addPadding(int pad){
		int size = pad;
		if(pad % 16 == 0){
			size = 16;
		}

		byte[] padding = new byte[size];
		for(int i = 0; i < size; i++){
			padding[i] = (byte) pad;
		}

		return padding;
	}

	public static byte[] keyExtract(File keyFile) throws Exception{
		String keyDir = keyFile.getAbsolutePath();
		String key = "";

		try (Scanner scanner = new Scanner(new File(keyDir))){
			while (scanner.hasNext()){
				key = scanner.nextLine();
			}
		} catch (IOException ex){
			throw new CryptoException("Caught IOException: Error while scanning key from " + keyDir + "\n" + ex.getMessage(), ex);
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

	public static int hexDigit(char ch) throws Exception{
		int ret;

		if (ch >= '0' && ch <= '9')
			ret = ch - '0';
		else if (ch >= 'A' && ch <= 'F')
			ret = ch - 'A' + 10;
		else if (ch >= 'a' && ch <= 'f')
			ret = ch - 'a' + 10;
		else ret = -1;

		if(ret == -1){
			throw new CryptoException("Caught Exception: Invalid key format, key must use Hexadecimal format.\nCharacter " + ch + " is not a Hexadecimal character", new Exception());
		}
		return ret;
	}
}
