if [ ! -f "$1" ]
	then
	
	echo
	echo
	printf "ERROR \"$1\" : file not found\n"
	echo 
	echo "Usage $0 <testDescription>\n"
	printf "\t this script generate lkotlin code to test the play card code from the IA\n"
	printf "\t it takes as input a file to describe the test\n"
	printf "\t see exempleTest.txt as an example\n"
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
