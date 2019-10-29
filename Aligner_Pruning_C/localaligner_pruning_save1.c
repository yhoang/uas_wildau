#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define MAXLENGTH_SEQ1 150
#define MAXLENGTH_SEQ2 4000
#define PENALTY 10
#define BUFFER 40
#define PRUNING 0.1

int N_seq1, N_seq2;
int fit = PENALTY, penalty = -PENALTY;
int i, j, imax, jmax;
int it, n, a;
int score, sc;
int prebuffer;
float pruning, output;

char preseq1[MAXLENGTH_SEQ1], seq1[MAXLENGTH_SEQ1], preseq2[MAXLENGTH_SEQ2],
		seq2[MAXLENGTH_SEQ2];
int M[MAXLENGTH_SEQ1][MAXLENGTH_SEQ2], S[MAXLENGTH_SEQ1][MAXLENGTH_SEQ2],
		T[MAXLENGTH_SEQ1][MAXLENGTH_SEQ2], Mind[2][MAXLENGTH_SEQ1];
char align1[MAXLENGTH_SEQ2], align2[MAXLENGTH_SEQ2], amatch[MAXLENGTH_SEQ2];

enum boolean {
	FALSE, TRUE
} help;

int main(void) {
	//---------------------------- Input ---------------------------------
	pruning=PRUNING;
	help = TRUE;
	FILE *datei1;
	FILE *datei2;
	datei1 = fopen("seq1.txt", "r");
	if (datei1 != NULL) {
		fscanf(datei1, "%150c", preseq1);
		preseq1[101] = '\0';
		fclose(datei1);
	}
	seq1[0] = ' ';
	for (i = 1; i < strlen(preseq1); i++) {
		seq1[i] = preseq1[i - 1];
	}
	datei2 = fopen("seq2.txt", "r");
	if (datei2 != NULL) {
		fscanf(datei2, "%4000c", preseq2);
		preseq2[4000] = '\0';
		fclose(datei2);
	}
	seq2[0] = ' ';
	for (i = 1; i < strlen(preseq2); i++) {
		seq2[i] = preseq2[i - 1];
	}
	N_seq1 = strlen(seq1);
	N_seq2 = strlen(seq2);
	printf("seq1:%s\nseq2:%s\n", seq1, seq2);
	printf("seq1_length=%d\tseq2_length=%d\tpruning=%f\tbuffer=%d\n", N_seq1,
			N_seq2, pruning, BUFFER);
	//------------------------ Initialising -------------------------
	for (i = 0; i < MAXLENGTH_SEQ1; i++) {
		align1[i] = align2[i] = amatch[i] = 0;
		for (j = 0; j < MAXLENGTH_SEQ2; j++) {
			M[i][j] = 0;
			S[i][j] = 0;
			T[i][j] = 0;
		}
		Mind[0][i] = 0;
		Mind[1][i] = 0;
	}
	// --------------------------- Preparations ----------------------
	for (i = 0; i < N_seq1; i++) {
		for (j = 0; j < N_seq2; j++) {
			if (seq1[i] == seq2[j]) {
				M[i][j] = fit;
			} else {
				M[i][j] = penalty;
			}
		}
	}
	score = -1000000000;
	for (i = 0; i < N_seq1; i++) {
		for (j = 0; j < N_seq2; j++) {
			S[i][j] = score;
		}
	}
	for (j = 0; j < N_seq2; j++) {
		S[0][j] = j * penalty + M[0][j];
		T[0][j] = -1;
		if (S[0][j] < 0) {
			S[0][j] = 0;
		}
	}
	for (i = 0; i < N_seq1; i++) {
		S[i][0] = i * penalty + M[i][0];
		T[i][0] = 1;
		if (S[i][0] < 0) { // only positive scores allowed!
			S[i][0] = 0;
		}
	}
	//---------------------- Alignment -------------------------
	for (i = 1; i < N_seq1; i++) {
		for (j = 1; j < N_seq2; j++) {
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
	//---------------------- Storing max(score) -------------------------
	jmax = it = 0;
	imax = N_seq1 - 1;
	for (j = 0; j < N_seq2; j++) {
		if (S[imax][j] > score) {
			score = S[imax][j];
			Mind[0][it] = j;
			Mind[1][it] = S[imax][j];
			//printf("%d %d %d\n",it,j,S[imax][j]);
			it++;
		}
	}
	output = Mind[1][it-1] - pruning*Mind[1][it-1];
	//---------------------- Printings -------------------------
	for (n = 1; n < it; n++) {
		jmax = Mind[0][it - n]; // position with best scores first
		score = Mind[1][it - n]; // score of position jmax
		a = 0; // number of alignment units
		i = imax;
		j = jmax;
		//---------------------- Storing print alignment -------------------------
		while (help == TRUE) {
			if (T[i][j] == 0) {
				align1[a] = seq1[i];
				amatch[a] = '*';
				align2[a] = seq2[j];
				i--;
				j--;
			} else if (T[i][j] == 1) {
				align1[a] = seq1[i];
				amatch[a] = ' ';
				align2[a] = '-';
				i--;
			} else if (T[i][j] == -1) {
				align1[a] = '-';
				amatch[a] = ' ';
				align2[a] = seq2[j];
				j--;
			}
			if (i == 0 && j < jmax - imax + 1) {
				help = FALSE;
			}
			a++;
		}
		//---------------------- Printing with buffer -------------------------
		printf("\nn=%d score=%d, aligning from position %d to %d:\n\n", n,
				score, j, jmax);
		prebuffer = BUFFER;
		if (j - prebuffer < 0) { //  prebuffer is too big -> set prebuffer=j
			prebuffer = j;
		}
		for (i = a + prebuffer; i > a; i--) {
			align1[i - 1] = ' ';
			amatch[i - 1] = ' ';
			align2[i - 1] = seq2[j + a - i + 1];
		}
		for (i = a + prebuffer - 1; i >= 0; i--) {
			printf("%c", align1[i]);
		}
		printf("\n");
		for (i = a + prebuffer - 1; i >=0; i--) {
			printf("%c", amatch[i]);
		}
		printf("\n");
		for (i = a + prebuffer - 1; i >= 0; i--) {
			printf("%c", align2[i]);
		}
		for (j = jmax + 1; j < jmax + BUFFER; j++) {
			printf("%c", seq2[j]);
			if (j == N_seq2) {
				break;
			}
		}
		printf("\n\n");
		help = TRUE;
		if (score <= output){
			break;
		}
	}
	//system("PAUSE");
	return 0;
}
