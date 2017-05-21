import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

public class Engine {

	// public static Map<String, GameState> evaluatedGameStates = new
	// HashMap<String, GameState>();
	// public static Queue<GameState> queue = new LinkedList<GameState>();

	public static int exploredNode = 0;

	public GameState loadGameState(String filename) {
		String input = null;
		GameState gameState = new GameState();
		try {
			Scanner in = new Scanner(new FileReader(filename));
			input = in.next();
			String[] inputS = input.split(",");
			int w = Integer.valueOf(inputS[0]);
			int h = Integer.valueOf(inputS[1]);
			gameState.initState(h, w);
			for (int i = 0; i < Integer.valueOf(inputS[1]); i++) {
				input = in.next();
				String[] line = input.split(",");
				int[] lineInt = new int[w];
				for (int j = 0; j < w; j++) {
					lineInt[j] = Integer.valueOf(line[j]);
				}
				gameState.setLine(i, lineInt);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return gameState;
	}

	public static void main(String[] args) {
		Engine e = new Engine();
		String code = "";
		while (!code.equals("E")) {
			Scanner reader = new Scanner(System.in); // Reading from System.in
			System.out.println(
					"Enter R for Randon walk,B for BFS ,D for DFS,\n M for A* with Manhattan ,H for A* with custom Heuristic else E to exit: ");
			code = reader.next().toUpperCase();
			if (code.equals("E")) {
				break;
			}
			System.out.println("Enter File num to load");
			int file = reader.nextInt();
			String ans = "Y";
			if (file < 5) {
				System.out.println("File with bricks in file name or not : Y for yes or N for No");
				ans = reader.next();
			}
			ClassLoader classLoader = Engine.class.getClassLoader();
			File classpathRoot = new File(classLoader.getResource("").getPath());
			GameState gameState;
			if (ans.equals("Y")) {
				System.out.println(classpathRoot.getPath() + File.separator + "SBP-bricks-level" + file + ".txt");
				gameState = e.loadGameState(classpathRoot + File.separator + "SBP-bricks-level" + file + ".txt");
			} else {
				System.out.println(classpathRoot.getPath() + File.separator + "SBP-level" + file + ".txt");
				gameState = e.loadGameState(classpathRoot + File.separator + "SBP-level" + file + ".txt");

			}
			// queue = new LinkedList<GameState>();

			Map<String, GameState> evaluatedGameStates = new HashMap<String, GameState>();
			// openList = new LinkedList<GameState>();
			gameState.normalizeState(gameState);
			gameState.outputGameState();
			gameState.allMoves(evaluatedGameStates);

			if (!gameState.gameStateSolved()) {
				evaluatedGameStates.put(gameState.getKey(), gameState);
			}
			long lStartTime;
			long lEndTime;
			long difference;
			switch (code) {
			case "R":
				System.out.println("Random");
				e.printRandomMoves(gameState);
				break;
			case "B":
				System.out.println("BFS");
				exploredNode = 0;
				lStartTime = System.currentTimeMillis();
				List<Move> movesGeneratedUsingBFS = e.findMovesRequiredUsingBreadthSearch(gameState);
				lEndTime = System.currentTimeMillis();
				difference = lEndTime - lStartTime;
				e.printSolutionMoves(movesGeneratedUsingBFS);
				System.out.println(exploredNode + " Nodes explored in BFS.Found Solution requires "
						+ movesGeneratedUsingBFS.size() + " moves");
				System.out.println("Time required " + difference + " ms");
				break;
			case "D":
				System.out.println("DFS");
				exploredNode = 0;
				lStartTime = System.currentTimeMillis();
				// Map<String, GameState> evaluatedGameStates1 = new
				// HashMap<String, GameState>();
				if (!gameState.gameStateSolved()) {
					evaluatedGameStates.put(gameState.getKey(), gameState);
				}
				List<Move> movesGeneratedUsingDFS = gameState.findMovesRequiredUsingDepthSearch(evaluatedGameStates);
				e.printSolutionMovesR(movesGeneratedUsingDFS);
				System.out.println(exploredNode + " Nodes explored in DFS. Found Solution requires "
						+ movesGeneratedUsingDFS.size() + " moves");
				lEndTime = System.currentTimeMillis();
				difference = lEndTime - lStartTime;
				System.out.println("Time required " + difference + " ms");
				break;
			case "M":
				System.out.println("Solving using A* with Manhatten distance");
				exploredNode = 0;
				lStartTime = System.currentTimeMillis();
				List<Move> movesGeneratedUsingAStarManhatten = e.findMovesRequiredUsingAStarManhatten(gameState);
				e.printSolutionMovesR(movesGeneratedUsingAStarManhatten);
				System.out.println(exploredNode + " Nodes explored in A* using manhatten. Found Solution requires "
						+ movesGeneratedUsingAStarManhatten.size() + " moves");
				lEndTime = System.currentTimeMillis();
				difference = lEndTime - lStartTime;
				System.out.println("Time required " + difference + " ms");
				break;
			case "H":
				System.out.println("Solving using A* with Custom Heuristic");
				exploredNode = 0;
				lStartTime = System.currentTimeMillis();
				List<Move> movesGeneratedUsingAStarHeuristic = e.findMovesRequiredUsingAStarHeuristic(gameState);
				e.printSolutionMovesR(movesGeneratedUsingAStarHeuristic);
				System.out.println(exploredNode + " Nodes explored in with Custom Heuristic. Found Solution requires "
						+ movesGeneratedUsingAStarHeuristic.size() + " moves");
				lEndTime = System.currentTimeMillis();
				difference = lEndTime - lStartTime;
				System.out.println("Time required " + difference + " ms");
				break;
			case "E":
				break;
			default:
				System.out.println("Invalid option");
			}
		}

	}

	private void insertIntoOpenList(GameState gameState, LinkedList<GameState> openList) {
		if (!openList.isEmpty()) {
			ListIterator<GameState> iter = openList.listIterator();
			while (iter.hasNext()) {
				GameState test = iter.next();
				if (gameState.stateEqual(test)) {
					break;
				}
				if (gameState.getTotalCostF() < test.getTotalCostF()) {
					iter.add(gameState);
					return;
				}

				if ((gameState.getTotalCostF() == test.getTotalCostF())
						& (gameState.getPathToThisNode().size() < test.getPathToThisNode().size())) {
					iter.add(gameState);
					return;
				}
			}
			openList.add(gameState);
		} else {
			openList.add(gameState);
		}

	}

	private void printRandomMoves(GameState gameState) {
		int availableMoves = gameState.getAllPossibleMovesInThisGameState().size();
		System.out.println("Enter random num of tries to " + availableMoves + " moves available");
		Scanner reader = new Scanner(System.in);
		int num = reader.nextInt();
		List<Move> doneMoves = new ArrayList<Move>();
		Random rand = new Random();
		int n = rand.nextInt(availableMoves);
		while (num != 0) {
			List<Move> moves = gameState.getAllPossibleMovesInThisGameState();
			if (doneMoves.contains(moves.get(n))) {
				n = rand.nextInt(availableMoves);
			} else {
				GameState newState = gameState.applyMoveAndGetClone(moves.get(n));
				doneMoves.add(moves.get(n));
				System.out.println(moves.get(n));
				newState.outputGameState();
				num--;
			}
		}
	}

	private void printSolutionMoves(List<Move> movesGeneratedUsingDFS) {
		for (Move move : movesGeneratedUsingDFS) {
			System.out.print(move);
			System.out.print("\n");
		}
	}

	private void printSolutionMovesR(List<Move> movesGeneratedUsingDFS) {
		for (int i = movesGeneratedUsingDFS.size() - 1; i >= 0; i--) {
			System.out.print(movesGeneratedUsingDFS.get(i));
			System.out.print("\n");
		}
	}

	private GameState applyMove(GameState gameState, Move move) {
		GameState newGameState = gameState.applyMove(move, gameState);
		return newGameState;
	}

	private GameState applyMoveCloning(GameState gameState, Move move) {
		GameState newGameState = gameState.applyMoveAndGetClone(move);
		return newGameState;
	}

	/*
	 * private List<Move> allMoves(GameState gameState) { List<Move>
	 * listOfAllPossibleMoves = gameState.allMoves(); return
	 * listOfAllPossibleMoves; }
	 */
	private GameState normalizeState(GameState gameState) {
		return gameState.normalizeState(gameState);
	}

	public List<Move> findMovesRequiredUsingBreadthSearch(GameState gameState) {
		List<Move> bubbledUpSolutionMove = new ArrayList<Move>();
		Map<String, GameState> evaluatedGameStates = new HashMap<String, GameState>();
		Queue<GameState> queue = new LinkedList<GameState>();
		if (!gameState.gameStateSolved()) {
			evaluatedGameStates.put(gameState.getKey(), gameState);
		}
		List<Move> movesInThisState = gameState.allMoves(evaluatedGameStates);
		queue.add(gameState);
		exploredNode++;
		if (bubbledUpSolutionMove.isEmpty()) {
			while (queue.peek() != null) {
				GameState node = queue.poll();
				for (Move move : node.getAllPossibleMovesInThisGameState()) {
					if (node.checkIfSolvedAfterMakingMove(move)) {
						exploredNode++;
						GameState test = node.applyMoveAndGetClone(move);
						test.outputGameState();
						// test.allMoves();
						test.getPathToThisNode().add(move);
						bubbledUpSolutionMove = test.getPathToThisNode();
						return bubbledUpSolutionMove;
					} else {
						GameState test = node.applyMoveAndGetClone(move);
						exploredNode++;
						// test.outputGameState();
						test.allMoves(evaluatedGameStates);
						test.setHeuristicCostAndRetruCoOrdinates();
						test.getPathToThisNode().add(move);
						queue.add(test);
					}
				}
			}
		}
		return bubbledUpSolutionMove;
	}

	private List<Move> findMovesRequiredUsingAStarManhatten(GameState gameState) {
		List<Move> bubbledUpSolutionMove = new ArrayList<Move>();
		// evaluatedGameStates = new HashMap<String, GameState>();
		Map<String, GameState> evaluatedGameStates = new HashMap<String, GameState>();
		if (!gameState.gameStateSolved()) {
			evaluatedGameStates.put(gameState.getKey(), gameState);
		}
		LinkedList<GameState> openList = new LinkedList<GameState>();
		List<Move> movesInThisState = gameState.allMoves(evaluatedGameStates);
		gameState.setHeuristicCostAndRetruCoOrdinates();
		gameState.setTotalCostF();
		openList.add(gameState);
		exploredNode++;

		while (!openList.isEmpty()) {
			GameState node = openList.getFirst();
			openList.removeFirst();
			for (Move move : node.getAllPossibleMovesInThisGameState()) {
				if (node.checkIfSolvedAfterMakingMove(move)) {
					exploredNode++;
					GameState test = node.applyMoveAndGetClone(move);
					test.outputGameState();
					test.getPathToThisNode().add(move);
					bubbledUpSolutionMove = test.getPathToThisNode();
					return bubbledUpSolutionMove;
				} else {
					GameState test = node.applyMoveAndGetClone(move);
					exploredNode++;
					// test.outputGameState();
					test.allMoves(evaluatedGameStates);
					test.getPathToThisNode().add(move);
					test.setHeuristicCostAndRetruCoOrdinates();
					test.setTotalCostF();
					insertIntoOpenList(test, openList);
				}
			}
		}

		return bubbledUpSolutionMove;
	}

	private List<Move> findMovesRequiredUsingAStarHeuristic(GameState gameState) {

		List<Move> bubbledUpSolutionMove = new ArrayList<Move>();
		Map<String, GameState> evaluatedGameStates = new HashMap<String, GameState>();
		if (!gameState.gameStateSolved()) {
			evaluatedGameStates.put(gameState.getKey(), gameState);
		}
		LinkedList<GameState> openList = new LinkedList<GameState>();
		List<Move> movesInThisState = gameState.allMoves(evaluatedGameStates);
		gameState.setHeuristicCostAndRetruCoOrdinates();
		gameState.setTotalCostF();
		openList.add(gameState);
		exploredNode++;
		while (!openList.isEmpty()) {
			GameState node = openList.getFirst();
			openList.removeFirst();
			for (Move move : node.getAllPossibleMovesInThisGameState()) {
				if (node.checkIfSolvedAfterMakingMove(move)) {
					exploredNode++;
					GameState test = node.applyMoveAndGetClone(move);
					test.outputGameState();
					test.getPathToThisNode().add(move);
					bubbledUpSolutionMove = test.getPathToThisNode();
					return bubbledUpSolutionMove;
				} else {
					GameState test = node.applyMoveAndGetClone(move);
					exploredNode++;
					// test.outputGameState();
					evaluatedGameStates.put(test.getKey(), test);
					test.allMoves(evaluatedGameStates);
					test.getPathToThisNode().add(move);
					test.setHeuristicCostAndRetruCoOrdinates();
					;
					test.setTotalCostUsingCustomHeuristicF();
					if (move.getTileNumber() == 2) {
						test.heuristicCost--;
					}
					insertIntoOpenList(test, openList);
				}
			}
		}

		return bubbledUpSolutionMove;

	}
}