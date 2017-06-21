import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 */

/**
 * @author x-d3
 *
 */

public class AsciiInputStream extends FilterInputStream
{
	String name;
	
	protected AsciiInputStream(InputStream in, String filename)
	{
		super(in);
		name = filename;
		// TODO Auto-generated constructor stub
	}
	public int read() 
	{
		try 
		{
			int c;
			char b;
			
			FileOutputStream fos = new FileOutputStream(name);
			
					while( (c = this.in.read()) >0)
					{
						b = (char) c; 
						if(b == '<')
						{
							while(b != '>')
							{
								b = (char)this.in.read();
							}
						
						}
						else
						{	
							fos.write(c);
						}
					}
					in.close();
            		fos.close();
		}

		catch(IOException e)
		{
	    return 0;
		}
		return 0;
	}
}