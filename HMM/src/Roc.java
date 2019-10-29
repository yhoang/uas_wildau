//import java.util.*;
import java.io.*;
//import java.math.*;
/**
* Ausfuehrung: <code>java Main sequence-file Zustandszahl Praezision</code> <br>
* <br>
* Die Sequenzen werden ueber eine FASTA-aehnliche Datei eingelesen. Der Dateiname wird als erstes Argument angegeben.<br>
* Ein Parser auf Korrektheit des FASTA-Files ist nicht vorhanden.<br>
* Dabei ist zu beachten, dass Kommata nicht zulaessig sind. Ausserdem sollen Leerzeilen vermieden werden
* und die Sequenz nur aus den Nukleotiden A,T,G,C bestehen.<br>
* Beispiel:<br>
* <code>
* >Seq1<br>
* ATGAGACATGCACA<br>
* >Seq2<br>
* ATACACAGAGAGAGTACCACAG<br>
* ACATAGACAC<br>
* >Seq3<br>
* GCGTCGAT<br>
* </code>
* Als zweiter Parameter wird die Anzahl der zu verwendenden Zustaende angegeben.<br>
* Parameter 3 beschreibt die Praezision fuer die Anzahl der Iterationen des Baum-Welch-Trainings.<br>
* Beispielsweise 0.00001 fuehrt soviele Iterationen des Trainings durch, bis kein Element der
* Alpha oder Betamatrix sich um die Differenz von 0.00001 vom Wert der vorherigen Iteration unterscheidet.
*/
public class Roc {

	public static void main(String[] args) throws IOException {
		String filename = args[0];
		int nZustaende = Integer.parseInt(args[1]);
		double precision = Double.parseDouble(args[2]);
		FileInputStream file = new FileInputStream(filename);
		BufferedReader in = new BufferedReader(new InputStreamReader(file));
		String str;
		String seqs = "";

		while ((str = in.readLine()) != null) {
			if (str.matches("^>.*")) {
				seqs += ",";
			} else {
				seqs += str;
			}
		}
		seqs += ",";

		double[][] a_matrix = new double[nZustaende][nZustaende];
		double[][] b_matrix = new double[nZustaende][5];
		double[][] new_a = new double[nZustaende][nZustaende];
		double[][] new_b = new double[nZustaende][5];
		double[][] alpha = new double[nZustaende][seqs.length()];
		alpha[0][0] = 1;
		for (int i = 1; i < nZustaende; i++) {
			alpha[i][0] = 0;
		}
		double[][] beta = new double[nZustaende][seqs.length()];
		beta[0][seqs.length() - 1] = 1;
		for (int i = 1; i < nZustaende; i++) {
			beta[i][seqs.length() - 1] = 0;
		}
		double[][] vit = new double[nZustaende][seqs.length()];
		for (int i = 1; i < nZustaende; i++) {
			vit[i][0] = 0;
		}
		int[] seqchars = new int[seqs.length()];
		for (int i = 0; i < seqs.length(); i++) {
			if (seqs.charAt(i) == 'A') {
				seqchars[i] = 0;
			}
			if (seqs.charAt(i) == 'T') {
				seqchars[i] = 1;
			}
			if (seqs.charAt(i) == 'G') {
				seqchars[i] = 2;
			}
			if (seqs.charAt(i) == 'C') {
				seqchars[i] = 3;
			}
			if (seqs.charAt(i) == ',') {
				seqchars[i] = 4;
			}
		}
		for (int i = 0; i < nZustaende; i++) {
			for (int j = 0; j < nZustaende; j++) {
				if (i == j) {
					a_matrix[i][j] = 0.5;
					if (i == nZustaende - 1) {
						a_matrix[i][0] = 0.5;
					}
				} else if (j == i + 1) {
					a_matrix[i][j] = 0.5;
				}

				else {
					a_matrix[i][j] = 0;
				}
			}
		}

		for (int i = 0; i < nZustaende; i++) {
			for (int j = 0; j < 5; j++) {
				if (i == 0) {
					b_matrix[i][j] = 0;
					if (j == 4) {
						b_matrix[i][j] = 1;
					}
				} else {
					b_matrix[i][j] = 1.0 / nZustaende;
					if (j == 4) {
						b_matrix[i][j] = 0;
					}
				}

			}
		}
		print_a(a_matrix);
		int cnt = 1;
		boolean rdy = false;
		boolean rdy2 = false;
		while (!rdy || !rdy2) {
			alpha = forward(a_matrix, b_matrix, alpha, seqchars);
			beta = backward(a_matrix, b_matrix, beta, seqchars);
			new_a = train_a(a_matrix, b_matrix, alpha, beta, seqchars);
			new_b = train_b(a_matrix, b_matrix, alpha, beta, seqchars);
			rdy = check_a(a_matrix, new_a, precision);
			rdy2 = check_b(b_matrix, new_b, precision);
			a_matrix = new_a;
			b_matrix = new_b;
			cnt++;
		}
		System.out.println("***Transitionsmatrix nach "+cnt+" Iterationen Baum-Welch-Training:");
		print_a(a_matrix);
		System.out.println("***Emissionsmatrix nach "+cnt+" Iterationen Baum-Welch-Training:");
		print_b(b_matrix);
		System.out.println("***Alphamatrix aus Forwardalgorithmus nach "+cnt+ "Iterationen:");
		print_alphabeta(alpha, seqs);
		System.out.println("***Betamatrix aus Backwardalgorithmus nach "+cnt+ "Iterationen:");
		print_alphabeta(beta, seqs);
		
		int[] path = path(alpha);
		System.out.println("***Sequenz und Pfad mit maximaler Wahrscheinlichkeit:");
		for (int i = 0; i < seqs.length(); i++) {
			System.out.print(seqs.charAt(i) + " ");
		}
		System.out.println();
		for (int i = 0; i < path.length; i++) {
			System.out.print(path[i] + " ");
		}
		System.out.println();
		String[] seqarray = seqs.substring(1).split(",");

		String path2 = "";
		for (int i = 0; i < path.length; i++) {
			if (path[i] != 0) {
				path2 += path[i];
			}
		}
		String[] path3 = new String[seqarray.length];
		for (int i = 0; i < seqarray.length; i++) {
			path3[i] = path2.substring(0, seqarray[i].length());
			path2 = path2.substring(seqarray[i].length());
		}
		System.out.println("***Sequenzen und dazugehoerige Pfade:");
		for (int i = 0; i < path3.length; i++) {
			System.out.println(seqarray[i]);
			System.out.println(path3[i]);
			System.out.println();
		}
		

	}

	public static void print_a(double[][] a_matrix) {
		System.out.print("\t");
		for (int i = 0; i < a_matrix.length; i++) {
			System.out.print("Z" + i + "\t");
		}
		System.out.println();
		for (int i = 0; i < a_matrix.length; i++) {
			System.out.print("Z" + i + "\t");
			for (int j = 0; j < a_matrix[0].length; j++) {
				System.out.print(a_matrix[i][j] + "\t");
			}
			System.out.println();
		}
	}

	public static void print_b(double[][] b_matrix) {
		System.out.println("\t A\t T\t G\t C\t ,");
		for (int i = 0; i < b_matrix.length; i++) {
			System.out.print("Z" + i + "\t");
			for (int j = 0; j < b_matrix[0].length; j++) {
				System.out.print(b_matrix[i][j] + "\t");
			}
			System.out.println();
		}
	}

	public static void print_alphabeta(double[][] alphabeta, String seqs) {
		for (int i = 0; i < seqs.length(); i++) {
			System.out.print("\t" + seqs.charAt(i));
		}
		System.out.println();
		for (int i = 0; i < alphabeta.length; i++) {
			System.out.print("Z" + i + "\t");
			for (int j = 0; j < alphabeta[0].length; j++) {
				System.out.print(alphabeta[i][j] + "\t");
			}
			System.out.println();
		}
	}

	public static double[][] forward(double[][] a_matrix, double[][] b_matrix,
			double[][] alpha, int[] seqchars) {
		for (int tau = 1; tau < seqchars.length; tau++) {
			for (int j = 0; j < a_matrix.length; j++) {
				for (int i = 0; i < a_matrix.length; i++) {
					double tmp = alpha[i][tau - 1] * a_matrix[i][j]
							* b_matrix[j][seqchars[tau]];
					alpha[j][tau] += tmp;
				}
			}
		}
		return alpha;
	}

	public static double[][] backward(double[][] a_matrix, double[][] b_matrix,
			double[][] beta, int[] seqchars) {
		for (int tau = seqchars.length - 2; tau >= 0; tau--) {
			for (int j = 0; j < a_matrix.length; j++) {
				for (int k = 0; k < b_matrix.length; k++) {
					double tmp = beta[k][tau + 1] * a_matrix[j][k]
							* b_matrix[k][seqchars[tau + 1]];
					beta[j][tau] += tmp;
				}
			}
		}
		return beta;
	}

	public static double[][] train_a(double[][] a_matrix, double[][] b_matrix,
			double[][] alpha, double[][] beta, int[] seqchars) {
		double[][] new_a = new double[a_matrix.length][a_matrix[0].length];
		for (int j = 0; j < a_matrix.length; j++) {
			for (int i = 0; i < a_matrix.length; i++) {
				double tmp1 = 0;
				double tmp2 = 0;
				for (int t = 0; t <= seqchars.length - 2; t++) {
					tmp1 += alpha[i][t] * a_matrix[i][j]
							* b_matrix[j][seqchars[t + 1]] * beta[j][t + 1];
					tmp2 += alpha[i][t] * beta[i][t];
				}
				new_a[i][j] = tmp1 / tmp2;
			}

		}
		return new_a;
	}

	public static double[][] train_b(double[][] a_matrix, double[][] b_matrix,
			double[][] alpha, double[][] beta, int[] seqchars) {
		double[][] new_b = new double[b_matrix.length][b_matrix[0].length];
		for (int i = 0; i < a_matrix.length; i++) {
			for (int o = 0; o < 5; o++) {
				double tmp1 = 0;
				double tmp2 = 0;
				for (int t = 1; t < seqchars.length - 1; t++) {
					int delta = 0;
					if (seqchars[t] == o) {
						delta = 1;
					}
					tmp1 += alpha[i][t] * beta[i][t] * delta;
					tmp2 += alpha[i][t] * beta[i][t];
				}
				new_b[i][o] = tmp1 / tmp2;
			}
		}
		return new_b;
	}

	public static boolean check_a(double[][] a_matrix, double[][] new_a,
			double precision) {
		boolean rdy = true;
		for (int i = 0; i < a_matrix.length; i++) {
			for (int j = 0; j < a_matrix.length; j++) {
				if (Math.abs(a_matrix[i][j] - new_a[i][j]) > precision) {
					rdy = false;
				}
			}
		}
		return rdy;
	}

	public static boolean check_b(double[][] b_matrix, double[][] new_b,
			double precision) {
		boolean rdy = true;
		for (int i = 0; i < b_matrix.length; i++) {
			for (int j = 0; j < b_matrix[0].length; j++) {
				if (Math.abs(b_matrix[i][j] - new_b[i][j]) > precision) {
					rdy = false;
				}
			}
		}

		return rdy;
	}

	public static int[] path(double[][] alpha) {
		int[] path = new int[alpha[0].length];
		for (int j = 0; j < alpha[0].length; j++) {
			int tmp = 0;
			for (int i = 0; i < alpha.length; i++) {
				if (alpha[i][j] > alpha[tmp][j]) {
					tmp = i;
				}
			}
			path[j] = tmp;
		}
		return path;
	}
}