import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InternalToIEEE754Converter extends JFrame {
    private JTextField binaryInputField;
    private JLabel resultLabel;
    private JLabel internalFormatLabel;
    private JLabel ieeeFormatLabel;
    private JButton convertButton;

    public InternalToIEEE754Converter() {
        super("Konwerter formatów liczbowych");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 250);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
    }
//ttest
    private void initComponents() {
        binaryInputField = new JTextField(20);
        convertButton = new JButton("Konwertuj");
        resultLabel = new JLabel("Wynik: ");
        internalFormatLabel = new JLabel("Format wewnętrzny: ");
        ieeeFormatLabel = new JLabel("Format IEEE 754: ");

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        });

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        resultLabel.setFont(labelFont);
        internalFormatLabel.setFont(labelFont);
        ieeeFormatLabel.setFont(labelFont);
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Wprowadź liczbę binarną:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(binaryInputField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(convertButton, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(resultLabel, gbc);

        gbc.gridy = 3;
        mainPanel.add(internalFormatLabel, gbc);

        gbc.gridy = 4;
        mainPanel.add(ieeeFormatLabel, gbc);

        add(mainPanel);
    }

    private void performConversion() {
        String binaryInput = binaryInputField.getText().trim();

        if (binaryInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Proszę wprowadzić liczbę binarną",
                    "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            System.out.println("Binary input: " + binaryInput);
            long decimalValue = Long.parseLong(binaryInput, 2); // Zmiana na long
            System.out.println("Decimal value: " + decimalValue);
            int internalFormat = convertDecimalToInternal(decimalValue);
            int ieeeFormat = convertInternalToIEEE754(internalFormat);

            resultLabel.setText("Wynik: " + decimalValue);
            internalFormatLabel.setText("Format wewnętrzny (hex): 0x"
                    + String.format("%08X", internalFormat));
            ieeeFormatLabel.setText("Format IEEE 754 (hex): 0x"
                    + String.format("%08X", ieeeFormat));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Nieprawidłowy format liczby binarnej",
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int convertDecimalToInternal(long value) { // Parametr zmieniony na long
        if (value == 0) return 0;

        int exponent = 0;
        long temp = value; // Zmiana typu na long
        while (temp > 1) {
            temp >>= 1;
            exponent++;
        }

        int mantissa = 0;
        double fractionalPart = ((double) value / (1L << exponent)) - 1.0; // Użycie 1L dla long
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


    private int convertInternalToIEEE754(int internalFormat) {
        if (internalFormat == 0) return 0;

        int exponent = (internalFormat >> 24) & 0x7F;
        int mantissa = internalFormat & 0x7FFFFF;

        return ((exponent + 127) << 23) | mantissa;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InternalToIEEE754Converter().setVisible(true);
            }
        });
    }
}
