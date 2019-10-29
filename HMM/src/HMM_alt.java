import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
//import java.lang.StringBuffer;

public class HMM_alt {

	    public static void main(String[] args) {
	        //Einblenden um Dateiname der Wahl zu definieren
	               System.out.println("Sequenzen werden mit einem > gefolgt von der Bezeichnung eingeleitet, anschliessend Folgt die Sequenz. Es sind keine Leerzeilen erlaubt.\nEs wurde angenommen, dass ähnliche Sequenzen miteinander verglichen werden sollen,\ndaher sollten sich die Sequenzen weniger als ein Drittel in ihrer laenge voneinander unterscheiden \n(tun sie es doch, sollte entweder die kuerzste, oder aber eine Mittellange Sequenz die nicht mehr als ein Drittel kuerzer ist als die kuerzste als erstes eingegeben werden). \nDas Programm wurde mit Beispielsequenzen, die sich um wenige Basenpaare voneinander Unterschieden getestet (Seq.txt). ");
	                Scanner input=new Scanner(System.in);//einlesefkt anktiveren
	                System.out.print("Sequenzenbeinhaltende Datei eingeben (z.B. seq.txt)\n  ");
	                String dateiname = input.next();//liest eingaben und speichert in line
	               
	               // String dateiname="seq2.txt";//auskommentieren wenn Dateinaeme abgefragt werden soll
	                String[][] Sequenzen=getsequences(dateiname);
	                print(Sequenzen);
	                double[][] bMatrix=Initialmodell1(Sequenzen);
	                double[][] aMatrix=Initialmodell2(Sequenzen);
					String allseq=alleseq(Sequenzen);
					//int[] a= allsequmformen(allseq);
	                double[][] alpha=forward(alleseq(Sequenzen),aMatrix,bMatrix);// forward ausfuehren und printen
					double[][] beta=backward(alleseq(Sequenzen),aMatrix,bMatrix);// forward ausfuehren und printen
					int[] allseqnum=allsequmformen(allseq);
					double[][] aMatrix_neu=neuaMatrix(allseqnum,aMatrix,bMatrix,alpha,beta);
					double[][] bMatrix_neu=neubMatrix(allseqnum,aMatrix,bMatrix,alpha,beta);
							//Subrotuine vergleicht die uebergebenen Matrizen, bis die alte der neuen entspricht
			//Es wird der zähler help hochgezählt, wenn sich eine POsition in Beiden Matrizen gleicht, erst wenn alle Felder sich gleichen, erfolgt der Abbruch
					int iterations=0;
					 boolean ok=true;
					while(ok){
						alpha=forward(alleseq(Sequenzen),aMatrix,bMatrix);
						System.out.println();
						beta=backward(alleseq(Sequenzen),aMatrix,bMatrix);
						System.out.println();
						aMatrix_neu=neuaMatrix(allseqnum,aMatrix,bMatrix,alpha,beta);
						System.out.println();
						bMatrix_neu=neubMatrix(allseqnum,aMatrix,bMatrix,alpha,beta);
						System.out.println();
						boolean as=iterationen(aMatrix,aMatrix_neu);
						System.out.println();
						boolean bs=iterationen(bMatrix,bMatrix_neu);
						System.out.println();
						if(as&&bs==false)ok=false;
						System.out.println();
						aMatrix=aMatrix_neu;
						bMatrix=bMatrix_neu;
						iterations++;
					 }
					 
					//iterationen(aMatrix,aMatrix_neu);

					double[][]vit=Viterbi(alpha);
					System.out.println("viterbi\n");
					printab(vit,allseq);
					System.out.println("");
					System.out.println("------------Consensussequenz---------------");
					ConsensusJ(vit,allseq,Sequenzen);
					Consensus(vit,allseq,Sequenzen);
					//String temp=String.valueOf(aMatrix_neu[1][1]).substring(0,8);
					//System.out.println(temp);
					System.out.println("in "+iterations+" Iterationen");

			
	        }


		 
		public static Boolean iterationen(double[][] Matrix_neu,double[][]Matrix){//baum welch so oft wiederholen, bis sich an den ersten 4 zeichen der Werte nichts mehr ändert
			boolean ok=true;
			String a="";
			String b="";
			int help=0;
			for(int i=0;i<Matrix.length;i++){
				for(int j=0;j<Matrix[0].length;j++){
					a=String.valueOf(Matrix[i][j]);
					b=String.valueOf(Matrix_neu[i][j]);
					/*
					if(a.charAt(2)=='0'){//wenn der
							if(Matrix[i][j]==Matrix_neu[i][j])help++;
					}
					 else {*/
						if(a.length()<4){
							if(a.subSequence(0,a.length()).equals(b.subSequence(0,a.length())))help++;
							}
							else if(b.length() < 4){
							if(a.subSequence(0,b.length()).equals(b.subSequence(0,b.length())))help++;
							}

						 else{
						if(a.subSequence(0,4).equals(b.subSequence(0,4)))help++;}
//						System.out.println("ah=bh");
					//System.out.println("ValueoF!!"+String.valueOf(Matrix[i][j])+"\t neu "+String.valueOf(Matrix_neu[i][j]));
					//}

				
				}
			}
			//System.out.println(help);
			if(help==(Matrix.length*Matrix[1].length))ok=false;
			return ok;
		}
		public static void ConsensusJ(double[][] vit,String allseq, String[][]Sequenzen){
			String zust="";
			String[][] allzust=new String[Sequenzen.length][2];
			int helf=0;
			for(int i=0;i<vit[0].length-1;i++){//Spalten der Viterbi Matrix durchfahren
				if(allseq.charAt(i)==','){
					System.out.println(zust+"\n"+Sequenzen[(helf)][1]);
					if(helf>0)allzust[helf-1][0]=zust;
					allzust[helf][1]=Sequenzen[helf][1];
					i++;//Wenn , dann i erhoehen, weil , nicht interessant ist
					helf++;//neue Sequenz betrachten
					zust="";
					}
				if(i<vit[0].length){		//weil i auf mehr als die länge der matrix erhöht wird
					for(int j=0;j<vit.length;j++){//Zeilen der Viterbi Matrix durchfahren
					//System.out.println(i+" "+j);
					if(vit[j][i]==1){
						//System.out.println(j);
						zust+=j;
							}
						}
					}
			}
			System.out.println(zust);
			allzust[helf-1][0]=zust;
			//print(allzust);
			//String Consensus="";
			String[][] allzust_neu=new String[allzust.length][allzust[0].length];

			//System.out.println("\n");
			//print(allzust);
			
			String[] zwischen=new String[allzust.length];
			for(int i=0;i<allzust.length;i++){
				zwischen[i]=allzust[i][1];
			}
			Arrays.sort(zwischen);//), new StringLengthComparator());//Array Sortieren
			//----------------------------------------> Sotiert leider Alphabetisch, statt nach laenge, wuerde er korrekt nach laenge sorierten, koennte man zumindest eine konsensussequenz ohne gaps erzegen
			//print(zwischen);
			for(int i=0;i<allzust.length;i++){
				//System.out.println(zwischen[i].length());
				//System.out.println(i);
				for(int j=0;j<allzust.length;j++){
					if(zwischen[i].equals(allzust[j][1]))
					{
					allzust_neu[i][0]=allzust[j][0];
					allzust_neu[i][1]=allzust[j][1];
					}
				}
			
			}
			//System.out.println();
			//print(allzust_neu);

			//Consensus=seqermitteln(Consensus,allzust_neu,allzust,0,0);
			//System.out.println("!hier"+allzust_neu[allzust_neu.length-1][1].length());
			//System.out.println(Consensus);
		}
		
		public static String seqermitteln(String Consensus,String[][] allzust_neu,String[][] allzust, int anfang, int welche){

			String untersuch="";
			//System.out.println("allzust_neu[welche][0].length: "+allzust_neu[welche][0].length()+" anfang "+anfang+" allzust_neu.length "+allzust_neu.length+" welche "+ welche);
			//System.out.println(allzust_neu[2][0].length());
			for(int i=anfang;i<allzust_neu[welche][0].length();i++){
				for(int j=welche;j<allzust_neu.length;j++){
					System.out.println("j "+j);
					untersuch+=allzust_neu[j][1].charAt(i);

				}
				int big=0;
				int buchst=0;
				int[]x=Anzahl(untersuch);
				for(int j=0;j<x.length;j++){
					if(x[j]>big){
						big=x[j];
						buchst=j;
					}
				}
				if(buchst==0)Consensus+='A';
					if(buchst==1)Consensus+='T';
					if(buchst==2)Consensus+='G';
					if(buchst==3)Consensus+='C';
				//System.out.println(big);
				//print(Anzahl(untersuch));
				untersuch="";
				
				anfang++;
			}
			welche++;
			//System.out.println("anfang"+anfang);
			//System.out.println(anfang+"\t"+welche);
			if(anfang<allzust_neu[allzust_neu.length-1][1].length())Consensus=seqermitteln(Consensus,allzust_neu,allzust,anfang,welche);
			return Consensus;
		}


	public static void Consensus(double[][] vit,String allseq, String[][]Sequenzen){
		int i=0;int s=4; //s Zustaende
		int k=0;
		int[][] consens=new int[s][vit[0].length];
		//int[][] laeng=new int[4][Sequenzen.length];
		while (i<vit[0].length){//Alle Zeilen der Matrix durchfahren
			
			for (int j=1;j<vit.length;j++){
				if(vit[j][i]==1.0){
					consens[j][k]++;
					//System.out.print("c["+j+"]["+k+"]="+consens[j][k]+" ");
				}
              /*  for (int l=0; l<allseq.length()-1; l++){  //ATGC zaehlen
                    if (allseq.charAt(l)=='A'){laeng[0][k]++;}
                    if (allseq.charAt(l)=='T'){laeng[1][k]++;}
                    if (allseq.charAt(l)=='G'){laeng[2][k]++;}
                    if (allseq.charAt(l)=='C'){laeng[3][k]++;}
                }*/
			}if(allseq.charAt(i)==','){k++;}
			i++;//System.out.println("");
			
		}
		int[] big=new int [s];
		for (int m=0;m<s;m++){
			//System.out.print("m="+m);
			for (int n=0;n<k;n++){
				//System.out.println(" n="+m+" big="+big[m]+" cons="+consens[m][n]);
				if (consens[m][n]>big[m]){
					big[m]=consens[m][n];
					//System.out.println("");
					//System.out.print(m+" "+n+" "+big[m]+" "+consens[m][n]);
				}
			}
		}
		System.out.println("");
		
		int m=1;
		while (m<vit[0].length-1){//Alle Zeilen der Matrix durchfahren
			//if(allseq.charAt(m)==','){m++;System.out.println("m wird hochgesetzt wegen ',',m="+m);}
			int q=1;
			int j=1;
			while(j<vit.length){
				while (q<vit.length){
				//System.out.print("j="+j+" q="+q);
				//System.out.print(" q="+q+" cons="+consens[j][q]+" big="+big[q]+" ");
				//System.out.println("con="+consens[j][q]+" big"+big[q]+" ");
				if (consens[j][q]<big[q]){
					//System.out.print(" j="+j);
					//System.out.print(" q2="+q+" "+"con["+j+"]["+q+"]="+consens[j][q+1]+" big"+big[q]);
					int a=big[q]-consens[j][q];
					for (int u=0;u<consens[j][q];u++){
						//System.out.print(" a="+a+" u2="+u+" ");
						if(m==vit[0].length-1){}
						else{
						System.out.print(allseq.charAt(m));
						m++;//System.out.println("m wird hochgesetzt wegen N,m="+m);
						if(allseq.charAt(m)==','){m++;System.out.println("");
						j++;if(j==4){j=1;}}//System.out.println("m wird hochgesetzt wegen ',',m="+m)}
					}}
					for (int u2=0;u2<a;u2++){
						System.out.print("-");
						consens[j][q+1]++;
					}
				}
				else{//System.out.print(" q1="+q+" ");
					//System.out.print(" j="+j+" ");
					//if(allseq.charAt(m)==','){m++;System.out.println("m wird hochgesetzt wegen ',',m="+m);}
					for (int u=0;u<consens[j][q];u++){if(m==vit[0].length-1){}
					else{
						//System.out.print(" u1="+u);
						System.out.print(allseq.charAt(m));m++;//System.out.println("m wird hochgesetzt wegen N,m="+m);
						if(allseq.charAt(m)==','){m++;System.out.println("");j++;if(j==4){j=1;}}//System.out.println("m wird hochgesetzt wegen ',',m="+m);}
										}
					//j++;if(j==4){j=1;}
					}}q++;
			}
			if(q==big.length){j=100000;}	
			}	//System.out.println("");
		}
              /*  for (int l=0; l<allseq.length()-1; l++){  //ATGC zaehlen
                    if (allseq.charAt(l)=='A'){laeng[0][k]++;}
                    if (allseq.charAt(l)=='T'){laeng[1][k]++;}
                    if (allseq.charAt(l)=='G'){laeng[2][k]++;}
                    if (allseq.charAt(l)=='C'){laeng[3][k]++;}
                }
			}*/
			//if(allseq.charAt(m)==','){n++;}
			
//		}
	}

	public static double [][] Viterbi(double[][]Viterbi){
		double biggest=0;
		for(int i=0;i<Viterbi[0].length;i++){//Spalten durchfahren
			for(int j=0;j<Viterbi.length;j++){//Spalte untersuchen
				if(Viterbi[j][i]>biggest)biggest=Viterbi[j][i];
			}
			for(int j=0;j<Viterbi.length;j++){//Spalte untersuchen
				if(Viterbi[j][i]==biggest)Viterbi[j][i]=1;
				else Viterbi[j][i]=0;
			}
			biggest=0;
		}
		System.out.println("---------------------------Viterbi Alignment----------------------------------");
		return Viterbi;
	}


    public static double[][] neuaMatrix(int[] seq, double[][] a,double[][] b,double[][] alpha,double[][] beta){
        int s=4;   //zustaende
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
        System.out.println();
        System.out.println("neue aMatrix:");
        print(aMatrix_neu);
        System.out.println("");
        return(aMatrix_neu);
    }

	    public static double[][] neubMatrix(int[] seq, double[][] a,double[][] b,double[][] alpha,double[][] beta){
            int s=4; //zustaende
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
    System.out.println("neue bMatrix:");
    print(bMatrix_neu);
    System.out.println("");
    return(bMatrix_neu);
    }


    public static double[][] backward(String seq,double[][] a, double[][] b){
                int[]allseqnum=allsequmformen(seq);
                int count=0;
                int it=1; //iterationen
                double [][] beta=new double[4][seq.length()];
                beta[0][seq.length()-1]=1;
                        while(count < it) {
                        System.out.println("------------------------backward-----------------beta-------------------------");
                        count++;
                        for(int t=seq.length()-2;t>=0;t--){
                               // System.out.print(seq.charAt(t+1)+"\t");
                                for(int j=0;j<4;j++){
                                        for(int k=0;k<4;k++){
                                                double merk=beta[k][t+1]*a[j][k]*b[k][allseqnum[t+1]];
                                                beta[j][t]+=merk;
                                        }
                                }
                        }
                        printab(beta,seq);
                }
                return beta;
        }
          public static double[][] forward(String seq,double[][] a, double[][] b){
                int[]allseqnum=allsequmformen(seq);
                int count=0;
                int it=1; //iterationen
                double [][] alpha=new double[4][seq.length()];
                alpha[0][0]=1;
                        while (count < it) {
                        System.out.println("------------------------forward------------------alpha-------------------------");
                        count++;
                        for(int t=1;t<seq.length();t++){
								for(int j=0;j<4;j++){
                                        for(int i=0;i<4;i++){
                                                double merk=alpha[i][t-1]*a[i][j]*b[j][allseqnum[t]];
                                                alpha[j][t]+=merk;
                                        }
                                }
                        }//double[][] alpha=new double[4]
                        printab(alpha,seq);
                }                
                return alpha;
        }

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

        public static double[][] Initialmodell2(String[][] Sequenzen){    //a-Matrix
                int Abschnitt=(Sequenzen[0][1].length()/3);//Ermitteln der AbschnittslÃ¤ngen
                double[][] aMatrix=new double[4][4];
                double letztes=0;
                for(int i=0;i<Sequenzen.length;i++){
                        letztes=letztes+Sequenzen[i][1].substring((2)*Abschnitt).length();
                }
                double a=Abschnitt;
                double b=Sequenzen.length;
        //        int j=0;
                aMatrix[0][1]=1;
                aMatrix[0][0]=aMatrix[0][2]=aMatrix[0][3]=aMatrix[1][0]=aMatrix[2][0]=aMatrix[1][3]=aMatrix[2][1]=aMatrix[3][1]=aMatrix[3][2]=0;
                aMatrix[1][1]=(a*b-b)/(a*b);
                aMatrix[1][2]=1-(a*b-b)/(a*b);
                aMatrix[2][2]=(a*b-b)/(a*b);
                aMatrix[2][3]=1-(a*b-b)/(a*b);
                aMatrix[3][3]=(letztes*b-b)/(letztes*b);
                aMatrix[3][0]=1-(letztes*b-b)/(letztes*b);
                System.out.println("\naMatrix");
                System.out.println("0\t1\t2\t3");
                print(aMatrix);System.out.println();
                return(aMatrix);
                }

        public static double [][] Initialmodell1(String[][] Sequenzen){//B Matrix berechnen durch Splitten der Sequenz in 3 Abschnitte.
                int Abschnitt=(Sequenzen[0][1].length()/3);//Ermitteln der AbschnittslÃ¤ngen
                double [][] bMatrix=new double[4][5];
                int[] ATGCs=new int[4];
                int zustaende=4;//zustaende=3+1(0)
                bMatrix[0][4]=1;        //Bmatrix Anzanhl der ATGCs in den Zustaenden ermitteln
                for(int j=1;j<zustaende-1;j++){//alle zustaende durchfahren
                        for(int i=0;i<Sequenzen.length;i++){//alle SEq nacheiandner betrachtetn
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
                                bMatrix[i][j]=bMatrix[i][j]/Zeile;//ATGCs d
                        }
                        Zeile=0;
                }                                                                //bMatrix zur Kontrolle ausgeben
                System.out.println("bMatrix");
                System.out.println("A\tT\tG\tC\t,");
                print(bMatrix);
                return bMatrix;
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
                        //int        Arraylaenge=0;
                        while ((strLine = rdr.readLine())!=null){//solange datei nicht zuende Ã¼bergibt er die zeilen
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
                        //System.out.println(new DataInputStream(datei).readLine() );
                        int Arraypos=0;
                        boolean hilf=false;
                        while ((strLine = rdr.readLine())!=null){//solange datei nicht zuende Ã¼bergibt er die zeilen
                                if(strLine.charAt(0)!='>'){
                                        //System.out.println(strLine+""+Arraypos);
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
                                        //System.out.println(Sequenzen[Arraypos][1]+""+Arraypos+""+strLine);
                                        Arraypos++;
                                        hilf=false;
                                } //System.out.println(Arraypos+" "+strLine);
                        }
                datei.close();
                }
                catch (IOException e){
                        System.err.println ("Datei kann nicht gelesen werden");    //System.exit(-1);
                }
                return Sequenzen;
        }

//---------------------------------------PRINTROUTINEN-----------------------------------------------------

        public static void print(int[][] scores){
                for( int i=0;i<scores.length;i++){
                        for (int j=0;j<scores[0].length;j++){
                                System.out.print(scores[i][j]);
                        }
                        System.out.println("");
                }
        }

        public static void print(double[][] scores){
                for( int i=0;i<scores.length;i++){
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
                System.out.println("");
        }

        public static void print(int[] Seq){
                for( int i=0;i<Seq.length;i++){
                                System.out.print(Seq[i]+" ");
                }
                                System.out.println("");
        } 
}