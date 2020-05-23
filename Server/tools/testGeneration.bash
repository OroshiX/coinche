#!/bin/bash


TRUMP=${TRUMP_private:-"Atout :"}
MYSELF=${MYSELF_private:-"Moi :"}
BID=${BID_private:-"Bid :"}
MYGAME=${MYGAME_private:-"Mon jeu :"}
TRICK=${TRICK_private:-"Pli "}
TABLE=${TABLE_private:-"Table :"}
TEST=${TEST_private:-"Test :"}
RESULT=${RESULT_private:-"Resultat :"}
NAME=${NAME_private:-"Fonction :"}

if [ ! -f "$1" ]
	then
	
	echo >&2
	echo >&2
	printf "ERROR \"$1\" : file not found\n" >&2
	echo  >&2
	echo "Usage $0 <testDescription>\n" >&2
	printf "\t this script generate kotlin code to test the play card code from the IA\n" >&2
	printf "\t it takes as input a file to describe the test\n" >&2
	printf "\t As an example you can run $0 > example.txt ; $0 example.txt \n" >&2
	cat <<!
// Legend : 
// H : Heart
// S : Spade
// C : Club
// D : Diamond
// n : north
// e : east
// s : south
// w : west
// 1 As
// 7
// 8
// 9
// 10
// 11 Jack
// 12 Queen
// 13 King
// Note : Upper case and lowercase are important to respect as well as space between symbols !!!

$TRUMP S 
$NAME Armand
$MYSELF s 
$BID S 80 e 
$MYGAME [ 7 S , 1 H , 12 H , 13 D , 1 D ]
$TRICK 1 : [ n 11 S , e 8 S , s 12 S , w 7 C ]
$TRICK 2 : [ n 9 C , e 1 C , s 7 C , w 10 C ]
$TRICK 3 : [ e 1 D , s 8 D , w 9 H , n 7 D ]
$TABLE [ e 13 H ]
$TEST call playersHaveColors and check rules R1 and R2 are correctly applied
$RESULT : w has no D 
!

	printf "\n\n"
	exit 2
fi

name=`grep "^${NAME}" $1 | sed -e "s/^.*: *\(.*\)/fun test\1() { /"`

cat <<!
/*
!

cat $1
cat <<!

*/

@Test
$name
        val nameTest = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
	val plisEW: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
        val plisNS: MutableMap<Int, List<CardPlayed>> = mutableMapOf()
	var nb=0

!

cat $1 | sed -e "s/${TRICK}.* :.*\[/plisNS\[nb++\] = listOf(/"\
	     -e "s/^.*${BID} * \([SHDC]\) \([0-9][0-9]*\) \([news] \)$/val bid = SimpleBid(\1,\2,\3) /"\
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
	     -e "/SimpleBid/s/Bid(\([HCDS]\)\(..*\)\([news]\)/Bid( \1 \2 \3 )™val listBids: MutableList<Bid> = mutableListOf(bid, Pass(west), Pass(north), Pass(east), Pass(south)/"\
	     -e "s/ S / spade /g" \
	     -e "s/ C / club /g" \
	     -e "s/ H / heart /g" \
	     -e "s/ D / diamond /g" \
	     -e "s/ n / north /g" \
	     -e "s/ e / east /g" \
	     -e "s/ w / west /g" \
	     -e "s/ s / south /g" \
	     -e "s/${TRUMP} /val atout=/"\
	     -e "s/${MYSELF} /val myPosition=/"\
	     -e "s/${MYGAME} \[/val myCards = mutableListOf(/"\
	     -e "s/${TABLE} \[/val onTable :MutableList<CardPlayed> = mutableListOf(/"\
	     -e "/onTable/s/$/™validateHand(myCards,bid = listBids.last{ (it is SimpleBid) ||  (it is General) || (it is Capot)},onTable = onTable)/"\
	     -e "/onTable/s/$/™val nbPlis = plisEW.size + plisNS.size™var currentPli: Map<Int, MutableList<CardPlayed>> = mapOf()™if (onTable.isNotEmpty()) {™currentPli = listOf(Pair(nbPlis, onTable)).toMap()™}™val allCardPli = (plisEW + plisNS + currentPli).toSortedMap().map { it.value }™/"\
	     -e "s/\]/)/g"\
	     -e "s/\+\+)/\+\+\]/" \
	     -e "s/\(${RESULT}.*$\)/                   \/* \1 *\//g"\
	     -e "s/\(${TEST}.*$\)/val result = TODO(\"\1\")/" | egrep -v "^.*$NAME.*" |  tr "™" "\012"

cat <<!
			// val result = whatToPlay(myPosition, myCards, listBids, atout, onTable, plisNS, plisEW) 
			// val result = IARun.enchere(myPosition, listBids, myCards, 0)
			// val result = playersHaveColor(atout, atout, bid, allCardPli, myPosition, myCards)



	               	/* you need to check result here */

traceLevel = oldTraceLevel
//assert(result[east]!! && result[north]!! && result[west]!! && result[south]!! ) { "\$nameTest FAIL \$result is not accurate" }
//assert(result.value == heart && result.color == king ) { "\$nameTest FAIL \$result is not accurate" }
//assert((result.curColor() == heart) && (result.curPoint() == 80)) { "$nameTest FAIL $result is not accurate" }

assert(TODO()) {"\$nameTest FAIL : \$result is not OK ")

debugPrintln(dbgLevel.REGULAR,"\$nameTest:PASS  we play  :\$result ")

}


!
