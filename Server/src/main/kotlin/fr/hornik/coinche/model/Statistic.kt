package fr.hornik.coinche.model

data class Statistic (
        // Uid of the player
        val uid: String = "",

        //nb of Capots successful announced by player
        var nbCapot: Int = 0,

        //nb of General successful announced by player
        var nbGenerale: Int = 0,

        // nb of game joined by the player
        var nbGamesJoined:Int = 0,

        // nb of games created by the player
        var nbCreatedGames:Int = 0,

        // nb of contract played by the player
        var nbContract:Int = 0,

        //nb of contract made in the attack team
        var nbContractAttack:Int=0,

        // nb of contract made in the defanse team
        var nbContractDefense:Int=0,

        // nb of contract played where player was first to announce the color
        var nbContractInitiated:Int=0,

        //average nb of point on contract played (where player was the last one to announce the amount )
        var averageContractTaken:Float= 0F,

        // average nb of point on contract won (where player was the last one to announce the amount )
        var averageContractWin:Float= 0F,

        // average nb of point on contract lost (where player was the last one to announce the amount)
        var averageContractLost:Float= 0F,

        // nb of contract lost (where player was the last one to announce the amount)
        var nbLostContract:Int=0,

        // nb of contract win (where the player was the last one to announce the amount)
        var nbWinContract:Int =0,

        // nb of time the player help his partner by bidding with a higher number
        var nbSurBidSameColor:Int = 0,

        //nb of game played as partner but did not announced a higher announce
        var nbNoAnnounceHelpingPartner:Int = 0,

        // nb Games where player stayed in game until on team reached 1000 pts
        var nbEndedGames:Int=0,

        // nb game lost ( as a team member e.g. initiated or not by the player)
        var nbLostGame:Int=0,

        // nb of game won ( as a team member e.g. initiated or not by the player)
        var nbWinGame:Int =0
)