import java.util.Scanner;

public class InternalToIEEE754Converter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Wprowadź liczbę binarną naturalną:");
        String binaryInput = scanner.nextLine().trim();

        if (binaryInput.isEmpty()) {
            System.out.println("Nie wprowadzono żadnej liczby.");
        } else {
            try {
                int decimalValue = Integer.parseInt(binaryInput, 2);
                int internalFormat = convertDecimalToInternal(decimalValue);
                int ieeeFormat = convertInternalToIEEE754(internalFormat);

                System.out.println("Liczba: " + decimalValue);
                System.out.println("Format wewnętrzny (hex): 0x" + String.format("%08X", internalFormat));
                System.out.println("Format IEEE 754 (hex): 0x" + String.format("%08X", ieeeFormat));
            } catch (NumberFormatException e) {
                System.out.println("Błędny format liczby binarnej.");
            }
        }

        scanner.close();
    }

    private static int convertDecimalToInternal(int value) {
        if (value == 0) {
            return 0;
        }

        int exponent = 0;
        int temp = value;
        while (temp > 1) {
            temp >>= 1;
            exponent++;
        }

        int mantissa = 0;
        double fractionalPart = ((double)value / (1 << exponent)) - 1.0;
        if (fractionalPart > 0) {
            for (int i = 1; i <= 24; i++) {
                fractionalPart *= 2;
                if (fractionalPart >= 1) {
                    mantissa |= (1 << (24 - i));
                    fractionalPart -= 1;
                }
                if (fractionalPart == 0) break;
            }
        }

        return 0x80000000 | (exponent << 24) | mantissa;
    }

    private static int convertInternalToIEEE754(int internalFormat) {
        if (internalFormat == 0) {
            return 0;
        }

        int exponent = (internalFormat >> 24) & 0x7F;
        int mantissa = internalFormat & 0x7FFFFF;

        return ((exponent + 127) << 23) | mantissa;
    }
}
