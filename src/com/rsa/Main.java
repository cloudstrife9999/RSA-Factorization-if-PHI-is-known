package com.rsa;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

public class Main {
    
    private Main(){}
    
    public static void main(String[] args) {
	RSAKeyPairGenerator keygen = new RSAKeyPairGenerator();
	BigInteger e = new BigInteger("65537");
	SecureRandom rng = new SecureRandom();
	RSAKeyGenerationParameters params = new RSAKeyGenerationParameters(e, rng, 4096, 180);
	keygen.init(params);
	
	AsymmetricCipherKeyPair keypair = keygen.generateKeyPair();
	RSAPrivateCrtKeyParameters priv = (RSAPrivateCrtKeyParameters) keypair.getPrivate();
	RSAKeyParameters pub = (RSAKeyParameters) keypair.getPublic();
	
	BigInteger n = pub.getModulus();
	BigInteger p = priv.getP();
	BigInteger q = priv.getQ();
	BigInteger z = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
	
	System.out.println("n = " + n);
	System.out.println("n is " + n.bitLength() + " bits long.");
	System.out.println("phi(n) = " + z);
	System.out.println("Starting factorization");
	
	double begin = System.currentTimeMillis();
	
	BigInteger minusSum = z.subtract(n).subtract(BigInteger.ONE);
	BigInteger pRecovered = minusSum.negate().add(sqrt(minusSum.pow(2).subtract(BigInteger.valueOf(4).multiply(n)), BigInteger.ONE)).divide(BigInteger.valueOf(2));
	BigInteger qRecovered = n.divide(pRecovered);
	
	double end = System.currentTimeMillis();
	
	System.out.println("Finished factorization in " + (end - begin) + " milliseconds, which are " + ((end - begin) / (double) 1000) + " seconds.");
	
	if(p.equals(pRecovered) && q.equals(qRecovered)) {
	    System.out.println("p = " + p);
	    System.out.println("Recovered p = " + pRecovered);
	    System.out.println("q = " + q);
	    System.out.println("Recovered q = " + qRecovered);
	}
	else if(q.equals(pRecovered) && p.equals(qRecovered)) {
	    System.out.println("p = " + p);
	    System.out.println("Recovered p = " + qRecovered);
	    System.out.println("q = " + q);
	    System.out.println("Recovered q = " + pRecovered);
	}
	else {
	    throw new AssertionError();
	}
    }
    
    private static BigInteger sqrt(BigInteger n, BigInteger x0) {
	final BigInteger x1 = n.divide(x0).add(x0).shiftRight(1);
	
	return x0.equals(x1) || x0.equals(x1.subtract(BigInteger.ONE)) ? x0 : sqrt(n, x1);
    }
}