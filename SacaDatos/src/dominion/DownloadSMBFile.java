/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dominion;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DownloadSMBFile{
/**
     * Download files from SMB shared folder to local
     * 
     */
    public static void downloadFile(String sRemoteFile, String slocalFile)  {
        InputStream in = null;
        OutputStream out = null;
        try {
            
            File smbfile = new File(sRemoteFile);
            File localFile = new File(slocalFile);
            in = new BufferedInputStream(new FileInputStream(smbfile));
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            byte[] bytesFile = new byte[(int) smbfile.length()];
            out.write(in.read(bytesFile, 0, (int)smbfile.length() -1));
            out.flush();
            
            
        } catch (IOException e) {
	
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(DownloadSMBFile.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        }
    }
}