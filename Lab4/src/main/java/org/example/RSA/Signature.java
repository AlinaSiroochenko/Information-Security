package org.example.RSA;

import java.security.*;
import java.util.Base64;

public class Signature {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private java.security.Signature signature;
    private String message;

    public Signature() throws NoSuchAlgorithmException {
        // Generate a new key pair for RSA encryption and decryption
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        // Create a signature object using SHA-256 with RSA encryption
        signature = java.security.Signature.getInstance("SHA256withRSA");
    }

    // This method signs a given string message using the private RSA key
    // then encodes the signature in Base64 format.
    public String sign(String message) throws InvalidKeyException, SignatureException {
        this.message = message;
        signature.initSign(privateKey);
        byte[] messageBytes = message.getBytes();
        signature.update(messageBytes);
        byte[] digitalSignature = signature.sign();
        return Base64.getEncoder().encodeToString(digitalSignature);
    }

    // This method verifies a given digital signature against a message using a public RSA key
    // It returns true if the signature is valid and false otherwise
    public boolean verify(PublicKey publicKey, String message, String signatureString) throws InvalidKeyException, SignatureException {
        byte[] digitalSignature = Base64.getDecoder().decode(signatureString);
        signature.initVerify(publicKey);
        byte[] messageBytes = message.getBytes();
        signature.update(messageBytes);
        return signature.verify(digitalSignature);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getMessage() {
        return message;
    }

    public java.security.Signature getSignature() {
        return signature;
    }
}