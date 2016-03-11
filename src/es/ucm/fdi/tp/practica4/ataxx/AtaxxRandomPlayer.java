package es.ucm.fdi.tp.practica4.ataxx;



import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.Utils;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;


/**
 * A random player for Ataxx.
 * 
 * <p>
 * Un jugador aleatorio para Ataxx.
 *
 */
public class AtaxxRandomPlayer extends Player {

	private static final long serialVersionUID = 1L;

	@Override
	public GameMove requestMove(Piece p, Board board, List<Piece> pieces, GameRules rules) {
		List<GameMove> moves = new ArrayList<GameMove>();
		moves = rules.validMoves(board, pieces, p);
		if(moves.size()!=0){
			int random=Utils.randomInt(moves.size());
		return moves.get(random);
		}
		else 
			return null;
	}

	/**
	 * Creates the actual move to be returned by the player. Separating this
	 * method from {@link #requestMove(Piece, Board, List, GameRules)} allows us
	 * to reuse it for other similar games by overriding this method.
	 * 
	 * <p>
	 * Crea el movimiento concreto que sera devuelto por el jugador. Se separa
	 * este metodo de {@link #requestMove(Piece, Board, List, GameRules)} para
	 * permitir la reutilizacion de esta clase en otros juegos similares,
	 * sobrescribiendo este metodo.
	 * 
	 * @param row
	 *            row number.
	 * @param col
	 *            column number..
	 * @param p
	 *            Piece to place at ({@code row},{@code col}).
	 * @return
	 */
	protected GameMove createMove(int oldRow, int oldCol, int row, int col, Piece p) {
		return new AtaxxMove(oldRow, oldCol, row, col, p);
	}

}
