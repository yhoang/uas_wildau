#!/usr/bin/perl
#open (FILE, "<seq1.txt") or die "Can't open first File!\n";
#while ($line1 = <FILE>) {
#	chomp ($line1);
#	@seq1 = (split( //, $line1));
#	print "@seq1\n";
#}
#close(FILE);
#@seqq=split( //,@seq1);
#open (FILE, "<seq2.txt") or die "Can't open second File!\n";
#while ($line2 = <FILE>) {
#	chomp ($line2);
#	@seq2 = split( //, $line2);
#	print "@seq2\n";
#}
#close(FILE);
$Seq1 = $ARGV[0];
$Seq2 = $ARGV[1];
@seq1 = ( split( //, $Seq1 ) );
@seq2 = ( split( //, $Seq2 ) );
##############Sequenzen printen###########################
#for ( $i = 0 ; $i <= $#seq1 ; $i++ ) { printf "$seq1[$i] "; }
#printf "\n";
#for ( $i = 0 ; $i <= $#seq2 ; $i++ ) { printf "$seq2[$i] "; }
#printf "\nseq1laenge: $#seq+1 \t seq2laenge: $#seq2+1\n";
$Penalty = 10;
$gp      = -$Penalty;    # gap penalty (transition penalty)
$fp      = $Penalty;     # fit penalty M(i,j)
$sp      = -$Penalty;    # substitution penalty M(i,j)
############ Match/Mismatch-Matrix #########################
for ( $i = 0 ; $i <= $#seq1 ; $i++ ) {

	for ( $j = 0 ; $j <= $#seq2 ; $j++ ) {
		if ( $seq1[$i] ne $seq2[$j] ) {
			$M[$i][$j] = $sp;
		}                #printf"M1[$i][$j]=$M[$i][$j]\t" } #subsitution penalty
		else {
			$M[$i][$j] = $fp;
		}                #printf"M2[$i][$j]=$M[$i][$j]\t"  } #fit penalty
	}    #printf"\n";
}

printf "\n------------------------ M(isM)atch-Matrix ----------------------------\n";
#printf "M\t";
#for ( $i = 0 ; $i <= $#seq2 ; $i++ ) {
#	printf " $seq2[$i]\t";
#}
#printf "\n";
#for ( $i = 0 ; $i <= $#seq1 ; $i++ ) {
#	printf "$seq1[$i]\t";
#	for ( $j = 0 ; $j <= $#seq2 ; $j++ ) {
#		printf "$M[$i][$j]\t";
#	}
#	printf "\n";
#}
printf "\n------------------------ Score-Matrix ---------------------------------\n";
#################### Score-Matrix ########################
$MINSCORE = -1000000000;
for ( $i = 0 ; $i <= $#seq1 ; $i++ ) {
	for ( $j = 0 ; $j <= $#seq2 ; $j++ ) {
		$S[$i][$j] = $MINSCORE;
	}
}
for ( $i = 0 ; $i <= $#seq1 ; $i++ ) {
	$S[$i][0] = $i * $gp;$T[$i][0]=1;
}
for ( $j = 0 ; $j <= $#seq2 ; $j++ ) {
	$S[0][$j] = $j * $gp;$T[0][$j]=-1;
}
$TotalScore = $MINSCORE;
for ( $i = 1 ; $i <= $#seq1 ; $i++ ) {
	for ( $j = 1 ; $j <= $#seq2 ; $j++ ) {
		$sc        = $S[ $i - 1 ][ $j - 1 ] + $M[$i][$j];
		$S[$i][$j] = $sc;$T[$i][$j]=0;
		$sc        = $S[ $i - 1 ][$j] + $gp;
		if ( $sc > $S[$i][$j] ) { $S[$i][$j] = $sc;$T[$i][$j]=1; }
		$sc = $S[$i][ $j - 1 ] + $gp;
		if ( $sc > $S[$i][$j] ) { $S[$i][$j] = $sc;$T[$i][$j]=-1; }
		if ( $S[$i][$j] > $TotalScore ) {
			$TotalScore = $S[$i][$j];
		}

	}
}

#printf "S\t";
#for ( $i = 0 ; $i <= $#seq2 ; $i++ ) {
#	printf " $seq2[$i]\t";
#}
#printf "\n";
#for ( $i = 0 ; $i <= $#seq1 ; $i++ ) {
#	printf "$seq1[$i]\t";
#	for ( $j = 0 ; $j <= $#seq2 ; $j++ ) {
#		printf "$S[$i][$j]\t";
#	}
#	printf "\n";
#}
#################### Traceback-Matrix #############################
$i = $#seq1;
$j = $#seq2;
while (1) {
	if ( $T[$i][$j] == 0 ) {
		@aseq1 = ( $seq1[$i], @aseq1 );
		@aseq2 = ( $seq2[$j], @aseq2 );
		@amatch = ( '*', @amatch );
		$i--;
		$j--;#print"$i $j gleich 0; ";
	}
	else {
		if ( $T[$i][$j] == 1 ) {
			@aseq1 = ( $seq1[$i], @aseq1 );
			@aseq2  = ( '-', @aseq2 );
			@amatch = ( ' ', @amatch );
			$i--;#print"$i $j gleich 1; ";
		}
		else {
			if ( $T[$i][$j] == -1 ) {
				@aseq1 = ( '-', @aseq1 );
				@aseq2 = ( $seq2[$j], @aseq2 );
				@amatch = ( ' ', @amatch );
				$j--;#print"$i $j gleich -1; ";
			}
		}
	}
	last if ( $i < 0 && $j < 0 );
}
printf "\nTraceback Alignment:\n\n\t";
for ( $i = 0 ; $i <= $#aseq1 ; $i++ ) {
	printf "$aseq1[$i] ";
}
printf "\n\t";
for ( $i = 0 ; $i <= $#amatch ; $i++ ) {
	printf "$amatch[$i] ";
}
printf "\n\t";
for ( $i = 0 ; $i <= $#aseq2 ; $i++ ) {
	printf "$aseq2[$i] ";
}
printf "\n";
