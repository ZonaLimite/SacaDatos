/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dominion;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class DownloadSMBFile{
/**
     * Download files from SMB shared folder to local
     * 
     */
    public static void downloadFile(String sRemoteFile, String slocalFile)  {
        InputStream in = null;
        OutputStream out = null;
        try {
            
            SmbFile smbfile = new SmbFile(sRemoteFile);
            File localFile = new File(slocalFile);
            in = new BufferedInputStream(new SmbFileInputStream(smbfile));
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            
            out.write(in.readAllBytes());
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