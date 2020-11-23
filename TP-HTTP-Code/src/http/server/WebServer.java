///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Collectors;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 3000");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(3000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(
            remote.getInputStream()));
   
        
        BufferedOutputStream out = new BufferedOutputStream(remote.getOutputStream());

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String str = ".";
        String header="";
        while (str != null && !str.equals("")) {
        	str = in.readLine();
        	header+=str+"\r\n";
        	System.out.println(str);
        }
        
        System.out.println(header);
        
        //analyse the header and display the corresponding ressource or the default index content
        
        String [] splitHeader = header.split("\r\n");
        
        String type = splitHeader[0].split(" ")[0];
        String ressourcePath = splitHeader[0].split(" ")[1];
        if(ressourcePath.length()!=1) { //case only a '/' means no filepath/filename for the ressource
        	ressourcePath = ressourcePath.substring(1, ressourcePath.length());
        }else {  
        	ressourcePath="";
        }

        System.out.println(type+"  "+ressourcePath);
        
        switch(type) {
        	case "GET" : 
        		getRequest(out,ressourcePath);
        		break;
        	case "POST":
        		postRequest(out,ressourcePath);
        		break;
        	case "HEAD":
        		headRequest(out,ressourcePath);
        		break;
        	case "PUT":
        		putRequest(out,ressourcePath);
        		break;
        	case "DELETE":
        		deleteRequest(out,ressourcePath);
        		break;		
        }
        
        /*
         * HTTP Status code pris en compte : 
         * 
         * - 200 : OK
         * - 404 : Not Found
         * - 500 : Internal Server Error
         */
        
        /*
         * Questions : 
         * 
         */
        
        /*
         * infos sur moodle :
         * 
         * Le traitement d'une ressource web statique se traduit, côté serveur HTTP, par la lecture 
         * du fichier qui contient cette ressource et l'envoi de son contenu (texte ou binaire) au 
         * client HTTP. 
         * 
         * Le traitement d'une ressource web dynamique se traduit, côté serveur HTTP, 
         * par l'exécution du programme qui contient cette ressource et l'envoi du résultat au client HTTP.
         * 
         */
        
        
        
        // Send the response
        // Send the headers
        
//        out.println("HTTP/1.0 200 OK");
//        out.println("Content-Type: text/html");
//        out.println("Server: Bot");
//        // this blank line signals the end of the headers
//        out.println("");
        // Send the HTML page
//        InputStream htmlIn = this.getClass().getClassLoader()
//                .getResourceAsStream("Adder.html");
//        if(htmlIn!=null) {
//        	
//        	String si = new BufferedReader(new InputStreamReader(htmlIn))
//        			.lines().collect(Collectors.joining("\n"));
//        	out.println(si);
//        }
                

        
        // image etc.

        
        out.flush();
        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
        try {
        	Socket remote = s.accept();
        	BufferedOutputStream out = new BufferedOutputStream(remote.getOutputStream());
        	//https://www.baeldung.com/java-string-to-byte-array
        	String inputString = makeServerErrorHeader();
        	byte[] byteArray = inputString.getBytes();
        	out.write(byteArray);
        	
        } catch (Exception ex) {
        	System.out.println("Error: " + ex);
        }
      }
    }
  }
  
  public String makeServerErrorHeader() {
	  String res = "HTTP/1.0 500 Internal Server Error"+"\r\n";
	  res+="Content-Type: text/html"+"\r\n"+"Server: Bot"+"\r\n\r\n";
	  return res;
  }
  
  public String makeNotFoundHeader() {
	  String res = "HTTP/1.0 404 Not Found"+"\r\n";
	  res+="Content-Type: text/html"+"\r\n"+"Server: Bot"+"\r\n\r\n";
	  return res;
  }
  
  public String makeHeaderOk(String ressourceName,long length) {
	  String header = "HTTP/1.0 200 OK"+"\r\n";
	  String []split = ressourceName.split(".");
	  String extension = split[1];
	  System.out.println("extension du fichier"+extension);
	  //https://developer.mozilla.org/fr/docs/Web/HTTP/Basics_of_HTTP/MIME_types
	  switch(extension){
	  	case "html" :
	  		header+="Content-Type: text/html"+"\r\n";
	  		break;
	  	case"png":
	  		header+="Content-Type: image/png"+"\r\n";
	  		break;
	  	case"jpg":
	  		header+="Content-Type: image/jpeg"+"\r\n";
	  		break;
	  	case"jpeg":
	  		header+="Content-Type: image/jpeg"+"\r\n";
	  		break;
	  }
	  header += "Content-Length: " + length + "\r\n";
	  header+="Server: Bot"+"\r\n\r\n";
	  return header;
  }
  
  /**
   * GET is used to request data from a specified resource. 
   * source of the definition : www.w3schools.com
   * @param out
   * @param ressourceName
   */
  public void getRequest(BufferedOutputStream out, String ressourceName) {
	  try {
		  if(!ressourceName.isEmpty()) {
			  File file = new File(ressourceName);
			  if(file.exists()) {//OK
				  /* lire le fichier 
				   * envoie du contenu 
				   */
				  //header de succes
				  out.write(makeHeaderOk(ressourceName,file.length()).getBytes());
				  // lecture fichier 
				  
				  
			  }else {//Not Found
				  out.write(makeNotFoundHeader().getBytes());
			  }
		  }else {
			  
		  }   
	  }catch (Exception ex) {
      	System.out.println("Error: " + ex);
      	try {
      		String inputString = makeServerErrorHeader();
        	byte[] byteArray = inputString.getBytes();
        	out.write(byteArray);
      	}catch (Exception e) {
            System.out.println("Error: " + e);
      	}
      }
  }
  
  /**
   * POST is used to send data to a server to create/update a resource.
   * source of the definition : www.w3schools.com
   * @param out
   * @param ressourceName
   */
  public void postRequest(BufferedOutputStream out, String ressourceName) {
	  try {
		  
	  }catch (Exception ex) {
      	System.out.println("Error: " + ex);
      	try {
      		String inputString = makeServerErrorHeader();
        	byte[] byteArray = inputString.getBytes();
        	out.write(byteArray);
      	}catch (Exception e) {
            System.out.println("Error: " + e);
      	}
      }
  }
  /**
   * HEAD is almost identical to GET, but without the response body. 
   * In other words, if GET /users returns a list of users, then HEAD 
   * /users will make the same request but will not return the list of users.
   * HEAD requests are useful for checking what a GET request will return before 
   * actually making a GET request - like before downloading a large file or response body.
   * source of the definition : www.w3schools.com
   * @param out
   * @param ressourceName
   */
  public void headRequest(BufferedOutputStream out, String ressourceName) {
	  try {
		  
	  }catch (Exception ex) {
      	System.out.println("Error: " + ex);
      	try {
      		String inputString = makeServerErrorHeader();
        	byte[] byteArray = inputString.getBytes();
        	out.write(byteArray);
      	}catch (Exception e) {
            System.out.println("Error: " + e);
      	}
      }
  }
  /**
   * PUT is used to send data to a server to create/update a resource. 
   * The difference between POST and PUT is that PUT requests are idempotent. 
   * That is, calling the same PUT request multiple times will always produce the same result. 
   * In contrast, calling a POST request repeatedly have side effects of creating the same resource 
   * multiple times.
   * source of the definition : www.w3schools.com
   * @param out
   * @param ressourceName
   */
  public void putRequest(BufferedOutputStream out, String ressourceName) {
	  try {
		  
	  }catch (Exception ex) {
      	System.out.println("Error: " + ex);
      	try {
      		String inputString = makeServerErrorHeader();
        	byte[] byteArray = inputString.getBytes();
        	out.write(byteArray);
      	}catch (Exception e) {
            System.out.println("Error: " + e);
      	}
      }
  }
  
  /**
   * The DELETE method deletes the specified resource.
   * source of the definition : www.w3schools.com
   * @param out
   * @param ressourceName
   */
  public void deleteRequest(BufferedOutputStream out, String ressourceName) {
	  try {
		  
	  }catch (Exception ex) {
      	System.out.println("Error: " + ex);
      	try {
      		String inputString = makeServerErrorHeader();
        	byte[] byteArray = inputString.getBytes();
        	out.write(byteArray);
      	}catch (Exception e) {
            System.out.println("Error: " + e);
      	}
      }
  }
  
  
  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }
}
