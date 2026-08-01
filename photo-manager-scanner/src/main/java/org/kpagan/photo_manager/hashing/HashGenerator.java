package org.kpagan.photo_manager.hashing;

import lombok.experimental.UtilityClass;
import org.kpagan.photo_manager.hashing.error.HashingException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public final class HashGenerator {

    public HashInformation getHashInformation(Path path) throws HashingException {
        try {
            String sha256 = calculateSHA256(path);
            long pHash = calculatePerceptualHash(path);
            return new HashInformation(sha256, pHash);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new HashingException(String.format("Error while calculating hash on %s", path), e);
        }
    }

    public static String calculateSHA256(Path path) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream is = Files.newInputStream(path)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Generates a 64-bit perceptual hash for a given image file path.
     */
    public static long calculatePerceptualHash(Path path) throws IOException {
        BufferedImage original = ImageIO.read(path.toFile());
        if (original == null) {
            throw new IOException("Unsupported or corrupt image file: " + path);
        }

        // 1. Scale down to 9x8 pixels and convert to Grayscale
        // We use 9 width so we can compare adjacent horizontal pixels (8 differences per row x 8 rows = 64 bits)
        BufferedImage scaled = new BufferedImage(9, 8, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = scaled.createGraphics();
        g.drawImage(original, 0, 0, 9, 8, null);
        g.dispose();

        // 2. Build 64-bit hash by comparing adjacent pixel brightness
        long hash = 0L;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int leftPixel = scaled.getRaster().getSample(x, y, 0);
                int rightPixel = scaled.getRaster().getSample(x + 1, y, 0);

                // Set bit to 1 if left pixel is brighter than right pixel
                if (leftPixel > rightPixel) {
                    hash |= (1L << (y * 8 + x));
                }
            }
        }
        return hash;
    }

    /**
     * Calculates the Hamming Distance (number of differing bits) between two hashes.
     */
    public static int calculateHammingDistance(long hash1, long hash2) {
        // Long.bitCount counts set bits in XOR result (1s where bits differed)
        return Long.bitCount(hash1 ^ hash2);
    }

}
