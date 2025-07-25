import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

class Profile extends JFrame {
    //this will activate only if s1 is username
    String naya = "";

    Profile(String username) {
        Font f = new Font("Futura", Font.BOLD, 35);
        Font f2 = new Font("Calibri", Font.PLAIN, 20);

        JLabel title = new JLabel("Profile Settings", JLabel.CENTER);
        title.setFont(f);

        JLabel l1 = new JLabel("Select Field to Update:");
        JComboBox<String> box = new JComboBox<>(new String[]{"Username", "Password", "Phone", "Email"});

        JLabel l2 = new JLabel("Enter New Value:");
        JTextField t1 = new JTextField(15);

        JButton b1 = new JButton("Update");
        JButton b2 = new JButton("Back");

        l1.setFont(f2);
        box.setFont(f2);
        l2.setFont(f2);
        t1.setFont(f2);
        b1.setFont(f2);
        b2.setFont(f2);

        Container c = getContentPane();
        c.setLayout(null);

        title.setBounds(250, 20, 300, 40);
        l1.setBounds(200, 100, 200, 30);
        box.setBounds(400, 100, 200, 30);
        l2.setBounds(200, 160, 200, 30);
        t1.setBounds(400, 160, 200, 30);
        b1.setBounds(250, 220, 120, 40);
        b2.setBounds(400, 220, 120, 40);

        c.add(title);
        c.add(l1);
        c.add(box);
        c.add(l2);
        c.add(t1);
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
                    //passwrod ya fir username jo bhi select kiya hai user ne voh vaha jayega
                    //kaunsa columne change karna hai
                    String s1 = box.getSelectedItem().toString().toLowerCase();
                    //new value of the column to change
                    String s2 = t1.getText();
                    //s2 khali nahi hona chahiye
                    if(s2.isEmpty())
                    {
                        JOptionPane.showMessageDialog(null,"Cannot Be Empty");
                        return;
                    }



                    //DATABASE
                    //yaha jake update karna hai
                    String url = "jdbc:mysql://localhost:3306/batch2";
                    try(Connection con = DriverManager.getConnection(url,"root","SohamSQL#1211"))
                    {
                        //update whatever(s1) you want to update for that user
                        String sql = "update users set "+s1+" =? where username=?";
                        try(PreparedStatement pst = con.prepareStatement(sql))
                        {
                            pst.setString(1,s2);
                            pst.setString(2,username);

                            //table mai ched chaani karna hai isiliye executeUpdate
                            pst.executeUpdate();
                            JOptionPane.showMessageDialog(null,"Successfully Updated "+s1.toUpperCase());
                            t1.setText("");
                        }
                    }
                    catch(Exception e)
                    {
                        JOptionPane.showMessageDialog(null,e.getMessage());
                        return;
                    }

                    //agar username change ho raha hai toh you need to update the username on the dashboard too
                    if(s1.equals("username"))
                    {
                        //reload the profile i.e. dispose and new (reload)
                        /* the issue here was that when you change the username and then change the password both at the same time
                         then because the profile page has the previous username stored in it, it cannot update the password of the
                         new username and therefore to avoid this we reload the profile page everytime we change something
                         */
                        dispose();
                        //reload with new username
                        new Profile(s2);
                      //  String url = "jdbc:mysql://localhost:3306/batch2";
                        try(Connection con = DriverManager.getConnection(url,"root","SohamSQL#1211"))
                        {
                            String sql = "update transactions set username=? where username=?";
                            try(PreparedStatement pst = con.prepareStatement(sql))
                            {
                                pst.setString(1,s2);
                                pst.setString(2,username);
                                pst.executeUpdate();

                                //JOptionPane.showMessageDialog(null,"Successfully Updated "+s1.toUpperCase());

                            }
                        }
                        catch(Exception e)
                        {
                            JOptionPane.showMessageDialog(null,e.getMessage());
                        }
                    }

                }
        );


        setVisible(true);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Profile Settings");
    }

    public static void main(String[] args) {
        new Profile("Shreshth");
    }
}
