import java.net.*;
import java.io.*;
import java.util.*;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Server{

	public static String showImageInHtml(String imageLine) throws FileNotFoundException , IOException{
		//find path from imageline
		int start = imageLine.indexOf("\"");
		int end = imageLine.indexOf("\"" , start+1);
		String path = imageLine.substring(start+1,end);
		String extention = path.substring(path.indexOf('.')+1);
		//
		File pic = new File(path);
		FileInputStream fileIn = new FileInputStream(pic);
		byte fileContent[] = new byte[(int)pic.length()];
		fileIn.read(fileContent);
		String we = Base64.getEncoder().encodeToString(fileContent);
  		String strFileContent = new String(fileContent);
  		String image = "data:image/"+extention+";base64,"+we;
  		//System.out.println(image);

  		return imageLine.substring(0,start+1)+image+imageLine.substring(end);
	}

	public static ArrayList <String> readFileToString(String fileName){
		ReadFile file = new ReadFile(fileName);
		ArrayList <String> lines = file.get_lines();
		return lines;
	}

    public static void replaceNeeds(String search , String replacement, String file)throws Exception{
        File log= new File(file);

        try{
            FileReader fr = new FileReader(log);
            String s;
            String totalStr = "";
            try (BufferedReader br = new BufferedReader(fr)) {

                    while ((s = br.readLine()) != null) {
                        totalStr += s;
                    }
                    totalStr = totalStr.replaceAll(search, replacement);
                    FileWriter fw = new FileWriter(log);
                    fw.write(totalStr);
                    fw.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void provideBestResponseToLogin(BufferedWriter out,String username , String password)throws Exception{
        replaceNeeds("@username" , username , "user_page.html");
        sendHtmlFile(out,"user_page.html");
    }

    public static void sendHtmlFile(BufferedWriter out , String file)throws Exception{
            ArrayList <String>  htmlFile = readFileToString(file);
     
            out.write("HTTP/1.0 200 OK\r\n");
            out.write("Content-Type: text/html\r\n");
            out.write("Content-Length: "+String.valueOf(htmlFile.size()+5)+"\r\n");
            out.write("\r\n");
            
            for (int i=0 ; i < htmlFile.size() ; i++ ) {
                //System.out.println(htmlFile.get(i));
                if(htmlFile.get(i).indexOf("<img") > -1 || htmlFile.get(i).indexOf("< img") > -1 ){
                    out.write(showImageInHtml(htmlFile.get(i)));
                    continue;
                }
                out.write(htmlFile.get(i));
            }

            System.out.println("connection terminated!");
            
        
    }
	
	public static void main(String[] args) throws Exception{
		ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        boolean getResponse = false;

		while(true){
			Socket  clientSocket = serverSocket.accept();	

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        	BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        	//System.out.println(in.readLine());
        	String s;
        	while ((s = in.readLine()) != null) {
            	System.out.println("INPUT : "+s);
                String[] splited = s.split("\\s+");
                if(splited[0].equals("GET")){
                    Pattern MY_PATTERN = Pattern.compile("uname=(.*)&psw=(.*)");
                    Matcher m = MY_PATTERN.matcher(splited[1]);
                    if (m.find()) {
                        getResponse = true;
                        System.out.println("USERNAME : "+m.group(1)+"PASSWORD : "+m.group(2));
                        provideBestResponseToLogin(out,m.group(1),m.group(2));
                    }
                    //String[] UserAndPass = splited[1].split("=");
                    //System.out.println(UserAndPass[1]);
                    //provideBestResponse(splited);
                }
            	if (s.isEmpty()) {
                	break;
            	}
        	}
            if(!getResponse){
                sendHtmlFile(out , "w.html");
            }
            out.close();
            in.close();
            clientSocket.close();
        }
	}
	
}