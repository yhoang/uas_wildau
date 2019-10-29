#!/usr/bin/perl
use strict;
open( INS, "<seq1.txt" ) || die "Datei seq1.txt nicht gefunden\n";
my @Seq1;my @Seq2;my @seq1;my @seq2;
while (<INS>) {
	push( @Seq1, $_ );
}
close(INS);
open( INS2, "<seq2.txt" ) || die "Datei seq2.txt nicht gefunden\n";
while (<INS2>) {
	push( @Seq2, $_ );
	
}
close(INS2);
@seq1 = ( ' ', split( //, $Seq1[0] ) );
@seq2 = ( ' ', split( //, $Seq2[0] ) );  
my $Penalty = 10;
my $gp      = -$Penalty;                      # gap/substituion penalty
my $fp      = $Penalty;                       # fit penalty
my $n       = 0;
my $l       = 0;
my $N       = $#seq2 - $#seq1;
my $tmp     = 10;
my $tmp2    = -100000000000000;
my $help=1;
my @amatch=("");
my @aseq1=("");my @aseq2=("");#,@merke1,@mmatch,@merke2;
my @M;my @S;my @T;
while ( $n != $N ) {
	my $t = $n + $#seq1 - 1;
	my $s = $#seq1 - 1;
	for ( my $i = 0 ; $i <= $s ; $i++ ) {
		for (my  $j = $n ; $j <= $t ; $j++ ) {
			if ( $seq1[$i] ne $seq2[$j] ) {
				$M[$i][$j] = $gp;
			}    #subsitution penalty
			else {
				$M[$i][$j] = $fp;
			}    #fit penalty
		}
	}
	my $MINSCORE = -1000000000;
	for ( my $i = 0 ; $i <= $s ; $i++ ) {
		for ( my $j = $n ; $j <= $t ; $j++ ) {
			$S[$i][$j] = $MINSCORE;
		}
	}
	my $k = 0;
	for ( my $j = $n ; $j <= $t ; $j++ ) {
		$S[0][$j] = $k * $gp + $M[0][$j];
		$T[0][$j] = -1;
		$k++;
	}

	for ( my $i = 0 ; $i <= $s ; $i++ ) {
		$S[$i][$n] = $i * $gp + $M[$i][$n];
		$T[$i][$n] = 1;
	}
	my $MaxScore   = $MINSCORE;
	my $TotalScore = 0;
	for ( my $i = 1 ; $i <= $s ; $i++ ) {
		for ( my $j = $n + 1 ; $j <= $t ; $j++ ) {
			my $sc        = $S[ $i - 1 ][ $j - 1 ] + $M[$i][$j];
			my $sc2       = $S[ $i - 1 ][ $j - 1 ] + $M[$i][$j];
			$S[$i][$j] = $sc;
			$T[$i][$j] = 0;
			$TotalScore += $sc;
			$sc = $S[ $i - 1 ][$j] + $gp;
			if ( $sc > $S[$i][$j] ) {
				$S[$i][$j]  = $sc;
				$T[$i][$j]  = 1;
				$TotalScore = $TotalScore - $sc2 + $sc;
			}
			$sc2 = $S[ $i - 1 ][$j] + $gp;
			$sc  = $S[$i][ $j - 1 ] + $gp;
			if ( $sc > $S[$i][$j] ) {
				$S[$i][$j]  = $sc;
				$T[$i][$j]  = -1;
				$TotalScore = $TotalScore - $sc2 + $sc;
			}
			if ( $S[$i][$j] > $MaxScore ) {
				$MaxScore = $S[$i][$j];
			}
		}
	}
	$a = 0;
	while ($help==1) {
		if ( $T[$s][$t] == 0 ) {
			$aseq1[$a]  = $seq1[$s];
			$amatch[$a] = '*';
			$aseq2[$a]  = $seq2[$t];
			$s--;
			$t--;
		}
		else {
			if ( $T[$s][$t] == 1 ) {
				$aseq1[$a]  = $seq1[$s];
				$amatch[$a] = ' ';
				$aseq2[$a]  = '-';
				$s--;   
			}
			else {
				if ( $T[$s][$t] == -1 ) {
					$aseq1[$a]  = '-';
					$amatch[$a] = ' ';
					$aseq2[$a]  = $seq2[$t];
					$t--;   
				}
			}
		}
		$a++;#print "s=$s t=$t\n";
		#last if ( $s == 0 && $t == $n );
		if ($s==0 && $t==$n){$help=0;}
	}
	if ( $MaxScore >= $tmp && $TotalScore >= $tmp2 ) {
		$tmp  = $MaxScore;
		$tmp2 = $TotalScore;
		$l++;
#		my @merke1;
#		my @mmatch;
#		my @merke2;
#		for ( $i = $a ; $i >= 0 ; $i-- ) {#
#			$merke1[ $a - $i ] = $aseq1[$i];
#			$mmatch[ $a - $i ] = $amatch[$i];
#			$merke2[ $a - $i ] = $aseq2[$i];
#		}
		printf "\nn=$l, maxScore= $tmp, totalScore=$tmp2, Alignment an Position $n:\n\t";
		for ( my $i = 0 ; $i <= $a ; $i++ ) {
			printf "$aseq1[$i]";
		}
		print "\n\t";
		for ( my $i = 0 ; $i <= $a ; $i++ ) {
			print "$amatch[$i]";
		}
		print "\n\t";
		for (my  $i = 0 ; $i <= $a ; $i++ ) {
			print "$aseq2[$i]";
		}
#		print "$#merke1\n";
	}
	$n++;
	$help=1;
}
