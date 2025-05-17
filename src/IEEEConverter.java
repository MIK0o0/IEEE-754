import org.kerbaya.ieee754lib.*;

import java.math.BigInteger;

public class IEEEConverter {

    private static final IEEE754Format IEEE_754_FORMAT = IEEE754Format.SINGLE;
    private static final IEEE754Format INTERNAL_IEEE_FORMAT = new IEEE754Format(7, 23, BigInteger.valueOf(0));

    public static byte[] convertStringToBytes(String input) {
        input = input.replaceAll(" ", "");
        System.out.println("Input: " + input);
        int length = input.length();
        if (length == 8) {
            return new BigInteger(input, 16).toByteArray();
        } else if (length == 32) {
            return new BigInteger(input, 2).toByteArray();
        } else {
            throw new IllegalArgumentException("Input must be 8 or 32 bits!");
        }
    }

    public static IEEE754 parseIEEE(String binaryString) {
        if (isZeroBinary(formatBinaryString(binaryString))) {
            return IEEE754.POSITIVE_ZERO;
        }
        BitSource bitSource = BitUtils.wrapSource(convertStringToBytes(binaryString));
        return IEEE754.decode(IEEE_754_FORMAT, bitSource);
    }

    public static IEEE754 parseCustomFormat(String binaryString) {
        String formattedBinary = formatBinaryString(binaryString);

        if (isZeroBinary(formattedBinary)) {
            return IEEE754.POSITIVE_ZERO;
        }

        formattedBinary = adjustSignBit(formattedBinary);
        formattedBinary = String.join("", formattedBinary.substring(0, 8), formattedBinary.substring(9), "0");

        BitSource bitSource = BitUtils.wrapSource(convertStringToBytes(formattedBinary));
        return IEEE754.decode(INTERNAL_IEEE_FORMAT, bitSource);
    }

    public static String convertToIEEEBinary(IEEE754 ieeeObject) {
        byte[] outputBytes = new byte[4];
        BitSink bitSink = BitUtils.wrapSink(outputBytes);
        ieeeObject.toBits(IEEE_754_FORMAT, bitSink);
        return bytesToBinaryString(outputBytes);
    }

    public static String convertToCustomBinary(IEEE754 ieeeObject) {
        byte[] outputBytes = new byte[4];
        BitSink bitSink = BitUtils.wrapSink(outputBytes);
        ieeeObject.toBits(INTERNAL_IEEE_FORMAT, bitSink);

        String binaryString = bytesToBinaryString(outputBytes);
        if (!ieeeObject.equals(IEEE754.POSITIVE_ZERO)) {
            binaryString = adjustSignBit(binaryString);
        }
        return insertHiddenBit(binaryString).substring(0, 32);
    }

    private static String formatBinaryString(String binaryString) {
        return String.format("%32s", new BigInteger(convertStringToBytes(binaryString)).toString(2)).replace(' ', '0');
    }

    private static boolean isZeroBinary(String binaryString) {
        return !binaryString.contains("1");
    }

    private static String adjustSignBit(String binaryString) {
        if (binaryString.startsWith("0")) {
            return binaryString.replaceFirst("\\d", "1");
        } else {
            return binaryString.replaceFirst("\\d", "0");
        }
    }

    private static String insertHiddenBit(String binaryString) {
        return String.join("", binaryString.substring(0, 8), "0", binaryString.substring(8));
    }

    private static String bytesToBinaryString(byte[] bytes) {
        StringBuilder binaryStringBuilder = new StringBuilder();
        for (byte b : bytes) {
            String binarySegment = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            binaryStringBuilder.append(binarySegment);
        }
        return binaryStringBuilder.toString();
    }
}