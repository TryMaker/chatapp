package testg02;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class URLConnectionReader {
	public static void main(String[] args) throws Exception {
		URL site=new URL("https://www.naver.com/");
		URLConnection url=site.openConnection();
		BufferedReader in=new BufferedReader(
							new InputStreamReader(
								url.getInputStream(),"utf-8")); //대쉬 뺐다 넣다
		String inLine;
		
		while ((inLine=in.readLine())!=null) {
			System.out.println(inLine);
		}
		in.close();
	}

}