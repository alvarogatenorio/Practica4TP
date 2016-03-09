package es.ucm.fdi.tp.practica4.ataxx;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import es.ucm.fdi.tp.basecode.bgame.control.ConsolePlayer;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.DummyAIPlayer;
import es.ucm.fdi.tp.basecode.bgame.control.GameFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.AIAlgorithm;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.basecode.bgame.views.GenericConsoleView;

public class AtaxxFactory implements GameFactory {

	private int dim;

	private int obstacles;

	/**
	 * Constructor without arguments, when the main parameters are wrong, we
	 * create an Ataxx board of 5x5 by default.
	 */
	public AtaxxFactory() {
		this.dim = 5;
		this.obstacles = 4;
	}

	/**
	 * Constructor with arguments, used when the parameters in the main method
	 * are correct, to create a custom board of ataxx.
	 * 
	 * @param dim
	 * 
	 *            NOTE: It throws game errors (runtime exceptions) if the
	 *            arguments are bad (less than five), To avoid future errors, we
	 *            have to make sure that the arguments here are correct..
	 */
	public AtaxxFactory(int dim, int obstacles) {
		/*JAMÁS debería saltar esta excepción, debemos asegurarnos de la
		 * corrección de los datos antes de llegar aquí.*/
		// Illegal dimensions cases (treat before here)!!
		/*if (dim < 5) {
			throw new GameError("Dimension must be at least 5: " + dim);
		} else if (dim % 2 == 0) {
			throw new GameError("Dimesion must be odd: " + dim);
		} else { //everything went good. (ESTÁ GARANTIZADO)*/
			this.dim = dim;
			this.obstacles = obstacles;
		//}
	}
	
	@Override
	public GameRules gameRules() {
		return new AtaxxRules(dim, obstacles);
	}

	@Override
	public Player createConsolePlayer() {
		ArrayList<GameMove> possibleMoves = new ArrayList<GameMove>();
		//WTF
		possibleMoves.add(new AtaxxMove());
		return new ConsolePlayer(new Scanner(System.in), possibleMoves);
	}

	@Override
	public Player createRandomPlayer() {
		return new AtaxxRandomPlayer();
	}

	@Override
	public Player createAIPlayer(AIAlgorithm alg) {
		return new DummyAIPlayer(createRandomPlayer(), 1000);
	}

	/**
	 * If a concrete list of pieces is not specified in the command line, by
	 * default, we will create two, X and O.
	 * <p>
	 * Si no se especifica nada por la linea de comandos acerca de las piezas,
	 * por defecto insertaremos dos con nombres X y O.
	 */
	@Override
	public List<Piece> createDefaultPieces() {
		List<Piece> pieces = new ArrayList<Piece>();
		/*
		 * WARNING: If you want to touch the pieces names, mind its correctness,
		 * otherwise the full program will explode.
		 */
		pieces.add(new Piece("X"));
		pieces.add(new Piece("O"));
		return pieces;
	}

	@Override
	public void createConsoleView(Observable<GameObserver> g, Controller c) {
		new GenericConsoleView(g, c);
	}

	@Override
	public void createSwingView(final Observable<GameObserver> g, final Controller c, final Piece viewPiece,
			Player random, Player ai) {
		throw new UnsupportedOperationException("There is no swing view");
	}

}
