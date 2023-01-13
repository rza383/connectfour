package connectfour

fun main() {
    val game = ConnectFour()
    game.playGame()
    println("Game over!")
}

class ConnectFour() {
    var rows = 0
    var columns = 0
    var winCondition = false
    fun initPlayers() : List<String> {
        println("Connect Four")
        println("First player's name:")
        val firstPlayer = readln()
        println("Second player's name:")
        val secondPlayer = readln()
        return listOf(firstPlayer, secondPlayer)
    }
    fun initBoard()  {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        val boardDim = readln().filter { !it.isWhitespace() }
        val regSign = Regex("^\\d+[Xx]\\d+$")
        when {
            boardDim.isEmpty() -> {
                rows = 6
                columns = 7
            }
            !boardDim.matches(regSign) -> {
                println("Invalid input")
                initBoard()
            }
            boardDim.mapNotNull { if (it.isDigit()) it.digitToInt() else null }[0] !in 5..9 -> {
                println("Board rows should be from 5 to 9")
                initBoard()
            }
            boardDim.mapNotNull { if (it.isDigit()) it.digitToInt() else null }[1] !in 5..9 -> {
                println("Board columns should be from 5 to 9")
                initBoard()
            }
            else -> {
                    rows = boardDim.mapNotNull { if (it.isDigit()) it.digitToInt() else null }[0]
                    columns = boardDim.mapNotNull { if (it.isDigit()) it.digitToInt() else null }[1]
            }
        }
    }
    fun printBoard(board:  MutableList<MutableList<String>>) {
        val vertical = "║"
        val horizontal = "═"
        val leftConnector = "╚"
        val rightConnector = "╝"
        val tShape = "╩"
        for (i in 1..columns) {
            print(" $i")
        }
        println()
        for(line in board) {
            println(line.joinToString ("")  )
        }
        println("$leftConnector$horizontal${(tShape+horizontal).repeat(columns - 1)}$rightConnector")
    }
    fun playGame() {
        var turn = true
        var gamesPlayed = 0
        val moves = mutableListOf<String>()
        var(firstPlayer, secondPlayer) = initPlayers()
        val pointsScored = mutableMapOf(firstPlayer to 0, secondPlayer to 0)
        val(firstPlayerMem, secondPlayerMem) = listOf(firstPlayer, secondPlayer)
        var(p1Symbol,p2Symbol) = listOf("║o", "║*")
        initBoard()
        var board = MutableList(rows){ MutableList(columns + 1) {"║ "} }
        val gamesRequested = defineRounds()
        println("$firstPlayer VS $secondPlayer\n$rows X $columns board")
        println(if (gamesRequested == 1) "Single game" else "Total $gamesRequested games\nGame #1")
        printBoard(board)
        fun nextStep() {
            if (gamesRequested != 1) println("Score\n${firstPlayerMem}: ${pointsScored[firstPlayerMem]} ${secondPlayerMem}: ${pointsScored[secondPlayerMem]}")
            if (gamesPlayed != gamesRequested) {
                println("Game #${gamesPlayed + 1}")
                turn = true
                moves.clear()
                board = MutableList(rows) { MutableList(columns + 1) { "║ " } }
                printBoard(board)
                firstPlayer = secondPlayer.also { secondPlayer = firstPlayer }
                p1Symbol = p2Symbol.also { p2Symbol = p1Symbol }
            }
        }
        do {
            println("${if(turn) firstPlayer else secondPlayer}'s turn:")
            val move = readln()
            val moveSymbol = if(turn) p1Symbol else p2Symbol
            when{
                move.contains("end") -> return
                move.isNotNumeric() -> println("Incorrect column number")
                move.toInt() !in 1..columns -> println("The column number is out of range (1 - $columns)")
                else -> {
                    moves.add(move)
                    if(moves.count{it == move} > rows) println("Column $move is full")
                    else {
                        turn = !turn
                        board[rows - moves.count{it == move}][move.toInt() - 1] = moveSymbol
                        printBoard(board)
                        if(checkWin(move.toInt() - 1, rows - moves.count{it == move}, board, moveSymbol)) {
                            gamesPlayed++
                            if(turn) pointsScored.merge(secondPlayer, 2, Int::plus) else pointsScored.merge(firstPlayer, 2, Int::plus)
                            println("Player ${if(turn) secondPlayer else firstPlayer} won")
                            nextStep()
                        }
                        else if(checkDraw(board)) {
                            pointsScored.merge(firstPlayer, 1, Int::plus)
                            pointsScored.merge(secondPlayer, 1, Int::plus)
                            gamesPlayed++
                            println("It is a draw")
                            nextStep()
                        }
                    }
                }
            }
        } while(gamesPlayed != gamesRequested)
    }

    fun checkWin(i: Int, j: Int, board:  MutableList<MutableList<String>>, symbol: String) : Boolean {
        return count(board, i, j, 1, 0, symbol ) == 3 ||
                count(board, i, j, -1, 0, symbol ) == 3 ||
                count(board, i, j, 0, 1, symbol ) == 3 ||
                count(board, i, j, 0, -1, symbol ) == 3 ||
                count(board, i, j, -1, -1, symbol ) == 3 ||
                count(board, i, j, 1, 1, symbol ) == 3 ||
                count(board, i, j, -1, 1, symbol ) == 3 ||
                count(board, i, j, 1, -1, symbol ) == 3
    }
    fun count(board:  MutableList<MutableList<String>>, i: Int, j: Int, dX: Int, dY: Int, symbol: String) : Int {
        var x = i
        var y = j
        var quantity = 0
        x += dX
        y += dY
        while( x in 0 until columns && y in 0 until rows && board[y][x] == symbol) {
            x += dX
            y += dY
            quantity ++
        }
        return quantity
    }
    fun checkDraw(board:  MutableList<MutableList<String>>) : Boolean {
        var count = 0
        board.forEach{line -> if(line.filter{it != "║ " }.count() == columns ) count++}
        return count == rows
    }
    fun defineRounds() : Int {
        val reg = Regex("^[1-9]+$")
        println("Do you want to play single or multiple games?\nFor a single game, input 1 or press Enter\nInput a number of games:")
        val choice = readln()
        val noGames = if(choice.isEmpty()) 1 else if (choice.matches(reg))  choice.toInt() else {
            println("Invalid input")
            defineRounds()
        }
        return noGames
    }
}
fun String.isNotNumeric() = this.map { it.isDigit() }.contains(false)
