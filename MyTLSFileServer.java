import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import javax.naming.ldap.*;
import javax.net.*;
import java.net.*;
import java.io.*;
import java.util.*;

//java MyTLSFileServer 40202


class MyTLSFileServer{

	public static void main(String args[]){
		try{
			if(args.length != 1)
			{
				System.out.println("No");
				return;
			}

			int port = Integer.valueOf(args[0]);
			ServerSocketFactory ssf = getSSF();
			SSLServerSocket ss = (SSLServerSocket)ssf.createServerSocket(port);

			String EnabledProtocols[] = {"TLSv1.2", "TLSv1.1"};
			ss.setEnabledProtocols(EnabledProtocols);

			SSLSocket s = (SSLSocket)ss.accept();

			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			InputStream i = s.getInputStream();
			byte[] b = new byte[64];
			i.read(b);
			
			int a = 0;
			
			while(b[a] != (byte)0x00)
			{
				a++;
			}
			String f = new String(b, 0, a);

			System.out.println(f);
						
			File file = new File(f);
			
			if(!file.exists())
			{
				br.close();
				s.close();
				return;
			}
		
			FileInputStream fis = new FileInputStream(file);
			
			BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream()); 
			
			int r = 0;
			byte[] byteArray = new byte[1024];
			
			while((r = fis.read(byteArray)) != -1)
			{
				System.out.println(r);
				bos.write(byteArray, 0, r);
				byteArray = new byte[1024];
			}
			
			bos.flush();

			
			br.close();
			s.close();	
		}
		catch(Exception e){
			System.out.println(e);
		}
	
	}
	
	private static ServerSocketFactory getSSF()
	{
		try{	
			SSLContext ctx = SSLContext.getInstance("TLS");
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			KeyStore ks = KeyStore.getInstance("JKS");
			
			char[] passphrase = "cccbba".toCharArray();
			ks.load(new FileInputStream("server.jks"), passphrase);
			
			kmf.init(ks, passphrase);
			ctx.init(kmf.getKeyManagers(), null, null);
		
			SSLServerSocketFactory ssf = ctx.getServerSocketFactory();
			
			return ssf;
		}
		catch(Exception e){
			System.out.println(e);
			return null;
		}
	}	
	
}
