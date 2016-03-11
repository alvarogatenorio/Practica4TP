package es.ucm.fdi.tp.practica4.ataxx;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.Utils;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.FiniteRectBoard;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
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
 * (horizontal, vertical o diagonal) de N fichas consecutivas del mismo tipo.</li>
 * </ul>
 *
 */
public class AtaxxRules implements GameRules {

	// This object is returned by gameOver to indicate that the game is not
	// over. Just to avoid creating it multiple times, etc.
	//
	protected final Pair<State, Piece> gameInPlayResult = new Pair<State, Piece>(
			State.InPlay, null);

	private int dim;

	private int obstacles;

	private Piece obstacle;

	public AtaxxRules(int dim, int obstacles) {
		/*
		 * if (dim < 5) { throw new GameError("Dimension must be at least 5: " +
		 * dim); } else if (dim % 2 == 0) { throw new
		 * GameError("Dimesion must be odd: " + dim); } else {
		 */
		this.dim = dim;
		// }
		this.obstacles = obstacles;
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
		/* Initializing the board. */
		FiniteRectBoard board = new FiniteRectBoard(dim, dim);
		embedPlayers(pieces, board);
		spreadObstacles(board);
		return board;
	}

	private void embedPlayers(List<Piece> pieces, Board board) {
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
	}

	private void spreadObstacles(Board board) {
		int randomRow, randomCol;
		obstacle = new Piece("*");
		for (int i = 0; i < obstacles / 4; i++) {
			randomRow = Utils.randomInt(dim / 2);
			randomCol = Utils.randomInt(dim / 2);
			while (board.getPosition(randomRow, randomCol) != null) {
				randomRow = Utils.randomInt(dim / 2);
				randomCol = Utils.randomInt(dim / 2);
			}
			board.setPosition(randomRow, randomCol, obstacle);
			board.setPosition(randomRow, dim - randomCol - 1, obstacle);
			board.setPosition(dim - randomRow - 1, randomCol, obstacle);
			board.setPosition(dim - randomRow - 1, dim - randomCol - 1,
					obstacle);
		}
		if (obstacles % 4 == 1) {
			board.setPosition(dim / 2, dim / 2, obstacle);
		}
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
	public Pair<State, Piece> updateState(Board board,
			List<Piece> playersPieces, Piece lastPlayer) {

		refreshPiecesCounters(board, playersPieces);

		/*
		 * If the board is full and the maximum score is shared by at least two
		 * players, we have a DRAW.
		 */
		if (board.isFull() && equalPoints(board, playersPieces)) {
			return new Pair<State, Piece>(State.Draw, null);
		}

		/*
		 * If the board is full and there is no draw, the only possible option
		 * is that someone has won the game.
		 * 
		 * If the board is full and there is a draw, this point should be
		 * unreachable.
		 * 
		 * If the board has empty cells but no one has valid moves we will check
		 * for the draw or for the winner.
		 * 
		 * FOR SURE CAN BE DONE BETTER!!
		 */
		if (board.isFull() || noValidMoves(board, playersPieces)) {
			if (equalPoints(board, playersPieces)) {
				return new Pair<State, Piece>(State.Draw, null);
			} else {
				return new Pair<State, Piece>(State.Won, wonPlayer(board,
						playersPieces));
			}
		}

		/*
		 * The there is only one piece in the board, the owner of the piece is
		 * trivially the winner.
		 */
		Integer pos = onlyOneAlive(board, playersPieces);
		if (pos != null) {
			return new Pair<State, Piece>(State.Won, playersPieces.get(pos));
		}

		return gameInPlayResult;
	}

	private boolean noValidMoves(Board board, List<Piece> playersPieces) {
		boolean correct = true;
		for (int i = 0; i < playersPieces.size(); i++) {
			List<GameMove> moves = validMoves(board, playersPieces,
					playersPieces.get(i));
			if (moves.size() != 0) {
				correct = false;
			}
		}

		return correct;
	}

	private void refreshPiecesCounters(Board board, List<Piece> playersPieces) {
		for (int i = 0; i < playersPieces.size(); i++) {
			board.setPieceCount(playersPieces.get(i), 0);
		}

		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				if (board.getPosition(i, j) != null
						&& board.getPosition(i, j).getId() != "*") {
					Integer actualpoints = board.getPieceCount(board
							.getPosition(i, j)) + 1;
					board.setPieceCount(board.getPosition(i, j), actualpoints);
				}

			}
		}
	}

	private Integer onlyOneAlive(Board board, List<Piece> playersPieces) {
		int alive = 0;
		int pos = -1;
		for (int i = 0; i < playersPieces.size() && alive < 2; i++) {
			if (board.getPieceCount(playersPieces.get(i)) != 0) {
				alive++;
				if (alive == 1)
					pos = i;

			}
		}
		if (pos == -1 || alive == 2) {
			return null;
		} else {
			return pos;
		}
	}

	private Piece wonPlayer(Board board, List<Piece> playersPieces) {
		int max = board.getPieceCount(playersPieces.get(0));
		int pos = 0;
		for (int i = 1; i < playersPieces.size(); i++) {
			if (max < board.getPieceCount(playersPieces.get(i))) {
				max = board.getPieceCount(playersPieces.get(i));
				pos = i;
			}
		}
		return playersPieces.get(pos);
	}

	private boolean equalPoints(Board board, List<Piece> playersPieces) {
		boolean equal = false;
		Piece winnerPiece = wonPlayer(board, playersPieces);
		for (int i = 0; i < playersPieces.size(); i++) {
			if (board.getPieceCount(playersPieces.get(i)) == board
					.getPieceCount(winnerPiece)
					&& playersPieces.get(i).getId() != winnerPiece.getId()) {
				equal = true;
			}
		}
		return equal;
	}

	@Override
	public Piece nextPlayer(Board board, List<Piece> playersPieces,
			Piece lastPlayer) {
		int i = playersPieces.indexOf(lastPlayer);
		List<GameMove> moves = validMoves(board, playersPieces,
				playersPieces.get((i + 1) % playersPieces.size()));
		while (board.getPieceCount(playersPieces.get((i + 1)
				% playersPieces.size())) == 0
				|| moves.size() == 0) {
			i++;
			moves = validMoves(board, playersPieces,
					playersPieces.get((i + 1) % playersPieces.size()));
		}
		return playersPieces.get((i + 1) % playersPieces.size());
	}

	@Override
	public double evaluate(Board board, List<Piece> playersPieces, Piece turn) {
		return 0;
	}

	private static final int deltas[][] = { { 0, 1 }, { 1, 1 }, { 1, 0 },
			{ 1, -1 }, { 0, -1 }, { -1, 1 }, { -1, 0 }, { -1, -1 }, { -2, -2 },
			{ -2, -1 }, { -2, 0 }, { -2, 1 }, { -2, 2 }, { -1, -2 }, { -1, 2 },
			{ 0, -2 }, { 0, 2 }, { 1, -2 }, { 1, 2 }, { 2, -2 }, { 2, -1 },
			{ 2, 0 }, { 2, 1 }, { 2, 2 } };

	private boolean dentro(int x, int y) {
		return x >= 0 && y >= 0 && x < dim && y < dim;
	}

	@Override
	public List<GameMove> validMoves(Board board, List<Piece> playersPieces,
			Piece turn) {
		List<GameMove> moves = new ArrayList<GameMove>();
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getCols(); j++) {
				if (board.getPosition(i, j) == turn) {
					for (int[] ds : deltas) {
						int x = i + ds[0];
						int y = j + ds[1];
						if (dentro(x, y) && board.getPosition(x, y) == null) {
							moves.add(new AtaxxMove(i, j, x, y, turn));
						}

					}

				}
			}
		}
		return moves;
	}
}
