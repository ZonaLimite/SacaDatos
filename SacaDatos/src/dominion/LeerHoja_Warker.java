package dominion;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


public class LeerHoja_Warker extends SwingWorker<String ,String>  {
	Excel2 myExcel;
	JLabel out;
	JTextArea OutLine;
	String pathHoja;
	String ref;
	LeerHoja_Warker(Excel2 excel,JLabel label, JTextArea textarea, String pathHoja, String ref){
		this.myExcel = excel;
		this.out = label;
		this.OutLine = textarea;
		this.pathHoja = pathHoja;
		this.ref = ref;
	
	}
	@Override //
	protected String doInBackground(){
		// TODO Auto-generated method stub
		 String sPathHoja = "smb:"+pathHoja;
		 String valorRef="";
		 StringTokenizer st = new StringTokenizer(ref, "-");

			 		//publish(" Abriendo Hoja : "+ pathHoja);
			 		Workbook libro=null;
					try {
						libro = Workbook.getWorkbook(new SmbFileInputStream(new SmbFile(sPathHoja)));
		                // Para obtener el libro .xls
		                Sheet hoja1 = libro.getSheet(0);
		                int indexIni = Integer.parseInt(st.nextToken());
		                int indexFin = Integer.parseInt(st.nextToken());
		                Cell a1 = hoja1.getCell(indexIni,indexFin); 
		                valorRef = a1.getContents();
		                //publish("valor : " + valorRef + " leido de hoja "+ pathHoja);						
					} catch (BiffException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally {
						libro.close();
					}

	                return valorRef;
		
	}
    @Override
    protected void process(List<String> chunks) {
    	for (String row : chunks ){
    		out.setText(row + "\n");

    	}

    }	
    @Override
    protected void done() {
        try { 
            //out.setText("valor celda leido " + get() + "/n");
        } catch (Exception ignore) {
        }
    }

}
