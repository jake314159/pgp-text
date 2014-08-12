import javax.swing.*;

/**
 * Created by jake on 12/08/14.
 */
public class pgpText {
    public static void main(String[] args) {

        String file = null;

        //Use the first param as a file if given
        if(args.length>0) {
            file = args[0];
        }

        JFrame frame = new JFrame("PGP text ["+file+"]");
        frame.setContentPane(new main_gui(file).panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,500);
        frame.setVisible(true);
    }
}
