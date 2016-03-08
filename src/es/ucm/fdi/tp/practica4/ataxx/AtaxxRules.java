package es.ucm.fdi.tp.practica4.ataxx;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.Utils;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.FiniteRectBoard;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Pair;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;


/**
 * Rules for Ataxx game.
 * <ul>
 * <li>The game is played on an NxN board (with N>=5).</li>
 * <li>The number of players is between 2 and 4.</li>
 * <li>The player turn in the given order, each placing a piece on an empty
 * cell. The winner is the one who construct a line (horizontal, vertical or
 * diagonal) with N consecutive pieces of the same type.</li>
 * </ul>
 * 
 * <p>
 * Reglas del juego Ataxx.
 * <ul>
 * <li>El juego se juega en un tablero NxN (con N>=5).</li>
 * <li>El numero de jugadores esta entre 2 y 4.</li>
 * <li>Los jugadores juegan en el orden proporcionado, cada uno colocando una
 * ficha en una casilla vacia. El ganador es el que consigua construir una linea
 * (horizontal, vertical o diagonal) de N fichas consecutivas del mismo tipo.
 * </li>
 * </ul>
 *
 */
public class AtaxxRules implements GameRules {

	// This object is returned by gameOver to indicate that the game is not
	// over. Just to avoid creating it multiple times, etc.
	//
	protected final Pair<State, Piece> gameInPlayResult = new Pair<State, Piece>(State.InPlay, null);

	private int dim;
	
	private int obstacles;

	public AtaxxRules(int dim, int obstacles) {
		if (dim < 5) {
			throw new GameError("Dimension must be at least 5: " + dim);
		} else if (dim % 2 == 0) {
			throw new GameError("Dimesion must be odd: " + dim);
		} else {
			this.dim = dim;
		}
		this.obstacles= obstacles;
	}

	@Override
	public String gameDesc() {
		return "Ataxx " + dim + "x" + dim;
	}

	/**
	 * Proceso de creación del tablero de juego, debemos, insertar las piezas
	 * iniciales al inicio.
	 * 
	 * Recibimos como argumento una lista de piezas como caida del cielo que
	 * debe decirnos cuantos jugadores hay.
	 */
	@Override
	public Board createBoard(List<Piece> pieces) {
		FiniteRectBoard board = new FiniteRectBoard(dim, dim);
		/*
		 * At least it will be two players, with pieces in the opposite corners.
		 */
		board.setPosition(0, 0, pieces.get(0));
		board.setPosition(dim - 1, dim - 1, pieces.get(0));
		board.setPosition(0, dim - 1, pieces.get(1));
		board.setPosition(dim - 1, 0, pieces.get(1));
		if (pieces.size() > 2 && pieces.size() <= 4) {
			board.setPosition(dim / 2, 0, pieces.get(2));
			board.setPosition(dim / 2, dim - 1, pieces.get(2));
			if (pieces.size() == 4) {
				board.setPosition(0, dim / 2, pieces.get(3));
				board.setPosition(dim - 1, dim / 2, pieces.get(3));
			}
		}
		int randomRow, randomCol;
		for(int i = 0; i < obstacles/4; i++){
			do{
			randomRow=Utils.randomInt(dim/2);
			randomCol=Utils.randomInt(dim/2);
			}while(board.getPosition(randomRow, randomCol)!=null);
			//board.setPosition(randomRow, randomCol, );
		}
		//Ahora hay que reproducirlos simetricamente de manera rotacional 
		//Alvaro eso es tuyo que no estoy seguro de como hacerlo. 

		return board;
	}

	@Override
	public Piece initialPlayer(Board board, List<Piece> playersPieces) {
		return playersPieces.get(0);
	}

	@Override
	public int minPlayers() {
		return 2;
	}

	@Override
	public int maxPlayers() {
		return 4;
	}

	@Override
	public Pair<State, Piece> updateState(Board board, List<Piece> playersPieces, Piece lastPlayer) {

		// Funcion a cambiar, en esta funcion tenemos que comprobar cual es el
		// estado del tablero (won, draw, inplay).

		return gameInPlayResult;
	}

	@Override
	public Piece nextPlayer(Board board, List<Piece> playersPieces, Piece lastPlayer) {
		List<Piece> pieces = playersPieces;
		int i = pieces.indexOf(lastPlayer);
		return pieces.get((i + 1) % pieces.size());
	}

	@Override
	public double evaluate(Board board, List<Piece> playersPieces, Piece turn) {
		return 0;
	}
	private static final int deltas [][] = {
		{0,1}, {1,1}, {1,0}, {1,-1}, {0,-1}, {-1,1}, {-1,0}, {-1,-1},
		{-2,-2}, {-2,-1}, {-2,0}, {-2,1}, {-2,2}, {-1,-2}, {-1,2}, {0,-2},
		{0,2}, {1,-2}, {1,2}, {2,-2}, {2,-1}, {2,0}, {2,1}, {2,2}
	};
	
	private boolean dentro(int x, int y){
		return x>=0 && y>=0 && x<dim && y<dim;
	}
	
	@Override
	public List<GameMove> validMoves(Board board, List<Piece> playersPieces, Piece turn) {
		List<GameMove> moves = new ArrayList<GameMove>();
		for(int i = 0; i<board.getRows(); i++){
			for(int j = 0; j<board.getCols(); j++){
				if(board.getPosition(i, j)==turn){ 
					for(int [] ds: deltas){
						int x = i + ds[0];
						int y = j + ds[1];
						if(dentro(x,y) && board.getPosition(x, y)!=null){ 
							//Falta que compruebe que no es un OBSTACULO...... NO SE COMO HACERLO TAMBIEN PASA EN ATAXX MOVE
							moves.add(new AtaxxMove(i, j, x, y, turn));
						}
						
					}
					
				}
			}
		}
		return moves;
	}
}
