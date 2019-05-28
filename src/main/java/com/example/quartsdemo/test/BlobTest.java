package com.example.quartsdemo.test;

import java.io.*;
import java.sql.*;

public class BlobTest {

    public static void main(String[] args) throws IOException, SQLException {
        //把图片存为blob的格式到数据库
        // storePicBlog();

        //从数据库读取blob的格式的图片数据
        getPicBlog();
    }

    public static void storePicBlog() throws FileNotFoundException, SQLException, IOException {

        String m_dbDriver    ="com.mysql.jdbc.Driver";
        String m_dbUrl   ="jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
        Connection conn = DriverManager.getConnection(m_dbUrl, "root", "root");

        File f = new File("C:\\Users\\admin\\Desktop\\timg.jpg");
        FileInputStream fis = new FileInputStream(f);

        String sql = "insert into image_save(image_name,image_in) values(?,?)";

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, "临时图片");
        ps.setBinaryStream(2, fis, (int)f.length());

        ps.executeUpdate();

        conn.close();
        ps.close();
    }
    public static void getPicBlog() throws FileNotFoundException, SQLException, IOException {

        String m_dbDriver    ="com.mysql.jdbc.Driver";
        String m_dbUrl   ="jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
        Connection conn = DriverManager.getConnection(m_dbUrl, "root", "root");

        File f = new File("C:\\Users\\admin\\Desktop\\timg.jpg");
        FileInputStream fis = new FileInputStream(f);

        String sql = "select image_in from image_save where image_name='临时图片'";

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        Blob imageIn = null;
        while (rs.next()){
             imageIn = rs.getBlob("image_in");
        }
        InputStream in = imageIn.getBinaryStream();

        OutputStream out = new FileOutputStream("C:\\Users\\admin\\Desktop\\cat.jpg");
        byte[] buffer = new byte[1024];
        int len = 0;
        while((len = in.read(buffer)) != -1){
            out.write(buffer, 0, len);
        }
        in.close();
        out.close();

        conn.close();
        ps.close();
    }
}
