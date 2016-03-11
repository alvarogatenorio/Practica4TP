package es.ucm.fdi.tp.practica4.ataxx;

import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

/**
 * A Class representing a move for ConnectN.
 * 
 * <p>
 * Clase para representar un movimiento del juego conecta-n.
 * 
 */
public class AtaxxMove extends GameMove {

	private static final long serialVersionUID = 1L;

	/**
	 * The row where to place the piece return by {@link GameMove#getPiece()}.
	 * <p>
	 * Fila en la que se coloca la ficha devuelta por
	 * {@link GameMove#getPiece()}.
	 */
	protected int row;

	/**
	 * The column where to place the piece return by {@link GameMove#getPiece()}
	 * .
	 * <p>
	 * Columna en la que se coloca la ficha devuelta por
	 * {@link GameMove#getPiece()}.
	 */
	protected int col;

	/**
	 * The row which the piece is moved from.
	 * 
	 * <p>
	 * Fila desde la que se intenta mover la pieza.
	 */
	protected int oldRow;

	/**
	 * The column which the piece is moved from.
	 * 
	 * <p>
	 * Columna desde la que se intenta mover la pieza.
	 */
	protected int oldCol;

	/**
	 * This constructor should be used ONLY to get an instance of
	 * {@link AtaxxMove} to generate game moves from strings by calling
	 * {@link #fromString(String)}
	 * 
	 * <p>
	 * Solo se debe usar este constructor para obtener objetos de
	 * {@link AtaxxMove} para generar movimientos a partir de strings usando el
	 * metodo {@link #fromString(String)}
	 * 
	 */
	public AtaxxMove() {
	}

	/**
	 * Constructs a move for placing (int the ataxx sense) a piece of the type
	 * referenced by {@code p} from a valid position in the board to another
	 * valid position.
	 * <p>
	 * In this case we consider a valid position somewhere in the closed ball of
	 * radius 2 (if we are in the d_infinite distance topology).
	 * 
	 * @param oldRow
	 * @param oldCol
	 * @param row
	 * @param col
	 * @param p
	 */
	public AtaxxMove(int oldRow, int oldCol, int row, int col, Piece p) {

		super(p);
		this.oldRow = oldRow;
		this.oldCol = oldCol;
		this.row = row;
		this.col = col;
		if (maximum() != 1 && maximum() != 2) {
			throw new NumberFormatException();
		}
	}

	@Override
	public void execute(Board board, List<Piece> pieces) {
		if (board.getPosition(oldRow, oldCol) == getPiece()) {
			if (board.getPosition(row, col) == null) {
				if (maximum() == 1) {
					board.setPosition(row, col, getPiece());
					transformAdjecents(board, pieces, row, col);

				} else if (maximum() == 2) {
					board.setPosition(row, col, getPiece());
					board.setPosition(oldRow, oldCol, null);
					transformAdjecents(board, pieces, row, col);
				} else {
					throw new GameError("position (" + row + "," + col + ") is illegal!");
				}

			} else {
				throw new GameError("position (" + row + "," + col + ") is already occupied!");
			}
		} else {
			throw new GameError("In the position (" + oldRow + "," + oldCol + ") there is no piece of yours.");
		}
	}

	private int maximum() {
		int maximum;
		maximum = Math.abs(oldRow - row);
		if (maximum < Math.abs(oldCol - col)) {
			maximum = Math.abs(oldCol - col);
		}
		return maximum;
	}

	private static final int deltas[][] = { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, 1 }, { -1, 0 },
			{ -1, -1 }, };

	private boolean dentro(int x, int y, int dim) {
		return x >= 0 && y >= 0 && x < dim && y < dim;
	}

	private void transformAdjecents(Board board, List<Piece> pieces, int oldRow, int oldCol) {
		for (int[] ds : deltas) {
			int x = oldRow + ds[0];
			int y = oldCol + ds[1];
			if (dentro(x, y, board.getRows()) && board.getPosition(x, y) != null
					&& board.getPosition(x, y).getId() != "*") {
				board.setPosition(x, y, getPiece());
			}
		}
	}

	/**
	 * This move can be constructed from a string of the form "row SPACE col"
	 * where row and col are integers representing a position.
	 * 
	 * <p>
	 * Se puede construir un movimiento desde un string de la forma
	 * "row SPACE col" donde row y col son enteros que representan una casilla.
	 */
	@Override
	public GameMove fromString(Piece p, String str) {
		String[] words = str.split(" ");
		if (words.length != 4) {
			return null;
		}

		try {
			int oldRow, oldCol, row, col;
			oldRow = Integer.parseInt(words[0]);
			oldCol = Integer.parseInt(words[1]);
			row = Integer.parseInt(words[2]);
			col = Integer.parseInt(words[3]);
			return createMove(oldRow, oldCol, row, col, p);
		} catch (NumberFormatException e) {
			return null;
		}

	}

	/**
	 * Creates a move that is called from {@link #fromString(Piece, String)}.
	 * Separating it from that method allows us to use this class for other
	 * similar games by overriding this method.
	 * 
	 * <p>
	 * Crea un nuevo movimiento con la misma ficha utilizada en el movimiento
	 * actual. Llamado desde {@link #fromString(Piece, String)}; se separa este
	 * metodo del anterior para permitir utilizar esta clase para otros juegos
	 * similares sobrescribiendo este metodo.
	 * 
	 * @param row
	 *            Row of the move being created.
	 *            <p>
	 *            Fila del nuevo movimiento.
	 * 
	 * @param col
	 *            Column of the move being created.
	 *            <p>
	 *            Columna del nuevo movimiento.
	 */
	protected GameMove createMove(int oldRow, int oldCol, int row, int col, Piece p) {
		return new AtaxxMove(oldRow, oldCol, row, col, p);
	}

	@Override
	public String help() {
		return "Row and column for origin and for destination, separated by spaces (four numbers).";
	}

	@Override
	public String toString() {
		if (getPiece() == null) {
			return help();
		} else {
			return "Place a piece '" + getPiece() + "' at (" + row + "," + col + ")";
		}
	}
}