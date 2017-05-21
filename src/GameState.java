import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GameState implements Cloneable {

	private int[][] state;
	private String key;
	private int w;
	private int h;
	private List<Move> allPossibleMovesInThisGameState = new ArrayList<Move>();
	private boolean visited = false;
	// private List<GameState> availableGameStates = new ArrayList<GameState>();
	private List<Move> pathToThisNode = new ArrayList<Move>();
	private int totalCostF;
	public int heuristicCost;

	public String hash() {
		final int prime = 31;
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				result.append(this.state[i][j]);
			}

		}
		key = result.toString();
		return key;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameState other = (GameState) obj;
		if (!Arrays.deepEquals(state, other.state))
			return false;
		return true;
	}

	public String getKey() {
		if (key == null) {
			hash();
		}
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getTotalCostF() {
		return totalCostF;
	}

	public void setTotalCostF() {
		this.totalCostF = this.heuristicCost + this.pathToThisNode.size();
	}

	public void setTotalCostUsingCustomHeuristicF() {
		this.totalCostF = this.heuristicCost + this.pathToThisNode.size();
		int lastIndex = this.pathToThisNode.size();
		/*
		 * if (this.pathToThisNode.get(lastIndex - 1).tileNumber == 2) {
		 * this.totalCostF++; }
		 */
		int[] pointPack = setHeuristicCostAndRetruCoOrdinates();
		int ringTwoI = pointPack[0] - 1;
		int ringTwoJ = pointPack[1] - 1;
		int ringTwoEndI = pointPack[2] + 1;
		int ringTwoEndJ = pointPack[3] + 1;
		boolean zeroInVisinity = false;
		for (int i = ringTwoI; i < ringTwoJ; i++) {
			for (int j = ringTwoEndI; i < ringTwoJ; i++) {
				if (this.state[i][j] == 0) {
					zeroInVisinity = true;
				}
			}
		}
		if (zeroInVisinity) {
			this.totalCostF--;
		}
	}

	public int getManhanttanCostH() {
		return heuristicCost;
	}

	public int[] setHeuristicCostAndRetruCoOrdinates() {
		int startTileI = 0, endTileI = 0, startTileJ = 0, endTileJ = 0, startGoalI = 0, endGoalI = 0, startGoalJ = 0,
				endGoalJ = 0;
		int[] pointPack = new int[8];
		this.heuristicCost = 0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (state[i][j] == 2) {
					if (startTileI == 0) {
						startTileI = i;
						startTileJ = j;
					}
					endTileI = i;
					endTileJ = j;
				}
				if (state[i][j] == -1) {
					if (startGoalI == 0) {
						startGoalI = i;
						startGoalJ = j;
					}
					endGoalI = i;
					endGoalJ = j;
				}
			}
		}
		if (endTileI > endGoalI) {
			this.heuristicCost = endTileI - endGoalI;
		} else {
			this.heuristicCost = endGoalI - endTileI;
		}
		if (endTileJ > endGoalJ) {
			this.heuristicCost += (endTileJ - endGoalJ);
		} else {
			this.heuristicCost += (endGoalJ - endTileJ);
		}
		pointPack[0] = startTileI;
		pointPack[1] = startTileJ;
		pointPack[2] = endTileI;
		pointPack[3] = endTileJ;
		pointPack[4] = startGoalI;
		pointPack[5] = startGoalJ;
		pointPack[7] = endGoalJ;
		pointPack[6] = endGoalI;

		return pointPack;
	}

	public List<Move> getPathToThisNode() {
		return pathToThisNode;
	}

	public void setPathToThisNode(List<Move> pathToThisNode) {
		this.pathToThisNode = pathToThisNode;
	}

	public int[][] getState() {
		return state;
	}

	public void setState(int[][] state) {
		this.state = state;
	}

	public List<Move> getAllPossibleMovesInThisGameState() {
		return allPossibleMovesInThisGameState;
	}

	public void setAllPossibleMovesInThisGameState(List<Move> allPossibleMovesInThisGameState) {
		this.allPossibleMovesInThisGameState = allPossibleMovesInThisGameState;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public void initState(int h, int w) {
		this.state = new int[h][w];
		this.w = w;
		this.h = h;
	}

	public void setLine(int l, int[] lineInt) {
		for (int i = 0; i < w; i++) {
			state[l][i] = lineInt[i];
		}
	}

	public void outputGameState() {
		System.out.print(w + "," + h + "\n");
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				System.out.print(state[i][j] + ",");
			}
			System.out.print("\n");
		}
	}

	public List<Move> allMoves(Map<String, GameState> evaluatedGameStates) {
		allPossibleMovesInThisGameState = new ArrayList<Move>();
		int highestTileNumber = getHighestTileNumber();
		for (int i = 2; i < highestTileNumber + 1; i++) {
			allPossibleMovesInThisGameState.addAll(allMoveHelp(i, evaluatedGameStates));
		}
		return allPossibleMovesInThisGameState;
	}

	public List<Move> allMoveHelp(int blockNum, Map<String, GameState> evaluatedGameStates) {

		List<Move> allMoveForBlock = new ArrayList<Move>();
		if (checkIfBlockCanBeMovedLeft(blockNum)) {

			Move move = new Move();
			move.setDirection(Direction.LEFT);
			move.setTileNumber(blockNum);
			if (checkIfMoveIsUnique(move, evaluatedGameStates)) {
				allMoveForBlock.add(move);
				GameState evauluatedState = this.applyMoveAndGetClone(move);
				evaluatedGameStates.put(evauluatedState.getKey(), evauluatedState);
			}
		}
		if (checkIfBlockCanBeMovedRight(blockNum)) {
			Move move = new Move();
			move.setDirection(Direction.RIGHT);
			move.setTileNumber(blockNum);
			if (checkIfMoveIsUnique(move, evaluatedGameStates)) {
				allMoveForBlock.add(move);
				GameState evauluatedStates = this.applyMoveAndGetClone(move);
				evaluatedGameStates.put(evauluatedStates.getKey(), evauluatedStates);
			}
		}
		if (checkIfBlockCanBeMovedUp(blockNum)) {
			Move move = new Move();
			move.setDirection(Direction.UP);
			move.setTileNumber(blockNum);
			if (checkIfMoveIsUnique(move, evaluatedGameStates)) {
				allMoveForBlock.add(move);
				GameState evauluatedStates = this.applyMoveAndGetClone(move);
				evaluatedGameStates.put(evauluatedStates.getKey(), evauluatedStates);
			}
		}
		if (checkIfBlockCanBeMovedDown(blockNum)) {
			Move move = new Move();
			move.setDirection(Direction.DOWN);
			move.setTileNumber(blockNum);
			if (checkIfMoveIsUnique(move, evaluatedGameStates)) {
				allMoveForBlock.add(move);
				GameState evauluatedStates = this.applyMoveAndGetClone(move);
				evaluatedGameStates.put(evauluatedStates.getKey(), evauluatedStates);
			}
		}
		return allMoveForBlock;
	}

	private boolean checkIfMoveIsUnique(Move move, Map<String, GameState> evaluatedGameStates) {
		boolean unique = true;
		GameState test = this.applyMoveAndGetClone(move);
		if (evaluatedGameStates.containsKey(test.getKey())) {
			unique = false;

		}
		/*
		 * for (GameState eval : Engine.openList) { if (eval.stateEqual(this)) {
		 * if (eval.getPathToThisNode().size() >
		 * test.getPathToThisNode().size()) {
		 * eval.setPathToThisNode(test.getPathToThisNode()); } }
		 * 
		 * }
		 */
		return unique;
	}

	private boolean checkIfBlockCanBeMovedLeft(int blockNum) {
		boolean canBeMoved = false;
		for (int i = 1; i < h; i++) {
			for (int j = 1; j < w; j++) {
				if (this.state[i][j] == blockNum) {
					if (!(this.state[i][j] < 2)) {
						if (this.state[i][j - 1] == 0 | this.state[i][j - 1] == blockNum
								| (this.state[i][j - 1] == -1 && blockNum == 2)) {
							canBeMoved = true;
						} else {
							canBeMoved = false;
							return canBeMoved;
						}
					}
				}
			}
		}
		return canBeMoved;
	}

	private boolean checkIfBlockCanBeMovedRight(int blockNum) {
		boolean canBeMoved = false;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (this.state[i][j] == blockNum) {
					if (!(this.state[i][j] < 2)) {
						if (this.state[i][j + 1] == 0 | this.state[i][j + 1] == blockNum
								| (this.state[i][j + 1] == -1 && blockNum == 2)) {
							canBeMoved = true;
						} else {
							canBeMoved = false;
							return canBeMoved;
						}
					}
				}
			}
		}
		return canBeMoved;
	}

	private boolean checkIfBlockCanBeMovedUp(int blockNum) {
		boolean canBeMoved = false;
		for (int i = 1; i < h; i++) {
			for (int j = 1; j < w; j++) {
				if (this.state[i][j] == blockNum) {
					if (!(this.state[i][j] < 2)) {
						if (this.state[i - 1][j] == 0 | this.state[i - 1][j] == blockNum
								| (this.state[i - 1][j] == -1 && blockNum == 2)) {
							canBeMoved = true;
						} else {
							canBeMoved = false;
							return canBeMoved;
						}
					}
				}
			}
		}
		return canBeMoved;
	}

	private boolean checkIfBlockCanBeMovedDown(int blockNum) {
		boolean canBeMoved = false;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (this.state[i][j] == blockNum) {
					if (!(this.state[i][j] < 2)) {
						if (this.state[i + 1][j] == 0 | this.state[i + 1][j] == blockNum
								| (this.state[i + 1][j] == -1 && blockNum == 2)) {
							canBeMoved = true;
						} else {
							canBeMoved = false;
							return canBeMoved;
						}
					}
				}
			}
		}
		return canBeMoved;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		GameState clone = (GameState) super.clone();
		int[][] cloneState = new int[clone.h][clone.w];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				cloneState[i][j] = this.state[i][j];
			}
		}
		clone.setState(cloneState);
		List<Move> clonedMoves = new ArrayList<Move>();
		List<Move> clonePathToThisdMoves = new ArrayList<Move>();
		for (Move cloneMove : allPossibleMovesInThisGameState) {
			clonedMoves.add(cloneMove);
		}
		for (Move cloneMove : pathToThisNode) {
			clonePathToThisdMoves.add(cloneMove);
		}
		clone.setAllPossibleMovesInThisGameState(clonedMoves);
		clone.setPathToThisNode(clonePathToThisdMoves);
		// clone.setAvailableGameStates(new ArrayList<GameState>());
		return clone;
	}

	public GameState applyMove(Move move, GameState oldGameState) {
		for (int i = 1; i < h; i++) {
			for (int j = 1; j < w; j++) {
				if (oldGameState.state[i][j] == move.tileNumber) {
					switch (move.direction) {
					case LEFT:
						this.state[i][j - 1] = move.tileNumber;
						if (oldGameState.state[i][j + 1] != move.tileNumber) {
							this.state[i][j] = 0;
						}
						break;
					case RIGHT:
						this.state[i][j + 1] = move.tileNumber;
						if (oldGameState.state[i][j - 1] != move.tileNumber) {
							this.state[i][j] = 0;
						}
						break;
					case UP:
						this.state[i - 1][j] = move.tileNumber;
						if (oldGameState.state[i + 1][j] != move.tileNumber) {
							this.state[i][j] = 0;
						}
						break;
					case DOWN:
						// swapDown(i, j, i + 1, j);
						this.state[i + 1][j] = move.tileNumber;
						if (oldGameState.state[i - 1][j] != move.tileNumber) {
							this.state[i][j] = 0;
						}
						break;
					default:
						break;
					}
				}
			}
		}
		return this;
	}

	public boolean gameStateSolved() {
		boolean solved = true;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (this.state[i][j] == -1) {
					solved = false;
				}
			}
		}
		return solved;
	}

	private int getHighestTileNumber() {
		int num = 2;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (state[i][j] > num) {
					num = state[i][j];
				}
			}
		}
		return num;
	}

	boolean stateEqual(GameState comparedState) {
		boolean isEqual = true;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (this.state[i][j] != comparedState.state[i][j]) {
					isEqual = false;
				}
			}
		}
		return isEqual;
	}

	/*
	 * Normalize the gamestate to ensure repeted state is detected and not added
	 * to list of available moves
	 */
	public GameState normalizeState(GameState gameState) {
		int nextIdx = 3;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (state[i][j] == nextIdx) {
					nextIdx++;
				} else if (state[i][j] > nextIdx) {
					swapIdx(nextIdx, state[i][j]);
					nextIdx++;
				}
			}
		}
		return gameState;
	}

	private void swapIdx(int idx1, int idx2) {
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (state[i][j] == idx1) {
					state[i][j] = idx2;
				} else if (state[i][j] == idx2) {
					state[i][j] = idx1;
				}
			}
		}
	}

	/*
	 * This method recursively goes into the left element first till a solution
	 * is found,otherwise moves one level up and goes into the right element
	 */
	public List<Move> findMovesRequiredUsingDepthSearch(Map<String, GameState> evaluatedGameStates) {
		List<Move> bubbledUpSolutionMove = new ArrayList<Move>();
		for (Move move : allPossibleMovesInThisGameState) {
			if (checkIfSolvedAfterMakingMove(move)) {
				GameState test = applyMoveAndGetClone(move);
				Engine.exploredNode++;
				test.outputGameState();
				bubbledUpSolutionMove.add(move);
				break;
			} else {
				GameState test = applyMoveAndGetClone(move);
				// test.outputGameState();
				// test.findMovesThatWillGiveUniqueGameState(availableGameStates);
				test.allMoves(evaluatedGameStates);
				Engine.exploredNode++;
				bubbledUpSolutionMove = test.findMovesRequiredUsingDepthSearch(evaluatedGameStates);
				if (!bubbledUpSolutionMove.isEmpty()) {
					bubbledUpSolutionMove.add(move);
					break;
				}
			}
		}
		return bubbledUpSolutionMove;
	}
/*
	private void findMovesThatWillGiveUniqueGameState(List<GameState> previousGameStates2) {
		setAllPossibleMovesInThisGameState(allMoves());

	}*/

	boolean checkIfSolvedAfterMakingMove(Move move) {
		GameState newStateAfterMove = applyMoveAndGetClone(move);
		// Engine.evaluatedGameStates.put(newStateAfterMove.getKey(),
		// newStateAfterMove);
		return newStateAfterMove.gameStateSolved();
	}

	public GameState applyMoveAndGetClone(Move move) {
		GameState newGameState = new GameState();
		try {
			newGameState = (GameState) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		newGameState.applyMove(move, this);
		newGameState.setKey(newGameState.hash());
		newGameState.normalizeState(newGameState);
		return newGameState;
	}
}
