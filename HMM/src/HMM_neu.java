import java.io.*;
import java.util.Scanner;
//import java.util.Arrays;
//import java.lang.StringBuffer;
public class HMM_neu {
	    public static void main(String[] args) {
	    	double precision=0.0000001;
	    	int zustaende=5;
	        //Einblenden um Dateiname der Wahl zu definieren
	    	//System.out.println("Sequenzen werden mit einem > gefolgt von der Bezeichnung eingeleitet, anschliessend Folgt die Sequenz. Es sind keine Leerzeilen erlaubt.\nEs wurde angenommen, dass ähnliche Sequenzen miteinander verglichen werden sollen,\ndaher sollten sich die Sequenzen weniger als ein Drittel in ihrer laenge voneinander unterscheiden \n(tun sie es doch, sollte entweder die kuerzste, oder aber eine Mittellange Sequenz die nicht mehr als ein Drittel kuerzer ist als die kuerzste als erstes eingegeben werden). \nDas Programm wurde mit Beispielsequenzen, die sich um wenige Basenpaare voneinander Unterschieden getestet (seq.txt). ");
	    	Scanner input=new Scanner(System.in);//einlesefkt aktivieren
	    	System.out.print("Sequenzenbeinhaltende Datei eingeben (z.B. seq.txt)\n  ");
	    	String dateiname = input.next();//liest eingaben und speichert in line
	    	// String dateiname="seq2.txt";//auskommentieren wenn Dateinaeme abgefragt werden soll
	    	String[][] Sequenzen=getsequences(dateiname);
	    	print(Sequenzen);
	    	double[][] bMatrix=Initialmodell1(Sequenzen,zustaende);
	    	double[][] aMatrix=Initialmodell2(Sequenzen,zustaende);
	    	double[][] alpha;double[][] beta;double[][] aMatrix_neu;double[][] bMatrix_neu;
	    	String allseq=alleseq(Sequenzen);
	    	int[] allseqnum=allsequmformen(allseq);
	    	int iterations=0;
	    	boolean ok=true;
	    	while(ok){
	    		alpha=forward(allseq,allseqnum,aMatrix,bMatrix);
	    		beta=backward(allseq,allseqnum,aMatrix,bMatrix);
	    		System.out.println();
	    		System.out.println("++++++++++++++++++++++++++++++++++++++ "+iterations+".Iteration ++++++++++++++++++++++++++++++++++++++");
	    		aMatrix_neu=neuaMatrix(allseqnum,aMatrix,bMatrix,alpha,beta);
	    		bMatrix_neu=neubMatrix(allseqnum,aMatrix,bMatrix,alpha,beta);
	    		System.out.println();
	    		boolean as=iterationen(aMatrix,aMatrix_neu,precision);
	    		boolean bs=iterationen(bMatrix,bMatrix_neu,precision);
	    		if(as&&bs==false){ok=false;}
	    		aMatrix=aMatrix_neu;
	    		bMatrix=bMatrix_neu;
	    		iterations++;
	    	}
		}
		public static boolean iterationen(double[][] Matrix,double[][] Matrix_neu,double precision){
			boolean ok2=true;
			int help=0;
			for(int i=0;i<Matrix.length;i++){
				for(int j=0;j<Matrix[1].length;j++){
					if(Math.abs(Matrix_neu[i][j] - Matrix[i][j]) < precision){
						help++;
					}
				}
			}
			if(help==Matrix.length*Matrix[1].length){ok2=false;}
			return ok2;
		}
//----------------------FORWARD++++++++++++++++++++BACKWARD----------------------------------------//
		public static double[][] forward(String seq,int[] allseqnum,double[][] a, double[][] b){
        	int count=0;
        	int it=1; //iterationen
        	double [][] alpha=new double[6][seq.length()];
        	alpha[0][0]=1;
        	while (count < it) {
        		//System.out.println("----------------------------- forward------------------alpha -----------------------------");
        		count++;
        		for(int t=1;t<seq.length();t++){
        			for(int j=0;j<6;j++){
        				for(int i=0;i<6;i++){
        					double merk=alpha[i][t-1]*a[i][j]*b[j][allseqnum[t]];
        					alpha[j][t]+=merk;
        				}
        			}
        		}
        		//printab(alpha,seq);
        	}                
        	return alpha;
        }
		public static double[][] backward(String seq,int[] allseqnum,double[][] a, double[][] b){
	    	int count=0;
	    	int it=1; //iterationen
	    	double [][] beta=new double[6][seq.length()];
	    	beta[0][seq.length()-1]=1;
	    	while(count < it) {
	    		//System.out.println("----------------------------- backward-----------------beta ------------------------------");
	    		count++;
	    		for(int t=seq.length()-2;t>=0;t--){
	    			for(int j=0;j<6;j++){
	    				for(int k=0;k<6;k++){
	    					double merk=beta[k][t+1]*a[j][k]*b[k][allseqnum[t+1]];
	    					beta[j][t]+=merk;
	    				}
	    			}
	    		}
	    		//printab(beta,seq);
	    	}
	    	return beta;
        }
//-----------------------------MODELLE a UND b (MATRIZEN)-----------------------------------------//
		public static double [][] Initialmodell1(String[][] Sequenzen,int s){//B Matrix berechnen durch Splitten der Sequenz in 3 Abschnitte.
        	int Abschnitt=(Sequenzen[0][1].length()/s);//Ermitteln der Abschnittslaengen
        	int zustaende=s+1;//zustaende=3+1(0)
        	double [][] bMatrix=new double[zustaende][5];
        	int[] ATGCs=new int[4];
        	bMatrix[0][4]=1;        //bmatrix Anzahl der ATGCs in den Zustaenden ermitteln
        	for(int j=1;j<zustaende-1;j++){//alle zustaende durchfahren
        		for(int i=0;i<Sequenzen.length;i++){//alle Seq nacheiandner betrachtetn
        			ATGCs=(Anzahl(Sequenzen[i][1].substring((j-1)*Abschnitt,(j)*Abschnitt)));//Anzahl =ATGC zaehlende Subroutine
        			bMatrix[j][0]=bMatrix[j][0]+ATGCs[0];
        			bMatrix[j][1]=bMatrix[j][1]+ATGCs[1];
        			bMatrix[j][2]=bMatrix[j][2]+ATGCs[2];
        			bMatrix[j][3]=bMatrix[j][3]+ATGCs[3];
        		}
        	}
        	for(int i=0;i<Sequenzen.length;i++){
        		ATGCs=(Anzahl(Sequenzen[i][1].substring((zustaende-2)*Abschnitt)));
        		bMatrix[zustaende-1][0]=bMatrix[zustaende-1][0]+ATGCs[0];
        		bMatrix[zustaende-1][1]=bMatrix[zustaende-1][1]+ATGCs[1];
        		bMatrix[zustaende-1][2]=bMatrix[zustaende-1][2]+ATGCs[2];
        		bMatrix[zustaende-1][3]=bMatrix[zustaende-1][3]+ATGCs[3];
        	}
        	double Zeile=0;
        	for(int i=1;i<zustaende;i++){
        		for(int j=0;j<Sequenzen.length;j++){
        			Zeile=Zeile+bMatrix[i][j];//Elemente in Zustand berechnen
        		}
        		for(int j=0;j<4;j++){
        			bMatrix[i][j]=bMatrix[i][j]/Zeile;//ATGCs
        		}
        		Zeile=0;
        	}   
        	System.out.println("bMatrix");
        	System.out.println("\tA\tT\tG\tC\t,");
        	print(bMatrix);
        	return bMatrix;
        }
        public static double[][] Initialmodell2(String[][] Sequenzen,int s){    //a-Matrix
        	int Abschnitt=(Sequenzen[0][1].length()/s);//Ermitteln der Abschnittslaengen
        	double[][] aMatrix=new double[s+1][s+1];
        	double letztes=0;
        	for(int i=0;i<Sequenzen.length;i++){
        		letztes=letztes+Sequenzen[i][1].substring((2)*Abschnitt).length();
        	}
        	double a=Abschnitt;
        	double b=Sequenzen.length;
            aMatrix[0][1]=1;
            aMatrix[0][0]=aMatrix[0][2]=aMatrix[0][4]=aMatrix[0][5]=aMatrix[1][5]=aMatrix[1][4]=aMatrix[2][5]=aMatrix[2][4]=aMatrix[3][5]=aMatrix[5][4]=aMatrix[5][3]=aMatrix[5][2]=aMatrix[4][3]=aMatrix[4][2]=aMatrix[4][1]=aMatrix[4][0]=aMatrix[0][3]=aMatrix[1][0]=aMatrix[2][0]=aMatrix[1][3]=aMatrix[2][1]=aMatrix[3][1]=aMatrix[3][2]=0;
            aMatrix[1][1]=(a*b-b)/(a*b);
            aMatrix[1][2]=1-(a*b-b)/(a*b);
            aMatrix[2][2]=(a*b-b)/(a*b);
            aMatrix[2][3]=1-(a*b-b)/(a*b);
            aMatrix[3][3]=(a*b-b)/(a*b);
            aMatrix[3][4]=1-(a*b-b)/(a*b);
            aMatrix[4][4]=(a*b-b)/(a*b);
            aMatrix[4][5]=1-(a*b-b)/(a*b);
            aMatrix[5][5]=(letztes*b-b)/(letztes*b);
            aMatrix[5][0]=1-(letztes*b-b)/(letztes*b);
            System.out.println("\naMatrix");
            for (int i=0;i<s;i++){
            	System.out.print("\t"+i);
            }
            System.out.println();
            print(aMatrix);
            return(aMatrix);
        }
        public static double[][] neuaMatrix(int[] seq, double[][] a,double[][] b,double[][] alpha,double[][] beta){
			int s=6;   //zustaende+0
			double[][] aMatrix_neu= new double [s][s];
			for (int j=0; j<s;j++){
				for(int i=0; i<s;i++){
					double merkn=0;
					double merkz=0;
					for (int t=0;t<seq.length-1;t++){
						merkz+=alpha[i][t]*a[i][j]*b[j][seq[t+1]]*beta[j][t+1];
						merkn+=alpha[i][t]*beta[i][t];
					}
					aMatrix_neu[i][j]=merkz/merkn;
				}
			}
			System.out.println(">>>>>>> neue aMatrix:");
			System.out.println("\t0\t1\t2\t3\t4\t5");
			print(aMatrix_neu);
			return(aMatrix_neu);
		}
	    public static double[][] neubMatrix(int[] seq, double[][] a,double[][] b,double[][] alpha,double[][] beta){
            int s=6; //zustaende
            double[][] bMatrix_neu=new double[s][5];
             for(int o=0;o<5;o++){
            	 for(int i=0;i<s;i++){
            		 double merkn=0;
            		 double merkz=0;
            		 for(int t=1;t<seq.length;t++){
            			 int delta=0;
            			 if(seq[t]==o){//soll das für , stehen?
            				 delta=1;
            			 }
            			 merkz += alpha[i][t]*beta[i][t]*delta;
            			 merkn += alpha[i][t]*beta[i][t];
            		 }
            		 //System.out.println("merkn:"+merkn+"merkz:"+merkz+"merkz/merkn"+(merkz/merkn));
            		 bMatrix_neu[i][o]=merkz/merkn;
            	 }
            }
             System.out.println(">>>>>>> neue bMatrix:");
             System.out.println("\tA\tT\tG\tC\t,");
             print(bMatrix_neu);
             return(bMatrix_neu);
	    }
//-----------------------------EINLESEN,ZAEHLEN,UMFORMEN----------------------------//
        public static int[] allsequmformen(String allseq){
        	int[] allseqnum=new int[allseq.length()];
        	for(int i=0;i<allseq.length();i++){
                if (allseq.charAt(i)=='A')allseqnum[i]=0;
                if (allseq.charAt(i)=='T')allseqnum[i]=1;
                if (allseq.charAt(i)=='G')allseqnum[i]=2;
                if (allseq.charAt(i)=='C')allseqnum[i]=3;
                if (allseq.charAt(i)==',')allseqnum[i]=4;
            }
            return allseqnum;
        }
        public static int[] Anzahl (String sequenz){ //Nukleotidanzahl zaehlen
        	int[] Anzahl= new int [4];
        	for (int i=0; i<sequenz.length(); i++){
        		if (sequenz.charAt(i)=='A')Anzahl[0]=Anzahl[0]+1;
        		if (sequenz.charAt(i)=='T')Anzahl[1]=Anzahl[1]+1;
        		if (sequenz.charAt(i)=='G')Anzahl[2]=Anzahl[2]+1;
        		if (sequenz.charAt(i)=='C')Anzahl[3]=Anzahl[3]+1;
        	}
        	return Anzahl;
        }
        public static String alleseq(String[][] seq){ //alle Sequenzen in einen String packen
        	String allseq=",";
        	for(int i=0;i<seq.length;i++){
        		allseq=allseq+seq[i][1]+",";
        	}
        	return allseq;
        }
        public static String[][] getsequences(String dateiname){
        	//File Einlesen und Sequenzlaenge ermitteln
        	int Arraylaenge=0;
        	try{
        		FileInputStream datei=new FileInputStream (dateiname);
        		BufferedReader rdr= new BufferedReader(new InputStreamReader(datei));
        		String strLine;
        		while ((strLine = rdr.readLine())!=null){//solange datei nicht zuende uebergibt er die zeilen
        			if(strLine.charAt(0)=='>'){
        				Arraylaenge++;//Ermittelt noetige Arraylaenge
        			}
        		}
        		datei.close();
        	}
        	catch (IOException e){
        		System.err.println ("Datei kann nicht gelesen werden");    //System.exit(-1);
        	}
        	String[][] Sequenzen=new String[Arraylaenge][2];//neues Array der entsprecehnden Laenge erstellen
        	try{                //Array befuellen
        		FileInputStream datei=new FileInputStream (dateiname);
        		BufferedReader rdr= new BufferedReader(new InputStreamReader(datei));
        		String strLine;
        		int Arraypos=0;
        		boolean hilf=false;
        		while ((strLine = rdr.readLine())!=null){//solange datei nicht zuende uebergibt er die zeilen
        			if(strLine.charAt(0)!='>'){
        				if(hilf==true){
        					Sequenzen[Arraypos - 1][1] = Sequenzen[Arraypos - 1][1] + strLine;
        				}
        				if(hilf==false){
        					Sequenzen[Arraypos-1][1]=strLine;
        					hilf=true;
        				}
        			}
        			if(strLine.charAt(0)=='>'){
        				Sequenzen[Arraypos][0]=strLine; 
        				Arraypos++;
        				hilf=false;
        			}
        		}
        		datei.close();
        	}
        	catch (IOException e){
        		System.err.println ("Datei kann nicht gelesen werden");  
        	}
        	return Sequenzen;
        }
//---------------------------------------PRINTROUTINEN-----------------------------------------------------
        public static void print(int[][] scores){
        	for( int i=0;i<scores.length;i++){
        		for (int j=0;j<scores[0].length;j++){
        			System.out.print(scores[i][j]);
        		}
        	}
        }
        public static void print(double[][] scores){
        	for( int i=0;i<scores.length;i++){
        		System.out.print(i+"\t");
        		for (int j=0;j<scores[0].length;j++){
        			System.out.print(scores[i][j]+"\t");
        		}
        		System.out.println(" ");
        	}
        }
		public static void printab(double[][] sequenzen, String seq){
            for(int i=0;i<seq.length();i++){
				System.out.print(seq.charAt(i)+"\t");
			}
			System.out.println();
			for( int i=0;i<sequenzen.length;i++){
				for (int j=0;j<sequenzen[0].length;j++){
					System.out.print(sequenzen[i][j]+"\t");
				}
				System.out.println("");
			}
		}
        public static void print(String[][] sequenzen){
        	for( int i=0;i<sequenzen.length;i++){
        		for (int j=0;j<sequenzen[0].length;j++){
        			System.out.print(sequenzen[i][j]+"\t");
        		}
        		System.out.println("");
        	}
        	//System.out.println("");
        }
        public static void print(int[] Seq){
        	for( int i=0;i<Seq.length;i++){
        		System.out.print(Seq[i]+" ");
        	}
        	System.out.println("");
        } 
}