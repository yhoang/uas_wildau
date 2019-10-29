#!/usr/bin/perl
open(INS, "<seq1.txt") || die "Datei mit E-Mails nicht gefunden\n";
while(<INS>)
 {
  push(@Seq1,$_);
 }
close(INS);
open(INS2, "<seq2.txt") || die "Datei mit E-Mails nicht gefunden\n";
while(<INS2>)
 {
  push(@Seq2,$_);
 }
close(INS2);
@seq1    = (' ', split( //, $Seq1[0] ) );
@seq2    = (' ', split( //, $Seq2[0] ) );#print "$seq1[3]$seq1[4]\n";
$Penalty = 10;
$gp      = -$Penalty;                # gap penalty (transition penalty)
$fp      = $Penalty;                 # fit penalty M(i,j)
$sp      = -$Penalty;                # substitution penalty M(i,j)
$n       = 0;
$N       = $#seq2 - $#seq1;
$tmp=10;$tmp2=-100000000000000;$l=0;
while ( $n != $N ) {
	$t=$n+$#seq1-1;$s=$#seq1-1;
    for ( $i = 0 ; $i <= $s; $i++ ) {
        for ( $j = $n ; $j <= $t; $j++ ) {
            if ( $seq1[$i] ne $seq2[$j] ) {
                $M[$i][$j] = $sp;
            }                        #subsitution penalty
            else {
                $M[$i][$j] = $fp;
            }                        #fit penalty
        }
    }
    $MINSCORE = -1000000000;
    for ( $i = 0 ; $i <= $s ; $i++ ) {
        for ( $j = $n ; $j <= $t ; $j++ ) {
            $S[$i][$j] = $MINSCORE;
        }
    }
    $k = 0;
    for ( $j = $n ; $j <= $t ; $j++ ) {
        $S[0][$j] = $k * $gp + $M[0][$j];
        $T[0][$j] = -1;
        $k++;
    }
   
    for ( $i = 0 ; $i <= $s; $i++ ) {
        $S[$i][$n] = $i * $gp + $M[$i][$n];
        $T[$i][$n] = 1;
    }
    $MaxScore = $MINSCORE;
    $TotalScore = 0;
    for ( $i = 1 ; $i <= $s ; $i++ ) {
        for ( $j = $n + 1 ; $j <= $t; $j++ ) {
            $sc		= $S[$i-1][$j-1] + $M[$i][$j];
            $sc2        = $S[$i-1][$j-1] + $M[$i][$j];
            $S[$i][$j] 	= $sc;
            $T[$i][$j] 	= 0;
	    	$TotalScore+=$sc;
            $sc        	= $S[ $i-1][$j] + $gp;
            if ( $sc > $S[$i][$j] ) { 
            	$S[$i][$j] = $sc; 
            	$T[$i][$j] = 1; 
            	$TotalScore=$TotalScore-$sc2+$sc;
            }
            $sc2 		= $S[ $i-1][$j] + $gp;
            $sc		 	= $S[$i][$j-1] + $gp;
            if ( $sc > $S[$i][$j] ) { 
            	$S[$i][$j] = $sc; 
            	$T[$i][$j] = -1; 
            	$TotalScore=$TotalScore-$sc2+$sc;
            }
            if ( $S[$i][$j] > $MaxScore ) {
                $MaxScore = $S[$i][$j];
            }
        }
    }
    $a=0;
    while (1) {
        if ( $T[$s][$t] == 0 ) {
            @aseq1 = ( $seq1[$s], @aseq1 );
            @aseq2 = ( $seq2[$t], @aseq2 );
            @amatch = ( '*', @amatch );
            $s--;$a++;
            $t--;   
        }
        else {
            if ( $T[$s][$t] == 1 ) {
                @aseq1 = ( $seq1[$s], @aseq1 );
                @aseq2  = ( '-', @aseq2 );
                @amatch = ( ' ', @amatch );
                $s--;$a++;   # print"bei 1: i=$i j=$j a=$a \n";
            }
            else {
                if ( $T[$s][$t] == -1 ) {
                    @aseq1 = ( '-', @aseq1 );
                    @aseq2 = ( $seq2[$t], @aseq2 );
                    @amatch = ( ' ', @amatch );
                    $t--;$a++;  #  print"bei -1: i=$i j=$j a=$a \n";
                }
            }
        }
        last if ( $s == 0 && $t == $n );
    }
    if ($MaxScore>=$tmp && $TotalScore>=$tmp2){$l++;
        $tmp=$MaxScore;
        $tmp2=$TotalScore;
        for ($i=0;$i<=$a;$i++){
            $merke0[$i]=$aseq1[$i];
            $merke1[$i]=$amatch[$i];
            $merke2[$i]=$aseq2[$i];
        }
        printf "\nn=$l, MaxScore= $tmp, TotalScore=$tmp2, Alignment an Position $n:\n\t";
        for ($i=0;$i<=$a;$i++){
        	print"$merke0[$i]";
        }
        print"\n\t";
        for ($i=0;$i<=$a;$i++){
        	print"$merke1[$i]";
        }
        print"\n\t";
        for ($i=0;$i<=$a;$i++){
        	print"$merke2[$i]";
        }
        print"\n";
    }
    printf "it=$n, MaxScore= $MaxScore, TotalScore=$TotalScore\n";
    $n++;
}
