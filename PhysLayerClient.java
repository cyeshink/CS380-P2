import java.io.*;
import java.net.*;
import java.util.*;

public class PhysLayerClient {

    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("codebank.xyz", 38002)) {
            System.out.println("Connected to Server");
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            OutputStream out = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(out);

            //Calculate preamble
            double bl = 0;
            for (int i = 0; i < 64; i++) {
                bl += is.read();
            }
            bl /= 64.00;
            System.out.print("Baseline established from preamble: " + bl + "\n");

            //Read in 5B Signal and convert it to 4B using switch case
            boolean prev = false;
            boolean signal;
            int fiveb = 0;
            int[] hbyte = new int[64];

            for (int i = 0; i < hbyte.length; i++) {
                fiveb = 0;
                
                //First Calculate 5-bit Value
                for (int j = 0; j < 5; j++) {
                    signal = is.read() > bl;
                    if (prev == signal) {
                        fiveb *= 2;
                    } else {
                        fiveb *= 2;
                        fiveb += 1;
                    }
                    prev = signal;
                }

                
                switch (fiveb) {
                    case 30: hbyte[i] = 0;
                            break;
                    case 9: hbyte[i] = 1;
                            break;
                    case 20: hbyte[i] = 2;
                            break;
                    case 21: hbyte[i] = 3;
                            break;
                    case 10: hbyte[i] = 4;
                            break;
                    case 11: hbyte[i] = 5;
                            break;
                    case 14: hbyte[i] = 6;
                            break;
                    case 15: hbyte[i] = 7;
                            break;
                    case 18: hbyte[i] = 8;
                            break;
                    case 19: hbyte[i] = 9;
                            break;
                    case 22: hbyte[i] = 10;
                            break;
                    case 23: hbyte[i] = 11;
                            break;
                    case 26: hbyte[i] = 12;
                            break;
                    case 27: hbyte[i] = 13;
                            break;
                    case 28: hbyte[i] = 14;
                            break;
                    case 29: hbyte[i] = 15;
                            break;
                    
                }
            }

            //Combine the half bytes into bytes and print to console as well as to server
            byte[] byteArray = new byte[32];
            System.out.print("Received 32 bytes: ");

            for(int i = 0; i < 32; i++){
                int fHalf = hbyte[2*i];
                int sHalf = hbyte[2*i + 1];
                System.out.print((Integer.toHexString(fHalf)).toUpperCase());
                System.out.print((Integer.toHexString(sHalf)).toUpperCase());
                byteArray[i] = (byte) (16*fHalf + sHalf);
            }
            System.out.println();           
            out.write(byteArray);

            //Get Response and print accordingly
            if (is.read() == 1){
                System.out.println("Response good.");
            } else {
                System.out.println("Response bad.");
            }
            System.out.println("Disconnected from server.");


        } catch (Exception e) {
        }

    }
}