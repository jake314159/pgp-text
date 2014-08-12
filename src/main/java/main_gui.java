import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * Created by jake on 12/08/14.
 */
public class main_gui {
    private String file;
    private char[] key;

    public JPanel panel1;
    private JTextArea text_body;
    private JButton save_button;
    private JButton change_button;
    private JButton exit_button;

    public main_gui(String file_in) {
        this.file = file_in;

        // No file given so lets ask the user what they want to open
        if(this.file == null) {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file_selected = fc.getSelectedFile();
                this.file = file_selected.getAbsolutePath();
            } else {
                System.exit(0);
            }
        }

        //Ask for the key
        JPasswordField pf = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(null, pf, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okCxl == JOptionPane.OK_OPTION) {
            this.key = pf.getPassword();
        } else {
            System.exit(0); //Didn't enter a password
        }

        save_button.setEnabled(false);
        save_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean success = false;
                OutputStream os = null;
                try {
                    os = EncryptIO.EncryptStream(file, key);
                } catch (EncryptIOException e) {
                    e.printStackTrace();
                }

                if(os == null) {
                    System.out.println("Error: Output stream error");
                    return;
                }

                try {
                    text_body.write(new OutputStreamWriter(os));
                    success = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Saved file");
                if(success) {
                    save_button.setEnabled(false);
                }
            }
        });
        change_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Ask for the new key
                JPasswordField pf = new JPasswordField();
                int okCxl = JOptionPane.showConfirmDialog(null, pf, "Enter new Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (okCxl == JOptionPane.OK_OPTION) {
                    key = pf.getPassword();
                    save_button.setEnabled(true);
                }
            }
        });
        exit_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean exitOK = false;

                if(save_button.isEnabled()) {
                    // About to lose data so check with the user
                    if (JOptionPane.showConfirmDialog(null,  "You are about to lose some data\nAre you sure you want to exit?", "Exit?",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                        exitOK = true;
                    }
                } else {
                    exitOK = true;
                }
                if(exitOK) {
                    for (int i = 0; i < key.length; i++) {
                        key[i] = (char) 0; //zero the key
                    }
                    System.exit(0);
                }
            }
        });

        try {
            InputStream is = EncryptIO.DecryptStream(file, key);
            try {
                text_body.read(new InputStreamReader(is), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (EncryptIOException e) {
            e.printStackTrace();
        }

        text_body.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                save_button.setEnabled(true);
            }
        });
    }
}
