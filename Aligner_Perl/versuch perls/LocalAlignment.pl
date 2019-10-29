#!/usr/bin/perl
$Seq1    = $ARGV[0];
$Seq2    = $ARGV[1];
@seq1    = (' ', split( //, $Seq1 ) );
@seq2    = (' ', split( //, $Seq2 ) );
$Penalty = 10;
$gp      = -$Penalty;                # gap penalty (transition penalty)
$fp      = $Penalty;                 # fit penalty M(i,j)
$sp      = -$Penalty;                # substitution penalty M(i,j)
$n       = 0;
$N       = $#seq2 - $#seq1;
@merke1=();
$tmp=10;$tmp2=-100000000000000;$l=0;
while ( $n != $N ) {
############ Match/Mismatch-Matrix #########################
    for ( $i = 0 ; $i <= $#seq1 ; $i++ ) {
        for ( $j = $n ; $j <= $n + $#seq1; $j++ ) {
            if ( $seq1[$i] ne $seq2[$j] ) {
                $M[$i][$j] = $sp;
            }                        #subsitution penalty
            else {
                $M[$i][$j] = $fp;
            }                        #fit penalty
        }
    }
#    printf "---------------------- M(isM)atch-Matrix ------------------------\n";
#    printf "M\t";
#    for ( $j = $n ; $j <= $n + $#seq1 ; $j++ ) {
#        printf " $seq2[$j]\t";
#    }
#    printf "\n";
#    for ( $i = 0 ; $i <= $#seq1 ; $i++ ) {
#        printf "$seq1[$i]\t";
#        for ( $j = $n ; $j <= $n + $#seq1 ; $j++ ) {
#            printf "$M[$i][$j]\t";
#        }
#        printf "\n";
#    }
#    printf "---------------------- Score-Matrix -----------------------------\n";
    $MINSCORE = -1000000000;
    for ( $i = 0 ; $i <= $#seq1 ; $i++ ) {
        for ( $j = $n ; $j <= $n + $#seq1 ; $j++ ) {
            $S[$i][$j] = $MINSCORE;
        }
    }
    $k = 0;
    for ( $j = $n ; $j <= $n + $#seq1 ; $j++ ) {
        $S[0][$j] = $k * $gp + $M[0][$j];
        $k++;
        $T[0][$j] = -1;
    }
   
    for ( $i = 0 ; $i <= $#seq1 ; $i++ ) {
        $S[$i][$n] = $i * $gp + $M[$i][$n];
        $T[$i][$n] = 1;
    }
#    if ($S[0][$n]==10){$T[0][$n]=0;}
    $MaxScore = $MINSCORE;
    $TotalScore = 0;
    for ( $i = 1 ; $i <= $#seq1 ; $i++ ) {
        for ( $j = $n + 1 ; $j <= $n + $#seq1; $j++ ) {
            $sc			= $S[$i-1][$j-1] + $M[$i][$j];
            $sc2        = $S[$i-1][$j-1] + $M[$i][$j];
            $S[$i][$j] 	= $sc;
            $TotalScore+=$sc2;
            $T[$i][$j] 	= 0;
            $sc        	= $S[ $i-1][$j] + $gp;
            if ( $sc > $S[$i][$j] ) { $S[$i][$j] = $sc; $T[$i][$j] = 1; $TotalScore=$TotalScore-$sc2+$sc;}
            $sc2 		= $S[ $i-1][$j] + $gp;
            $sc		 	= $S[$i][$j-1] + $gp;
            if ( $sc > $S[$i][$j] ) { $S[$i][$j] = $sc; $T[$i][$j] = -1; $TotalScore=$TotalScore-$sc2+$sc;}
            if ( $S[$i][$j] > $MaxScore ) {
                $MaxScore = $S[$i][$j];#print " score=$MaxScore i=$i j=$j\n";
            }
        }
    }
#    printf "\nS\t";
#    for ( $i = $n ; $i <= $n + $#seq1 ; $i++ ) {
#        printf " $seq2[$i]\t";
#    }
#    printf "\n";
#    for ( $i = 0 ; $i <= $#seq1 ; $i++ ) {
#        printf "$seq1[$i]\t";
#        for ( $j = $n ; $j <= $n + $#seq1 ; $j++ ) {
#            printf "$S[$i][$j]\t";
#        }
#        printf "\n";
#    }
##################### Traceback-Matrix #############################
#    printf "\nT\t";
#    for ( $i = $n ; $i <= $n + $#seq1 ; $i++ ) {
#        printf "$seq2[$i]\t";
#    }
#    printf "\n";
#    for ($i=0;$i<=$#seq1;$i++){
#        printf "$seq1[$i]\t";   
#        for ($j=$n;$j<=$n+$#seq1;$j++){
#            printf "$T[$i][$j]\t";
#        }
#        print"\n";
#    }
    $i = $#seq1;
    $j = $n+$#seq1;# print "istart=$i jstart=$j\n";
    $a=0;
    while (1) {
        if ( $T[$i][$j] == 0 ) {
            @aseq1 = ( $seq1[$i], @aseq1 );
            @aseq2 = ( $seq2[$j], @aseq2 );
            @amatch = ( '*', @amatch );
            $i--;$a++;
            $j--;   # print"bei 0: i=$i j=$j a=$a \n";
        }
        else {
            if ( $T[$i][$j] == 1 ) {
                @aseq1 = ( $seq1[$i], @aseq1 );
                @aseq2  = ( '-', @aseq2 );
                @amatch = ( ' ', @amatch );
                $i--;$a++;   # print"bei 1: i=$i j=$j a=$a \n";
            }
            else {
                if ( $T[$i][$j] == -1 ) {
                    @aseq1 = ( '-', @aseq1 );
                    @aseq2 = ( $seq2[$j], @aseq2 );
                    @amatch = ( ' ', @amatch );
                    $j--;$a++;  #  print"bei -1: i=$i j=$j a=$a \n";
                }
            }
        }
        last if ( $i == 0 && $j == $n );
    }
#    if ($T[0][$n]==0){
#        @aseq1 = ( $seq1[0], @aseq1 );
#        @aseq2 = ( $seq2[$n], @aseq2 );
#        @amatch = ( '*', @amatch );
#        $a++;
#    }
#    else{
#        if ($T[0][$n]==1){
#            @aseq1 = ( '-',$seq1[0], @aseq1 );   
#            @aseq2  = ( $seq2[0],'-', @aseq2 );
#            @amatch = ( ' ',' ', @amatch );
#            $a=$a+2;
#        }
#        else{
#            if($T[0][$n]==-1){
#                @aseq1  = ( $seq1[0],'-', @aseq1 );
#                @aseq2 = ( '-',$seq2[0], @aseq2 );
#                @amatch = ( ' ',' ', @amatch );
#                $a=$a+2;
#            }
#        }
#    }
#    printf "\nTraceback Alignment: Score=$MaxScore\n\n";
#    for ( $i = 0 ; $i <= $a ; $i++ ) {
#        printf "$aseq1[$i] ";
#    }
#    printf "\n";
#    for ( $i = 0 ; $i <= $a ; $i++ ) {
#        printf "$amatch[$i] ";
#    }
#    printf "\n";
#    for ( $i = 0 ; $i <= $a ; $i++ ) {
#        printf "$aseq2[$i] ";
#    }printf "\n";
    if ($MaxScore>=$tmp || $TotalScore>=$tmp2){$l++;
        $tmp=$MaxScore;
        $tmp2=$TotalScore;
        for ($i=0;$i<=$a;$i++){
            $merke0[$i]=$aseq1[$i];
            $merke1[$i]=$amatch[$i];
            $merke2[$i]=$aseq2[$i];
        }
        printf "\nit=$l, MaxScore= $tmp, TotalScore=$TotalScore, Alignment an Position $n der 2.Sequenz:\n\n\t@merke0\n\t@merke1\n\t@merke2\n";
    }
    $n++;
}
