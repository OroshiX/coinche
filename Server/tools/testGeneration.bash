if [ ! -f "$1" ]
	then
	
	echo
	echo
	printf "ERROR \"$1\" : file not found\n"
	echo 
	echo "Usage $0 <testDescription>\n"
	printf "\t this script generate kotlin code to test the play card code from the IA\n"
	printf "\t it takes as input a file to describe the test\n"
	printf "\t see exempleTest.txt as an example\n"
	cat <<!
Legend : 
H : Heart
S : Spade
C : Club
D : Diamond
n : north
e : east
s : south
w : west
1 As
7
8
9
10
11 Jack
12 Queen
13 King
Note : Upper case and lowercase are important to respect as well as space between symbols !!!

Atout : S 
Moi : s 
Mon jeu : [ 7 S , 1 H , 12 H , 13 D , 1 D ]
Pli 1 : [ n 11 S , e 8 S , s 12 S , w 7 C ]
Pli 2 : [ n 9 C , e 1 C , s 7 C , w 10 C ]
Pli 3 : [ e 1 D , s 8 D , w 9 H , n 7 D ]
Table : [ e 13 H ]
Test : w , R1C + R2C
Resultat : w has no diamonds
!

	printf "\n\n"
	exit 2
fi

cat <<!
/*
!

cat $1
cat <<!

*/

@Test
fun testIAPlayXXX() {
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
	val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
	var nb=0
!

cat $1 | sed -e "s/Pli .* :.*\[/plisNS\[nb++\] = listOf(/"\
	     -e "s/ \([1789][0-3]*\) \([SCDH]\) / Card( \1 , \2 ) /g"\
	     -e "s/ S / spade /g" \
	     -e "s/ C / club /g" \
	     -e "s/ H / heart /g" \
	     -e "s/ D / diamond /g" \
	     -e "s/ 7 / seven /g" \
	     -e "s/ 8 / eight /g" \
	     -e "s/ 9 / nine /g" \
	     -e "s/ 10 / ten /g" \
	     -e "s/ 11 / jack /g" \
	     -e "s/ 12 / queen /g" \
	     -e "s/ 13 / king /g" \
	     -e "s/ 1 / ace /g" \
	     -e "s/\( [news] \)\([^)]*)\)/CardPlayed(\2 , position=\1 )/g"\
	     -e "s/ n / north /g" \
	     -e "s/ e / east /g" \
	     -e "s/ w / west /g" \
	     -e "s/ s / south /g" \
	     -e "s/Atout : /val atout=/"\
	     -e "s/Moi : /val myPosition=/"\
	     -e "s/Mon jeu : \[/val myCards = listOf(/"\
	     -e "s/Table : \[/val onTable = listOf(/"\
	     -e "s/\]/)/g"\
	     -e "s/\+\+)/\+\+\]/" \
	     -e "s/\(Resultat.*$\)/\/*\1*\//g"\
	     -e "s/\(Test.*$\)/val result = TODO(\"\1\")/"

cat <<!
/* you need to check result here */
assert(TODO()) { "\$nameTest FAIL \$result is not accurate" }
}


!
