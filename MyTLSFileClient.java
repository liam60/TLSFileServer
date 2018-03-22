import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import javax.naming.ldap.*;
import javax.net.*;
import java.net.*;
import java.io.*;
import java.util.*;

/*javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
*/

//javax.net.ssl.SSLHandshakeException: java.security.cert.CertificateException: No name matching localhost found


//java -Djavax.net.ssl.trustStore=ca-cert.jks MyTLSFileClient cms-r1-17.cms.waikato.ac.nz 40202 med.jpg

class MyTLSFileClient{

	public static void main(String args[]){
		try{
			if(args.length != 3)
			{
				System.out.println("No");
				return;
			}
			
			String filename = args[2];
			
			
			//comment
			SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
			
			SSLSocket socket = (SSLSocket)factory.createSocket(args[0], Integer.valueOf(args[1]));
			
			SSLParameters params = new SSLParameters();
			params.setEndpointIdentificationAlgorithm("HTTPS");
			socket.setSSLParameters(params);
			
			socket.startHandshake();
			
			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
			bos.write(filename.getBytes());
			bos.flush();
			
			byte[] buf = new byte[1024];

			InputStream is = socket.getInputStream();
			
			FileOutputStream fos = new FileOutputStream("_"+args[2]);
			
			int x = 0;
			while((x = is.read(buf)) != -1)
			{
				System.out.println(x);
				fos.write(buf, 0, x);
				buf = new byte[1024];
			}
			
			


			
			SSLSession sesh = socket.getSession();
			X509Certificate cert = (X509Certificate)sesh.getPeerCertificates()[0];
			
			System.out.println(getCommonName(cert).equals(args[0]));
			
			is.close();
			fos.close();
			bos.close();
			socket.close();
		}
		catch(Exception e){
			System.err.println(e);
		}
	
	}	
	
	private static String getCommonName(X509Certificate cert)
	{
		try{
	
			String name = cert.getSubjectX500Principal().getName();
			LdapName ln = new LdapName(name);
			String cn = null;
			for(Rdn rdn : ln.getRdns())
			if("CN".equalsIgnoreCase(rdn.getType()))
			cn = rdn.getValue().toString();
			return cn;
		}
		catch(Exception e)
		{
			System.err.println(e);
			return null;
		}
	}
}
