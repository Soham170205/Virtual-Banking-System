import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

class Transfer extends JFrame {
    Transfer(String username) {
        Font f = new Font("Futura", Font.BOLD, 30);
        Font f2 = new Font("Calibri", Font.PLAIN, 18);

        JLabel title = new JLabel("Transfer Funds", JLabel.CENTER);
        JLabel l1 = new JLabel("Receiver:");
        JTextField t1 = new JTextField(10);

        JLabel l2 = new JLabel("Amount:");
        JTextField t2 = new JTextField(10);

        JButton b1 = new JButton("Transfer");
        JButton b2 = new JButton("Back");

        title.setFont(f);
        l1.setFont(f2);
        t1.setFont(f2);
        l2.setFont(f2);
        t2.setFont(f2);
        b1.setFont(f2);
        b2.setFont(f2);

        Container c = getContentPane();
        c.setLayout(null);

        int labelX = 200, fieldX = 400, yStart = 80, width = 150, height = 30, gap = 40;

        title.setBounds(250, 20, 300, 40);

        l1.setBounds(labelX, yStart, width, height);
        t1.setBounds(fieldX, yStart, width, height);

        l2.setBounds(labelX, yStart + gap, width, height);
        t2.setBounds(fieldX, yStart + gap, width, height);

        b1.setBounds(250, yStart + 2 * gap, 120, 40);
        b2.setBounds(400, yStart + 2 * gap, 120, 40);

        c.add(title);
        c.add(l1);
        c.add(t1);
        c.add(l2);
        c.add(t2);
        c.add(b1);
        c.add(b2);

        b2.addActionListener(
                a->
                {
                    new Home(username);
                    dispose();
                }
        );

        b1.addActionListener(
                a->
                {
                    String samnewala = t1.getText();
                    String s2 = t2.getText();

                    if(samnewala.isEmpty() || s2.isEmpty())
                    {
                        JOptionPane.showMessageDialog(null,"Cannot be empty");
                        return;
                    }
                  
                    double balance = fetchbalance(username);
                    double amount = Double.parseDouble(s2);

                    if(amount > balance)
                    {
                        JOptionPane.showMessageDialog(null,"Insufficient amount");
                        return;
                    }
                 
                    double total = balance - amount;

                   
                    updatebalance(username,total);
              
                   updatePassbook(username,"Transfer to "+samnewala,-amount,balance-amount);
              
                    balance = fetchbalance(samnewala);
                    total = amount + balance;
               
                    updatebalance(samnewala,total);
                    updatePassbook(samnewala,"Transfer from "+username,amount,balance+amount);
                    JOptionPane.showMessageDialog(null,"Transfer successful");
            
                    t1.setText("");
                    t2.setText("");
                }
        );



        setVisible(true);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Transfer Funds");
    }


    double fetchbalance(String username)
    {
        double balance = 0.0;
        String url = "jdbc:mysql://localhost:3306/batch2";
        try(Connection con = DriverManager.getConnection(url,"root",""))
        {
            String sql = "select balance from users where username=?";
            try(PreparedStatement pst = con.prepareStatement(sql))
            {
                pst.setString(1,username);

                ResultSet rs = pst.executeQuery();
                if(rs.next())
                {
                    balance = rs.getDouble("balance");
                }
            }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e.getMessage());
        }
        return balance;
    }


    void updatebalance(String username, double total)
    {
        String url = "jdbc:mysql://localhost:3306/batch2";
        try(Connection con = DriverManager.getConnection(url,"root",""))
        {
            String sql = "update users set balance=? where username=?";
            try(PreparedStatement pst = con.prepareStatement(sql))
            {
                pst.setDouble(1,total);
                pst.setString(2,username);
                pst.executeUpdate();

             
            }
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    void updatePassbook(String username,String desc,double amount,double total)
    {
        String url = "jdbc:mysql://localhost:3306/batch2";
        try(Connection con = DriverManager.getConnection(url,"root",""))
        {
            String sql = "insert into transactions(username,description,amount,balance) values(?,?,?,?) ";
            try(PreparedStatement pst = con.prepareStatement(sql))
            {
                pst.setString(1,username);
                pst.setString(2,desc);
                pst.setDouble(3,amount);
                pst.setDouble(4,total);

                pst.executeUpdate();
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null,e.getMessage());
        }
    }


    public static void main(String[] args) {
        new Transfer("Soham");
    }
}
