///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
   
        PrintWriter out = new PrintWriter(remote.getOutputStream());

        OutputStream outPut = remote.getOutputStream();
        
        //BufferedOutputStream out = new BufferedOutputStream(remote.getOutputStream());

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
        
        //https://restfulapi.net/http-methods/
        switch(type) {
        	case "GET" : 
        		getRequest(out,outPut,ressourcePath);
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
        	PrintWriter out = new PrintWriter(remote.getOutputStream());
        	//https://www.baeldung.com/java-string-to-byte-array
        	makeServerErrorHeader(out);
        	//byte[] byteArray = inputString.getBytes();
        	//out.write(inputString);
        	
        } catch (Exception ex) {
        	System.out.println("Error: " + ex);
        }
      }
    }
  }
  
  public void makeServerErrorHeader(PrintWriter out) {
	  out.println("HTTP/1.0 500 Internal Server Error");
	  out.println("Content-Type: text/html");
	  out.println("Server: Bot");
	  out.println("");  
  }
  
  public void makeNotFoundHeader(PrintWriter out,long length) {
	  out.println("HTTP/1.0 404 Not Found");
	  out.println("Content-Type: text/html");
	  out.println("Content-Length: " + length );
	  out.println("Server: Bot");
	  out.println("");  
  }
  
  public void makeCreatedHeader(PrintWriter out) {
	  out.println("HTTP/1.0 201 Created");
	  out.println("Content-Type: text/html");
	  out.println("Server: Bot");
	  out.println("");  
  }
  
  public void makeHeaderOk(PrintWriter out,String ressourceName,long length) {
	  out.println("HTTP/1.0 200 OK");
	  String []split = ressourceName.split("\\.");
	  //System.out.println("split "+split[0]);
	  String extension = split[1];
	  System.out.println("extension du fichier : "+extension);
	  //https://developer.mozilla.org/fr/docs/Web/HTTP/Basics_of_HTTP/MIME_types
	  switch(extension){
	  	case "html" :
	  		out.println("Content-Type: text/html");
	  		break;
	  	case"png":
	  		out.println("Content-Type: image/png");
	  		break;
	  	case"jpg":
	  		out.println("Content-Type: image/jpeg");
	  		break;
	  	case"jpeg":
	  		out.println("Content-Type: image/jpeg");
	  		break;
	  	case"gif":
	  		out.println("Content-Type: image/gif");
	  		break;
	  	case "rtf":
	  		out.println("Content-Type: text/plain");
	  		break;
	  	default : 
	  		out.println("Content-Type: text/plain");
	  		break;
	  }
	  out.println("Content-Length: " + length );
	  out.println("Server: Bot");
	  out.println("");
	  
  }
  
  /**
   * GET is used to request data from a specified resource. 
   * source of the definition : www.w3schools.com
   * @param out
   * @param ressourceName
   */
  public void getRequest(PrintWriter out, OutputStream outPut, String ressourceName) {
	  try {
		  if(!ressourceName.isEmpty()) {
			  File file = new File(ressourceName);
			  if(file.exists()) {//OK
				  /* lire le fichier 
				   * envoie du contenu 
				   */
				  //header de succes
				  makeHeaderOk(out,ressourceName,file.length());
				  out.flush();
				  // lecture fichier 
				  BufferedReader br = new BufferedReader(new FileReader(file)); 
				  
				  Path path = Paths.get(ressourceName);
				  
				  Files.copy(path,outPut);
				  
//				  String st; 
//				  while ((st = br.readLine()) != null){
//				    System.out.println(st); 
//				    //outPut.write(st.getBytes());
//				    out.println(st); 
//				  	//out.write(st);
//				  } 
//				  out.flush();
				  
			  }else {//Not Found
				  makeNotFoundHeader(out,"<H1>Page Not Found</H2>".length()); 
				  out.write("<H1>Page Not Found</H2>");
			  }
		  }else {
			  makeHeaderOk(out,ressourceName,0);
			  out.flush();
		  }   
	  }catch (Exception ex) {
      	System.out.println("Error: " + ex);
      	try {
      		makeServerErrorHeader(out);
      		//String inputString = makeServerErrorHeader();
        	//byte[] byteArray = inputString.getBytes();
        	//out.write(inputString);
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
  public void postRequest(PrintWriter out, String ressourceName) {
	  try {
		  //Writer fileWriter = new FileWriter("c:\\data\\output.txt", true);  //appends to file
		  File f = new File(ressourceName);
		  if(f.exists() && !f.isDirectory()) { //OK 200 overwrites the file
			  Writer fileWriter = new FileWriter(ressourceName, true); //append file
			  f = new File(ressourceName);
			  //header de succes
			  makeHeaderOk(out,ressourceName,f.length());
			  out.flush();
		  }else {
			 if(f.createNewFile()) { //Created 201 creates the new file
				 makeCreatedHeader(out);
			 } else { //500 Internal Server Error
				 makeServerErrorHeader(out);
			 }
		  }
		  
	  }catch (Exception ex) {
      	System.out.println("Error: " + ex);
      	try {
      		makeServerErrorHeader(out);
      		//String inputString = makeServerErrorHeader();
        	//byte[] byteArray = inputString.getBytes();
        	//out.write(inputString);
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
  public void headRequest(PrintWriter out, String ressourceName) {
	  try {
		  if(!ressourceName.isEmpty()) {
			  File file = new File(ressourceName);
			  if(file.exists()) {//OK

				  //header de succes
				  makeHeaderOk(out,ressourceName,file.length());
				  out.flush();
				  
				  //sans le body
				  
			  }else {//Not Found
				  makeNotFoundHeader(out,"<H1>Page Not Found</H2>".length()); 
				  out.flush();
				  //out.write("<H1>Page Not Found</H2>");
				  
			  }
		  }else {
			  makeHeaderOk(out,ressourceName,0);
			  out.flush();
		  }
	  }catch (Exception ex) {
      	System.out.println("Error: " + ex);
      	try {
      		makeServerErrorHeader(out);
      		//String inputString = makeServerErrorHeader();
        	//byte[] byteArray = inputString.getBytes();
        	//out.write(inputString);
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
  public void putRequest(PrintWriter out, String ressourceName) {
	  try {
		  File f = new File(ressourceName);
		  if(f.exists() && !f.isDirectory()) { //OK 200 overwrites the file
			  Writer fileWriter = new FileWriter(ressourceName, false); //overwrites file
			  f = new File(ressourceName);
			  //header de succes
			  makeHeaderOk(out,ressourceName,f.length());
			  out.flush();
		  }else {
			 if(f.createNewFile()) { //Created 201 creates the new file
				 makeCreatedHeader(out);
			 } else { //500 Internal Server Error
				 makeServerErrorHeader(out);
			 }
		  }
		  
	  }catch (Exception ex) {
      	System.out.println("Error: " + ex);
      	try {
      		makeServerErrorHeader(out);
      		//String inputString = makeServerErrorHeader();
        	//byte[] byteArray = inputString.getBytes();
        	//out.write(inputString);
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
  public void deleteRequest(PrintWriter out, String ressourceName) {
	  try {
		  if(!ressourceName.isEmpty()) {
			  File file = new File(ressourceName);
			  if(file.delete()) {//OK

				  //header de succes
				  makeHeaderOk(out,ressourceName,file.length());
				  out.flush();
				  
				  //sans le body
				  
			  }else {//Not Found
				  makeNotFoundHeader(out,"<H1>Page Not Found</H2>".length()); 
				  out.write("<H1>Page Not Found</H2>");
			  }
		  }else {
			  makeHeaderOk(out,ressourceName,0);
			  out.flush();
		  }
		  
	  }catch (Exception ex) {
      	System.out.println("Error: " + ex);
      	try {
      		makeServerErrorHeader(out);
      		//String inputString = makeServerErrorHeader();
        	//byte[] byteArray = inputString.getBytes();
        	//out.write(inputString);
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
