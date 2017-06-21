import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.omg.CORBA.portable.OutputStream;

/**
 * @author Marc Sola Ramos y Javier Garcia Cantero
 *
 */
public class Wget implements Runnable  {
	/**
	 * 
	 * @param c variable en la que guardaremos los bytes leidos por el inputStream
	 * @param asc parametro que decide si el filtro Ascii se utiliza
	 * @param zip parametro que decide si el filtro Ascii se utiliza
	 * @param gzip parametro que decide si el filtro Ascii se utiliza
	 * @param url URL desde la que descargaremos
	 * @param is InputStream que usaremos para leer los datos de la URL
	 * @param fos FileoutputStream para guardar los datos leidos en un fichero
	 * @param nameStr es un string que contiene la URL a descargar
	 * @param argumentos contiene los argumentos proporcionados mediante la consola, que regulan la utilizacion de los filtros 
	 * @param gzipOs outputStream para guardar los datos leidos en un fichero comprimidos en gzip
	 * @param zipOs outputStream para guardar los datos leidos en un fichero comprimidos en zip
	 * @param name string para pasar el nombre del fichero a los filtros
	 */
	int c;
	int asc = 0;
	int zip = 0;
	int gzip = 0;
	URL url;
	InputStream is;
	FileOutputStream fos;
	String nameStr;
	String [] argumentos;
	GZIPOutputStream gzipOs;
	ZipOutputStream zipOs;
	String name;

	/**
	 * Constructor de la clase, recibe la url a descargar y los argumentos que determinan las opciones de compresion
	 */
	public Wget(String urlStr, String[] args){
		nameStr = urlStr;
		argumentos = args;
	} //Cierre del constructor
	
	/**
	 * Aplica los filtros dependiendo de los argumentos dados en la terminal
	 * @param fos FileoutputStream para guardar los datos leidos en un fichero
	 * @param zip parametro que decide si el filtro Ascii se utiliza
	 * @param gzip parametro que decide si el filtro Ascii se utiliza
	 * @param name string para pasar el nombre del fichero a los filtros
	 * @throws IOException
	 */
	public void FiltroZip (FileOutputStream fos, int zip, int gzip, String name) throws IOException
	{
		
		if (gzip == 1 && zip == 1)
		{
			ZipOutputStream zipOs = new ZipOutputStream (new GZIPOutputStream (fos));
			ZipEntry zipe = new ZipEntry(name);
			zipOs.putNextEntry(zipe);
		}
		else if(gzip == 1)
		{
			GZIPOutputStream gzipOs = new GZIPOutputStream(fos);
		}	
		else if(zip == 1)
		{
			ZipOutputStream zipOs = new ZipOutputStream(fos);
			ZipEntry zipe = new ZipEntry(name);
			zipOs.putNextEntry(zipe);
		}
	}
	/**
	 * Escribe los datos comprimidos o no dependiendo de los valores dados en la terminal
	 * @param c variable en la que guardaremos los bytes leidos por el inputStream
	 * @param zip parametro que decide si se escribe en un fichero zip
	 * @param gzip parametro que decide si se escribe en un fichero gzip
	 * @throws IOException
	 */
	public void write (int c, int zip, int gzip) throws IOException
	{
		if (gzip == 1 && zip == 1)
		{
			zipOs.write(c);
		}
		else if(gzip == 1)
		{
			gzipOs.write(c);
		}
		else if(zip == 1)
		{
			zipOs.write(c);
		}
		else
		{
		fos.write(c);
		}
	}
	/**
	 * Cierra los flujos que hemos utilizado
	 * @param zip parametro que decide si se cierra el outputStream de zip
	 * @param gzip parametro que decide si se cierra el outputStream de gzip
	 * @throws IOException
	 */
	public void close (int zip, int gzip) throws IOException
	{
		if (gzip == 1 && zip == 1)
		{
			zipOs.close();
		}
		else if(gzip == 1)
		{
			gzipOs.close();
		}
		else if(zip == 1)
		{
			zipOs.close();
		}
		else
		{
		fos.close();
		}
	}
	/**
	 * Ejecuta simultaneamente mediante threads
	 */
	public void run()
		{
		try 
			{	
				String nameFol;
				url = new URL(nameStr);
//				comprobamos que filtros hay que utilizar
				if (Arrays.asList(argumentos).contains("-asc"))
				{
					asc = 1;				
				}
				if (Arrays.asList(argumentos).contains("-gzip"))
				{
					gzip = 1;
				}
				if (Arrays.asList(argumentos).contains("-zip"))
				{
					zip = 1;
				}
				
				System.out.println("Filtro ascii es "+asc+"\n");
				System.out.println("Filtro gzip es "+gzip+"\n");
				System.out.println("Filtro zip es "+zip+"\n");
//				Obtenemos el path, que sera un string vacio si la URL no tiene un fichero al final.
				String path = url.getPath();
//				Si no hay fichero, a lo que hemos descargado se le llama index.html
				if(path.isEmpty())
				{
					nameFol = "index.html";
	           		File fileIndex = new File(nameFol);
	           		int i = 2;
//	           		Tenemos en cuenta si index ya existe para llamar al index.html nuevo de otra manera
	           		while (fileIndex.exists());
	           		{
	           			nameFol = "index" + i + ".html";
	                    fileIndex = new File(nameFol);
	                    i++;
	           		}
				}
//				Si existe un fichero en la url, obtenemos su nombre, junto con el formato, para guardarlo luego con ese nombre
				else
				{
				String fileName = url.getFile();
				String[] partes = fileName.split("/");
				nameFol = partes[partes.length-1];
				}	
				System.out.println("antes");
//				Abrimos el flujo
				is = url.openStream();
//				Si el filtro ascii esta activado , comprobamos que el tipo de archivo sea compatible, y si no lo es, mandamos un mensaje de error
				if(asc == 1)
				{
					System.out.println("partes "+nameFol+"");
					String format = nameFol.substring(nameFol.lastIndexOf(".") + 1);
					System.out.println("formato del ascii "+format+"");
					if (format.equals("txt") || format.equals("html") || format.equals("htm"))
					{	
//						ponemos .asc al formato del fichero
						nameFol += ".asc";
						InputStream asciiIs = new AsciiInputStream(is, nameFol);
//						Ponemos el formato correspondiente al nombre del fichero si los filtros de compresion se utilizan
						if(gzip == 1 && zip == 1)
						{
							nameFol += ".zip.gz";
							System.out.println(nameFol);
						}
						else if(gzip == 1)
						{
							nameFol += ".gz";
							System.out.println(nameFol);
						}
						else if(zip == 1)
						{
							nameFol += ".zip";
							System.out.println(nameFol);
						}
						if (gzip == 1 || zip == 1)
						{
							
							fos = new FileOutputStream(nameFol);
//							Llamamos a la funcion que aplica los filtros
							FiltroZip (fos, zip, gzip, nameFol);
//							Leemos los datos y los escribimos comprimidos o no, dependiendo de los argumentos
							while ((c = asciiIs.read()) != -1) 
			        		{
			        			write(c, zip, gzip);
			        		}
						}
						else
						{
							while ((c = asciiIs.read()) != -1) 
			        		{
			        			write(c, zip, gzip);
			        		}
						}
					}
				else
				{
					System.out.println("Formato del archivo no es compatible con Filtro Ascii, por lo tanto no se aplica");
					asc = 0;
				}
				}
//				Ponemos el formato correspondiente al nombre del fichero si los filtros de compresion se utilizan
				if(gzip == 1 && zip == 1)
				{
					nameFol += ".zip.gz";
					System.out.println(nameFol);
				}
				else if(gzip == 1)
				{
					nameFol += ".gz";
					System.out.println(nameFol);
				}
				else if(zip == 1)
				{
					nameFol += ".zip";
					System.out.println(nameFol);
				}
				
				fos = new FileOutputStream(nameFol);
//				Llamamos a la funcion que aplica los filtros
				FiltroZip (fos, zip, gzip, nameFol);
				System.out.println("aqui estoy");
//				Leemos los datos y los escribimos comprimidos o no, dependiendo de los argumentos
	        		while ((c = is.read()) != 0) 
	        		{
	        			write(c, zip, gzip);
	        		}
			//	}
			}
		catch (MalformedURLException e) 
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}

	/**
	 * Lee el documento linea a linea, y en cada bucle do-while crea un thread y le pasa a Wget la URL leida y los argumentos para tratarla
	 * @param args dados por la consola, determina que filtros se usan y el archivo que contiene las url
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//el archivo que contiene las URL es el primer argumento dado por la consola
		File file = new File(args[0]);
		FileReader fileReader = new FileReader(file);
		String line;
		int lines = 10;
		int n = 1;		
		Thread[] threads = new Thread[lines];

				BufferedReader reader = new BufferedReader(new FileReader(args[0]));			
				line = reader.readLine();
				do{					
						/*stringBuffer.append(line);*/
						System.out.println(line);
						threads[n] = new Thread(new Wget(line, args));
						threads[n].start();
						System.out.println(n);
						n++;
						line = "";
					} while ((line = reader.readLine()) != null);
				fileReader.close();
				reader.close();
	
		}
	}


