package Client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.Icon;



public class Client_Invitation extends JFrame{
	BufferedImage img = null;
	
	Font fontbt = new Font("SansSerif", Font.BOLD,24);

	
	public static void main(String args[]){
		new Client_Invitation ();
	}


	public Client_Invitation (){
		getContentPane().setBackground(Color.WHITE);
		setTitle("Cryptonite");
		setBounds(500,300,816,480);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		
		getContentPane().setLayout(null);
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 816, 480);
        layeredPane.setLayout(null);
        
        try {
            img = ImageIO.read(new File("img/�ʴ���.png"));
        } catch (IOException e) {
            System.out.println("�̹��� �ҷ����� ����");
            System.exit(0);
        }
        
        MyPanel panel = new MyPanel();
        panel.setBounds(0, 0, 900, 500);
        
        layeredPane.add(panel);
        getContentPane().add(layeredPane);
        setVisible(true);
        
        
	}
	class MyPanel extends JPanel {
        public void paint(Graphics g) {
            g.drawImage(img, 0, 0, null);
       }
   }
	
}