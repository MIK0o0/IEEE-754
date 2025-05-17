import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.kerbaya.ieee754lib.IEEE754;

public class IEEE754GUI extends JFrame {
    private JTextField inputField;
    private JTextField outputField;
    private JTextField outputField2;

    public IEEE754GUI() {
        setTitle("IEEE754 Converter");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Wprowadź 32-bitowy ciąg binarny:"));
        inputField = new JTextField(30);
        inputPanel.add(inputField);
        add(inputPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton toIEEEButton = new JButton("Wewnętrzny -> IEEE754");
        JButton toInternalButton = new JButton("IEEE754 -> Wewnętrzny");
        buttonPanel.add(toIEEEButton);
        buttonPanel.add(toInternalButton);
        add(buttonPanel);

        JPanel outputPanel = new JPanel(new FlowLayout());
        outputPanel.add(new JLabel("Wynik bin:"));
        outputField = new JTextField(30);
        outputField.setEditable(false);
        outputPanel.add(outputField);
        add(outputPanel);

        JPanel outputPanel2 = new JPanel(new FlowLayout());
        outputPanel2.add(new JLabel("Wynik hex:"));
        outputField2 = new JTextField(30);
        outputField2.setEditable(false);
        outputPanel2.add(outputField2);
        add(outputPanel2);

        toIEEEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText().trim();
                if (isValidBinary(input, 32)) {
                    IEEE754 in = IEEEConverter.parseCustomFormat(input);
                    String out = IEEEConverter.convertToIEEEBinary(in);
                    outputField.setText(out);
                    outputField2.setText("(0x" + toHex(out) + ")");
                } else {
                    showError("Niepoprawny format. Podaj dokładnie 32 bity.");
                }
            }
        });

        toInternalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText().trim();
                if (isValidBinary(input, 32)) {
                    IEEE754 in = IEEEConverter.parseIEEE(input);
                    String out = IEEEConverter.convertToCustomBinary(in);
                    outputField.setText(out);
                    outputField2.setText("(0x" + toHex(out) + ")");
                } else {
                    showError("Niepoprawny format. Podaj dokładnie 32 bity.");
                }
            }
        });
    }

    private boolean isValidBinary(String s, int length) {
        return s.replace(" ", "").matches("[01]{" + length + "}") 
                || s.replace(" ", "").matches("[0-9A-Fa-f]{" + (length / 4) + "}");
    }

    private String toHex(String binary) {
        long val = Long.parseUnsignedLong(binary, 2);
        return String.format("%08X", val);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Błąd", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IEEE754GUI gui = new IEEE754GUI();
            gui.setVisible(true);
        });
    }
}