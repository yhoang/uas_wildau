import java.util.Arrays;

public class Aligner {
	private String seq1 = null;
	private String seq2 = null;
	private static String[] binaer = new String[5];
	private double pruning = 0.1;
	private int[] j_merk = new int[5];
	private int penalty = -4;
	private int buffer = 1000000;
	private static String printer = null;
	public void set_S1(String sequence) {
		this.seq1 = sequence;
	}
	public void set_S2(String sequence) {
		this.seq2 = sequence;
	}
	public String get_S1() {
		return seq1;
	}
	public String get_S2() {
		return seq2;
	}
	public static String[] get_binary() {
		for (int i = 0; i < binaer.length; i++) {
			if (binaer[i] == null) {
				binaer[i] = "0";
			}
		}
		return binaer;
	}
	public static String getTextOut(){
		return printer;
	}
	public void switchSequences(){
		System.out.println("switch!");
		seq1=new String(seq2);
		seq2=new String(seq1);
	}
	public Aligner(String seq1, String seq2, int[][] matrix, String Nucs) {
		if(seq1.length()>seq2.length()){
			String tmp=new String(seq1);
			seq1=new String(seq2);
			seq2=new String(tmp);
		}
		int N_seq1 = seq1.length(), N_seq2 = seq2.length();
		int it = 0, a, score = 0, sc = 0;
		int ia, ja, imax, jmax;
		double output;
		submatrix PAM = new submatrix(matrix, Nucs);
		int prebuffer;
		boolean help = true;
		int[][] M, S, T, Mind;
		int[] S_sort;
		char[] align1, align2, amatch;
		M = new int[N_seq1][N_seq2];
		S = new int[N_seq1][N_seq2];
		S_sort = new int[N_seq2];
		T = new int[N_seq1][N_seq2];
		Mind = new int[2][N_seq2];
		align1 = new char[2 * N_seq2];
		align2 = new char[2 * N_seq2];
		amatch = new char[2 * N_seq2];
		// ---------------------------- Preparations ---------------------------
		M[0][0] = 5;
		for (int i = 1; i < N_seq1; i++) {
			for (int j = 1; j < N_seq2; j++) {
				M[i][j] = PAM.get_Item(seq1.charAt(i), seq2.charAt(j));
			}
		}
		score = -10000000;
		for (int i = 0; i < N_seq1; i++) {
			for (int j = 0; j < N_seq2; j++) {
				S[i][j] = score;
			}
		}
		for (int j = 0; j < N_seq2; j++) {
			S[0][j] = j * penalty + M[0][j];
			T[0][j] = -1;
			if (S[0][j] < 0) { // only positive scores allowed!
				S[0][j] = 0;
			}
		}
		for (int i = 0; i < N_seq1; i++) {
			S[i][0] = i * penalty + M[i][0];
			T[i][0] = 1;
			if (S[i][0] < 0) { // only positive scores allowed!
				S[i][0] = 0;
			}
		}
		// ---------------------------- Alignment ---------------------------
		for (int i = 1; i < N_seq1; i++) {
			for (int j = 1; j < N_seq2; j++) {
				sc = S[i - 1][j - 1] + M[i][j];
				S[i][j] = sc;
				T[i][j] = 0;
				sc = S[i - 1][j] + penalty;
				if (sc > S[i][j]) {
					S[i][j] = sc;
					T[i][j] = 1;
				}
				sc = S[i][j - 1] + penalty;
				if (sc > S[i][j]) {
					S[i][j] = sc;
					T[i][j] = -1;
				}
				if (S[i][j] < 0) { // only positive scores allowed!
					S[i][j] = 0;
				}
			}
		}
		// ------------------------ Storing max(score) ---------------------
		imax = N_seq1 - 1;
		for (int i = 0; i < N_seq2 - 1; i++) {
			S_sort[i] = S[imax][i];
		}
		Arrays.sort(S_sort);
		output = S_sort[N_seq2 - 1] - pruning * S_sort[N_seq2 - 1];
		for (int j_sort = N_seq2 - 1; j_sort > 0; j_sort--) {
			if (S_sort[j_sort] < output) {
				break;
			} else if (S_sort[j_sort] == S_sort[j_sort - 1]) {
				continue;
			} else {
				for (int j = 0; j <= N_seq2 - 1; j++) {
					if (S[imax][j] == S_sort[j_sort]) {
						Mind[0][it] = j;
						Mind[1][it] = S[imax][j];
						it++;
					}
				}
			}
		}
		char mark = ' ';
		if (it > 5)
			it = 5;
		int tmp_it = 0;
		int k = 0;
		int[] tmp = new int[N_seq2 + (N_seq2 / 10)];
		printer = "\n";
		// ------------------------ Print ---------------------------
		for (int n = 0; n < it; n++) {
			jmax = Mind[0][n]; // position with best scores first
			score = Mind[1][n]; // score of position jmax
			a = 0;
			ia = imax;
			ja = jmax;
			for (int i = N_seq2; i > jmax; i--) {
				tmp[tmp_it] = 0;
				tmp_it++;
			}
			// ------------- Storing print alignment -------------
			while (help == true) {
				if (T[ia][ja] == 0) {
					if (seq1.charAt(ia) == seq2.charAt(ja)) {
						mark = ':';
						tmp[tmp_it] = 1;
					} else {
						mark = '*';
						tmp[tmp_it] = 0;
					}
					align1[a] = seq1.charAt(ia);
					amatch[a] = mark;
					align2[a] = seq2.charAt(ja);
					ia--;
					ja--;
				} else if (T[ia][ja] == 1) {
					align1[a] = seq1.charAt(ia);
					amatch[a] = ' ';
					align2[a] = '-';
					ia--;
					tmp[tmp_it] = 0;
				} else if (T[ia][ja] == -1) {
					align1[a] = '-';
					amatch[a] = ' ';
					align2[a] = seq2.charAt(ja);
					ja--;
					tmp[tmp_it] = 0;
				}
				if (ia == 0 || ja == 0) {
					help = false;
					j_merk[n] = ja;
				}
				a++;
				tmp_it++;
			}
			for (int i = 0; i < n; i++) {
				if (ja == j_merk[i])
					help = true;
			}
			if (help == false) {
				for (int i = ja; i > 0; i--) {
					tmp[tmp_it] = 0;
					tmp_it++;
				}
				int[] tmp2 = new int[tmp_it];
				for (int i = tmp_it - 1; i > 0; i--) {
					tmp2[tmp_it - i - 1] = tmp[i];
				}
				binaer[k] = Arrays.toString(tmp2).replace(", ", "")
						.replace("[", "").replace("]", "");
				k++;
				// ---------------------- Printing with buffer ----------------
				prebuffer = buffer;
				if (ja - prebuffer < 0) {// prebuffer is too big -> set
											// prebuffer=j
					prebuffer = ja;
				}
				for (int i = a + prebuffer; i > a; i--) {
					align1[i - 1] = ' ';
					amatch[i - 1] = ' ';
					align2[i - 1] = seq2.charAt(ja + a - i + 1);
				}
				printer = printc2(align2, seq2, a, prebuffer, jmax, buffer,
						N_seq2, printer);
				printer = printc(amatch, a, prebuffer, printer);
				printer = printc(align1, a, prebuffer, printer);
				printer += "\n";
			}
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = 0;
			}
			help = true;
			tmp_it = 0;
		}
	}

	// --------------------------- Printsubs -----------------------------
	public static String printc2(char align[], String seq, int a,
			int prebuffer, int jmax, int buffer, int end, String printer) {
		for (int i = a + prebuffer - 1; i >= 0; i--) {
			printer += align[i];
		}
		for (int j = jmax + 1; j < jmax + buffer; j++) {
			if (j == end) {
				break;
			}
			printer += seq.charAt(j);
		}
		printer += "\n";
		return (printer);
	}

	public static String printc(char align[], int a, int prebuffer,
			String printer) {
		for (int i = a + prebuffer - 1; i >= 0; i--) {
			printer += align[i];
		}
		printer += "\n";
		return (printer);
	}

	public static String print(String seq, String printer) {
		for (int i = 0; i < seq.length(); i++) {
			printer += seq.charAt(i);
		}
		printer += "\n";
		return (printer);
	}

	public static void prints(String[][] seq) {
		for (int i = 0; i < seq.length; i++) {
		}
	}
}

class submatrix {
	private int[][] matrix;
	private String seq1;

	public submatrix(int[][] matrix, String seq1) {
		this.matrix = matrix;
		this.seq1 = seq1;
	}

	public int get_Item(char A, char B) {
		int v1 = seq1.indexOf(A);
		int v2 = seq1.indexOf(B);
		return matrix[v1][v2];
	}
}