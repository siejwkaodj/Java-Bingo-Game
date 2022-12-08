package java_bingo_game;

import java.util.*;

public class Bingo{
	FileManager fm = new FileManager();
    // Members
    public static int n;
    private static Word[][] comBingoBoard;
    private static Word[][] userBingoBoard;
    
    private static boolean[][] comBoard;
    private static boolean[][] userBoard;

    // Constructor
    Bingo(int n){
    	Bingo.n = n;
    }
    
    // 다음 빙고 하나 맞추기까지 몇개 남았는지 알려주는 함수
    public int getLeftBingoTileNum(boolean[][] board) {
    	int cnt = 0, maxCnt = 0;
    	
    	// 가로줄 체크
    	for(int i = 0; i < n; i++) {
    		cnt = 0;
    		for(int j = 0; j < n; j++) {
    			if(board[i][j]) {
    				cnt++;
    			}
    		}
    		if(cnt > maxCnt && cnt < n) {
    			maxCnt = cnt;
    		}
    	}
    	// 세로줄 체크	
		for(int i = 0; i < n; i++) {
			cnt = 0;
    		for(int j = 0; j < n; j++) {
    			if(board[j][i]) {
    				cnt++;
    			}
    		}
    		if(cnt > maxCnt && cnt < n) {
    			maxCnt = cnt;
    		}
    	}
    	
    	// \대각선 체크	
		cnt = 0;
		for(int j = 0; j < n; j++) {
			if(board[j][j]) {
				cnt++;
			}
		}
		if(cnt > maxCnt && cnt < n) {
			maxCnt = cnt;
		}
    	
    	// / 대각선 체크	
		cnt = 0;
		for(int j = 0; j < n; j++) {
			if(board[n - 1 - j][j]) {
				cnt++;
			}
		}
		if(cnt > maxCnt && cnt < n) {
			maxCnt = cnt;
		}
		return n - maxCnt;
    }
    
    // 빙고 개수 반환하는 함수
    public int getBingoNum(boolean[][] board, Word[][] wordBoard, String name) {
    	int cnt = 0;
    	boolean isBingoExist;
    	
		System.out.println();
		System.out.println(name + "빙고 체크 -------");
		for(boolean[] boollst : board) {
			for(boolean b : boollst) {
				if(b)
					System.out.print("O ");
				else
					System.out.print("X ");
			}
			System.out.println();
		}
    	
		// 가로줄 체크
    	for(int i = 0; i < n; i++) {
    		isBingoExist = true;
    		for(int j = 0; j < n; j++) {
    			if(!board[i][j]) {
    				isBingoExist = false;
    				break;
    			}
    		}
    		if(isBingoExist) {
    			cnt++;
    		}
    	}
    	// 세로줄 체크	
		for(int i = 0; i < n; i++) {
			isBingoExist = true;
    		for(int j = 0; j < n; j++) {
    			if(!board[j][i]) {
    				isBingoExist = false;
    				break;
    			}
    		}
    		if(isBingoExist) {
    			cnt++;
    		}
    	}
    	
    	// \대각선 체크	
		isBingoExist = true;
		for(int j = 0; j < n; j++) {
			if(!board[j][j]) {
				isBingoExist = false;
				break;
			}
		}
		if(isBingoExist) {
			cnt++;
		}
    	
    	// / 대각선 체크	
		isBingoExist = true;
		for(int j = 0; j < n; j++) {
			if(!board[n - 1 - j][j]) {
				isBingoExist = false;
				break;
			}
		}
		if(isBingoExist) {
			cnt++;
		}
    	
    	return cnt;
    }
    
    // 0 -> draw | 1 -> com win | 2 -> user win
    public int checkBingo() {	// 둘 다 빙고일시, GameFrame에서 먼저 시작한 사람이 이긴 걸로 처리.
    	int[] bingoNum = {0, 0};
    	
    	bingoNum[0] = getBingoNum(comBoard, comBingoBoard, "--- com 보드 ---");
    	bingoNum[1] = getBingoNum(userBoard, userBingoBoard, "--- user 보드 ---");
    	
    	if(MainFrame.adminMode) {
    		System.out.println("com 빙고 개수 - " + bingoNum[0] + "\n"
    				+ "user 빙고 개수 - " + bingoNum[1]);
    	}
    	
    	// 같으면 0, 컴퓨터 승이면 1, user 승이면 2, 무승부로 게임 끝났으면 3
    	if(bingoNum[0] == bingoNum[1]) {
    		if(bingoNum[0] < 2 * n + 2) {
    			return 0;
    		}else{
    			return 3;
    		}
    	}else if(bingoNum[0] > bingoNum[1]) {
    		return 1;
    	}else {
    		return 2;
    	}
    }
    
    // bingoBoard, board 만드는 메소드
    public void setBingoBoard() {
    	comBingoBoard = fm.getRandomWordArray(n);
    	if(comBingoBoard == null) {
    		userBingoBoard = null;
    		System.out.println("빙고보드 제작 실패, n^2 > wordNum");
    		return;
    	}
    	userBingoBoard = fm.getRandomWordArray(n);
        
        comBoard = new boolean[n][n];
        userBoard = new boolean[n][n];
        
        System.out.println("bingoBoard 제작 완료");
        System.out.println("com 빙고 보드 ---");
		for(Word[] wlst : comBingoBoard) {
			for(Word w : wlst) {
				System.out.print(w.getEng() + " ");
			}
			System.out.println();
		}
    }

    
    // getter, setter
    // 빙고보드 getter
    public Word[][] getComBingoBoard() {
        return comBingoBoard;
    }

    public Word[][] getUserBingoBoard() {
        return userBingoBoard;
    }

    // 선택했는지 체크하는 빙고보드
	public boolean[][] getUserBoard() {
		return userBoard;
	}
	
	// userBoard[][]에서 s에 해당하는 칸 뒤집고
	public int[] setUserBoard(String s) {
		int[] arr = {-1, -1};
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(userBingoBoard[i][j].getEng().equals(s) && !userBoard[i][j]) {
					userBoard[i][j] = true;
					arr[0] = i;
					arr[1] = j;
					break;
				}
			}
		}
		return arr;
	}
	public int[] setUserBoard(Word w) {
		return setUserBoard(w.getEng());
	}

	
	public boolean[][] getComBoard() {
		return comBoard;
	}

	// user선택 -> com보드에 있다면 true로 설정하기 위한 함수	
	public int[] setComBoard(String s) {
		int[] coord = {-1, -1};
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(comBingoBoard[i][j].getEng().equals(s) && !comBoard[i][j]) {
					comBoard[i][j] = true;
					coord[0] = i;
					coord[1] = j;
					break;
				}
			}
		}
		return coord;
	}
	// 컴퓨터가 com보드에서 타일을 하나 고르고, 해당 좌표를 int[] 로 return하는 메소드
	public int[] setComBoard() {
		HashSet<Integer[]> unclicked = new HashSet<>();
//		Random r = new Random();	// random 전략시 사용
		// 클릭하지 않은 칸들 hashSet에 추가
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n;j++) {
				if(!comBoard[i][j]) {
					Integer[] arr = {i, j};
					unclicked.add(arr);
				}
			}
		}
		
		// 무작위로 하나 선택 및 userboard에 있다면 그것도 선택
		int[] returnArr = { -1, -1};	// 좌표 반환하는 배열, 만약 좌표 선택할 수 없다면 음수 좌표 return.
		int cnt, maxWeight = -1;
		if(unclicked.size() > 0) {
			int[][] weightMatrix = new int[n][n];
			// 1. weightMatrix 구성
			// 2. weight값 가장 높은 것 선택
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; j++) {
					cnt = 0;
					if(comBoard[i][j]) {
						weightMatrix[i][j] = -1;
					}else {
						// 대각선 개수만큼 ++
						if(i == j) {
							weightMatrix[i][j]++;
							
							cnt = 0;
							for(int k = 0; k < n; k++) {
								if(comBoard[k][k]) {
									cnt++;
								}
							}
							weightMatrix[i][j] += cnt * cnt;
						}
						if(n - 1 - i == j) {
							weightMatrix[i][j]++;
							
							cnt = 0;
							for(int k = 0; k < n; k++) {
								if(comBoard[n - 1 - k][k]) {
									cnt++;
								}
							}
							weightMatrix[i][j] += cnt * cnt;
						}
						
						// 가로 채워진 빙고 개수 체크
						cnt = 0;
						for(int k = 0; k < n; k++) {
							if(comBoard[i][k]) {
								cnt++;
							}
						}
						weightMatrix[i][j] += cnt * cnt;
						
						// 세로 채워진 빙고 개수 체크
						cnt = 0;
						for(int k = 0; k < n; k++) {
							if(comBoard[k][j]) {
								cnt++;
							}
						}
						weightMatrix[i][j] += cnt * cnt;
						if(weightMatrix[i][j] > maxWeight) {
							returnArr[0] = i;
							returnArr[1] = j;
							maxWeight = weightMatrix[i][j];
						}
					}
				}
			}
			System.out.println("weight matrix");
			System.out.println("---------------");
			for(int[] wlst : weightMatrix) {
				for(int weight : wlst) {
					System.out.print(weight + " ");
				}
				System.out.println();
			}
			System.out.println();
			/*	// random 전략시 사용
			Iterator<Integer[]> it = unclicked.iterator();
			for(int i = 0; i < r.nextInt(unclicked.size()); i++) it.next();
			Integer[] coord = it.next();
			int i = coord[0];
			int j = coord[1];
			comBoard[i][j] = true;
			returnArr[0] = i; returnArr[1] = j;
			*/
			
			// 3. 가장 높은 weight 지닌 tile 선택, 뒤집기
			int y = returnArr[0], x = returnArr[1];
			comBoard[y][x] = true;
		}
		
		return returnArr;	// 그냥 comBoard 좌표 return
	}
}
