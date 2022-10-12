package dominion;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
//import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
//import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
//import java.util.Locale;
import java.util.logging.Logger;

//import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jcifs.config.BaseConfiguration;
import jcifs.context.AbstractCIFSContext;
import jcifs.context.BaseContext;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import jxl.Sheet;
import jxl.Workbook;
//import jxl.Workbook;
//import jxl.WorkbookSettings;
//import jxl.read.biff.BiffException;
import jxl.write.WritableWorkbook;

public class Excel2 {
	//private final String libroRutas = "\\\\GMAO\\Datos\\Rutas2.xls";//LA hoja que define las rutas de los informes
	//private final String sCarpetaTrabajoCalma = "\\\\GMAO\\Datos\\Datos de Importacion\\"; 
    //private final String sCarpetaBase="E:\\\\SacaDatos\\DatosEstadisticos\\";
	private final String sCarpetaBase="C:\\\\EspacioIntercambioDatos\\DatosEstadisticos\\";
	private final String libroRutas = sCarpetaBase+"Rutas2.xls";//LA hoja que define las rutas de los informes
    private final String sCarpetaTrabajoCalma = sCarpetaBase+"DatosImportacion\\"; 
	private final String USER_NAME = "mis";
    private final String PASSWORD = "mismis";
    private final String DOMINIO = "workgroup";
    NtlmPasswordAuthentication auth = null;
    SmbFile sFile = null;
    SmbFileOutputStream sfos = null;
   
	//private final String libroRutas = "c:\\put\\RutasStandby.xls";
	private static JLabel Warning ;
	private static JTextArea out;
	public static JScrollPane jsp;
	//private static JFrame jfr;
	public static Excel2 myExcel;
	public static SimpleDateFormat sdf;
	public static DateFormat df; 
	public static Date fechaTrabajo;
	public static Display2 display;
	
	/**
	 * Importador de Datos Estadisticos Calma Dominion
	 */
	static public void main(String[] args){
		boolean bFaseImpCalma = false;
		boolean bFaseHTML = false;
		boolean bFaseExcel = false;
		String miFechadeTrabajo="";
		df = DateFormat.getInstance();
		fechaTrabajo= new Date();
		if(args.length>0){
			miFechadeTrabajo = args[0];
		}else{
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_YEAR, -1);
			miFechadeTrabajo = df.format(c.getTime());
		}
		//while( !(fechaTrabajo  == null) ){
			
			miFechadeTrabajo=JOptionPane.showInputDialog("Entrada Fecha :(dd/mm/aa hh:mm)", miFechadeTrabajo);				
			
			System.out.println(fechaTrabajo);
			if(!(miFechadeTrabajo==null)){
				if(display != null){
					display.setVisible(false);
					display.dispose();
					display=null;
				}
				
				myExcel = new Excel2();
				display = new Display2();
				display.setVisible(true);

				out = display.OutLine;
				Warning = display.Warning;

				try{
				fechaTrabajo = df.parse(miFechadeTrabajo);
				bFaseImpCalma = myExcel.FaseImpCalma(fechaTrabajo);
				bFaseHTML = myExcel.FaseHTML(fechaTrabajo);
				bFaseExcel = myExcel.FaseXLS(fechaTrabajo);
				if(bFaseImpCalma && bFaseHTML && bFaseExcel){
										System.out.println("Todo ha ido bien");
										Warning.setText("Proceso finalizado sin error");
				}else{
										System.out.println("Proceso finalizado con error");
										Warning.setText("Proceso finalizado con error");

				}

				}catch(java.text.ParseException e){
					
					Warning.setText("Excepcion parse fecha :"+ miFechadeTrabajo);
				}
		
			}else{
				if(display != null){
					display.setVisible(false);
					display.dispose();
					display=null;
					
				}
			return;
			}	
		//}
						    
	}	
	

	static public boolean sacaDatos(Date fecha){
		boolean quetal = false;
		Warning.setText("");
		out.setText("");
		quetal = (myExcel.FaseImpCalma(fecha) && myExcel.FaseHTML(fecha) && myExcel.FaseXLS(fecha));
		return  quetal;
		
	}
	public String LeerHoja(String pathHoja, String ref){
		String sLeerHoja = "";
		try{

			LeerHoja_Warker lw = new LeerHoja_Warker(myExcel, Warning, out, pathHoja, ref);
			lw.execute();
			sLeerHoja = lw.get();
			lw = null;

		}catch(Exception e){
			System.out.println("Excepcion en LeerHoja");
			out.append("Excepcion : No se pudo leer Hoja : " + pathHoja);
		}
		return sLeerHoja;		
	}//fin catch

	
	public boolean VerificaFecha(Date fechaTrabajo,String rutaInforme, String nombreInforme, String refFecha, String especial){
		boolean verificada = false;
	    String fechaIrregParte1 = "Intervalo de tiempo pedido (en días postales) :  entre el ";
	    String fechaIrregParte2 = " y el  ";
	    String fechaIrregParte3 = " (no incluso)";
	    String fechaComp;
	    String nombreLibro = rutaInforme + nombreInforme;
	    String fechaInforme = myExcel.LeerHoja(nombreLibro,refFecha);
	    	     if(especial.equals("2") | especial.equals("1")){
	    	        fechaComp = fechaIrregParte1 + myExcel.FechaAyerTipo1(fechaTrabajo) + fechaIrregParte2 + myExcel.FechaHoyTipo1(fechaTrabajo) + fechaIrregParte3;
	    	          if(nombreInforme.equals("Videos_por_Hora.xls")){
	    	            fechaComp = "Intervalo de tiempo pedido :  entre el " + myExcel.FechaAyerTipo1(fechaTrabajo) + fechaIrregParte2 + FechaHoyTipo1(fechaTrabajo) + fechaIrregParte3;
	    	          }

	    	    }else{
	    	            fechaComp = myExcel.FechaAyerTipo1(fechaTrabajo);
	    	    }

	    	    if(fechaInforme.equals(fechaComp)){
	    	       verificada = true;
	    	       out.append("Verificada fecha de :"+ nombreInforme +"\n");

	    	    }else{
	    	    	out.append("No coinciden las fechas del informe :" + nombreInforme +"   (Contenido):"+fechaInforme+"\n");
	    	    }
		return verificada;
		
	}
	public String FechaAyerTipo3 (Date diaTrabajado){
		//fecha tipo yymmdd
		String fecha="";
		Calendar cal = Calendar.getInstance();
		cal.setTime(diaTrabajado);
		//cal.add(Calendar.DAY_OF_YEAR, -1);
		String SAnio = String.valueOf(cal.get(Calendar.YEAR));
		String sMes = String.valueOf(cal.get(Calendar.MONTH)+1);
		if (sMes.length()==1){
			sMes = "0" +sMes; 
		}
		String sDia = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		if (sDia.length()==1){
			sDia = "0" + sDia;
		}
		StringBuffer sb = new StringBuffer(SAnio);
		SAnio = sb.substring(2, 4);
		fecha = SAnio+sMes+sDia;
		
		return fecha;
		
		
	}
	public void conviertea2000(String nameXLS){
		try{
			Workbook w = Workbook.getWorkbook(new File(nameXLS));
			WritableWorkbook copy = Workbook.createWorkbook(new File(nameXLS), w);
			copy.write();
			copy.close();
			w.close();
		}catch(Exception e){
			System.out.println("Excepcion convirtiendo a excel 2000 "+ e.toString());
		}
	}
	public String FechaHoyTipo1 (Date diaTrabajado){
		//diaTrabajado hace referencia a el dia del que se estan obteniendo informes (a la modalidad correos del dia anterior)
		//por lo tanto la fecha actual o de Hoy es la trabajada + 1
		//FechaAyerTipo1 = Format(Date - 1, "dd/mm/yyyy")
		String fecha="";
		Calendar cal = Calendar.getInstance();
		cal.setTime(diaTrabajado);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		String SAnio = String.valueOf(cal.get(Calendar.YEAR));
		String sMes = String.valueOf(cal.get(Calendar.MONTH)+1);
		if (sMes.length()==1){
			sMes = "0" +sMes; 
		}
		String sDia = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		if (sDia.length()==1){
			sDia = "0" + sDia;
		}
		fecha = sDia+ "/"+sMes+"/"+SAnio;
		
		return fecha;
	}
	public String FechaAyerTipo1 (Date diaTrabajado){
	    //FechaAyerTipo1 = Format(Date - 1, "dd/mm/yyyy")
		String fecha="";
		Calendar cal = Calendar.getInstance();
		cal.setTime(diaTrabajado);
		//cal.add(Calendar.DAY_OF_YEAR, -1);
		String SAnio = String.valueOf(cal.get(Calendar.YEAR));
		String sMes = String.valueOf(cal.get(Calendar.MONTH)+1);
		if (sMes.length()==1){
			sMes = "0" +sMes; 
		}
		String sDia = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		if (sDia.length()==1){
			sDia = "0" + sDia;
		}
		fecha = sDia+ "/"+sMes+"/"+SAnio;
		
		return fecha;
	}

	public String FechaAyerTipo2 (Date diaTrabajado){
	    //FechaAyerTipo2 = Format(Date - 1, "dd-mm-yy")
		String fecha="";
		Calendar cal = Calendar.getInstance();
		cal.setTime(diaTrabajado);
		//cal.add(Calendar.DAY_OF_YEAR, -1);
		String SAnio = String.valueOf(cal.get(Calendar.YEAR));
		String sMes = String.valueOf(cal.get(Calendar.MONTH)+1);
		if (sMes.length()==1){
			sMes = "0" +sMes; 
		}
		String sDia = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		if (sDia.length()==1){
			sDia = "0" + sDia;
		}
		StringBuffer sb = new StringBuffer(SAnio);
		SAnio = sb.substring(2, 4);
		fecha = sDia+ "-"+sMes+"-"+SAnio;
	
		return fecha;
	}

	public boolean FaseImpCalma (Date fechaTrabajo){
		boolean ComoHaido = false;
		String sPathNameFile, sNameFile, sCeldaVerif ,sNombreCompletoAntes, sNombreCompletoDespues, sEsFechaEspecial;
		String sPathHoja =this.libroRutas;
		FileInputStream smbfis=null ;
		try{
			smbfis = new FileInputStream(new File(sPathHoja));
			Workbook libro = Workbook.getWorkbook(new FileInputStream(new File(sPathHoja)));
			 Sheet hoja1 = libro.getSheet(0);
			 //recorre la plantilla rutas 2 con los parametros
			 for(int i=1;i<5;i++ ){
				 sPathNameFile = (hoja1.getCell(2,i)).getContents();
				 sNameFile = (hoja1.getCell(8,i)).getContents();
				 sCeldaVerif = (hoja1.getCell(1,i)).getContents();
				 sNombreCompletoAntes = sPathNameFile + sNameFile;
				 sNombreCompletoDespues = sCarpetaTrabajoCalma+ pegaRaizCentro() + FechaAyerTipo3(fechaTrabajo)+"_"+sNameFile; 
				 sEsFechaEspecial = (hoja1.getCell(0,i)).getContents();
				
				 //System.out.println(sNombreCompletoDespues);
				 Warning.setText("Tratando Informe : " + sNombreCompletoAntes);
			
			     if(myExcel.VerificaFecha(fechaTrabajo, sPathNameFile, sNameFile, sCeldaVerif, sEsFechaEspecial) == true){
			                
			                ComoHaido=myExcel.CopyFile(sNombreCompletoAntes, sNombreCompletoDespues, fechaTrabajo);
			    	 
			                //ComoHaido=myExcel.downloadFile(sNombreCompletoAntes, sNombreCompletoDespues, fechaTrabajo);
			     }
			 }
			 libro.close();
		}catch(Exception e){
			System.out.println("Fase Importacion Calma");
			e.printStackTrace();
		}finally {
			try {
				smbfis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return  ComoHaido;
	}
	
	
	/**
	 * Selecciona en la hoja Rutas el valor de la raiz asignado a este centro
	 * Se ha indicado posicion Columna 12, fila 1
	 * @return String la raiz a añadir
	 */
	private String pegaRaizCentro() {
		return myExcel.LeerHoja(libroRutas,"12-1");
	}


	public boolean FaseHTML(Date fechaTrabajo){
	/*
	' Obtencion de los ficheros HTML para envio EMAIL
	' 1 Se verifica la fecha en el contenido del fichero plantilla
	' 2 Si es correcta se mueve desde el MIS a el GMAO renombrado con la fecha apropiada
	' 3 Se hace una entrada en la bandeja de salida , para enviar dichos informes por EMail
	' 3 se anota en el fichero de seguimiento el resultado de la operacion
	*/
		
	    Boolean ComoHaIdoFaseHTML=false;
	    final String carpetaDeTrabajoHTML = sCarpetaBase+ DameCarpetaFecha(fechaTrabajo) + "\\";
	    //carpetaDeTrabajoHTML = "c:\\put\\put2\\";
	     for(int x = 6; x <= 8; x++){
	        String sRange = String.valueOf(x);
	        String cRange = "2-" + sRange;
	        String IRange = "8-" + sRange;
	        
	        String rutaFicheroInforme = myExcel.LeerHoja(libroRutas,cRange);
	        String nombreFicheroInforme = myExcel.LeerHoja(libroRutas,IRange);
	        String nombreCompletoAntes = rutaFicheroInforme + nombreFicheroInforme;
	        String nombreCompletoDespues = carpetaDeTrabajoHTML + myExcel.FechaAyerTipo2(fechaTrabajo) + " " + nombreFicheroInforme;
	        Warning.setText( "Tratando Informe " + nombreCompletoAntes);  
	        
	        //MsgBox (nombreCompletoAntes)
	        if( matchDate(nombreCompletoAntes, fechaTrabajo) == true){
	        	myExcel.CopyFile(nombreCompletoAntes, nombreCompletoDespues, fechaTrabajo);
	        	//myExcel.downloadFile(nombreCompletoAntes, nombreCompletoDespues, fechaTrabajo);
	        	ComoHaIdoFaseHTML= true;
	        	out.append("Verificada fecha de :"+ nombreFicheroInforme+"\n");
	        }else{
	            //MsgBox ("No coinciden las fechas del informe y la fecha de Trabajo del informe :" + nombreCompletoAntes)
	        	out.append("No coinciden las fechas del informe :" + nombreCompletoAntes+" (Contenido):"+ nombreCompletoAntes+"\n");
	        }
	    	
	    }
		
	    return ComoHaIdoFaseHTML;
	}

public boolean FaseXLS(Date fechaTrabajo){

	boolean ComoHaIdoFaseXLS = false;
	final String carpetaDeTrabajoXLS = sCarpetaBase + DameCarpetaFecha(fechaTrabajo) + "\\Excel\\";

	/*' Obtencion de los ficheros XLS para Mantenimiento
	' 1 Se verifica la fecha en el contenido del fichero excel
	' 2 si es correcta se mueve desde el MIS a el GMAO renombrado con la fecha apropiada
	' y formato compatible
	' 3 se anota en el fichero de seguimiento el resultado de la operacion

	  */  

    //String carpetaDeTrabajoXLS = "c:\\put\\put2\\";
	for(int x = 11; x <= 16 ; x++){
	        String sRange = String.valueOf(x);
	        String cRange = "2-" +  sRange;
	        String IRange = "8-" + sRange;
	        String bRange = "1-" + sRange;
	        String esEspecialRange = "0-" + sRange;
	       
	        
	        String rutaFicheroInforme = myExcel.LeerHoja(libroRutas, cRange);
	        String nombreFicheroInforme = myExcel.LeerHoja(libroRutas, IRange);
	        String celdaVerif = myExcel.LeerHoja(libroRutas, bRange);
	        String nombreCompletoAntes = rutaFicheroInforme  + nombreFicheroInforme;
	        String nombreCompletoDespues = carpetaDeTrabajoXLS + myExcel.FechaAyerTipo2(fechaTrabajo) + " " + nombreFicheroInforme;
	        String esEspecial = myExcel.LeerHoja(libroRutas, esEspecialRange);
	        Warning.setText("Tratando Informe " + nombreCompletoAntes);

	        if(myExcel.VerificaFecha(fechaTrabajo, rutaFicheroInforme, nombreFicheroInforme, celdaVerif, esEspecial) == true){
		          ComoHaIdoFaseXLS= myExcel.CopyFile(nombreCompletoAntes, nombreCompletoDespues, fechaTrabajo);
		           
	        }else{
	        
	        	ComoHaIdoFaseXLS = false;
	        }

	}
    return ComoHaIdoFaseXLS;
}
public boolean matchDate(String ficheroHTML, Date fechaTrabajo){
		// Lee un fichero secuencialmente hasta que encuentra la cadena de indicacion de fecha
    
		Boolean matchDate = false;
		
		String fechaIrregParte1 = "entre el ";
		String fechaIrregParte2 = " y el  ";
		String fechaIrregParte3 = " (no incluso)";
		String cadenaComp = fechaIrregParte1 + myExcel.FechaAyerTipo1(fechaTrabajo)+ fechaIrregParte2 + myExcel.FechaHoyTipo1(fechaTrabajo) + fechaIrregParte3;
	    //String cadenaComp = myExcel.FechaAyerTipo1(fechaTrabajo);
		try{
			BaseContext acc = new BaseContext(new BaseConfiguration(true));
			File f = new File(ficheroHTML);
			

			FileInputStream fis = new FileInputStream(f);
			BufferedReader d  = new BufferedReader(new InputStreamReader(fis));
			StringBuffer sb = new StringBuffer();
			char[] caracteres = new char[(int) f.length()]; 
			d.read(caracteres);
			sb.append(caracteres);
			int Index = sb.indexOf(cadenaComp);
			if( Index == -1){
					matchDate=false;
					//out.append("No se ha encontrado la fecha :"+ cadenaComp+" en " + ficheroHTML + "\n");
			}else{
					matchDate=true;
					//System.out.println("EL numero de Index es:"+String.valueOf(Index));
			}
			fis.close();
		
		}catch(Exception e){
			System.out.println("Problemas en matchDate"+e.toString());
    	  // No existe el fichero
    	  // MsgBox ("No se encuentra el fichero :" + ficheroHTML)
		}   
		return matchDate;
}	

public String DameCarpetaFecha(Date fecha){
		String fechaCarpeta="";
		String sMes="";
		Calendar c = Calendar.getInstance();
		c.setTime(fecha);
		//Recordar que Calendar tiene como 0 el Mes de Enero y asi sucesivamente, por eso le sumamos uno, para que coincida 
		int Mes = c.get(Calendar.MONTH);
		Mes++;
		if(Mes<10){
			sMes = "0" + String.valueOf(Mes);
		}else{
			sMes = String.valueOf(Mes);
		}
		fechaCarpeta = sMes + "-"+ String.valueOf(c.get(Calendar.YEAR));
		return fechaCarpeta;
	}
public boolean CreaCarpetasdeTrabajo(Date fechaRef){
	boolean bRight = false;
	//Se trata de verificar en la ruta de trabajo correspondiente, si exixten las carpetas previstas.
	//1 Fase : checkear si existe el host + recurso compartido 
	String pathFileFecha = sCarpetaBase+ this.DameCarpetaFecha(fechaRef);
	String pathDirFechaExcel = sCarpetaBase+ this.DameCarpetaFecha(fechaRef) + "\\Excel";
    String pathDirImportacion = sCarpetaBase+"DatosImportacion";
	File host = null;
	File hostImportacion = null;
	File hostFecha = null;
	File hostFechaExcel=null;
	
		host = new File(sCarpetaBase);
		hostImportacion = new File(pathDirImportacion);
		hostFecha = new File(pathFileFecha);
		hostFechaExcel = new File(pathDirFechaExcel);
	
  
	try {
		if(host.exists()){
		}else{ //del host
			Warning.setText("No existe el recurso :" + sCarpetaBase);
			
			out.append("Es necesario crear el recurso compartido :\n" + sCarpetaBase + "\n" );
		}
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
        
	try {
		if(hostImportacion.exists()){
		}else{ //del hostImportacion
			Warning.setText("No existe el recurso :" + hostImportacion);
			
			out.append("Es necesario crear el recurso compartido :\n" + hostImportacion + "\n");
		            int result = JOptionPane.showConfirmDialog(null, "No existe la carpeta : " + hostImportacion + "\n Desea crearla?");
			if(result==0){
				try{
					hostImportacion.mkdir();
					out.append("Creada carpeta :" + hostImportacion +"\n");
				}catch(Exception e){
					out.append("No se ha podido crear carpeta");
				}
			}
			System.out.println(Integer.valueOf(result));

		}
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	try {
		if(hostFecha.exists()){
		}else{//del hostFecha
			int result = JOptionPane.showConfirmDialog(null, "No existe la carpeta del Mes: " + pathFileFecha + "\n Desea crearla?");
			if(result==0){
				try{
					hostFecha.mkdir();
					out.append("Creada carpeta :" + pathFileFecha +"\n");
				}catch(Exception e){
					out.append("No se ha podido crear carpeta");
				}
			}
			System.out.println(Integer.valueOf(result));
		}
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
        
	try {
		if(hostFechaExcel.exists()){
		}else{//del FechaExcel
			int result = JOptionPane.showConfirmDialog(null, "No existe la carpeta : " + pathDirFechaExcel+"\n Desea crearla?");
			if(result==0){
				try{
					hostFechaExcel.mkdir();
					out.append("Creada carpeta :" + pathDirFechaExcel + "\n");
				}catch(Exception e){
					out.append("No se ha podido crear carpeta");
				}
			}
			System.out.println(Integer.valueOf(result));

		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	return bRight;
}
public boolean CopyFile (String source, String dest, Date fechaEnUso){
		boolean Vabien = true;
                //Comprobacion carpetas de trabajo
                this.CreaCarpetasdeTrabajo(fechaEnUso);
		
		try{
			File sourceFile = new File(source);
			//sourceFile.renameTo(new File(dest));
			
			File destFile = new File(dest);
			//crear una corriente de entrada
			FileInputStream fis = new FileInputStream(sourceFile);
			byte[] bytesFile = new byte[(int) sourceFile.length()];
			fis.read(bytesFile);
			FileOutputStream fos = new FileOutputStream(destFile);
			fos.write(bytesFile);
			fos.flush();
			fos.close();
			fis.close();
			
			

		}catch(IOException e){
			System.out.println("Falla copy :");
			File fTemp = new File(dest);
			//e.printStackTrace();
			out.append("Excepcion (CopyFile) :" +  e.getMessage()+ "\n");
			
			if(e.getMessage().contains("No se ha encontrado la ruta de acceso de la red")==true){
				out.append("No se puede acceder a esta ruta de red :" + fTemp.getParent());
			}
			
			
			Vabien=false;	
		}
		return Vabien;
	}

public boolean downloadFile(String sRemoteFile, String slocalFile, Date fechaEnUso)  {
    boolean Vabien = true;
    //verificacion existencia estructura de trabajo
    this.CreaCarpetasdeTrabajo(fechaEnUso);
    InputStream inStream = null;
    OutputStream outStream = null;
    try {
        String sNameRemote = "smb:"+sRemoteFile;
        String sNameLocal = slocalFile;
        jcifs.Config.registerSmbURLHandler();
        BaseContext acc = new BaseContext(new BaseConfiguration(true));
		SmbFile smbRemotefile = new SmbFile(sNameRemote, (acc.withCredentials(myExcel.auth)));
		File localFile = new File(sNameLocal);
        inStream = new BufferedInputStream(new SmbFileInputStream(smbRemotefile));
        outStream = new BufferedOutputStream(new FileOutputStream(localFile));
        byte[] bytesFile = new byte[(int) smbRemotefile.length()];
        outStream.write(inStream.read(bytesFile, 0, (int)smbRemotefile.length() -1));
        outStream.flush();
        
        
    } catch (Exception e) {
		//System.out.println("Falla copy :");
		//File fTemp = new File();
		e.printStackTrace();
		out.append("Excepcion (DownloadFile) :" +  e.getMessage()+ "\n");
		if(e.getMessage().contains("El sistema no puede encontrar la ruta especificada")==true){
			this.CreaCarpetasdeTrabajo(fechaTrabajo);
			//this.downloadFile(sRemoteFile, slocalFile);
		}
		if(e.getMessage().contains("No se ha encontrado la ruta de acceso de la red")==true){
			out.append("No se puede acceder a esta ruta de red :" + e.getMessage());
		}
		
		
		Vabien=false;	
    } finally {
        try {
            inStream.close();
            outStream.close();
        } catch (IOException ex) {
            Logger.getLogger(DownloadSMBFile.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    return Vabien;
}

}
