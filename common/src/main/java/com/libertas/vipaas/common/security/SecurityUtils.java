package com.libertas.vipaas.common.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import lombok.extern.slf4j.Slf4j;

import com.libertas.vipaas.common.exceptions.AuthenticationException;

@Slf4j
public class SecurityUtils {


	public static void validatePassword(String supplied, String retrieved) throws AuthenticationException{
		byte[] hash,salt,testHash;
        String[] parts = retrieved.toString().split(":");
        int iterations = Integer.parseInt(parts[0]);
        try{
	        salt = Hex.decodeHex(parts[1].toCharArray());
	        hash = Hex.decodeHex(parts[2].toCharArray());
		    PBEKeySpec spec = new PBEKeySpec(supplied.toCharArray(), salt, iterations, hash.length * 8);
	        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	        testHash = skf.generateSecret(spec).getEncoded();
        }catch(DecoderException|InvalidKeySpecException|NoSuchAlgorithmException e){
        	log.error(e.getMessage(),e);
        	throw new AuthenticationException("Could not authenticate user");
        }
        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++)
        {
            diff |= hash[i] ^ testHash[i];
        }
        if(diff != 0){
        	throw new AuthenticationException("Could not authenticate user");
        }
	}

	public static String hash(String password){
		int iterations = 1000;
        SecureRandom sr;
        byte[] salt= new byte[16];
        byte[] hash;
        try {
			sr= SecureRandom.getInstance("SHA1PRNG");
			sr.nextBytes(salt);
	        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 64 * 8);
	        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	        hash = skf.generateSecret(spec).getEncoded();
        }catch(InvalidKeySpecException|NoSuchAlgorithmException e){
			log.error(e.getMessage(),e);
			throw new IllegalStateException("Could not hash password");
		}
        return iterations + ":" + Hex.encodeHexString(salt) + ":" + Hex.encodeHexString(hash);
	}

}
