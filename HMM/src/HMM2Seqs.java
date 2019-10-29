import java.io.*;
public class HMM2Seqs {
	    public static void main(String[] args) throws IOException {
	    	String filename1 = args[0];
	    	String filename2 = args[1];
			int zustaende = Integer.parseInt(args[2]);
			double precision = Double.parseDouble(args[3]);
			String[][] Sequenzen=getsequences(filename1,filename2);
	    	print(Sequenzen);
	    	double[][] aMatrix=Initialmodella(Sequenzen,zustaende);
	    	double[][] bMatrix=Initialmodellb(Sequenzen,zustaende);
	    	double[][] alpha;double[][] beta;double[][] aMatrix_neu;double[][] bMatrix_neu;
	    	String allseq=alleseq(Sequenzen);
	    	int[] allseqnum=allsequmformen(allseq);
	    	int iterations=1;
	    	boolean ok=true;
	    	while(ok){
	    		alpha=forward(allseq,allseqnum,zustaende,aMatrix,bMatrix);
	    		beta=backward(allseq,allseqnum,zustaende,aMatrix,bMatrix);
	    		aMatrix_neu=neuaMatrix(allseqnum,zustaende,aMatrix,bMatrix,alpha,beta);
	    		bMatrix_neu=neubMatrix(allseqnum,zustaende,aMatrix,bMatrix,alpha,beta);
	    		boolean as=iterationen(aMatrix,aMatrix_neu,precision);
	    		boolean bs=iterationen(bMatrix,bMatrix_neu,precision);
	    		System.out.println("++++++++++++++++++++++++++++++++++++++ nach "+iterations+" Iterationen ++++++++++++++++++++++++++++++++++++++");
	    		if(as&&bs==false){
	    			ok=false;
	    			System.out.println();
	    			System.out.println("++++++++++++++++++++++++++++++++++++++ nach "+iterations+" Iterationen ++++++++++++++++++++++++++++++++++++++");
	    			System.out.println(">>>>>>> Matrix a:");
	    			for (int i=0;i<zustaende;i++){
	                	System.out.print("\t"+i);
	                }
	                System.out.println();
	    			print(aMatrix_neu);
	    			System.out.println(">>>>>>> Matrix b:");
	                System.out.println("\tA\tT\tG\tC\t,");
	                print(bMatrix_neu);
	    		}
	    		aMatrix=aMatrix_neu;
	    		bMatrix=bMatrix_neu;
	    		iterations++;
	    	}
		}
//--------------------------------EINLESEN--------------------------------//
        public static String[][] getsequences (String file1,String file2){
        	String[][] Sequenzen=new String[2][2];
        	try{        
        		FileInputStream datei1=new FileInputStream (file1);
        		BufferedReader rdr1= new BufferedReader(new InputStreamReader(datei1));
        		String strLine1;int i=0;
        		while ((strLine1 = rdr1.readLine())!=null){//solange datei nicht zuende, uebergibt er die zeilen
        			if (strLine1.matches("^>.*")) {
        				Sequenzen[0][0]=strLine1; 
        			}
        			if (strLine1.matches("^A.*") || strLine1.matches("^C.*") || strLine1.matches("^T.*") || strLine1.matches("^G.*")) {
        				if(i==0){
        					Sequenzen[0][1]=strLine1;i=1;
        				}
        				else{Sequenzen[0][1]+=strLine1;}
        			}
        		}
        		datei1.close();
        		FileInputStream datei2=new FileInputStream (file2);
        		BufferedReader rdr2= new BufferedReader(new InputStreamReader(datei2));
        		String strLine2;i=0;
        		while ((strLine2 = rdr2.readLine())!=null){
        			if (strLine2.matches("^>.*")) {
        				Sequenzen[1][0]=strLine2; 
        			}
        			if (strLine2.matches("^A.*") || strLine2.matches("^C.*") || strLine2.matches("^T.*") || strLine2.matches("^G.*")) {
        				if(i==0){
        					Sequenzen[1][1]=strLine2;i=1;
        				}
        				else{Sequenzen[1][1]+=strLine2;}
        			}
        		}
        		datei2.close();
        	}
        	catch (IOException e){
        		System.err.println ("Dateien können nicht gelesen werden");  
        	}
        	return Sequenzen;
        }
		public static boolean iterationen (double[][] Matrix,double[][] Matrix_neu,double precision){
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
		public static double[][] forward (String seq,int[] allseqnum,int s,double[][] a, double[][] b){
        	int count=0;
        	int it=1; //iterationen
        	double [][] alpha=new double[s+1][seq.length()];
        	alpha[0][0]=1;
        	while (count < it) {
        		System.out.println("----------------------------- forward------------------alpha -----------------------------");
        		count++;
        		for(int t=1;t<seq.length();t++){
        			for(int j=0;j<s+1;j++){
        				for(int i=0;i<s+1;i++){
        					double merk=alpha[i][t-1]*a[i][j]*b[j][allseqnum[t]];
        					alpha[j][t]+=merk;
        				}
        			}
        		}
        		//printab(alpha,seq);
        	}                
        	return alpha;
        }
		public static double[][] backward (String seq,int[] allseqnum,int s,double[][] a, double[][] b){
	    	int count=0;
	    	int it=1; //iterationen
	    	double [][] beta=new double[s+1][seq.length()];
	    	beta[0][seq.length()-1]=1;
	    	while(count < it) {
	    		System.out.println("----------------------------- backward-----------------beta ------------------------------");
	    		count++;
	    		for(int t=seq.length()-2;t>=0;t--){
	    			for(int j=0;j<s+1;j++){
	    				for(int k=0;k<s+1;k++){
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
		public static double[][] Initialmodella(String[][] Sequenzen,int s){    //a-Matrix
        	int Abschnitt=(Sequenzen[0][1].length()/s);//Ermitteln der Abschnittslaengen
        	double[][] aMatrix=new double[s+1][s+1];
        	double letztes=0;
        	for(int i=0;i<Sequenzen.length;i++){
        		letztes=letztes+Sequenzen[i][1].substring((2)*Abschnitt).length();
        	}
        	double a=Abschnitt;
        	double b=Sequenzen.length;
        	for (int i=0;i<aMatrix.length;i++){
        		for (int j=0;j<aMatrix[1].length;j++){
        			aMatrix[i][j]=0;
        		}
        	}
            aMatrix[0][1]=1;
            for (int i=1;i<aMatrix.length-1;i++){
            	aMatrix[i][i]=(a*b-b)/(a*b);
            	aMatrix[i][i+1]=1-(a*b-b)/(a*b);
            }
            aMatrix[s][s]=(letztes*b-b)/(letztes*b);
            aMatrix[s][0]=1-(letztes*b-b)/(letztes*b);
            System.out.println("\nMatrix a:");
            for (int i=0;i<=s;i++){
            	System.out.print("\t"+i);
            }
            System.out.println();
            print(aMatrix);
            return(aMatrix);
        }
		public static double [][] Initialmodellb(String[][] Sequenzen,int s){//B Matrix berechnen durch Splitten der Sequenz in 3 Abschnitte.
        	int Abschnitt=(Sequenzen[0][1].length()/s);//Ermitteln der Abschnittslaengen
        	int zustaende=s+1;//zustaende=3+1(0)
        	double [][] bMatrix=new double[zustaende][5];
        	int[] ATGCs=new int[4];
        	bMatrix[0][4]=1;        //bmatrix Anzahl der ATGCs in den Zustaenden ermitteln
        	for(int j=1;j<zustaende-1;j++){//alle zustaende durchfahren
        		for(int i=0;i<Sequenzen.length;i++){//alle Seq nacheinander betrachten
        			ATGCs=(Anzahl(Sequenzen[i][1].substring((j-1)*Abschnitt,(j)*Abschnitt)));//Anzahl =ATGC zaehlende Subroutine
        			bMatrix[j][0]=bMatrix[j][0]+ATGCs[0];
        			bMatrix[j][1]=bMatrix[j][1]+ATGCs[1];
        			bMatrix[j][2]=bMatrix[j][2]+ATGCs[2];
        			bMatrix[j][3]=bMatrix[j][3]+ATGCs[3];
        		}
        	}
        	for(int i=0;i<Sequenzen.length;i++){
        		ATGCs=(Anzahl(Sequenzen[i][1].substring((zustaende-2)*Abschnitt)));
        		bMatrix[zustaende-1][0]+=ATGCs[0];
        		bMatrix[zustaende-1][1]+=ATGCs[1];
        		bMatrix[zustaende-1][2]+=ATGCs[2];
        		bMatrix[zustaende-1][3]+=ATGCs[3];
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
        	System.out.println("Matrix b:");
        	System.out.println("\tA\tT\tG\tC\t,");
        	print(bMatrix);
        	return bMatrix;
        }
        public static double[][] neuaMatrix (int[] seq,int s,double[][] a,double[][] b,double[][] alpha,double[][] beta){
			double[][] aMatrix_neu= new double [s+1][s+1];
			for (int j=0; j<s+1;j++){
				for(int i=0; i<s+1;i++){
					double merkn=0;
					double merkz=0;
					for (int t=0;t<seq.length-1;t++){
						merkz+=alpha[i][t]*a[i][j]*b[j][seq[t+1]]*beta[j][t+1];
						merkn+=alpha[i][t]*beta[i][t];
						//System.out.print("merkz= "+merkz+",merkn= "+merkn);
						//System.out.println(",alpha["+i+"]["+t+"]="+alpha[i][j]+"*a["+i+"]["+j+"]="+a[i][j]+"*b["+j+"]["+seq[t+1]+"]="+b[j][seq[t+1]]);
					}
					aMatrix_neu[i][j]=merkz/merkn;
					//System.out.println("merkz= "+merkz+",merkn= "+merkn+"aMatrix_neu["+i+"]["+j+"]= "+aMatrix_neu[i][j]);
				}
			}
			System.out.println(">>>>>>> Matrix a:");
			for (int i=0;i<=s;i++){
            	System.out.print("\t"+i);
            }
            System.out.println();
			print(aMatrix_neu);
			return(aMatrix_neu);
		}
	    public static double[][] neubMatrix (int[] seq,int s,double[][] a,double[][] b,double[][] alpha,double[][] beta){
            double[][] bMatrix_neu=new double[s+1][5];
             for(int o=0;o<5;o++){
            	 for(int i=0;i<s+1;i++){
            		 double merkn=0;
            		 double merkz=0;
            		 for(int t=1;t<seq.length;t++){
            			 int delta=0;
            			 if(seq[t]==o){
            				 delta=1;
            			 }
            			 merkz += alpha[i][t]*beta[i][t]*delta;
            			 merkn += alpha[i][t]*beta[i][t];
            		 }
            		 bMatrix_neu[i][o]=merkz/merkn;
            	 }
            }
             System.out.println(">>>>>>> Matrix b:");
             System.out.println("\tA\tT\tG\tC\t,");
             print(bMatrix_neu);
             return(bMatrix_neu);
	    }
//-----------------------------ZAEHLEN,UMFORMEN----------------------------//
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
        public static String alleseq (String[][] seq){ //alle Sequenzen in einen String packen
        	String allseq=",";
        	for(int i=0;i<seq.length;i++){
        		allseq=allseq+seq[i][1]+",";
        	}
        	//System.out.println("allseq="+allseq);
        	return allseq;
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
        }
        public static void print(int[] Seq){
        	for( int i=0;i<Seq.length;i++){
        		System.out.print(Seq[i]+" ");
        	}
        	System.out.println("");
        } 
}